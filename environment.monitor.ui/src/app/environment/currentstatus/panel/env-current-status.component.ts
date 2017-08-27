import {Component, Input} from "@angular/core";
import {EnvironmentStatus} from "../../../shared/model/EnvironmentStatus";


@Component({
  moduleId: module.id,
  selector: 'current-environment-status',
  templateUrl: './env-current-status.component.html',
  styleUrls: ['./env-current-status.component.css'],

})
export class EnvironmentCurrentStatusComponent {

  @Input() environmentStatus: EnvironmentStatus;


}
