package org.yagel.monitor.api.rest.dto;

import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.resource.Status;

import java.util.List;

public class EnvironmentStatusDTO {

  private String name;

  private Status overallStatus;

  private List<ResourceStatus> resourcesStatus;

  public EnvironmentStatusDTO(String name, List<ResourceStatus> resourcesStatus) {
    this.name = name;
    this.resourcesStatus = resourcesStatus;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<ResourceStatus> getResourcesStatus() {
    return resourcesStatus;
  }

  public void setResourcesStatus(List<ResourceStatus> resourcesStatus) {
    this.resourcesStatus = resourcesStatus;
  }

  public Status getOverallStatus() {
    return overallStatus;
  }

  public void setOverallStatus(Status overallStatus) {
    this.overallStatus = overallStatus;
  }
}
