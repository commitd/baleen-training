package io.committed.training.baleenfootball.collectionreader;

import java.nio.file.Path;
import org.apache.uima.jcas.JCas;
import io.committed.training.baleenfootball.utils.AbstractConfigurableFolderReader;

public class NewsJson extends AbstractConfigurableFolderReader {

  @Override
  protected boolean isFileIncluded(Path path) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  protected boolean processFile(JCas jCas, Path file) {
    // TODO Auto-generated method stub
    return false;
  }

}
