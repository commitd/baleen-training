package io.committed.training.baleenfootball.utils;

import org.apache.uima.fit.descriptor.ConfigurationParameter;

public abstract class AbstractConfigurableFolderReader extends AbstractFolderReader {
  /**
   * Root folder to start from
   *
   * @baleen.config <i>Current directory</i>
   */
  public static final String PARAM_FOLDER = "folder";
  @ConfigurationParameter(name = PARAM_FOLDER, defaultValue = ".")
  private String rootFolder;

  /**
   * Should folders be processed recursively (i.e. should we watch subfolders too)?
   *
   * @baleen.config true
   */
  public static final String PARAM_RECURSIVE = "recursive";

  @ConfigurationParameter(name = PARAM_RECURSIVE, defaultValue = "true")
  private boolean recursive;

  /**
   * The length of time to wait for a new file candidate, in milliseconds. Default is to wait
   * forever (-1)
   *
   * @baleen.config true
   */
  public static final String PARAM_TIMEOUT = "timeout";

  @ConfigurationParameter(name = PARAM_TIMEOUT, defaultValue = "-1")
  private int timeout;

  @Override
  protected String[] getFolders() {
    return new String[] {rootFolder};
  }

  @Override
  protected boolean isRecursive() {
    return recursive;
  }

  @Override
  protected int getTimeout() {
    return timeout;
  }
}
