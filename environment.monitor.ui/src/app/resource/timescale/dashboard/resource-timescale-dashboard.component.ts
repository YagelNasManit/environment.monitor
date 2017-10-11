import {Component} from "@angular/core";
import {StatusTimeRange} from "../../../shared/model/StatusTimeRange";
import {Environment} from "../../../shared/model/Environment";
import {EnvironmentsService} from "../../../shared/service/environments.service";
import {DateRange} from "../../../shared/model/DateRange";
import * as moment from "moment";
import * as  _ from "lodash"

@Component({
  moduleId: module.id,
  selector: 'resource-timescale-dashboard',
  templateUrl: './resource-timescale-dashboard.component.html',

})
export class ResourceTimescaleDashboardComponent {


  public statusTimerange: StatusTimeRange;
  public detailsStatusTimerange: StatusTimeRange;
  public environments: Environment[];


  constructor(envService: EnvironmentsService) {
    envService.getEnvironments().subscribe(envs => {
        this.environments = envs;
        this.statusTimerange = new StatusTimeRange(
          new DateRange(moment().startOf('day').toDate(), moment().toDate(), null),
          envs[0],
          envs[0].checkedResources[0]
        );
      }
    );

  }


  /**
   * Set new status range when status range picker selection updates
   * @param statusTimerange
   */
  onStatusRangeChanged(statusTimerange: StatusTimeRange) {

    console.log(`dashboard status range updated:  
          env: ${statusTimerange.environment.environmentName}
          resource: ${statusTimerange.resource.id}
          DateRAnge: ${statusTimerange.daterange.start} - ${statusTimerange.daterange.end}`
    );
    this.statusTimerange = statusTimerange;
    this.onDetailsStatusTimerangeChanged(this.detailsStatusTimerange.daterange)

  }

  onDetailsStatusTimerangeChanged(dateRange: DateRange){
    this.detailsStatusTimerange = _.cloneDeep(this.statusTimerange);
    this.detailsStatusTimerange.daterange = dateRange;
    console.log(`chart timerange selection changed to: ${this.detailsStatusTimerange.daterange.start} - ${this.detailsStatusTimerange.daterange.end}`);
  }
}
