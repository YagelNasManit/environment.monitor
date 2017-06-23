package org.yagel.monitor.ui.widget;

import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import org.yagel.monitor.Resource;
import org.yagel.monitor.mongo.MongoConnector;
import org.yagel.monitor.resource.Status;
import org.yagel.monitor.ui.common.AbstractMultipleResourcesWidget;
import org.yagel.monitor.ui.component.JChartStatusPieChart;
import org.yagel.monitor.utils.DataUtils;

import java.util.Date;
import java.util.Map;
import java.util.Set;

public class EnvironmentDailyStatsWidget extends AbstractMultipleResourcesWidget {

  private static final String WIDGET_TITLE = "Environment <strong> %s</strong>";
  private CssLayout widgetLayout;

  public EnvironmentDailyStatsWidget(String environmentName, Set<String> resourcesToDisplayId) {
    super(environmentName, resourcesToDisplayId);
    setSizeFull();
    this.addStyleName("current-status-panel");
  }

  @Override
  public void loadWidget() {

    this.widgetLayout = new CssLayout();
    initWidgetHeader();
    initWidgetBody();
    setContent(widgetLayout);

  }

  private void initWidgetBody() {
    initCharts();
  }

  private void initWidgetHeader() {
    this.setCaptionAsHtml(true);
    this.setCaption(String.format(WIDGET_TITLE, environmentName));
  }


  private void initCharts() {
    Date toDate = new Date();
    Date fromDate = DataUtils.getYesterday(toDate);

    Map<String, Map<Status, Integer>> statusMap = MongoConnector.getInstance().getMonthDetailDAO().getAggregatedStatuses(environmentName, fromDate, toDate);

    for (Map.Entry<String, Map<Status, Integer>> status : statusMap.entrySet()) {
      widgetLayout.addComponent(initChart(status.getKey(), status.getValue()));
    }


  }

  private Component initChart(String resourceId, Map<Status, Integer> statuses) {
    Resource resource = MongoConnector.getInstance().getResourceDAO().find(resourceId);
    return new JChartStatusPieChart().createChart(resource.getName(), statuses);
  }


}
