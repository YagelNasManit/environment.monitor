import {Injectable} from "@angular/core";

@Injectable()
export class EnvironmentsService {

  getEnvironments() {
    return [
      "Prod", "Stg", "Test", "Dev1", "Dev1", "Dev2", "Dev3", "Dev4", "Dev5", "Dev6", "Dev7"
    ];
  }
}
