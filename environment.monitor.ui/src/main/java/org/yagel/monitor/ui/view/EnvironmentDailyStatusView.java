package org.yagel.monitor.ui.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import org.yagel.monitor.EnvironmentConfig;
import org.yagel.monitor.ScheduleRunnerImpl;
import org.yagel.monitor.ui.widget.EnvironmentDailyStatsWidget;
import org.yagel.monitor.ui.widget.EnvironmentStatusWidget;

public class EnvironmentDailyStatusView extends HorizontalLayout implements View {

  public EnvironmentDailyStatusView() {
    this.setSizeFull();
    this.setSpacing(true);
    this.setMargin(true);
  }

  @Override
  public void enter(ViewChangeListener.ViewChangeEvent event) {
    buildContent();
  }

  private void buildContent() {
    TabSheet tabsheet = new TabSheet();
    tabsheet.setSizeFull();

    for (EnvironmentConfig config : ScheduleRunnerImpl.getInstance().getConfig().getEnvironments()) {
      tabsheet.addTab(buildEnvTab(config));
    }

    this.addComponent(tabsheet);
  }


  private Component buildEnvTab(EnvironmentConfig config) {

    // init widgets
    EnvironmentStatusWidget statusWidget = new EnvironmentStatusWidget(config.getEnvName(), config.getCheckResources(), null);
    EnvironmentDailyStatsWidget dailyStatsWidget = new EnvironmentDailyStatsWidget(config.getEnvName(), config.getCheckResources());

    // configure tab
    HorizontalLayout horizontalLayout = new HorizontalLayout();
    horizontalLayout.setSizeFull();
    horizontalLayout.setSpacing(true);
    horizontalLayout.setMargin(true);
    horizontalLayout.setCaption(config.getEnvName());

    // add and configure widgets
    horizontalLayout.addComponent(statusWidget.compose());
    horizontalLayout.addComponent(dailyStatsWidget.compose());

    horizontalLayout.setExpandRatio(statusWidget, 1);
    horizontalLayout.setExpandRatio(dailyStatsWidget, 4);

    return horizontalLayout;
  }
}
