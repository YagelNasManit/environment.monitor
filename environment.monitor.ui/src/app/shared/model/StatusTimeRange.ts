import {Resource} from "./Resource";
import {DateRange} from "./DateRange";

export class StatusTimeRange {
  constructor(public daterange: DateRange, public environment: string, public resource: Resource) {
  }
}
