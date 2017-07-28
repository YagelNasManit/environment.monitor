import {Component} from "@angular/core";
import {EnvironmentStatus} from "../../../shared/model/EnvironmentStatus";
import {EnvironmentStatusService} from "../../../shared/service/environment-status.service";

@Component({
  moduleId: module.id,
  selector: 'current-status-dashboard',
  templateUrl: './env-current-status-dashboard-component.html',

})
export class EnvironmentCurrentStatusDashboardComponent {

  environments: EnvironmentStatus[];


  constructor(environmentStatusService: EnvironmentStatusService) {
    environmentStatusService.getOverallStatusPoll()
      .subscribe(
        statuses => this.environments = statuses
      );
  }
}
