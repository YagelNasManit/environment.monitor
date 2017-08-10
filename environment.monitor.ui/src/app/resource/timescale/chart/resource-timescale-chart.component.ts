import {AfterViewInit, Component, ElementRef, ViewChild} from "@angular/core";
import * as d3 from "d3";

@Component({
  selector: "resource-timescale-chart",
  templateUrl: "./resource-timescale-chart.component.html"
})
export class ResourceTimescaleChartComponent implements AfterViewInit {
  @ViewChild("containerBarChart") element: ElementRef;

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

  ngAfterViewInit(): void {

    this.configureSize();

    this.parseDate = d3.timeParse("%d/%m/%Y");

    // some colours to use for the bars
    this.colour = d3.scaleOrdinal()
      .range(["#00a65a", "#dd4b39", "#444444", "#f39c12"]);

    // mathematical scales for the x and y axes
    this.x = d3.scaleTime()
      .range([0, this.width]);
    this.y = d3.scaleLinear()
      .range([this.height, 0]);
    this.xOverview = d3.scaleTime()
      .range([0, this.width]);
    this.yOverview = d3.scaleLinear()
      .range([this.heightOverview, 0]);

    // rendering for the x and y axes

    this.xAxis = d3.axisBottom(this.x);

    this.yAxis = d3.axisLeft(this.y);

    this.xAxisOverview = d3.axisBottom(this.xOverview);

    this.basicConfigure();
    this.buildChart();
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

  private basicConfigure() {
    //this.chart = d3.select();
    // something for us to render the chart into
    let svg = d3.select(this.element.nativeElement)
      .append("svg") // the overall space
      .attr("width", this.width + this.margin.left + this.margin.right)
      .attr("height", this.height + this.margin.top + this.margin.bottom);
    this.main = svg.append("g")
      .attr("class", "main")
      .attr("transform", "translate(" + this.margin.left + "," + this.margin.top + ")");
    this.overview = svg.append("g")
      .attr("class", "overview")
      .attr("transform", "translate(" + this.marginOverview.left + "," + this.marginOverview.top + ")");
  }


  private buildChart() {

    let data = this.dataMock.map(dta => {
      return this.parse(dta);
    });

    // data ranges for the x and y axes
    this.x.domain(d3.extent(data, function (d) {
      return d.date;
    }));
    this.y.domain([0, d3.max(data, function (d) {
      return d.total;
    })]);
    this.xOverview.domain(this.x.domain());
    this.yOverview.domain(this.y.domain());

    // configure brush
    this.brush = d3.brushX()
      .extent([[0, 0], [this.width, this.heightOverview]])
      .on("brush", this.brushed);

    // data range for the bar colours
    // (essentially maps attribute names to colour values)
    this.colour.domain(d3.keys(data[0]));

    // draw the axes now that they are fully set up
    this.main.append("g")
      .attr("class", "x axis")
      .attr("transform", "translate(0," + this.height + ")")
      .call(this.xAxis);
    this.main.append("g")
      .attr("class", "y axis")
      .call(this.yAxis);
    this.overview.append("g")
      .attr("class", "x axis")
      .attr("transform", "translate(0," + this.heightOverview + ")")
      .call(this.xAxisOverview);

    // draw the bars
    this.main.append("g")
      .attr("class", "bars")
      // a group for each stack of bars, positioned in the correct x position
      .selectAll(".bar.stack")
      .data(data)
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

    this.overview.append("g")
      .attr("class", "bars")
      .selectAll(".bar")
      .data(data)
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

    console.log("buildChart " + this.x);
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

  private dataMock = [{"date": "8/19/2016", "count": 2296, "count2": 411, "count3": 1576},
    {"date": "12/17/2016", "count": 2914, "count2": 2519, "count3": 1309},
    {"date": "4/6/2017", "count": 2796, "count2": 2440, "count3": 1150},
    {"date": "12/3/2016", "count": 629, "count2": 2712, "count3": 884},
    {"date": "9/24/2016", "count": 2491, "count2": 1180, "count3": 2807},
    {"date": "5/6/2017", "count": 1673, "count2": 739, "count3": 1076},
    {"date": "6/10/2017", "count": 1051, "count2": 133, "count3": 1594},
    {"date": "10/17/2016", "count": 1727, "count2": 2001, "count3": 1898},
    {"date": "12/24/2016", "count": 638, "count2": 2484, "count3": 2577},
    {"date": "9/19/2016", "count": 599, "count2": 2577, "count3": 838},
    {"date": "6/18/2017", "count": 2283, "count2": 1964, "count3": 824},
    {"date": "3/5/2017", "count": 2206, "count2": 554, "count3": 2051},
    {"date": "9/29/2016", "count": 1124, "count2": 2826, "count3": 2763},
    {"date": "5/10/2017", "count": 67, "count2": 1965, "count3": 2269},
    {"date": "4/16/2017", "count": 1301, "count2": 695, "count3": 2920},
    {"date": "4/22/2017", "count": 2108, "count2": 1343, "count3": 1933},
    {"date": "9/30/2016", "count": 650, "count2": 2072, "count3": 1060},
    {"date": "5/27/2017", "count": 383, "count2": 1810, "count3": 1187},
    {"date": "8/16/2016", "count": 538, "count2": 973, "count3": 850},
    {"date": "12/6/2016", "count": 1433, "count2": 1839, "count3": 2417},
    {"date": "8/17/2016", "count": 2513, "count2": 2999, "count3": 943},
    {"date": "3/22/2017", "count": 2809, "count2": 2805, "count3": 1910},
    {"date": "10/28/2016", "count": 360, "count2": 1665, "count3": 1986},
    {"date": "6/19/2017", "count": 28, "count2": 1556, "count3": 340},
    {"date": "3/6/2017", "count": 2686, "count2": 2686, "count3": 177},
    {"date": "7/30/2016", "count": 257, "count2": 1031, "count3": 434},
    {"date": "7/29/2016", "count": 743, "count2": 1942, "count3": 522},
    {"date": "4/19/2017", "count": 2454, "count2": 1312, "count3": 2441},
    {"date": "4/5/2017", "count": 1311, "count2": 1821, "count3": 1286},
    {"date": "10/7/2016", "count": 211, "count2": 9, "count3": 1834},
    {"date": "9/14/2016", "count": 1636, "count2": 754, "count3": 1997},
    {"date": "3/22/2017", "count": 407, "count2": 246, "count3": 2798},
    {"date": "9/10/2016", "count": 2260, "count2": 832, "count3": 1965},
    {"date": "2/2/2017", "count": 1194, "count2": 2002, "count3": 1073},
    {"date": "11/26/2016", "count": 1502, "count2": 832, "count3": 2001},
    {"date": "8/2/2016", "count": 2571, "count2": 1048, "count3": 2956},
    {"date": "1/21/2017", "count": 331, "count2": 1656, "count3": 2356},
    {"date": "2/10/2017", "count": 201, "count2": 2445, "count3": 2660},
    {"date": "10/25/2016", "count": 2095, "count2": 463, "count3": 1871},
    {"date": "12/1/2016", "count": 2103, "count2": 451, "count3": 1941},
    {"date": "10/13/2016", "count": 2030, "count2": 5, "count3": 616},
    {"date": "11/25/2016", "count": 1430, "count2": 118, "count3": 1365},
    {"date": "4/23/2017", "count": 1687, "count2": 1108, "count3": 10},
    {"date": "11/18/2016", "count": 665, "count2": 703, "count3": 2343},
    {"date": "8/8/2016", "count": 1482, "count2": 1933, "count3": 1535},
    {"date": "8/18/2016", "count": 1029, "count2": 2442, "count3": 2744},
    {"date": "11/10/2016", "count": 672, "count2": 2621, "count3": 2256},
    {"date": "8/9/2016", "count": 2838, "count2": 1591, "count3": 721},
    {"date": "4/2/2017", "count": 2198, "count2": 641, "count3": 2269},
    {"date": "8/24/2016", "count": 2160, "count2": 1256, "count3": 1318},
    {"date": "5/10/2017", "count": 499, "count2": 361, "count3": 1791},
    {"date": "1/5/2017", "count": 1516, "count2": 2234, "count3": 985},
    {"date": "7/28/2016", "count": 250, "count2": 476, "count3": 1405},
    {"date": "1/15/2017", "count": 254, "count2": 2504, "count3": 2163},
    {"date": "5/11/2017", "count": 1396, "count2": 351, "count3": 248},
    {"date": "8/4/2016", "count": 570, "count2": 2371, "count3": 2475},
    {"date": "9/24/2016", "count": 1745, "count2": 653, "count3": 331},
    {"date": "10/1/2016", "count": 840, "count2": 135, "count3": 1515},
    {"date": "4/25/2017", "count": 2281, "count2": 1061, "count3": 1910},
    {"date": "11/4/2016", "count": 531, "count2": 1114, "count3": 1427},
    {"date": "6/22/2017", "count": 2441, "count2": 1830, "count3": 2644},
    {"date": "11/18/2016", "count": 193, "count2": 430, "count3": 2777},
    {"date": "7/19/2017", "count": 623, "count2": 2173, "count3": 539},
    {"date": "11/24/2016", "count": 685, "count2": 314, "count3": 2249},
    {"date": "3/21/2017", "count": 2932, "count2": 2477, "count3": 740},
    {"date": "1/6/2017", "count": 2131, "count2": 2377, "count3": 284},
    {"date": "4/29/2017", "count": 1594, "count2": 1689, "count3": 2752},
    {"date": "7/21/2016", "count": 1167, "count2": 832, "count3": 1178},
    {"date": "1/29/2017", "count": 859, "count2": 1142, "count3": 2222},
    {"date": "7/18/2017", "count": 1272, "count2": 2734, "count3": 2217},
    {"date": "5/15/2017", "count": 2098, "count2": 1325, "count3": 2823},
    {"date": "8/15/2016", "count": 468, "count2": 1992, "count3": 2708},
    {"date": "7/13/2017", "count": 2812, "count2": 520, "count3": 1207},
    {"date": "9/6/2016", "count": 717, "count2": 1736, "count3": 2358},
    {"date": "3/11/2017", "count": 1991, "count2": 1117, "count3": 1954},
    {"date": "5/12/2017", "count": 1130, "count2": 1300, "count3": 447},
    {"date": "1/26/2017", "count": 2559, "count2": 1986, "count3": 2068},
    {"date": "6/3/2017", "count": 1261, "count2": 1889, "count3": 63},
    {"date": "2/25/2017", "count": 817, "count2": 1474, "count3": 2906},
    {"date": "9/16/2016", "count": 72, "count2": 172, "count3": 1702},
    {"date": "10/31/2016", "count": 75, "count2": 688, "count3": 24},
    {"date": "6/21/2017", "count": 1151, "count2": 168, "count3": 1086},
    {"date": "8/15/2016", "count": 2767, "count2": 718, "count3": 2459},
    {"date": "4/22/2017", "count": 2021, "count2": 1552, "count3": 209},
    {"date": "1/16/2017", "count": 2283, "count2": 16, "count3": 2574},
    {"date": "6/28/2017", "count": 758, "count2": 1105, "count3": 2249},
    {"date": "12/3/2016", "count": 79, "count2": 1846, "count3": 1098},
    {"date": "8/28/2016", "count": 2413, "count2": 2360, "count3": 2474},
    {"date": "3/20/2017", "count": 1894, "count2": 1320, "count3": 2095},
    {"date": "4/6/2017", "count": 515, "count2": 326, "count3": 2620},
    {"date": "1/30/2017", "count": 1472, "count2": 758, "count3": 1700},
    {"date": "8/6/2016", "count": 1746, "count2": 1409, "count3": 1534},
    {"date": "9/11/2016", "count": 1110, "count2": 1751, "count3": 1807},
    {"date": "4/9/2017", "count": 1654, "count2": 2332, "count3": 1186},
    {"date": "2/2/2017", "count": 1930, "count2": 861, "count3": 971},
    {"date": "7/8/2017", "count": 2034, "count2": 1870, "count3": 1340},
    {"date": "1/11/2017", "count": 1056, "count2": 740, "count3": 1857},
    {"date": "3/15/2017", "count": 553, "count2": 2151, "count3": 2058},
    {"date": "3/17/2017", "count": 10, "count2": 1778, "count3": 573},
    {"date": "6/13/2017", "count": 1509, "count2": 2291, "count3": 2949},
    {"date": "4/25/2017", "count": 294, "count2": 379, "count3": 2026},
    {"date": "1/3/2017", "count": 888, "count2": 1665, "count3": 823},
    {"date": "1/5/2017", "count": 1503, "count2": 1812, "count3": 1313},
    {"date": "7/18/2017", "count": 517, "count2": 950, "count3": 1315},
    {"date": "9/15/2016", "count": 540, "count2": 1523, "count3": 1563},
    {"date": "2/9/2017", "count": 429, "count2": 240, "count3": 259},
    {"date": "3/10/2017", "count": 1583, "count2": 597, "count3": 983},
    {"date": "7/4/2017", "count": 106, "count2": 2969, "count3": 651},
    {"date": "4/26/2017", "count": 803, "count2": 511, "count3": 1119},
    {"date": "9/1/2016", "count": 465, "count2": 2803, "count3": 2129},
    {"date": "1/14/2017", "count": 2826, "count2": 609, "count3": 2589},
    {"date": "11/21/2016", "count": 2572, "count2": 27, "count3": 1887},
    {"date": "10/24/2016", "count": 1805, "count2": 2033, "count3": 2628},
    {"date": "3/2/2017", "count": 920, "count2": 960, "count3": 838},
    {"date": "12/13/2016", "count": 131, "count2": 2427, "count3": 2241},
    {"date": "7/31/2016", "count": 559, "count2": 2068, "count3": 648},
    {"date": "9/6/2016", "count": 50, "count2": 2888, "count3": 970},
    {"date": "11/20/2016", "count": 2911, "count2": 2804, "count3": 2797},
    {"date": "5/27/2017", "count": 1581, "count2": 931, "count3": 53},
    {"date": "5/27/2017", "count": 1064, "count2": 767, "count3": 1516},
    {"date": "3/19/2017", "count": 19, "count2": 317, "count3": 2364},
    {"date": "11/17/2016", "count": 1867, "count2": 661, "count3": 2232},
    {"date": "12/10/2016", "count": 1295, "count2": 935, "count3": 2952},
    {"date": "1/31/2017", "count": 1675, "count2": 833, "count3": 1260},
    {"date": "7/19/2017", "count": 2695, "count2": 2384, "count3": 1405},
    {"date": "11/15/2016", "count": 903, "count2": 2135, "count3": 40},
    {"date": "3/31/2017", "count": 1672, "count2": 82, "count3": 2117},
    {"date": "5/21/2017", "count": 1929, "count2": 1039, "count3": 2670},
    {"date": "4/10/2017", "count": 2851, "count2": 1347, "count3": 1677},
    {"date": "4/14/2017", "count": 618, "count2": 794, "count3": 2003},
    {"date": "4/23/2017", "count": 1515, "count2": 1914, "count3": 127},
    {"date": "11/24/2016", "count": 1492, "count2": 2894, "count3": 1713},
    {"date": "11/16/2016", "count": 2008, "count2": 1496, "count3": 1107},
    {"date": "9/15/2016", "count": 1375, "count2": 2105, "count3": 768},
    {"date": "12/20/2016", "count": 714, "count2": 969, "count3": 2832},
    {"date": "5/13/2017", "count": 398, "count2": 438, "count3": 1082},
    {"date": "11/21/2016", "count": 1862, "count2": 2531, "count3": 611},
    {"date": "1/9/2017", "count": 1543, "count2": 755, "count3": 2261},
    {"date": "4/25/2017", "count": 1088, "count2": 722, "count3": 2585},
    {"date": "5/31/2017", "count": 2632, "count2": 1500, "count3": 1127},
    {"date": "1/28/2017", "count": 1750, "count2": 1857, "count3": 2836},
    {"date": "6/7/2017", "count": 153, "count2": 2882, "count3": 1184},
    {"date": "9/29/2016", "count": 1454, "count2": 845, "count3": 431},
    {"date": "8/17/2016", "count": 1039, "count2": 519, "count3": 568},
    {"date": "1/19/2017", "count": 186, "count2": 596, "count3": 1869},
    {"date": "4/22/2017", "count": 2501, "count2": 919, "count3": 1079},
    {"date": "7/4/2017", "count": 364, "count2": 1362, "count3": 623},
    {"date": "8/3/2016", "count": 843, "count2": 2753, "count3": 1406},
    {"date": "12/31/2016", "count": 1579, "count2": 644, "count3": 2913},
    {"date": "3/4/2017", "count": 1464, "count2": 2064, "count3": 1573},
    {"date": "1/22/2017", "count": 1171, "count2": 2176, "count3": 1363},
    {"date": "11/4/2016", "count": 1571, "count2": 1173, "count3": 908},
    {"date": "8/30/2016", "count": 1897, "count2": 1531, "count3": 697},
    {"date": "8/29/2016", "count": 1983, "count2": 163, "count3": 2838},
    {"date": "5/8/2017", "count": 777, "count2": 1163, "count3": 436},
    {"date": "8/25/2016", "count": 1514, "count2": 354, "count3": 2898},
    {"date": "5/22/2017", "count": 483, "count2": 2746, "count3": 99},
    {"date": "11/2/2016", "count": 383, "count2": 65, "count3": 14},
    {"date": "5/3/2017", "count": 252, "count2": 555, "count3": 3},
    {"date": "5/6/2017", "count": 2523, "count2": 358, "count3": 1570},
    {"date": "5/31/2017", "count": 2088, "count2": 2364, "count3": 280},
    {"date": "3/11/2017", "count": 1464, "count2": 1603, "count3": 1264},
    {"date": "6/4/2017", "count": 255, "count2": 1461, "count3": 2253},
    {"date": "10/16/2016", "count": 133, "count2": 1196, "count3": 303},
    {"date": "5/18/2017", "count": 647, "count2": 1336, "count3": 159},
    {"date": "1/24/2017", "count": 952, "count2": 759, "count3": 1563},
    {"date": "7/23/2016", "count": 1758, "count2": 1872, "count3": 1783},
    {"date": "9/17/2016", "count": 2723, "count2": 2663, "count3": 130},
    {"date": "9/6/2016", "count": 431, "count2": 846, "count3": 2497},
    {"date": "12/9/2016", "count": 1648, "count2": 65, "count3": 1071},
    {"date": "12/2/2016", "count": 1266, "count2": 2308, "count3": 1724},
    {"date": "9/29/2016", "count": 157, "count2": 133, "count3": 2860},
    {"date": "11/22/2016", "count": 2260, "count2": 1983, "count3": 939},
    {"date": "10/25/2016", "count": 545, "count2": 1488, "count3": 747},
    {"date": "7/3/2017", "count": 2496, "count2": 1410, "count3": 2868},
    {"date": "6/26/2017", "count": 2874, "count2": 1481, "count3": 1666},
    {"date": "11/23/2016", "count": 187, "count2": 1458, "count3": 2828},
    {"date": "5/16/2017", "count": 1981, "count2": 269, "count3": 2268},
    {"date": "12/28/2016", "count": 1853, "count2": 2588, "count3": 1599},
    {"date": "2/19/2017", "count": 551, "count2": 701, "count3": 695},
    {"date": "6/6/2017", "count": 665, "count2": 1617, "count3": 2335},
    {"date": "4/14/2017", "count": 2176, "count2": 1531, "count3": 1130},
    {"date": "9/24/2016", "count": 77, "count2": 1884, "count3": 1985},
    {"date": "1/30/2017", "count": 2590, "count2": 2252, "count3": 135},
    {"date": "8/11/2016", "count": 2140, "count2": 181, "count3": 1763},
    {"date": "4/16/2017", "count": 650, "count2": 2164, "count3": 1092},
    {"date": "4/19/2017", "count": 1335, "count2": 2248, "count3": 1528},
    {"date": "12/13/2016", "count": 197, "count2": 1448, "count3": 2119},
    {"date": "8/27/2016", "count": 635, "count2": 1348, "count3": 1900},
    {"date": "1/23/2017", "count": 1203, "count2": 502, "count3": 1517},
    {"date": "3/11/2017", "count": 1961, "count2": 2341, "count3": 2215},
    {"date": "5/9/2017", "count": 2008, "count2": 2178, "count3": 2692},
    {"date": "11/8/2016", "count": 1413, "count2": 1439, "count3": 709},
    {"date": "12/28/2016", "count": 2671, "count2": 471, "count3": 1553},
    {"date": "12/11/2016", "count": 2504, "count2": 856, "count3": 2586},
    {"date": "12/20/2016", "count": 806, "count2": 2476, "count3": 1605},
    {"date": "6/5/2017", "count": 1742, "count2": 2718, "count3": 1611},
    {"date": "1/6/2017", "count": 2306, "count2": 178, "count3": 2373},
    {"date": "3/11/2017", "count": 1025, "count2": 1389, "count3": 1576},
    {"date": "1/10/2017", "count": 2688, "count2": 1496, "count3": 705},
    {"date": "12/29/2016", "count": 1899, "count2": 2303, "count3": 2484},
    {"date": "1/2/2017", "count": 1424, "count2": 1153, "count3": 1586},
    {"date": "9/27/2016", "count": 1717, "count2": 2161, "count3": 20},
    {"date": "8/31/2016", "count": 2356, "count2": 2406, "count3": 1103},
    {"date": "8/22/2016", "count": 680, "count2": 888, "count3": 1493},
    {"date": "2/6/2017", "count": 1179, "count2": 749, "count3": 1813},
    {"date": "6/24/2017", "count": 2640, "count2": 504, "count3": 1761},
    {"date": "3/2/2017", "count": 448, "count2": 2765, "count3": 5},
    {"date": "5/20/2017", "count": 1407, "count2": 834, "count3": 1132},
    {"date": "8/13/2016", "count": 2633, "count2": 2536, "count3": 1963},
    {"date": "8/20/2016", "count": 2847, "count2": 1155, "count3": 334},
    {"date": "4/4/2017", "count": 1937, "count2": 2346, "count3": 2130},
    {"date": "8/19/2016", "count": 2492, "count2": 2694, "count3": 1979},
    {"date": "7/7/2017", "count": 262, "count2": 598, "count3": 1000},
    {"date": "9/16/2016", "count": 55, "count2": 787, "count3": 2583},
    {"date": "2/22/2017", "count": 1255, "count2": 2967, "count3": 1641},
    {"date": "2/10/2017", "count": 2432, "count2": 2989, "count3": 328},
    {"date": "3/19/2017", "count": 2576, "count2": 1413, "count3": 1},
    {"date": "4/24/2017", "count": 1354, "count2": 240, "count3": 458},
    {"date": "1/14/2017", "count": 833, "count2": 2226, "count3": 2469},
    {"date": "7/23/2016", "count": 2865, "count2": 1724, "count3": 1691},
    {"date": "2/25/2017", "count": 2651, "count2": 2000, "count3": 1289},
    {"date": "1/17/2017", "count": 2298, "count2": 1225, "count3": 780},
    {"date": "2/3/2017", "count": 1895, "count2": 2853, "count3": 294},
    {"date": "2/27/2017", "count": 2559, "count2": 2273, "count3": 341},
    {"date": "3/12/2017", "count": 1343, "count2": 590, "count3": 596},
    {"date": "10/9/2016", "count": 1284, "count2": 1251, "count3": 548},
    {"date": "9/14/2016", "count": 1904, "count2": 311, "count3": 523},
    {"date": "4/21/2017", "count": 1038, "count2": 2879, "count3": 1040},
    {"date": "7/27/2016", "count": 1617, "count2": 1305, "count3": 487},
    {"date": "4/12/2017", "count": 671, "count2": 837, "count3": 1435},
    {"date": "5/30/2017", "count": 2082, "count2": 1977, "count3": 820},
    {"date": "3/8/2017", "count": 740, "count2": 2879, "count3": 902},
    {"date": "4/26/2017", "count": 2442, "count2": 1726, "count3": 2860},
    {"date": "4/19/2017", "count": 1899, "count2": 1131, "count3": 1699},
    {"date": "10/9/2016", "count": 252, "count2": 1056, "count3": 1432},
    {"date": "12/16/2016", "count": 732, "count2": 2709, "count3": 453},
    {"date": "9/8/2016", "count": 2015, "count2": 2664, "count3": 313},
    {"date": "9/20/2016", "count": 1036, "count2": 1435, "count3": 2736},
    {"date": "11/24/2016", "count": 1179, "count2": 2193, "count3": 1032},
    {"date": "2/7/2017", "count": 2668, "count2": 1505, "count3": 523},
    {"date": "1/31/2017", "count": 2577, "count2": 2303, "count3": 973},
    {"date": "1/20/2017", "count": 1045, "count2": 1234, "count3": 1062},
    {"date": "2/8/2017", "count": 2487, "count2": 1118, "count3": 1405},
    {"date": "5/10/2017", "count": 2348, "count2": 2772, "count3": 1239},
    {"date": "6/26/2017", "count": 1327, "count2": 1681, "count3": 2849},
    {"date": "6/25/2017", "count": 325, "count2": 3, "count3": 1463},
    {"date": "1/8/2017", "count": 1505, "count2": 2695, "count3": 1351},
    {"date": "1/10/2017", "count": 490, "count2": 2774, "count3": 702},
    {"date": "11/22/2016", "count": 2767, "count2": 1951, "count3": 1725},
    {"date": "4/2/2017", "count": 2444, "count2": 1582, "count3": 2073},
    {"date": "12/18/2016", "count": 1363, "count2": 1364, "count3": 2308},
    {"date": "12/3/2016", "count": 181, "count2": 1344, "count3": 2906},
    {"date": "12/23/2016", "count": 2044, "count2": 824, "count3": 743},
    {"date": "4/14/2017", "count": 2029, "count2": 2090, "count3": 1399},
    {"date": "9/13/2016", "count": 2854, "count2": 57, "count3": 1715},
    {"date": "9/28/2016", "count": 2160, "count2": 1348, "count3": 2761},
    {"date": "7/27/2016", "count": 2908, "count2": 1972, "count3": 594},
    {"date": "4/14/2017", "count": 1016, "count2": 1184, "count3": 2007},
    {"date": "6/1/2017", "count": 2095, "count2": 2110, "count3": 2563},
    {"date": "11/5/2016", "count": 2097, "count2": 2341, "count3": 1870},
    {"date": "10/22/2016", "count": 1492, "count2": 1985, "count3": 721},
    {"date": "10/24/2016", "count": 2271, "count2": 1949, "count3": 827},
    {"date": "8/16/2016", "count": 615, "count2": 1472, "count3": 1537},
    {"date": "10/16/2016", "count": 2934, "count2": 2397, "count3": 2204},
    {"date": "7/5/2017", "count": 1455, "count2": 666, "count3": 1392},
    {"date": "9/7/2016", "count": 222, "count2": 399, "count3": 65},
    {"date": "3/6/2017", "count": 145, "count2": 414, "count3": 155},
    {"date": "8/9/2016", "count": 1550, "count2": 438, "count3": 2356},
    {"date": "3/15/2017", "count": 2977, "count2": 644, "count3": 2969},
    {"date": "4/23/2017", "count": 2956, "count2": 328, "count3": 136},
    {"date": "10/31/2016", "count": 885, "count2": 2116, "count3": 509},
    {"date": "9/4/2016", "count": 789, "count2": 2837, "count3": 1701},
    {"date": "9/6/2016", "count": 1307, "count2": 1160, "count3": 2817},
    {"date": "10/22/2016", "count": 2657, "count2": 2022, "count3": 1694},
    {"date": "9/17/2016", "count": 2358, "count2": 2356, "count3": 1565},
    {"date": "3/1/2017", "count": 2862, "count2": 798, "count3": 1292},
    {"date": "10/31/2016", "count": 285, "count2": 18, "count3": 24},
    {"date": "10/9/2016", "count": 1776, "count2": 1063, "count3": 248},
    {"date": "6/20/2017", "count": 2982, "count2": 1721, "count3": 1793},
    {"date": "4/30/2017", "count": 1454, "count2": 2235, "count3": 1707},
    {"date": "11/25/2016", "count": 2395, "count2": 152, "count3": 841},
    {"date": "2/1/2017", "count": 220, "count2": 1260, "count3": 1030},
    {"date": "5/31/2017", "count": 1215, "count2": 2124, "count3": 1727},
    {"date": "11/4/2016", "count": 2812, "count2": 791, "count3": 2735},
    {"date": "4/16/2017", "count": 2040, "count2": 129, "count3": 2075},
    {"date": "7/27/2016", "count": 1132, "count2": 1756, "count3": 2116},
    {"date": "3/3/2017", "count": 660, "count2": 2700, "count3": 131},
    {"date": "5/30/2017", "count": 1795, "count2": 1729, "count3": 2410},
    {"date": "3/30/2017", "count": 801, "count2": 603, "count3": 365},
    {"date": "1/15/2017", "count": 1786, "count2": 1004, "count3": 573},
    {"date": "6/22/2017", "count": 2409, "count2": 1129, "count3": 423},
    {"date": "9/14/2016", "count": 646, "count2": 1472, "count3": 1856},
    {"date": "2/3/2017", "count": 2726, "count2": 1385, "count3": 1708},
    {"date": "1/25/2017", "count": 1005, "count2": 1053, "count3": 274},
    {"date": "2/9/2017", "count": 2649, "count2": 2627, "count3": 1859},
    {"date": "5/13/2017", "count": 2926, "count2": 1816, "count3": 794},
    {"date": "6/14/2017", "count": 1336, "count2": 1449, "count3": 1460}];
}
