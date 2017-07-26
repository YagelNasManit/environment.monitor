import {Component} from "@angular/core";
import {EnvironmentsService} from "../../../shared/service/environments.service";

@Component({
  moduleId: module.id,
  selector: 'current-status-dashboard',
  templateUrl: './env-current-status-dashboard-component.html',

})
export class EnvironmentCurrentStatusDashboardComponent {

  environments: string[];


  constructor(environmentsService: EnvironmentsService) {
    this.environments = environmentsService.getEnvironments();
  }
}
