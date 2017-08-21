import {Component, Input} from "@angular/core";
import {EnvironmentStatusService} from "../../../shared/service/environment-status.service";
import {StatusTimeRange} from "../../../shared/model/StatusTimeRange";
import {AggregatedResourceStatus} from "../../../shared/model/AggregatedResourceStatus";
import {Status} from "../../../shared/model/Status";

@Component({
  moduleId: module.id,
  selector: 'resource-timescale-table',
  styleUrls: ['./resource-timescale-table.component.css'],
  templateUrl: './resource-timescale-table.component.html',

})
export class ResourceTimescaleTableComponent {
  aggStatus: AggregatedResourceStatus;

  public statusEnum = Status;

  constructor(private dataService: EnvironmentStatusService) {
  }


  @Input()
  set statusTimerange(statusTimerange: StatusTimeRange) {
    this.dataService.getAggregatedResourceStatusesResource(
      statusTimerange.environment.environmentName,
      statusTimerange.resource.id,
      statusTimerange.daterange.start,
      statusTimerange.daterange.end).subscribe(data => {
      this.aggStatus = data;
      console.log("set new data");

    });
  }

}
