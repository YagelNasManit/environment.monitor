import {Component, EventEmitter, Input, Output, ViewChild} from "@angular/core";
import {StatusTimeRange} from "../../model/StatusTimeRange";
import {DateRange} from "../../model/DateRange";
import {DaterangePickerComponent} from "ng2-daterangepicker";
import {Resource} from "../../model/Resource";
import {Environment} from "../../model/Environment";


@Component({
  selector: 'status-range-picker',
  templateUrl: 'statusrange-picker.component.html',

})
export class StatusTimeRangePicker {

  @ViewChild(DaterangePickerComponent)
  private picker: DaterangePickerComponent;

  public daterange: DateRange;
  public environment: Environment;
  public resource: Resource;

  @Input() public showResPicker: boolean = false;

  @Output() onStatusRangeChanged = new EventEmitter<StatusTimeRange>();
  @Input() environments: Environment[];

  /**
   * Sets component state in accordance to data provided
   * @param statusTimeRange
   */
  @Input()
  set statusTimerange(statusTimeRange: StatusTimeRange) {
    if (statusTimeRange != null) {
      this.daterange = statusTimeRange.daterange;
      this.environment = statusTimeRange.environment;
      this.resource = statusTimeRange.resource;

      this.picker.datePicker.setStartDate(this.daterange.start);
      this.picker.datePicker.setEndDate(this.daterange.end);
    }
  }


  /**
   * DateRange picker options
   * @type {{locale: {format: string}; alwaysShowCalendars: boolean; timePicker: boolean; timePicker24Hour: boolean}}
   */
  public options: any = {
    locale: {format: 'YYYY-MM-DD'},
    alwaysShowCalendars: false,
    timePicker: true,
    timePicker24Hour: true,
  };


  /**
   * Send event to listening component once new DateRange selected from datepicker
   * @param value
   */
  public onDateSelected(value: any) {
    this.daterange = new DateRange(value.start, value.end, value.label);
    this.onStatusRangeChanged.emit(this.buildStatusRange());
  }

  /**
   * Send event to listening component once new Environment selected from datepicker
   * @param value
   */
  onEnvChange(value) {
    this.environment = this.environments.filter(env => env.environmentName === value)[0];
    this.onStatusRangeChanged.emit(this.buildStatusRange());
  }

  /**
   * Send event to listening component once new Resource selected from datepicker
   * @param value
   */
  onResourceChange(value) {
    this.resource = this.environment.checkedResources.filter(res => res.name === value)[0];
    console.log(`selected new resource:${this.resource.name}, event: ${value}`);
    this.onStatusRangeChanged.emit(this.buildStatusRange());
  }

  /**
   * Build new status time range DTO to be passed to parent component
   * @returns {StatusTimeRange}
   */
  private buildStatusRange(): StatusTimeRange {
    return new StatusTimeRange(this.daterange, this.environment, this.resource);

  }


}
