import {Component, EventEmitter, Output} from "@angular/core";
import {StatusTimeRange} from "../../model/StatusTimeRange";
import {EnvironmentsService} from "../../service/environments.service";


@Component({
  selector: 'status-range-picker',
  templateUrl: 'statusrange-picker.component.html',

})
export class StatusTimeRangePicker {

  @Output() onStatusRangeChanged = new EventEmitter<StatusTimeRange>();

  public daterange: any = {};
  public environment: string;

  private environments: string[];


  constructor(envService: EnvironmentsService) {
    // TODO mock
    envService.getEnvironments().subscribe(envs => this.environments = envs);
  }

// see original project for full list of options
  // can also be setup using the config service to apply to multiple pickers
  public options: any = {
    locale: {format: 'YYYY-MM-DD'},
    alwaysShowCalendars: false,
    timePicker: true,
    timePicker24Hour: true,
  };


  public onDateSelected(value: any) {
    this.daterange.start = value.start;
    this.daterange.end = value.end;
    this.daterange.label = value.label;
    console.log(`date selected from datepicker is: ${this.daterange.start} - ${this.daterange.end}`)
    this.onStatusRangeChanged.emit(this.buildStatusRange());
  }

  onEnvChange(value) {
    console.log(`env selection changed: ${value}`);
    this.environment = value;
    this.onStatusRangeChanged.emit(this.buildStatusRange());
  }

  buildStatusRange(): StatusTimeRange {
    return new StatusTimeRange(this.daterange, this.environment, null);

  }


}
