package org.yagel.monitor.resource;

import org.yagel.monitor.ResourceStatus;

import java.util.Date;
import java.util.Objects;

public class ResourceStatusImpl implements ResourceStatus {

  private String resourceId;
  private Status status;
  private Date updated = new Date();


  public ResourceStatusImpl(String resourceId, Status status) {
    this.resourceId = resourceId;
    this.status = status;
  }

  public ResourceStatusImpl(String resourceId, Status status, Date updated) {
    this(resourceId, status);
    this.updated = updated;
  }


  @Override
  public String getResourceId() {
    return resourceId;
  }

  @Override
  public void setResourceId(String resourceId) {
    this.resourceId = resourceId;
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
  public int hashCode() {
    return Objects.hash(getResourceId(), getStatus(), getUpdated());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ResourceStatusImpl)) return false;
    ResourceStatusImpl that = (ResourceStatusImpl) o;
    return Objects.equals(getResourceId(), that.getResourceId()) &&
        getStatus() == that.getStatus() &&
        Objects.equals(getUpdated(), that.getUpdated());
  }
}
