package org.yagel.monitor.ui.component;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.vaadin.addon.JFreeChartWrapper;
import org.yagel.monitor.resource.Status;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.text.NumberFormat;
import java.util.Map;

@Deprecated
public class StatusPieChart {

  public StatusPieChart() {
  }

  public JFreeChartWrapper createChart(String resourceName, Map<Status, Integer> amounts) {

    PieDataset dataset = createDataset(amounts);

    JFreeChart chart = buildChart("Resource Status: " + resourceName, dataset);
    return new JFreeChartWrapper(chart);

  }

  private PieDataset createDataset(Map<Status, Integer> amounts) {
    DefaultPieDataset dataset = new DefaultPieDataset();
    dataset.setValue("Online", amounts.get(Status.Online));
    dataset.setValue("Offline", amounts.get(Status.Unavailable));
    dataset.setValue("Unknown", amounts.get(Status.Unknown));
    dataset.setValue("Border Line", amounts.get(Status.BorderLine));
    return dataset;
  }

  private JFreeChart buildChart(String chartName, PieDataset dataset) {
    JFreeChart chart = ChartFactory.createPieChart(
        chartName,
        dataset,
        true,
        true,
        false
    );

    PiePlot plot = (PiePlot) chart.getPlot();

    // set section properties
    plot.setSectionPaint("Online", new Color(166, 215, 133));
    plot.setSectionPaint("Offline", new Color(255, 92, 92));
    plot.setSectionPaint("Unknown", new Color(255, 165, 0));
    plot.setSectionPaint("Border Line", new Color(209, 190, 46));
    plot.setSectionOutlinesVisible(true);
    plot.setBaseSectionOutlinePaint(Color.WHITE);
    plot.setBaseSectionOutlineStroke(new BasicStroke(2, 0, 0));


    // set label properties
    plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
    plot.setLabelGap(0.0);
    plot.setLabelOutlinePaint(Color.WHITE);
    plot.setLabelOutlinePaint(Color.WHITE);
    plot.setLabelBackgroundPaint(Color.WHITE);
    plot.setLabelOutlinePaint(Color.WHITE);
    plot.setLabelOutlineStroke(new BasicStroke(0, 0, 0));
    plot.setLabelShadowPaint(Color.WHITE);
    plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
        "{0} ({2})", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()
    ));


    plot.setNoDataMessage("No data available");
    plot.setCircular(false);
    plot.setBackgroundPaint(Color.WHITE);

    plot.setOutlineStroke(new BasicStroke(0, 0, 0));
    plot.setShadowXOffset(0);
    plot.setShadowYOffset(0);

    return chart;
  }
}
