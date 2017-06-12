package org.yagel.monitor;

import org.yagel.monitor.resource.Status;

import java.util.Date;

public interface ResourceStatus {

  String getResourceId();

  void setResourceId(String resourceId);

  Status getStatus();

  void setStatus(Status status);

  Date getUpdated();

  void setUpdated(Date updated);
}
