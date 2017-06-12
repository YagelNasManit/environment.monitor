package org.yagel.monitor.ui.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Responsive;
import com.vaadin.ui.CssLayout;
import org.yagel.monitor.EnvironmentConfig;
import org.yagel.monitor.ScheduleRunnerImpl;
import org.yagel.monitor.ui.widget.EnvironmentStatusWidget;


public class CurrentEnvironmentsStatusView extends CssLayout implements View {

  public CurrentEnvironmentsStatusView() {
    this.setWidth("100%");
    this.addStyleName("current-status-panel");
    Responsive.makeResponsive(this);

  }

  @Override
  public void enter(ViewChangeListener.ViewChangeEvent event) {
    this.buildContent();
  }

  private void buildContent() {

    for (EnvironmentConfig conf : ScheduleRunnerImpl.getInstance().getConfig().getEnvironments()) {
      EnvironmentStatusWidget widget = new EnvironmentStatusWidget(conf.getEnvName(), conf.getCheckResources(), null);
      addComponent(widget.compose());
      ScheduleRunnerImpl.getInstance().addListener(widget);
    }

  }
}
