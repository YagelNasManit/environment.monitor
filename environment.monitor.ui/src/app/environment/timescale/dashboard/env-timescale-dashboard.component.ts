import {Component} from "@angular/core";
import {StatusTimeRange} from "../../../shared/model/StatusTimeRange";

@Component({
  moduleId: module.id,
  selector: 'environment-timescale-dashboard',
  templateUrl: './env-timescale-dashboard.component.html',

})
export class EnvironmentTimescaleDashboardComponent {


  public statusTimerange: StatusTimeRange;


  onStatusRangeChanged(statusTimerange: StatusTimeRange) {
    console.log(statusTimerange);
    this.statusTimerange = statusTimerange;
  }
}
