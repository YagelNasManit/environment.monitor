import {Component} from "@angular/core";

@Component({
  moduleId: module.id,
  selector: 'environment-timescale-dashboard',
  templateUrl: './env-timescale-dashboard.component.html',

})
export class EnvironmentTimescaleDashboardComponent {

  startDate: Date;
  endDate: Date;
  environment: string;

  onRangeSelected(daterange): void {
    console.log("dashboard received date range event with following info:");
    console.log(daterange.start);
    console.log(daterange.end);
    console.log(daterange.label);

    this.startDate = daterange.start;
    this.endDate = daterange.end;

  }

  onEnvSelected(env: string) {
    console.log("dashboard received env change event with following info:");
    console.log(env);
    this.environment = env;
  }
}
