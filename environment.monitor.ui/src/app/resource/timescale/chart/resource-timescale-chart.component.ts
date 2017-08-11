import {AfterViewInit, Component, ElementRef, Input, ViewChild} from "@angular/core";
import * as d3 from "d3";
import {EnvironmentStatusService} from "../../../shared/service/environment-status.service";
import {StatusTimeRange} from "../../../shared/model/StatusTimeRange";
import {ResourceStatus} from "../../../shared/model/ResourceStatus";

@Component({
  selector: "resource-timescale-chart",
  templateUrl: "./resource-timescale-chart.component.html"
})
export class ResourceTimescaleChartComponent implements AfterViewInit {
  @ViewChild("containerBarChart") element: ElementRef;

  statuses: ResourceStatus[];

  /** charts margin */
  private margin;

  /** main chart width */
  private width: number;

  /** main chart height */
  private height: number;

  /** X scale of main chart */
  private x: any;

  /** Y scale of main chart */
  private y: any;

  /** color scheme for main chart*/
  private colour: any;

  /** x scale of overview chart */
  private xOverview: any;

  /** y scale of overview chart */
  private yOverview: any;


  private xAxis: any;
  private yAxis: any;
  private xAxisOverview: any;

  private main: any;
  private overview: any;
  private brush: any;

  private marginOverview;
  private heightOverview: number;
  private parseDate: any;

  /** main chart container */
  private svg: any;
  //private data: any;


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
        console.log(statuses);
        this.buildChart(statuses);
      });
  }

  ngAfterViewInit(): void {
  }

  buildChart(data: ResourceStatus[]) {
    this.configureSize();

    this.parseDate = d3.utcParse("%Y-%m-%dT%H:%M:%SZ");

    // some colours to use for the bars
    this.colour = d3.scaleOrdinal()
      .range(["#00a65a", "#dd4b39", "#444444", "#f39c12"]);

    // mathematical scales for the x and y axes
    this.x = d3.scaleTime()
      .range([0, this.width]);

    this.y = d3.scaleOrdinal()
      .domain(["Online", "Unavailable", "Unknown", "BorderLine"])
      .range(["#00a65a", "#dd4b39", "#444444", "#f39c12"]);


    this.svg = d3.select(this.element.nativeElement)
      .append("svg") // the overall space
      .attr("width", this.width + this.margin.left + this.margin.right)
      .attr("height", this.height + this.margin.top + this.margin.bottom);

    // configure brush
    this.brush = d3.brushX()
      .extent([[0, 0], [this.width, this.heightOverview]])
      .on("brush", this.brushed);

    this.buildMainChart(data);
    this.buildOverviewChart(data);
  }

  buildMainChart(data: ResourceStatus[]) {
    this.xAxis = d3.axisBottom(this.x);
    /*this.yAxis = d3.axisLeft(this.y);*/

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
    /* .call(this.yAxis);*/

    // appended basrs
    let bars = this.main.append("g")
      .attr("class", "bars");


    bars.selectAll(".bar")
      .data(data)
      .enter().append("rect")
      .attr("transform", (d) => "translate(" + this.x(d.updated) + ",0)")
      .attr("class", "bar")
      .attr("width", 6)
      .attr("y", 0)
      .attr("height", this.height)
      .style("fill", d => this.y(d.status));
  }


  buildOverviewChart(data: ResourceStatus[]) {

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
    /* this.yOverview.domain(this.y.domain());*/

    this.overview.append("g")
      .attr("class", "bars")
      .selectAll(".bar")
      .data(data)
      .enter().append("rect")
      .attr("class", "bar")
      .attr("x", (d) => this.xOverview(d.updated) - 3)
      .attr("width", 6)
      .attr("y", 0)
      .attr("height", this.heightOverview)
      .style("fill", d => this.yOverview(d.status));

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
}
