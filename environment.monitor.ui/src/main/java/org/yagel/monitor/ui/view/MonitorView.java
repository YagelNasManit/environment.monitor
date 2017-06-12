package org.yagel.monitor.ui.view;

import com.vaadin.navigator.View;

public enum MonitorView {

  ENVIRONMENTS_STATUS("envs_current_status", CurrentEnvironmentsStatusView.class),
  ENVIRONMENT_DAYLY_STATUS("env_daily_status", EnvironmentDailyStatusView.class);

  private final String viewName;
  private final Class<? extends View> viewClass;

  MonitorView(String viewName, Class<? extends View> viewClass) {
    this.viewName = viewName;
    this.viewClass = viewClass;

  }

  public String getViewName() {
    return viewName;
  }

  public Class<? extends View> getViewClass() {
    return viewClass;
  }
}
