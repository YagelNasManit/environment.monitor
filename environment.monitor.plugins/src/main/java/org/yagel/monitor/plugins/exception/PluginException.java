package org.yagel.monitor.plugins.exception;

public class PluginException extends RuntimeException {

  public PluginException(Throwable cause) {
    super(cause);
  }

  public PluginException(String message, Throwable cause) {
    super(message, cause);
  }
}
