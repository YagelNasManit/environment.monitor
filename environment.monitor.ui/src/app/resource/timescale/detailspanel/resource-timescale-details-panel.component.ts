import {Component, Input} from "@angular/core";
import {EnvironmentStatusService} from "../../../shared/service/environment-status.service";
import {StatusTimeRange} from "../../../shared/model/StatusTimeRange";
import {ResourceStatus} from "../../../shared/model/ResourceStatus";

@Component({
  moduleId: module.id,
  selector: 'resource-timescale-details-panel',
  templateUrl: './resource-timescale-details-panel.component.html',

})
export class ResourceTimescaleDetailsPanel {

  private statuses: ResourceStatus[];

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
        console.log("statuses arrived");
      });
  }

}
