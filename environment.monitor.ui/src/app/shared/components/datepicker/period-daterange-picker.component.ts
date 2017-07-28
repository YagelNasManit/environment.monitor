import {Component, EventEmitter, Output} from "@angular/core";


@Component({
  selector: 'period-daterange-picker',
  templateUrl: 'period-daterange-picker.component.html',

})
export class PeriodDateRangePickerComponent {

  @Output() onRangeSelected = new EventEmitter<any>();

  public daterange: any = {};

  // see original project for full list of options
  // can also be setup using the config service to apply to multiple pickers
  public options: any = {
    locale: {format: 'YYYY-MM-DD'},
    alwaysShowCalendars: false,
    timePicker: true,
    timePicker24Hour: true,
  };

  public selectedDate(value: any) {
    this.daterange.start = value.start;
    this.daterange.end = value.end;
    this.daterange.label = value.label;
    console.log(`date selected from datepicker is: ${this.daterange.start} - ${this.daterange.end}`)
    this.onRangeSelected.emit(this.daterange);
  }

}
