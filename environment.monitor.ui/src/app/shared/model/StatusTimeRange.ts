import {Resource} from "./Resource";

export class StatusTimeRange {
  constructor(public daterange: any, public environment: string, public resource: Resource) {
  }
}
