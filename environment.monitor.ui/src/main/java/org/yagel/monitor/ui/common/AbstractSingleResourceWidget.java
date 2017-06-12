package org.yagel.monitor.ui.common;


import org.yagel.monitor.Resource;


public abstract class AbstractSingleResourceWidget extends AbstractMonitorWidget {

  protected String resourceToDisplayId;
  protected Resource resourceToDisplay;

  protected AbstractSingleResourceWidget(String environmentName, String resourceToDisplayId) {
    super(environmentName);
    this.resourceToDisplayId = resourceToDisplayId;
  }


  public Resource getResourceToDisplay() {
    return resourceToDisplay;
  }

  public void setResourceToDisplay(Resource resourceToDisplay) {
    this.resourceToDisplay = resourceToDisplay;
  }
}
