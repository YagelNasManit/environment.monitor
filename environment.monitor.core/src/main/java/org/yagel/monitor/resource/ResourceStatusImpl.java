package org.yagel.monitor.resource;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.yagel.monitor.Resource;
import org.yagel.monitor.ResourceStatus;

import java.util.Date;
import java.util.Objects;

public class ResourceStatusImpl implements ResourceStatus {

  private Resource resource;
  private Status status;
  private Date updated = new Date();


  public ResourceStatusImpl(Resource resource, Status status) {
    this.resource = resource;
    this.status = status;
  }

  public ResourceStatusImpl(Resource resource, Status status, Date updated) {
    this(resource, status);
    this.updated = updated;
  }


  @Override
  public Resource getResource() {
    return resource;
  }

  @Override
  public void setResource(Resource resource) {
    this.resource = resource;
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
    return Objects.hash(getResource(), getStatus(), getUpdated());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ResourceStatusImpl)) return false;
    ResourceStatusImpl that = (ResourceStatusImpl) o;
    return Objects.equals(getResource(), that.getResource()) &&
        getStatus() == that.getStatus() &&
        Objects.equals(getUpdated(), that.getUpdated());
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("resource", resource)
        .append("status", status)
        .append("updated", updated)
        .toString();
  }
}
