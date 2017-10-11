package org.yagel.monitor.resource;

import org.yagel.monitor.StatusUpdate;

import java.util.Date;

public class StatusUpdateImpl implements StatusUpdate {

  private Status status;
  private Date updated = new Date();
  private String statusDetails;

  public StatusUpdateImpl(Status status, Date updated) {
    this.status = status;
    this.updated = updated;
  }

  public StatusUpdateImpl(Status status, Date updated,String statusDetails) {
    this.status = status;
    this.updated = updated;
    this.statusDetails = statusDetails;
  }

  @Override
  public Status getStatus() {
    return status;
  }

  @Override
  public void setStatus(Status status) {
    this.status = status;
  }

  @Override
  public Date getUpdated() {
    return updated;
  }

  @Override
  public void setUpdated(Date updated) {
    this.updated = updated;
  }

  @Override
  public String getStatusDetails() {
    return this.statusDetails;
  }

  @Override
  public void setStatusDetails(String details) {
    this.statusDetails = details;
  }
}
