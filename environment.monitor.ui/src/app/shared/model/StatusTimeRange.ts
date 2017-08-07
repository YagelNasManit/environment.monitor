import {Resource} from "./Resource";
import {DateRange} from "./DateRange";
import {Environment} from "./Environment";

export class StatusTimeRange {
  constructor(public daterange: DateRange, public environment: Environment, public resource: Resource) {
  }
}
