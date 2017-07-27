package org.yagel.monitor.logging;

import org.apache.log4j.AppenderSkeleton;

// todo uncomment when logging fixed
public class ThreadFileAppender /*extends AppenderSkeleton*/ {

  /*private static ThreadLocal<RollingFileAppender> threadBuffer = new ThreadLocal<>();
  private static List<RollingFileAppender> appenders = new ArrayList<>();

  *//**
   * Controls file truncatation. The default value for this variable is <code>true</code>, meaning that by default a
   * <code>FileAppender</code> will append to an existing file and not truncate it.
   * <p>
   * <p>
   * This option is meaningful only if the FileAppender opens the file.
   *//*
  protected boolean fileAppend = true;

  *//**
   * The name of the log file.
   *//*
  protected String fileName = null;

  *//**
   * Do we do bufferedIO?
   *//*
  protected boolean bufferedIO = false;

  *//**
   * Determines the size of IO buffer be. Default is 8K.
   *//*
  protected int bufferSize = 8 * 1024;
  *//**
   * The default maximum file size is 10MB.
   *//*
  protected String maxFileSize = String.valueOf(10 * 1024 * 1024);
  *//**
   * There is one backup file by default.
   *//*
  protected int maxBackupIndex = 1;
  private boolean append;

  public ThreadFileAppender() {
    super();
  }

  public ThreadFileAppender(Layout layout, String filename, boolean append) throws IOException {
    this.layout = layout;
    this.fileName = filename;
    this.append = append;
  }

  public ThreadFileAppender(Layout layout, String filename) throws IOException {
    this.layout = layout;
    this.fileName = filename;
  }

  private void initCurrentThreadAppender() {
    if (threadBuffer.get() != null)
      return;
    RollingFileAppender app;
    try {
      app = new RollingFileAppender(layout, fileName + "." + Thread.currentThread().getName(), append);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    app.setBufferSize(bufferSize);
    app.setBufferedIO(bufferedIO);
    app.setMaxFileSize(maxFileSize);
    app.setMaxBackupIndex(maxBackupIndex);
    threadBuffer.set(app);
    appenders.add(app);
  }

  @Override
  public void append(LoggingEvent event) {
    initCurrentThreadAppender();
    threadBuffer.get().append(event);
  }

  @Override
  public boolean requiresLayout() {
    return true;
  }

  @Override
  public void close() {
    for (RollingFileAppender app : appenders) {
      app.close();
    }
    threadBuffer = new ThreadLocal<>();
    appenders = new ArrayList<>();
  }

  public boolean isFileAppend() {
    return fileAppend;
  }

  public void setFileAppend(boolean fileAppend) {
    this.fileAppend = fileAppend;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFile(String fileName) {
    this.fileName = fileName.trim();
  }

  public boolean isBufferedIO() {
    return bufferedIO;
  }

  public void setBufferedIO(boolean bufferedIO) {
    this.bufferedIO = bufferedIO;
  }

  public int getBufferSize() {
    return bufferSize;
  }

  public void setBufferSize(int bufferSize) {
    this.bufferSize = bufferSize;
  }

  public boolean isAppend() {
    return append;
  }

  public void setAppend(boolean append) {
    this.append = append;
  }

  public String getMaxFileSize() {
    return maxFileSize;
  }

  public void setMaxFileSize(String maxFileSize) {
    this.maxFileSize = maxFileSize;
  }

  public int getMaxBackupIndex() {
    return maxBackupIndex;
  }

  public void setMaxBackupIndex(int maxBackupIndex) {
    this.maxBackupIndex = maxBackupIndex;
  }*/

}
