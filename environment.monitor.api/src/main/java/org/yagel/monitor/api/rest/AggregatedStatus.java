package org.yagel.monitor.api.rest;

import org.yagel.monitor.resource.Status;

public class AggregatedStatus {

  Status status;
  long count;

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
