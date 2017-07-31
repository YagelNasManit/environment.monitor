import {Component, EventEmitter, Input, Output, ViewChild} from "@angular/core";
import {StatusTimeRange} from "../../model/StatusTimeRange";
import {DateRange} from "../../model/DateRange";
import {DaterangePickerComponent} from "ng2-daterangepicker";


@Component({
  selector: 'status-range-picker',
  templateUrl: 'statusrange-picker.component.html',

})
export class StatusTimeRangePicker {

  @ViewChild(DaterangePickerComponent)
  private picker: DaterangePickerComponent;

  @Output() onStatusRangeChanged = new EventEmitter<StatusTimeRange>();

  public daterange: DateRange;
  public environment: string;

  /*@Input() _statusTimerange: StatusTimeRange;*/

  @Input() public environments: string[];


  @Input()
  set statusTimerange(statusTimeRange: StatusTimeRange) {
    if (statusTimeRange != null) {
      this.daterange = statusTimeRange.daterange;
      this.environment = statusTimeRange.environment;
      console.log("status timerange set");

      this.picker.datePicker.setStartDate(this.daterange.start);
      this.picker.datePicker.setEndDate(this.daterange.end);
    }
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
    this.daterange = new DateRange(value.start, value.end, value.label);
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
