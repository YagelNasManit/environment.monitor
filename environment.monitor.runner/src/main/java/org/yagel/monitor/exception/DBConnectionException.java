package org.yagel.monitor.exception;

public class DBConnectionException extends RuntimeException{

  public DBConnectionException(String message, Throwable cause) {
    super(message, cause);
  }
}
