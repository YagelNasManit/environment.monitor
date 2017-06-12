package org.yagel.monitor.ui.common;

import org.yagel.monitor.Resource;

import java.util.Set;


public abstract class AbstractMultipleResourcesWidget extends AbstractMonitorWidget {

  protected Set<Resource> resourcesToDisplay;
  protected Set<String> resourcesToDisplayId;

  protected AbstractMultipleResourcesWidget(String environmentName, Set<String> resourcesToDisplayId) {
    super(environmentName);
    this.resourcesToDisplayId = resourcesToDisplayId;
  }

  public Set<Resource> getResourcesToDisplay() {
    return resourcesToDisplay;
  }

  public void setResourcesToDisplay(Set<Resource> resourcesToDisplay) {
    this.resourcesToDisplay = resourcesToDisplay;
  }
}
