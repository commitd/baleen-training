package io.committed.training.baleenfootball.utils;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;

public abstract class AbstractFolderReader extends BaleenCollectionReader {

  private WatchService watcher;
  private Optional<Path> currentFile = Optional.empty();
  private final Map<WatchKey, Path> watchKeys = new HashMap<>();
  private final Queue<Path> fileQueue = new LinkedList<>();


  protected abstract String[] getFolders();

  protected abstract boolean isRecursive();

  protected abstract int getTimeout();

  protected abstract boolean isFileIncluded(Path path);

  protected abstract boolean processFile(JCas jCas, Path file);

  @Override
  protected void doInitialize(UimaContext context) throws ResourceInitializationException {
    String[] folders = getFolders();
    if (folders == null || folders.length == 0) {
      throw new ResourceInitializationException("No folders set", null);
    }

    try {
      watcher = FileSystems.getDefault().newWatchService();
    } catch (IOException ioe) {
      throw new ResourceInitializationException(ioe);
    }

    registerFolders(folders);
  }

  @Override
  protected void doGetNext(JCas jCas) throws IOException, CollectionException {
    boolean documentAdded = false;
    while (!documentAdded) {
      if (!currentFile.isPresent()) {
        currentFile = getNextFile();
      }
      if (currentFile.isPresent() && currentFile.get().toFile().exists()) {
        documentAdded = processFile(jCas, currentFile.get());
      }
      currentFile = Optional.empty();
    }
  }

  @Override
  public boolean doHasNext() throws IOException, CollectionException {
    if (!currentFile.isPresent()) {
      currentFile = getNextFile();
    }
    return currentFile.isPresent();
  }

  @Override
  protected void doClose() throws IOException {
    if (watcher != null) {
      watcher.close();
      watcher = null;
    }

    watchKeys.clear();
    fileQueue.clear();
  }

  private Optional<Path> getNextFile() {
    WatchKey key;
    /*
     * process file events until we have a file in our queue, either blocking for new file events or
     * if a timeout is configured and it elapses, return nothing
     */
    while (fileQueue.isEmpty()) {
      // may block
      Optional<WatchKey> keyOpt;
      try {
        keyOpt = pollEventKey();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return Optional.empty();
      }
      if (!keyOpt.isPresent()) {
        // we hit the timeout and we have nothing
        return Optional.empty();
      }
      key = keyOpt.get();
      for (WatchEvent<?> event : key.pollEvents()) {
        // this may add to fileQueue
        processFileEvent(key, event);
        getMonitor().meter("events").mark();
      }

      key.reset();
    }

    return Optional.of(fileQueue.poll());
  }

  private Optional<WatchKey> pollEventKey() throws InterruptedException {
    int timeout = getTimeout();
    if (timeout == -1) {
      return Optional.of(watcher.take());
    } else {
      return Optional.ofNullable(watcher.poll(timeout, TimeUnit.MILLISECONDS));
    }
  }

  private void registerFolders(String[] folders) {
    for (String folder : folders) {
      try {
        Path p = Paths.get(folder);
        p = p.toRealPath();

        if (isRecursive()) {
          Files.walkFileTree(
              p,
              new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attr)
                    throws IOException {
                  registerDirectory(dir);
                  return FileVisitResult.CONTINUE;
                }
              });
        } else {
          registerDirectory(p);
        }
        addFilesFromDir(p.toFile());
      } catch (IOException ioe) {
        getMonitor()
            .warn(
                "Could not find or register folder '{}' or it's subfolders - folder will be skipped",
                folder, ioe);
      }
    }
  }

  private void registerDirectory(Path path) throws IOException {
    WatchKey key = path.register(watcher, ENTRY_CREATE, ENTRY_DELETE);
    watchKeys.put(key, path);

    getMonitor().counter("directories").inc();
  }

  private void addFilesFromDir(File dir) {
    File[] files = dir.listFiles();

    if (files == null) {
      return;
    }

    for (File file : files) {
      if (!file.isDirectory()) {
        Path path = file.toPath();
        addFile(path);
      } else if (isRecursive()) {
        addFilesFromDir(file);
      }
    }
  }

  private void addFile(Path path) {
    if (isFileIncluded(path)) {
      fileQueue.add(path);
      getMonitor().counter("files").inc();
    }
  }

  private void processFileEvent(WatchKey key, WatchEvent<?> event) {
    @SuppressWarnings("unchecked")
    WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;

    if (event.kind() == OVERFLOW) {
      getMonitor().warn("OVERFLOW event received - some files may be missing from the queue");
    } else if (event.kind() == ENTRY_DELETE) {
      getMonitor()
          .debug(
              "ENTRY_DELETE event received - file '{}' will be removed from queue",
              pathEvent.context());

      try {
        Path dir = watchKeys.get(key);
        if (dir != null) {
          Path resolved = dir.resolve(pathEvent.context());
          fileQueue.remove(resolved);
        } else {
          getMonitor()
              .warn(
                  "WatchKey not found - file '{}' will not be removed from the queue",
                  pathEvent.context());
        }
      } catch (Exception ioe) {
        getMonitor()
            .warn(
                "An error occurred - file '{}' will not be removed from the queue",
                pathEvent.context(),
                ioe);
      }

      fileQueue.remove(pathEvent.context());
    } else {
      getMonitor()
          .debug(
              event.kind().name() + " event received - file '{}' will be added to the queue",
              pathEvent.context());
      try {
        Path dir = watchKeys.get(key);
        if (dir != null) {
          Path resolved = dir.resolve(pathEvent.context());
          if (resolved.toFile().isDirectory()) {
            if (isRecursive()) {
              addFilesFromDir(resolved.toFile());
              registerDirectory(resolved);
            }
          } else {
            addFile(resolved);
          }
        } else {
          getMonitor()
              .warn(
                  "WatchKey not found - file '{}' will not be added to the queue",
                  pathEvent.context());
        }
      } catch (Exception ioe) {
        getMonitor()
            .warn(
                "An error occurred - file '{}' will not be added to the queue",
                pathEvent.context(),
                ioe);
      }
    }
  }

  protected void addMetadata(final JCas jCas, final String key, @Nullable final String value) {
    if (value == null || value.isEmpty()) {
      return;
    }

    final Metadata m = new Metadata(jCas);
    m.setKey(key);
    m.setValue(value);
    m.addToIndexes();
  }

}
