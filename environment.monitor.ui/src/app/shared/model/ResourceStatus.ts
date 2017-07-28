import {Status} from "./Status";
import {Resource} from "./Resource";

export class ResourceStatus {

  status: Status;
  updated: Date;

  constructor(status: Status, public resource: Resource, updated: Date) {
    this.updated = new Date(updated);
  }
}
