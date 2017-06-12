package org.yagel.monitor.ui.common;

import com.vaadin.ui.Panel;


public abstract class AbstractMonitorWidget extends Panel implements MonitorWidget {

  protected String environmentName;

  protected AbstractMonitorWidget(String environmentName) {
    super();
    this.environmentName = environmentName;
  }

  public <T extends AbstractMonitorWidget> T compose() {
    loadWidget();
    return (T) this;
  }

  public String getEnvName() {
    return environmentName;
  }


}
