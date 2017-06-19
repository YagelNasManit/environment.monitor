package org.yagel.monitor.ui.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import org.yagel.monitor.EnvironmentConfig;
import org.yagel.monitor.ScheduleRunnerImpl;
import org.yagel.monitor.ui.widget.ResourceDetailsWidget;
import org.yagel.monitor.ui.widget.SelectViewRangeWidget;

import java.time.LocalDateTime;

public class ResourceDetailsView extends VerticalLayout implements View {


  @Override
  public void enter(ViewChangeListener.ViewChangeEvent event) {
    this.setSizeFull();
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

    LocalDateTime defaultDate = LocalDateTime.now();
    // todo get rid of iterator?
    String defaultResource = config.getCheckResources().iterator().next();


    // init widgets
    SelectViewRangeWidget viewRangeWidget = new SelectViewRangeWidget(
        config.getEnvName(),
        config.getCheckResources(),
        defaultDate,
        defaultResource
    );

    ResourceDetailsWidget detailsWidget = new ResourceDetailsWidget(config.getEnvName(), defaultResource, defaultDate);
    viewRangeWidget.setSelectionChangedListeners(detailsWidget);

    // configure tab
    VerticalLayout layout = new VerticalLayout();
    layout.setSizeFull();
    layout.setSpacing(true);
    layout.setMargin(true);
    layout.setCaption(config.getEnvName());

    // add and configure widgets

    layout.addComponent(viewRangeWidget.compose());
    layout.addComponent(detailsWidget.compose());
    layout.setExpandRatio(detailsWidget, 1f);

    return layout;
  }
}
