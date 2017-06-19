package org.yagel.monitor.ui.component;


import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.BarChartConfig;
import com.byteowls.vaadin.chartjs.data.BarDataset;
import com.byteowls.vaadin.chartjs.options.InteractionMode;
import com.byteowls.vaadin.chartjs.options.scale.Axis;
import com.byteowls.vaadin.chartjs.options.scale.DefaultScale;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.resource.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ResourceDetailsChart {


  private final Map<String, List<ResourceStatus>> statusListGrouped;
  List<ResourceStatus> statusList;

  public ResourceDetailsChart(List<ResourceStatus> statusList) {
    this.statusList = statusList;

    statusListGrouped = groupStatuses(statusList);
  }


  public ChartJs initChart() {


    BarDataset availableDataSet = new BarDataset().backgroundColor("rgba(161,215,126,0.5)").label("Available");
    BarDataset unavailableDataSet = new BarDataset().backgroundColor("rgba(248,63,76,0.5)").label("Unavailable");
    BarDataset unknownDataSet = new BarDataset().backgroundColor("rgba(235,236,235,0.5)").label("Unknown");

    List<Double> availableCount = calculateStatusAmount(Status.Online);
    List<Double> unavailableCount = calculateStatusAmount(Status.Unavailable);
    List<Double> unknownCount = calculateStatusAmount(Status.Unknown);

    availableDataSet.dataAsList(availableCount);
    unavailableDataSet.dataAsList(unavailableCount);
    unknownDataSet.dataAsList(unknownCount);

    BarChartConfig barConfig = new BarChartConfig();

    final List<String> labelsList = new ArrayList<>(statusListGrouped.keySet());

    barConfig.
        data()
        .labelsAsList(labelsList)
        .addDataset(availableDataSet)
        .addDataset(unavailableDataSet)
        .addDataset(unknownDataSet)
        .and();

    barConfig.
        options()
        .responsive(true)
        .hover()
        .mode(InteractionMode.INDEX)
        .intersect(false)
        .animationDuration(400)
        .and()
        .title()
        .display(true)
        .text("Resource status details")
        .and()
        .scales()
        .add(Axis.X, new DefaultScale()
            .stacked(true))
        .add(Axis.Y, new DefaultScale()
            .stacked(true).display(false))
        .and()
        .done();


    ChartJs chart = new ChartJs(barConfig);
    chart.setJsLoggingEnabled(true);
    chart.addClickListener((statusIndex, hourIndex) -> {
      Notification.show("A:" + statusIndex + "B:" + hourIndex);
      UI.getCurrent().addWindow(new LogWindow(statusListGrouped.get(labelsList.get(hourIndex))));
    });


    return chart;
  }

  private List<Double> calculateStatusAmount(Status status) {
    return statusListGrouped.values()
        .stream()
        .map(resourceStatuses ->
            resourceStatuses
                .stream()
                .filter(resourceStatus -> resourceStatus.getStatus() == status)
                .count()
        )
        .map(Long::doubleValue)
        .collect(Collectors.toList());
  }

  private TreeMap<String, List<ResourceStatus>> groupStatuses(List<ResourceStatus> statusList) {
    return statusList.stream().collect(Collectors.groupingBy(
        status -> status.getUpdated().getHours() + ":00",
        TreeMap::new,
        Collectors.toList()
    ));
  }
}
