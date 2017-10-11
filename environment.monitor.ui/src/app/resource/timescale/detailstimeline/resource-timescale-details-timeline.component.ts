import {Component, Input} from "@angular/core";
import {EnvironmentStatusService} from "../../../shared/service/environment-status.service";
import {StatusTimeRange} from "../../../shared/model/StatusTimeRange";
import {ResourceStatus} from "../../../shared/model/ResourceStatus";
import * as moment from "moment";
import * as _ from "lodash";

@Component({
  moduleId: module.id,
  selector: 'resource-timescale-details-timeline',
  templateUrl: './resource-timescale-details-timeline.component.html',
  styleUrls:['./resource-timescale-details-timeline.component.css']

})
export class ResourceTimescaleDetailsTimeline {

  private statuses;
  private viewRange;

  constructor(public statusService: EnvironmentStatusService) {
  }

  @Input()
  set detailsStatusTimerange(statusTimerange: StatusTimeRange) {
    console.log(`Set details status timerange into timeline component: ${statusTimerange.daterange.start} - ${statusTimerange.daterange.end}`);

    if(statusTimerange){
    this.statusService.getResourceStatusesDetailed(
      statusTimerange.environment.environmentName,
      statusTimerange.resource.id,
      statusTimerange.daterange.start,
      statusTimerange.daterange.end,
      true
    )
      .subscribe(statuses => {
        this.viewRange = statusTimerange;
        this.statuses = this.groupByDate(statuses);
        console.log("mapped states: " + JSON.stringify(this.groupByDate(statuses)));
      });
    }
    else {
      this.statuses = null;
      this.viewRange = null;
    }
  }

  private groupByDate(statuses: ResourceStatus[]) {
    let statesDict = _.groupBy(statuses, (status) => moment(status.updated).startOf('day'));
    return _.map(statesDict,
        (statuses, date) => {
           return {"date": date, "statuses": statuses}
        }
     )
  }
}
