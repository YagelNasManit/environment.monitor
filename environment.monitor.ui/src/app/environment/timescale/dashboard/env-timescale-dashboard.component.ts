import {Component} from "@angular/core";
import {StatusTimeRange} from "../../../shared/model/StatusTimeRange";
import {EnvironmentsService} from "../../../shared/service/environments.service";
import {DateRange} from "../../../shared/model/DateRange";

@Component({
  moduleId: module.id,
  selector: 'environment-timescale-dashboard',
  templateUrl: './env-timescale-dashboard.component.html',

})
export class EnvironmentTimescaleDashboardComponent {


  public statusTimerange: StatusTimeRange;

  public environments;

  constructor(envService: EnvironmentsService) {

    envService.getEnvironments().subscribe(envs => {
        this.environments = envs;
        // TODO hardcoded dates
        this.statusTimerange = new StatusTimeRange(new DateRange(new Date(), new Date(), null), envs[0], null)
      }
    );
  }

  onStatusRangeChanged(statusTimerange: StatusTimeRange) {
    console.log(statusTimerange);
    this.statusTimerange = statusTimerange;
  }
}
