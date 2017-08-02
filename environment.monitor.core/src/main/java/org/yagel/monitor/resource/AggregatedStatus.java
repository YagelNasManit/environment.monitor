package org.yagel.monitor.resource;

public class AggregatedStatus {

  private Status status;
  private long count;

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public long getCount() {
    return count;
  }

  public void setCount(long count) {
    this.count = count;
  }
}
