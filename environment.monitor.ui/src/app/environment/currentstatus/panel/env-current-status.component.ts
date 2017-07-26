import {Component, Input} from "@angular/core";
import {EnvironmentStatusService} from "../../../shared/service/environment-status.service";
import {ResourceStatus} from "../../../shared/model/ResourceStatus";


@Component({
  moduleId: module.id,
  selector: 'current-environment-status',
  templateUrl: './env-current-status.component.html',
  styleUrls: ['./env-current-status.component.css'],

})
export class EnvironmentCurrentStatusComponent {

  @Input() environment: string;
  resourceStatuses: ResourceStatus[];


  constructor(private environmentStatusService: EnvironmentStatusService) {
    this.resourceStatuses = environmentStatusService.getLastResourceStatuses(this.environment)

  }
}
