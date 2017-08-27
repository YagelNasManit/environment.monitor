package org.yagel.monitor.api.rest.dto;

import org.yagel.monitor.Resource;

import java.util.Set;

public class EnvironmentConfigDTO {

  private String environmentName;
  private Set<Resource> checkedResources;

  public EnvironmentConfigDTO(String environmentName, Set<Resource> checkedResources) {
    this.environmentName = environmentName;
    this.checkedResources = checkedResources;
  }

  public String getEnvironmentName() {
    return environmentName;
  }

  public void setEnvironmentName(String environmentName) {
    this.environmentName = environmentName;
  }

  public Set<Resource> getCheckedResources() {
    return checkedResources;
  }

  public void setCheckedResources(Set<Resource> checkedResources) {
    this.checkedResources = checkedResources;
  }
}
