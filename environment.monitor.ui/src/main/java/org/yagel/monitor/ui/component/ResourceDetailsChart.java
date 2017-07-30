package org.yagel.monitor.ui.component;


import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.BarChartConfig;
import com.byteowls.vaadin.chartjs.data.BarDataset;
import com.byteowls.vaadin.chartjs.data.Dataset;
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
import java.util.stream.Collectors;

public class ResourceDetailsChart {


  private final Map<String, List<ResourceStatus>> statusListGrouped;
  //List<ResourceStatus> statusList;

  public ResourceDetailsChart(Map<String, List<ResourceStatus>> statusListGrouped) {
    //this.statusList = statusList;

    this.statusListGrouped = statusListGrouped;
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
        .title()
        .display(true)
        .text("Resource status details")
        .and()

        .hover()
        .mode(InteractionMode.INDEX)
        .intersect(false)
        .animationDuration(400)
        .and()

        .scales()
        .add(Axis.X, new DefaultScale()
            .stacked(true))
        .add(Axis.Y, new DefaultScale()
            .stacked(true).display(true))
        .and()
        .done();


    ChartJs chart = new ChartJs(barConfig);
    chart.setPrimaryStyleName("monitor-chart");
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

  public ChartJs initChart2() {
    BarChartConfig config = new BarChartConfig();
    config.data()
        .labels("January", "February", "March", "April", "May", "June", "July")
        .addDataset(new BarDataset().label("Dataset 1").backgroundColor("rgba(220,220,220,0.5)"))
        .addDataset(new BarDataset().label("Dataset 2").backgroundColor("rgba(151,187,205,0.5)"))
        .addDataset(new BarDataset().label("Dataset 3").backgroundColor("rgba(151,187,145,0.5)"))
        .and()
        .options()
        .responsive(true)
        .title()
        .display(true)
        .text("Chart.js Bar Chart - Stacked")
        .and()
        .tooltips()
        .mode(InteractionMode.INDEX)
        .intersect(false)
        .and()
        .scales()
        .add(Axis.X, new DefaultScale()
            .stacked(true))
        .add(Axis.Y, new DefaultScale()
            .stacked(true))
        .and()
        .done();

    // add random data for demo
    List<String> labels = config.data().getLabels();
    for (Dataset<?, ?> ds : config.data().getDatasets()) {
      BarDataset lds = (BarDataset) ds;
      List<Double> data = new ArrayList<>();
      for (int i = 0; i < labels.size(); i++) {
        data.add((double) (Math.random() > 0.5 ? -1 : 1) * Math.round(Math.random() * 100));
      }
      lds.dataAsList(data);
    }

    ChartJs chart = new ChartJs(config);
    chart.setPrimaryStyleName("monitor-chart");
    chart.addClickListener((a, b) -> {
      BarDataset dataset = (BarDataset) config.data().getDatasets().get(a);

    });
    chart.setJsLoggingEnabled(true);

    return chart;
  }

}
