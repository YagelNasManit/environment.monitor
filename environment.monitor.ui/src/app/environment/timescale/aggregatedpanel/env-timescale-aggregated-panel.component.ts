import {Component, Input} from "@angular/core";
import {EnvironmentStatusService} from "../../../shared/service/environment-status.service";
import {StatusTimeRange} from "../../../shared/model/StatusTimeRange";
import {AggregatedResourceStatus} from "../../../shared/model/AggregatedResourceStatus";

@Component({
  selector: "environment-timescale-aggregated-panel",
  templateUrl: "./env-timescale-aggregated-panel.component.html"
})
export class EnvironmentTimescaleAggregatedPanelComponent {

  aggregatedStatuses: AggregatedResourceStatus[];

  constructor(private dataService: EnvironmentStatusService) {
  }


  @Input()
  set statusTimerange(statusTimerange: StatusTimeRange) {
    this.dataService.getAggregatedResourceStatuses(
      statusTimerange.environment.environmentName,
      statusTimerange.daterange.start,
      statusTimerange.daterange.end
    )
      .subscribe(data => {
        this.aggregatedStatuses = data;

      });
  }


}
