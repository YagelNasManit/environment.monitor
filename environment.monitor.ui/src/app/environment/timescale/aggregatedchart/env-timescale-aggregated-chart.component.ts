import {Component, ElementRef, Input, ViewChild} from "@angular/core";

import * as d3 from "d3";
import {AggregatedResourceStatus} from "../../../shared/model/AggregatedResourceStatus";

@Component({
  selector: "environment-timescale-aggregated-chart",
  templateUrl: "./env-timescale-aggregated-chart.component.html"
})
export class EnvironmentTimescaleAggregatedChartComponent {

  @ViewChild("chartContainer") elementContainer: ElementRef;


  private charts: any;
  private chart_r: any;
  private chart_m: any;
  private color: any;


  data: AggregatedResourceStatus[];


  @Input()
  set aggregatedStatus(aggregatedStatus: AggregatedResourceStatus) {
    // todo remove array in furutre as here is only single chart now
    this.data = [aggregatedStatus];
    if (this.charts == null)
      this.create(this.data);
    else
      this.update(this.data);

  }

  private create(dataset) {
    this.charts = d3.select(this.elementContainer.nativeElement);

    console.log(this.elementContainer.nativeElement.offsetWidth);
    this.chart_m = this.elementContainer.nativeElement.offsetWidth / dataset.length / 2 * 0.14;
    this.chart_r = this.elementContainer.nativeElement.offsetWidth / dataset.length / 2 * 0.85;

    this.color = d3.scaleOrdinal()
      .domain(["Online", "Unavailable", "Unknown", "BorderLine"])
      .range(["#00a65a", "#dd4b39", "#444444", "#f39c12"]);

    console.log("chart m ->  " + this.chart_m);
    console.log("chart r -> " + this.chart_r);


    this.charts.selectAll('.donut')
      .data(dataset)
      .enter().append('svg:svg')
      .attr('width', (this.chart_r + this.chart_m) * 2)
      .attr('height', (this.chart_r + this.chart_m) * 2)
      .append('svg:g')
      .attr('class', (d, i) => `donut type${i}`)
      .attr('transform', 'translate(' + (this.chart_r + this.chart_m) + ',' + (this.chart_r + this.chart_m) + ')');


    this.createCenter();

    this.updateDonuts();
  }

  private createCenter() {

    let donuts = this.charts.selectAll('.donut');

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
    // TODO consider adding units for future;
      .text((d) => (sum) ? sum.toFixed(0) /*+ d.unit*/ : d.count.toFixed(0) /*+ d.unit*/);
    thisDonut.select('.percentage')
      .text((d) => {
        return (sum) ? (sum / d.count * 100).toFixed(2) + '%'
          : '';
      });
  }

  private resetAllCenterText() {
    console.log(this.charts.selectAll('.donut').select('.value'));

    this.charts.selectAll('.donut').select('.value')
      .text((d) => d.count.toFixed(1)/*+ d.unit*/);
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
      .style('fill', (d, i) => this.color(d.data.status))
      .style('stroke', '#FFFFFF')
      .on('click', this.donutMouseClick)
      .on('mouseover', this.donutMouseOver)
      .on('mouseout', this.donutMouseOut);


    paths.exit().remove();

    this.resetAllCenterText();

  }

  update(dataset) {
    // Assume no new categ of data enter
    let donut = this.charts.selectAll(".donut")
      .data(dataset);

    this.updateDonuts();
  }

  // event handlers
  private donutMouseOver = (d, i, j) => {
    this.pathAnim(d3.select(j[i]), 1);

    let thisDonut = d3.select(j[i].parentNode);

    console.log(thisDonut.select('.value'));

    thisDonut.select('.value').text(function (donut_d) {
      console.log(d.data.count);
      return d.data.count.toFixed(1) /*+ donut_d.unit*/;
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
}
