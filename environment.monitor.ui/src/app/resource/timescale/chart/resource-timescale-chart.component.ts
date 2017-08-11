import {Component, ElementRef, Input, ViewChild} from "@angular/core";
import * as d3 from "d3";
import {EnvironmentStatusService} from "../../../shared/service/environment-status.service";
import {StatusTimeRange} from "../../../shared/model/StatusTimeRange";
import {ResourceStatus} from "../../../shared/model/ResourceStatus";

@Component({
  selector: "resource-timescale-chart",
  templateUrl: "./resource-timescale-chart.component.html"
})
export class ResourceTimescaleChartComponent {
  @ViewChild("containerBarChart") element: ElementRef;

  statuses: ResourceStatus[];

  /** charts margin */
  private margin: any;

  /** main chart width */
  private width: number;

  /** main chart height */
  private height: number;

  /** overview chart margin*/
  private marginOverview: any;

  /** overview chart height*/
  private heightOverview: number;

  /** X scale of main chart */
  private x: any;

  /** Y scale of main chart */
  private y: any;

  /** x scale of overview chart */
  private xOverview: any;

  /** y scale of overview chart */
  private yOverview: any;

  /** bottom data axis for main chart */
  private xAxis: any;

  /** bottom data axis for overview chart */
  private xAxisOverview: any;

  /** main chart selection*/
  private main: any;

  /** overview chart selection*/
  private overview: any;
  private brush: any;
  private brushMain: any;

  /** main chart container */
  private svg: any;


  private bars: any;
  private barsOverview: any;



  constructor(public statusService: EnvironmentStatusService) {
  }


  @Input()
  set statusTimerange(statusTimerange: StatusTimeRange) {
    this.statusService.getResourceStatuses(
      statusTimerange.environment.environmentName,
      statusTimerange.resource.id,
      statusTimerange.daterange.start,
      statusTimerange.daterange.end
    )
      .subscribe(statuses => {
        this.statuses = statuses;
        if (this.main) {
          console.log("update chart");
          this.updateChart(statuses);
        }
        else {
          console.log("draw chart");
          this.buildChart(statuses);
        }
      });
  }


  private buildChart(data: ResourceStatus[]) {
    this.configureSize();

    // scales for the x and y axes
    this.x = d3.scaleTime()
      .range([0, this.width]);

    // scales for color definition for particular state
    this.y = d3.scaleOrdinal()
      .domain(["Online", "Unavailable", "Unknown", "BorderLine"])
      .range(["#00a65a", "#dd4b39", "#444444", "#f39c12"]);

    // configure chart viewport
    this.svg = d3.select(this.element.nativeElement)
      .append("svg") // the overall space
      .attr("width", this.width + this.margin.left + this.margin.right)
      .attr("height", this.height + this.margin.top + this.margin.bottom);

    // configure brush
    this.brush = d3.brushX()
      .extent([[0, 0], [this.width, this.heightOverview]])
      .on("brush", this.brushed);

    this.brushMain = d3.brushX()
      .extent([[0, 0], [this.width, this.height]])
      .on("brush", this.brushedMain);

    // build charts
    this.buildMainChart(data);
    this.buildOverviewChart(data);
  }

  private updateChart(data: ResourceStatus[]) {
    // data ranges for the x and y axes
    this.x.domain(d3.extent(data, function (d) {
      return d.updated;
    }));

    this.xOverview.domain(this.x.domain());

    // update charts
    this.updateBars(data);
    this.updateBarsOverview(data);

  }

  private buildMainChart(data: ResourceStatus[]) {
    this.xAxis = d3.axisBottom(this.x);

    this.main = this.svg.append("g")
      .attr("class", "main")
      .attr("transform", "translate(" + this.margin.left + "," + this.margin.top + ")");


    // data ranges for the x and y axes
    this.x.domain(d3.extent(data, function (d) {
      return d.updated;
    }));

    // draw the axes now that they are fully set up
    this.main.append("g")
      .attr("class", "x axis")
      .attr("transform", "translate(0," + this.height + ")")
      .call(this.xAxis);
    this.main.append("g")
      .attr("class", "y axis");

    this.main.append("g")
      .attr("class", "x brush")
      .call(this.brushMain);

    // appended bars section
    this.bars = this.main.append("g")
      .attr("class", "bars");

    this.updateBars(data);

  }

  private updateBars(data: ResourceStatus[]) {

    this.bars.selectAll(".bar").remove().exit();

    this.bars.selectAll(".bar")
      .data(data)
      .enter().append("rect")
      .attr("transform", (d) => "translate(" + this.x(d.updated) + ",0)")
      .attr("class", "bar")
      .attr("width", 6)
      .attr("y", 0)
      .attr("height", this.height)
      .style("fill", d => this.y(d.status));
  }

  private updateBarsOverview(data: ResourceStatus[]) {
    this.barsOverview.selectAll(".bar").remove().exit();

    this.barsOverview.selectAll(".bar")
      .data(data)
      .enter().append("rect")
      .attr("class", "bar")
      .attr("x", (d) => this.xOverview(d.updated) - 3)
      .attr("width", 6)
      .attr("y", 0)
      .attr("height", this.heightOverview)
      .style("fill", d => this.yOverview(d.status));
  }

  private buildOverviewChart(data: ResourceStatus[]) {
    this.xOverview = d3.scaleTime()
      .range([0, this.width]);
    this.yOverview = this.y;

    this.xAxisOverview = d3.axisBottom(this.xOverview);

    this.overview = this.svg.append("g")
      .attr("class", "overview")
      .attr("transform", "translate(" + this.marginOverview.left + "," + this.marginOverview.top + ")");

    this.overview.append("g")
      .attr("class", "x axis")
      .attr("transform", "translate(0," + this.heightOverview + ")")
      .call(this.xAxisOverview);

    this.xOverview.domain(this.x.domain());

    this.barsOverview = this.overview.append("g")
      .attr("class", "bars");

    this.updateBarsOverview(data);

    // add the brush target area on the overview chart
    this.overview.append("g")
      .attr("class", "x brush")
      .call(this.brush)
      .selectAll("rect")
      // -6 is magic number to offset positions for styling/interaction to feel right
      .attr("y", -6)
      // need to manually set the height because the brush has
      // no y scale, i.e. we should see the extent being marked
      // over the full height of the overview chart
      .attr("height", this.heightOverview + 7);  // +7 is magic number for styling
  }

  private configureSize() {
    let containerWidth = this.element.nativeElement.offsetWidth;
    let containerHeight = 500;//this.element.nativeElement.offsetHeight;

    this.margin = {top: 20, right: 20, bottom: 100, left: 20};
    this.width = containerWidth - this.margin.left - this.margin.right;
    this.height = containerHeight - this.margin.top - this.margin.bottom;

    this.marginOverview = {top: 430, right: this.margin.right, bottom: 20, left: this.margin.left};
    this.heightOverview = 500 - this.marginOverview.top - this.marginOverview.bottom;
  }

  private brushed = () => {
    let s = d3.event.selection || this.xOverview.range();
    this.x.domain(s.map(this.xOverview.invert, this.xOverview));

    this.main.selectAll(".bar").attr("transform", (d) => "translate(" + this.x(d.updated) + ",0)");
    this.main.select(".x.axis").call(this.xAxis)
  };

  private brushedMain = () => {
    console.log("brushed");
    let selection = d3.event.selection;
    console.log(this.x.invert(selection[0]) + " -" + this.x.invert(selection[1]))
    // TODO having this dates we can query for exact statuses in future
  };


}
