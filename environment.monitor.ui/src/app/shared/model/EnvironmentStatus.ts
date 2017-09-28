import {ResourceStatus} from "./ResourceStatus";
import {Status} from "./Status";
export class EnvironmentStatus {

  name: string;
  overallStatus: Status;
  resourcesStatus: ResourceStatus[];
}
