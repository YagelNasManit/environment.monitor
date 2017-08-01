import {Component} from "@angular/core";
import {StatusTimeRange} from "../../../shared/model/StatusTimeRange";
import {EnvironmentsService} from "../../../shared/service/environments.service";
import {DateRange} from "../../../shared/model/DateRange";
import * as moment from "moment";

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
      // TODO return start date instead of afternoon
      this.statusTimerange = new StatusTimeRange(new DateRange(moment().startOf('day').toDate(), moment().toDate(), null), envs[0], null)
      }
    );
  }

  onStatusRangeChanged(statusTimerange: StatusTimeRange) {
    console.log(statusTimerange);
    this.statusTimerange = statusTimerange;
  }
}
