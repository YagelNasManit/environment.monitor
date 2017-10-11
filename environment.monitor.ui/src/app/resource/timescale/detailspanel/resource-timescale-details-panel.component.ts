import {Component, EventEmitter, Input, Output} from "@angular/core";
import {EnvironmentStatusService} from "../../../shared/service/environment-status.service";
import {StatusTimeRange} from "../../../shared/model/StatusTimeRange";
import {ResourceStatus} from "../../../shared/model/ResourceStatus";
import {DateRange} from "../../../shared/model/DateRange";

@Component({
  moduleId: module.id,
  selector: 'resource-timescale-details-panel',
  templateUrl: './resource-timescale-details-panel.component.html',

})
export class ResourceTimescaleDetailsPanel {

  private statuses: ResourceStatus[];

  @Output() onDetailsStatusTimerangeChanged = new EventEmitter<DateRange>();

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

  onDetailsTimeRangeSelected(dateRange: DateRange){
    // propagating value to dashboard
    this.onDetailsStatusTimerangeChanged.emit(dateRange);
  }

}
