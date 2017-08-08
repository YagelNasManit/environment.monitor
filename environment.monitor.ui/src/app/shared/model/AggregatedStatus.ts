import {Status} from "./Status";
export class AggregatedStatus {

  constructor(public status: Status, public count: number) {
  }
}
