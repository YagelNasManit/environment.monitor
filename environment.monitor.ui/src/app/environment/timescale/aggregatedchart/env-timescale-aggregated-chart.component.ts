import {Component, ElementRef, Input, ViewChild} from "@angular/core";

import * as d3 from "d3";
import {EnvironmentStatusService} from "../../../shared/service/environment-status.service";
import {StatusTimeRange} from "../../../shared/model/StatusTimeRange";
import {AggregatedResourceStatus} from "../../../shared/model/AggregatedResourceStatus";

@Component({
  selector: "environment-timescale-aggregated-chart",
  templateUrl: "./env-timescale-aggregated-chart.component.html"
})
export class EnvironmentTimescaleAggregatedChartComponent {
  @ViewChild("containerPieChart") element: ElementRef;

  private charts: any;
  private chart_r: any;
  private chart_m: any;
  private color: any;


  data: AggregatedResourceStatus[];

  constructor(private dataService: EnvironmentStatusService) {
  }


  @Input()
  set statusTimerange(statusTimerange: StatusTimeRange) {
    this.dataService.getAggregatedResourceStatuses(statusTimerange.environment, statusTimerange.daterange.start, statusTimerange.daterange.end).subscribe(data => {
      this.data = data;
      this.create(data);
    });
  }

  private create(dataset) {
    this.charts = d3.select(this.element.nativeElement);

    this.chart_m = this.element.nativeElement.offsetWidth / dataset.length / 2 * 0.14;
    this.chart_r = this.element.nativeElement.offsetWidth / dataset.length / 2 * 0.85;

    this.color = d3.scaleOrdinal()
      .range(["#00a65a", "#dd4b39", "#444444", "#f39c12"]);

    console.log("chart m ->  " + this.chart_m);
    console.log("chart r -> " + this.chart_r);

// todo hardcoded
    this.createLegend(["Online", "Unavailable", "Unknown", "BorderLine"]);

    let donut = this.charts.selectAll('.donut')
      .data(dataset)
      .enter().append('svg:svg')
      .attr('width', (this.chart_r + this.chart_m) * 2)
      .attr('height', (this.chart_r + this.chart_m) * 2)
      .append('svg:g')
      .attr('class', (d, i) => 'donut type' + i)
      .attr('transform', 'translate(' + (this.chart_r + this.chart_m) + ',' + (this.chart_r + this.chart_m) + ')');


    this.createCenter();

    this.updateDonuts();
  }

  private createLegend(catNames) {

    this.charts.append('svg')
      .attr('class', 'legend')
      .attr('width', '100%')
      .attr('height', 50)
      .attr('transform', 'translate(0, 25)');

    let legends = this.charts.select('.legend')
      .selectAll('g')
      .data(catNames)
      .enter().append('g')
      .attr('transform', (d, i) => 'translate(' + (i * 150 + 50) + ', 0)');

    legends.append('circle')
      .attr('class', 'legend-icon')
      .attr('r', 6)
      .style('fill', (d, i) => this.color(i));

    legends.append('text')
      .attr('dx', '1em')
      .attr('dy', '.3em')
      .text((d) => d);
  }

  private createCenter() {

    let donuts = d3.selectAll('.donut');

    // The circle displaying total data.
    donuts.append("svg:circle")
      .attr("r", this.chart_r * 0.6)
      .style("fill", "#E7E7E7");

    donuts.append('text')
      .attr('class', 'center-txt type')
      .attr('y', this.chart_r * -0.16)
      .attr('text-anchor', 'middle')
      .style('font-weight', 'bold')
      .text((d, i) => d.resource.name);
    donuts.append('text')
      .attr('class', 'center-txt value')
      .attr('text-anchor', 'middle');
    donuts.append('text')
      .attr('class', 'center-txt percentage')
      .attr('y', this.chart_r * 0.16)
      .attr('text-anchor', 'middle')
      .style('fill', '#A2A2A2');
  }

  private pathAnim(path, dir) {
    switch (dir) {
      case 0:
        path.transition()
          .duration(500)
          .ease(d3.easeCubic)
          .attr('d', d3.arc()
            .innerRadius(this.chart_r * 0.7)
            .outerRadius(this.chart_r)
          );
        break;

      case 1:
        path.transition()
          .duration(500)
          .attr('d', d3.arc()
            .innerRadius(this.chart_r * 0.7)
            .outerRadius(this.chart_r * 1.08)
          );
        break;
    }
  }

  private setCenterText(thisDonut) {
    let sum = d3.sum(thisDonut.selectAll('.clicked').data(), (d) => d.resourceStatuses.count);

    thisDonut.select('.value')
    // TODO units;
      .text((d) => (sum) ? sum.toFixed(1) /*+ d.unit*/ : d.count.toFixed(1) /*+ d.unit*/);
    // todo calc totals?
    thisDonut.select('.percentage')
      .text((d) => {
        return (sum) ? (sum / d.count * 100).toFixed(2) + '%'
          : '';
      });
  }

  private resetAllCenterText() {
    this.charts.selectAll('.value')
      .text((d) => {
        return d.count.toFixed(1) + d.unit;
      });
    this.charts.selectAll('.percentage')
      .text('');
  }


  private updateDonuts() {

    let pie = d3.pie()
      .sort(null)
      .value((d) => d.count);

    let arc = d3.arc<d3.Arc<number>>()
      .outerRadius(this.chart_r * 0.7)
      .innerRadius(this.chart_r);

    // Start joining data with paths
    let paths = this.charts.selectAll('.donut')
      .selectAll('path')
      .data((d, i) => pie(d.resourceStatuses));


    paths
      .transition()
      .duration(1000)
      .attr('d', arc);

    paths
      .enter()
      .append('svg:path')
      .attr('d', arc)
      .style('fill', (d, i) => this.color(i))
      .style('stroke', '#FFFFFF')
      .on('click', this.donutMouseClick)
      .on('mouseover', this.donutMouseOver)
      .on('mouseout', this.donutMouseOut);


    paths.exit().remove();

    this.resetAllCenterText();

  }


  // event handlers
  private donutMouseOver = (d, i, j) => {
    this.pathAnim(d3.select(j[i]), 1);

    let thisDonut = d3.select(j[i].parentNode);

    thisDonut.select('.value').text(function (donut_d) {
      return d.data.count.toFixed(1) + donut_d.unit;
    });

    thisDonut.select('.percentage').text(function (donut_d) {
      return (d.data.count / donut_d.count * 100).toFixed(2) + '%';
    });
  };

  private donutMouseOut = (d, i, j) => {
    let thisPath = d3.select(j[i]);
    if (!thisPath.classed('clicked')) {
      this.pathAnim(thisPath, 0);
    }
    let thisDonut = d3.select(j[i].parentNode);
    this.setCenterText(thisDonut);
  };

  private donutMouseClick = (d, i, j) => {
    let thisDonut = d3.select(j[i].parentNode);
    console.log(j[i]);
    console.log(d3.select(j[i].parentNode));
    console.log(thisDonut.selectAll('.clicked')[0].length);

    if (0 === thisDonut.selectAll('.clicked')[0].length) {
      thisDonut.select('circle').on('click')();
    }

    let thisPath = d3.select(j[i]);
    let clicked = thisPath.classed('clicked');
    this.pathAnim(thisPath, ~~(!clicked));
    thisPath.classed('clicked', !clicked);

    this.setCenterText(thisDonut);

  };


  onDateRangeChange(startDate: Date, endDate: Date) {
    console.log(startDate + " " + endDate)
  }
}
