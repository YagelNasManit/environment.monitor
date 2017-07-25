package org.yagel.monitor.ui.widget;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.mongo.MongoConnector;
import org.yagel.monitor.ui.common.AbstractSingleResourceWidget;
import org.yagel.monitor.ui.component.ResourceDetailsChart;
import org.yagel.monitor.ui.component.ResourceDetailsLineChart;
import org.yagel.monitor.ui.component.ResourceDetailsTable;
import org.yagel.monitor.utils.DataUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ResourceDetailsWidget extends AbstractSingleResourceWidget implements SelectViewRangeWidget.SelectionChangedListener {


  private VerticalLayout widgetLayout;
  private ChartJs detailsChart;
  private Grid detailsTable;
  private LocalDateTime displayDate;
  private ChartJs resourceDetailsLineChart;

  public ResourceDetailsWidget(String environmentName, String resourceToDisplayId, LocalDateTime displayDate) {
    super(environmentName, resourceToDisplayId);
    this.setCaption("Resource Detailed Statistics");
    this.setSizeFull();
    this.displayDate = displayDate;
  }

  @Override
  public void loadWidget() {

    /*List<ResourceStatus> statusList = loadResourceDBStatuses(displayDate);
    Map<String, List<ResourceStatus>> statusListGroupped = this.groupStatuses(statusList);
    detailsChart = new ResourceDetailsChart(statusListGroupped).initChart();
    detailsTable = new ResourceDetailsTable(statusListGroupped).loadTable();
    detailsChart.setWidth(100, Unit.PERCENTAGE);

    this.resourceDetailsLineChart = new ResourceDetailsLineChart(statusListGroupped).initChart();
    resourceDetailsLineChart.setWidth(100,Unit.PERCENTAGE);*/

    widgetLayout = new VerticalLayout();
    widgetLayout.setSizeFull();
    widgetLayout.setSpacing(true);
    widgetLayout.setMargin(true);

    selectionChanged(displayDate, resourceToDisplayId);
/*

    widgetLayout.addComponent(detailsTable);

    HorizontalLayout resourceDetails = new HorizontalLayout();
    resourceDetails.setSizeFull();
    resourceDetails.addComponent(detailsChart);
    resourceDetails.addComponent(resourceDetailsLineChart);
    resourceDetails.setComponentAlignment(resourceDetailsLineChart, Alignment.TOP_CENTER);
    resourceDetails.setComponentAlignment(detailsChart, Alignment.TOP_CENTER);

    widgetLayout.addComponent(resourceDetails);*/
    //widgetLayout.setExpandRatio(detailsChart, 1f);


    this.setContent(widgetLayout);
  }

  @Override
  public void selectionChanged(LocalDateTime date, String resourceId) {
    widgetLayout.removeAllComponents();
    resourceToDisplayId = resourceId;

    //reload new statuses
    List<ResourceStatus> statusList = loadResourceDBStatuses(date);
    Map<String, List<ResourceStatus>> grouppedStatuses = groupStatuses(statusList);

    // configure components
    this.detailsChart = new ResourceDetailsChart(grouppedStatuses).initChart();

    this.detailsTable = new ResourceDetailsTable(grouppedStatuses).loadTable();
    this.detailsChart.setWidth(100, Unit.PERCENTAGE);

    this.resourceDetailsLineChart = new ResourceDetailsLineChart(grouppedStatuses).initChart();
    resourceDetailsLineChart.setWidth(100, Unit.PERCENTAGE);

    HorizontalLayout resourceChartsLayout = new HorizontalLayout();
    resourceChartsLayout.setSizeFull();
    resourceChartsLayout.addComponent(detailsChart);
    resourceChartsLayout.addComponent(resourceDetailsLineChart);
    resourceChartsLayout.setComponentAlignment(resourceDetailsLineChart, Alignment.TOP_CENTER);
    resourceChartsLayout.setComponentAlignment(detailsChart, Alignment.TOP_CENTER);

    widgetLayout.addComponent(resourceChartsLayout);

    widgetLayout.addComponent(detailsTable);
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

  private TreeMap<String, List<ResourceStatus>> groupStatuses(List<ResourceStatus> statusList) {
    return statusList.stream().collect(Collectors.groupingBy(
        status -> status.getUpdated().getHours() + ":00",
        TreeMap::new,
        Collectors.toList()
    ));
  }
}
