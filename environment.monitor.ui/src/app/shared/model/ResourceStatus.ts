import {Status} from "./Status";
import {Resource} from "./Resource";

export class ResourceStatus {

  constructor(public status: Status, public resource: Resource, public updated: string) {
  }
}
