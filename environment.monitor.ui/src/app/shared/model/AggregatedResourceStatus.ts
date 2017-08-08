import {Resource} from "./Resource";
import {AggregatedStatus} from "./AggregatedStatus";
export class AggregatedResourceStatus {

  constructor(public resource: Resource, public resourceStatuses: AggregatedStatus[]) {
  }
}
