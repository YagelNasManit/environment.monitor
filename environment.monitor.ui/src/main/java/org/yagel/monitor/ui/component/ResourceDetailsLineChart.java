package org.yagel.monitor.ui.component;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.LineChartConfig;
import com.byteowls.vaadin.chartjs.data.LineDataset;
import com.byteowls.vaadin.chartjs.options.InteractionMode;
import com.byteowls.vaadin.chartjs.options.scale.Axis;
import com.byteowls.vaadin.chartjs.options.scale.CategoryScale;
import com.byteowls.vaadin.chartjs.options.scale.LinearScale;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.resource.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResourceDetailsLineChart {

  private final Map<String, List<ResourceStatus>> statusListGrouped;

  public ResourceDetailsLineChart(Map<String, List<ResourceStatus>> statusListGrouped) {
    //this.statusList = statusList;

    this.statusListGrouped = statusListGrouped;
  }


  public ChartJs initChart() {

    LineDataset availableDataSet = new LineDataset().backgroundColor("rgba(161,215,126,0.5)").label("Available");
    LineDataset unavailableDataSet = new LineDataset().backgroundColor("rgba(248,63,76,0.5)").label("Unavailable");
    LineDataset unknownDataSet = new LineDataset().backgroundColor("rgba(235,236,235,0.5)").label("Unknown");

    List<Double> availableCount = calculateStatusAmount(Status.Online);
    List<Double> unavailableCount = calculateStatusAmount(Status.Unavailable);
    List<Double> unknownCount = calculateStatusAmount(Status.Unknown);

    availableDataSet.dataAsList(availableCount);
    unavailableDataSet.dataAsList(unavailableCount);
    unknownDataSet.dataAsList(unknownCount);

    LineChartConfig lineConfig = new LineChartConfig();

    final List<String> labelsList = new ArrayList<>(statusListGrouped.keySet());

    lineConfig.
        data()
        .labelsAsList(labelsList)
        .addDataset(availableDataSet)
        .addDataset(unavailableDataSet)
        .addDataset(unknownDataSet)
        .and();

    lineConfig.
        options()
        .responsive(true)

        .hover()
        .mode(InteractionMode.INDEX)
          /*.intersect(false)*/
        .animationDuration(400)
        .and()

        .title()
        .display(true)
        .text("Resource status details")
        .and()

        .scales()
        .add(Axis.X, new CategoryScale()
            .scaleLabel()
            .display(true)
            .labelString("Hour")
            .and())
        .add(Axis.Y, new LinearScale()
            .stacked(true)
            .scaleLabel()
            .display(true)
            .labelString("Checks")
            .and())
        .and()

        .done();


    ChartJs chart = new ChartJs(lineConfig);
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

}
