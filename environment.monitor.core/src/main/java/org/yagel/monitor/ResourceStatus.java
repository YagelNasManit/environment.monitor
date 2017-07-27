package org.yagel.monitor;

import org.yagel.monitor.resource.Status;

import java.util.Date;

public interface ResourceStatus {

  Resource getResource();

  void setResource(Resource resource);

  Status getStatus();

  void setStatus(Status status);

  Date getUpdated();

  void setUpdated(Date updated);
}
