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
  private data: any;


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
        console.log(statuses)
      });
  }

  ngAfterViewInit(): void {

    this.configureSize();

    this.parseDate = d3.utcParse("%Y-%m-%dT%H:%M:%SZ");

    // some colours to use for the bars
    this.colour = d3.scaleOrdinal()
      .range(["#00a65a", "#dd4b39", "#444444", "#f39c12"]);

    // mathematical scales for the x and y axes
    this.x = d3.scaleTime()
      .range([0, this.width]);
    this.y = d3.scaleLinear()
      .range([this.height, 0]);

    this.data = this.dataMock.map(dta => {
      return this.parse(dta);
    });

    this.colour.domain(d3.keys(this.data[0]));

    this.svg = d3.select(this.element.nativeElement)
      .append("svg") // the overall space
      .attr("width", this.width + this.margin.left + this.margin.right)
      .attr("height", this.height + this.margin.top + this.margin.bottom);

    // configure brush
    this.brush = d3.brushX()
      .extent([[0, 0], [this.width, this.heightOverview]])
      .on("brush", this.brushed);

    this.buildMainChart();
    this.buildOverviewChart();

  }

  buildMainChart() {
    this.xAxis = d3.axisBottom(this.x);
    this.yAxis = d3.axisLeft(this.y);

    this.main = this.svg.append("g")
      .attr("class", "main")
      .attr("transform", "translate(" + this.margin.left + "," + this.margin.top + ")");

    // data ranges for the x and y axes
    this.x.domain(d3.extent(this.data, function (d) {
      return d.date;
    }));
    this.y.domain([0, d3.max(this.data, function (d) {
      return d.total;
    })]);

    // draw the axes now that they are fully set up
    this.main.append("g")
      .attr("class", "x axis")
      .attr("transform", "translate(0," + this.height + ")")
      .call(this.xAxis);
    this.main.append("g")
      .attr("class", "y axis")
      .call(this.yAxis);

    // draw the bars
    this.main.append("g")
      .attr("class", "bars")
      // a group for each stack of bars, positioned in the correct x position
      .selectAll(".bar.stack")
      .data(this.data)
      .enter().append("g")
      .attr("class", "bar stack")
      .attr("transform", (d) => "translate(" + this.x(d.date) + ",0)")
      // a bar for each value in the stack, positioned in the correct y positions
      .selectAll("rect")
      .data((d) => d.counts)
      .enter().append("rect")
      .attr("class", "bar")
      .attr("width", 6)
      .attr("y", (d) => this.y(d.y1))
      .attr("height", (d) => this.y(d.y0) - this.y(d.y1))
      .style("fill", (d) => this.colour(d.name));

  }


  buildOverviewChart() {

    this.xOverview = d3.scaleTime()
      .range([0, this.width]);
    this.yOverview = d3.scaleLinear()
      .range([this.heightOverview, 0]);

    this.xAxisOverview = d3.axisBottom(this.xOverview);

    this.overview = this.svg.append("g")
      .attr("class", "overview")
      .attr("transform", "translate(" + this.marginOverview.left + "," + this.marginOverview.top + ")");

    this.overview.append("g")
      .attr("class", "x axis")
      .attr("transform", "translate(0," + this.heightOverview + ")")
      .call(this.xAxisOverview);

    this.xOverview.domain(this.x.domain());
    this.yOverview.domain(this.y.domain());

    this.overview.append("g")
      .attr("class", "bars")
      .selectAll(".bar")
      .data(this.data)
      .enter().append("rect")
      .attr("class", "bar")
      .attr("x", (d) => this.xOverview(d.date) - 3)
      .attr("width", 6)
      .attr("y", (d) => this.yOverview(d.total))
      .attr("height", (d) => this.heightOverview - this.yOverview(d.total));

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

    this.main.selectAll(".bar.stack").attr("transform", (d) => "translate(" + this.x(d.date) + ",0)");
    this.main.select(".x.axis").call(this.xAxis)
  };

  // by habit, cleaning/parsing the data and return a new object to ensure/clarify data object structure
  private parse(d) {

    let value = {
      date: null,
      total: 0,
      counts: null
    };

    value.date = this.parseDate(d.date);
    //let value = { date: this.parseDate(d.date) }; // turn the date string into a date object

    // adding calculated data to each count in preparation for stacking
    let y0 = 0; // keeps track of where the "previous" value "ended"
    value.counts = ["count", "count2", "count3"].map(function (name) {
      return {
        name: name,
        y0: y0,
        // add this count on to the previous "end" to create a range, and update the "previous end" for the next iteration
        y1: y0 += +d[name]
      };
    });
    // quick way to get the total from the previous calculations
    value.total = value.counts[value.counts.length - 1].y1;
    return value;
  }

  private dataMock = [{"date": "2017-08-13T17:59:08Z", "count": 0, "count2": 0, "count3": 0},
    {"date": "2017-08-10T04:08:01Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-16T19:03:53Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-14T08:56:05Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-14T13:12:10Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-10T17:36:50Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-16T09:54:54Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-11T09:41:40Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-14T20:38:06Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-12T21:16:44Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-11T13:01:53Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-15T18:34:15Z", "count": 0, "count2": 0, "count3": 1},
    {"date": "2017-08-11T03:38:14Z", "count": 0, "count2": 0, "count3": 1},
    {"date": "2017-08-13T20:05:46Z", "count": 0, "count2": 0, "count3": 1},
    {"date": "2017-08-14T00:07:19Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-11T15:40:29Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-13T07:46:09Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-12T20:09:41Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-14T08:50:15Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-10T06:12:52Z", "count": 0, "count2": 0, "count3": 1},
    {"date": "2017-08-10T06:05:18Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-16T19:57:01Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-12T02:13:14Z", "count": 0, "count2": 0, "count3": 1},
    {"date": "2017-08-10T15:50:09Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-16T11:47:28Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-12T06:39:33Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-16T18:04:32Z", "count": 0, "count2": 0, "count3": 1},
    {"date": "2017-08-14T15:02:49Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-13T19:49:38Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-12T15:10:58Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-11T16:37:06Z", "count": 0, "count2": 0, "count3": 1},
    {"date": "2017-08-14T17:47:49Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-14T06:54:29Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-11T20:35:42Z", "count": 0, "count2": 0, "count3": 1},
    {"date": "2017-08-14T09:27:47Z", "count": 0, "count2": 0, "count3": 0},
    {"date": "2017-08-12T13:36:02Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-12T17:49:57Z", "count": 0, "count2": 0, "count3": 1},
    {"date": "2017-08-12T00:32:44Z", "count": 0, "count2": 0, "count3": 0},
    {"date": "2017-08-13T16:00:53Z", "count": 0, "count2": 0, "count3": 1},
    {"date": "2017-08-14T08:56:01Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-11T04:15:32Z", "count": 0, "count2": 0, "count3": 1},
    {"date": "2017-08-11T21:46:05Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-09T15:30:26Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-14T20:22:09Z", "count": 0, "count2": 0, "count3": 1},
    {"date": "2017-08-15T17:32:48Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-15T17:35:38Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-15T01:08:39Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-08T12:57:22Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-14T07:12:00Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-11T02:19:48Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-14T09:59:11Z", "count": 0, "count2": 0, "count3": 0},
    {"date": "2017-08-16T01:13:58Z", "count": 0, "count2": 0, "count3": 0},
    {"date": "2017-08-12T20:54:40Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-12T01:34:20Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-16T05:47:03Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-15T20:44:12Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-14T12:19:21Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-10T16:51:56Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-09T07:40:54Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-13T17:46:15Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-12T16:28:46Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-13T21:06:04Z", "count": 0, "count2": 0, "count3": 0},
    {"date": "2017-08-11T04:37:33Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-08T21:10:07Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-08T12:57:44Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-12T02:44:15Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-12T02:01:47Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-10T14:00:33Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-15T11:36:58Z", "count": 0, "count2": 0, "count3": 0},
    {"date": "2017-08-10T14:52:05Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-08T23:52:42Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-15T17:19:35Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-11T21:56:46Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-12T05:16:42Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-13T22:09:57Z", "count": 0, "count2": 0, "count3": 0},
    {"date": "2017-08-14T18:58:09Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-12T17:05:14Z", "count": 0, "count2": 0, "count3": 1},
    {"date": "2017-08-09T13:20:50Z", "count": 0, "count2": 0, "count3": 0},
    {"date": "2017-08-09T03:17:59Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-16T06:10:30Z", "count": 0, "count2": 0, "count3": 1},
    {"date": "2017-08-10T14:33:51Z", "count": 0, "count2": 0, "count3": 0},
    {"date": "2017-08-14T12:29:56Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-09T02:52:34Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-09T17:49:45Z", "count": 0, "count2": 0, "count3": 0},
    {"date": "2017-08-12T16:42:01Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-14T06:42:30Z", "count": 0, "count2": 0, "count3": 1},
    {"date": "2017-08-14T00:47:33Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-15T12:23:36Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-13T04:47:19Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-12T22:40:03Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-14T01:14:50Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-12T01:12:51Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-09T01:10:22Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-08T09:44:01Z", "count": 0, "count2": 0, "count3": 1},
    {"date": "2017-08-11T23:56:10Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-11T23:11:46Z", "count": 1, "count2": 0, "count3": 0},
    {"date": "2017-08-14T08:21:56Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-10T00:16:01Z", "count": 0, "count2": 0, "count3": 0},
    {"date": "2017-08-13T19:11:42Z", "count": 0, "count2": 1, "count3": 0},
    {"date": "2017-08-11T08:54:57Z", "count": 1, "count2": 0, "count3": 0}];
}
