package org.yagel.monitor.api.rest;

import org.yagel.monitor.Resource;

import java.util.List;

public class AggregatedResourceStatus {

  Resource resource;
  List<AggregatedStatus> resourceStatuses;

  public Resource getResource() {
    return resource;
  }

  public void setResource(Resource resource) {
    this.resource = resource;
  }

  public List<AggregatedStatus> getResourceStatuses() {
    return resourceStatuses;
  }

  public void setResourceStatuses(List<AggregatedStatus> resourceStatuses) {
    this.resourceStatuses = resourceStatuses;
  }
}
