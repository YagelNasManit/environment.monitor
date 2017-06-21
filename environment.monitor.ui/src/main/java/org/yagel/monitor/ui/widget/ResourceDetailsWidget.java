package org.yagel.monitor.ui.widget;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.mongo.MongoConnector;
import org.yagel.monitor.ui.common.AbstractSingleResourceWidget;
import org.yagel.monitor.ui.component.ResourceDetailsChart;
import org.yagel.monitor.ui.component.ResourceDetailsTable;
import org.yagel.monitor.utils.DataUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

public class ResourceDetailsWidget extends AbstractSingleResourceWidget implements SelectViewRangeWidget.SelectionChangedListener {


  private HorizontalLayout widgetLayout;
  private ChartJs detailsChart;
  private Grid detailsTable;
  private LocalDateTime displayDate;

  public ResourceDetailsWidget(String environmentName, String resourceToDisplayId, LocalDateTime displayDate) {
    super(environmentName, resourceToDisplayId);
    this.setCaption("Resource Detailed Statistics");
    this.setSizeFull();
    this.displayDate = displayDate;
  }

  @Override
  public void loadWidget() {

    List<ResourceStatus> statusList = loadResourceDBStatuses(displayDate);
    detailsChart = new ResourceDetailsChart(statusList).initChart();
    detailsTable = new ResourceDetailsTable().loadTable();
    detailsChart.setWidth(100, Unit.PERCENTAGE);

    widgetLayout = new HorizontalLayout();
    widgetLayout.setSizeFull();
    widgetLayout.setSpacing(true);
    widgetLayout.setMargin(true);

    widgetLayout.addComponent(detailsChart);
    widgetLayout.addComponent(detailsTable);
    widgetLayout.setExpandRatio(detailsChart, 1f);


    this.setContent(widgetLayout);
  }

  @Override
  public void selectionChanged(LocalDateTime date, String resourceId) {
    // removing old chart
    widgetLayout.removeComponent(detailsChart);
    resourceToDisplayId = resourceId;

    //reload new statuses
    List<ResourceStatus> statusList = loadResourceDBStatuses(date);

    // init and configure new chart
    this.detailsChart = new ResourceDetailsChart(statusList).initChart();
    detailsChart.setWidth(100, Unit.PERCENTAGE);
    widgetLayout.addComponent(detailsChart);

  }

  private List<ResourceStatus> loadResourceDBStatuses(LocalDateTime forDay) {

    Date dayStart = DataUtils.asDate(forDay.toLocalDate().atTime(LocalTime.MIN));

    Date currentDate;
    if (DataUtils.isToday(forDay))
      currentDate = DataUtils.asDate(forDay);
    else
      currentDate = DataUtils.asDate(forDay.toLocalDate().atTime(LocalTime.MAX));


    return MongoConnector.getInstance().getMonthDetailDAO().getStatuses(environmentName, resourceToDisplayId, dayStart, currentDate);
  }
}
