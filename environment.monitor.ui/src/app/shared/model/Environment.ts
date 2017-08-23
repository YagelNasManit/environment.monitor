import {Resource} from "./Resource";
export class Environment {

  constructor(public environmentName: string, public checkedResources: Resource[]) {
  }

}
