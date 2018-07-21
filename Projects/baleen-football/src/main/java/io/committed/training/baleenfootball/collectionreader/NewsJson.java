package io.committed.training.baleenfootball.collectionreader;

import java.nio.file.Path;
import org.apache.uima.jcas.JCas;
import io.committed.training.baleenfootball.utils.AbstractConfigurableFolderReader;

public class NewsJson extends AbstractConfigurableFolderReader {

  @Override
  protected boolean isFileIncluded(Path path) {
    // The News output is currently a guid
    // eg 0aa70355-e5bd-473c-9e94-bc2130d4745c
    // but that seems an unnecessary constraint
    // so we just return true here to consider any file

    return true;
  }

  @Override
  protected boolean processFile(JCas jCas, Path file) {

    try {
      Article article = readFile(file);
      saveToJCas(jCas, article);

      return true;
    } catch (Exception e) {
      getMonitor().warn("Unable to read {}", file, e);
      return false;
    }
  }

  private Article readFile(Path file) {
    // TODO Auto-generated method stub
    return null;
  }

  private void saveToJCas(JCas jCas, Article article) {
    // TODO Auto-generated method stub

  }



}
