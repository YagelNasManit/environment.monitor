package org.yagel.monitor;

public interface ResourceStatus extends StatusUpdate {

  Resource getResource();

  void setResource(Resource resource);

}
