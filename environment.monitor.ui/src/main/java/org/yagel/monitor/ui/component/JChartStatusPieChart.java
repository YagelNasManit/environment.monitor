package org.yagel.monitor.ui.component;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.PieChartConfig;
import com.byteowls.vaadin.chartjs.data.Dataset;
import com.byteowls.vaadin.chartjs.data.PieDataset;
import com.vaadin.ui.Component;
import org.yagel.monitor.resource.Status;

import java.util.Map;

public class JChartStatusPieChart {

  // green, red, yellow, gray
  private static String[] colors = new String[]{"#A1D77E", "#FC4E54", "#FDB45C", "#949FB1"};
  private static String[] labels = new String[]{"Online", "Offline", "Unknown", "Border Line"};

  public Component createChart(String resourceName, Map<Status, Integer> amounts) {

    // general config
    PieChartConfig config = new PieChartConfig();
    config
        .data()
        .labels(labels)
        .addDataset(new PieDataset().label("Dataset 1"))
        .and();

    // options
    config.
        options()
        .responsive(true)
        .title()
        .display(true)
        .text("Resource: " + resourceName + " Time: XX:YY")
        .and()
        .animation()
        //.animateScale(true)
        .animateRotate(true)
        .and()
        .done();


    for (Dataset<?, ?> ds : config.data().getDatasets()) {
      PieDataset lds = (PieDataset) ds;
      lds.backgroundColor(colors);

      amounts.values().forEach(lds::addData);

    }

    ChartJs chart = new ChartJs(config);
    chart.setJsLoggingEnabled(true);
    /*chart.addClickListener((a, b) -> {
      PieDataset dataset = (PieDataset) config.data().getDatasets().get(a);
    });*/

    return chart;

  }
}
