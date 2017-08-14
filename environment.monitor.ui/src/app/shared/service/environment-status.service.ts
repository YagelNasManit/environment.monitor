import {Injectable} from "@angular/core";
import {Http, Response} from "@angular/http";
import {Observable} from "rxjs/Observable";
import {EnvironmentStatus} from "../model/EnvironmentStatus";
import {Status} from "../model/Status";
import * as moment from "moment";
import {AggregatedResourceStatus} from "../model/AggregatedResourceStatus";
import {ResourceStatus} from "../model/ResourceStatus";

@Injectable()
export class EnvironmentStatusService {

  private poling_interval: number = 20000;

  constructor(private http: Http) {
  }

  getOverallStatus(): Observable<EnvironmentStatus[]> {
    return this.http.get('http://localhost:8080/environment/status/current')
      .map((resp: Response) => {
        return resp.json();
      })
      .map(envs => {
        // TODO find better solution that recursive iterate
        envs.forEach((env, index) => {
          env.resourcesStatus.forEach((status, index) => {
            status.updated = new Date(status.updated);
            status.status = Status[status.status];
          });
        });
        console.log("Mapped Response:" + JSON.stringify(envs));
        return envs;
      })
      .catch(this.handleError);
  }

  private handleError(error: Response) {
    console.error(error);
    let msg = `Error status code ${error.status} at ${error.url}`;
    return Observable.throw(msg);
  }

  getOverallStatusPoll(): Observable<EnvironmentStatus[]> {
    console.log("starting poling");
    return Observable.timer(0, this.poling_interval)
      .switchMap(() => this.getOverallStatus());
  }


  getAggregatedResourceStatuses(environment: string, startDate: Date, endDate: Date) {

    let start = moment(startDate).toISOString();
    let end = moment(endDate).toISOString();

    return this.http.get(`http://localhost:8080/environment/status/aggregated/${environment}?startDate=${start}&endDate=${end}`)
      .map((resp: Response) => {
        return resp.json();
      });
  }

  getAggregatedResourceStatusesResource(environment: string, resourceId: string, startDate: Date, endDate: Date): Observable<AggregatedResourceStatus> {

    let start = moment(startDate).toISOString();
    let end = moment(endDate).toISOString();

    return this.http.get(`http://localhost:8080/environment/status/aggregated/${environment}?startDate=${start}&endDate=${end}&resources=${resourceId}`)
      .map((resp: Response) => {
        return resp.json()[0];
      })
      .map(aggStatus => {
        aggStatus.resourceStatuses.forEach(resStatus => {
          resStatus.status = Status[resStatus.status]
        });
        return aggStatus;
      });
  }

  getResourceStatuses(environment: string, resourceId: string, startDate: Date, endDate: Date): Observable<ResourceStatus[]> {
    let start = moment(startDate).toISOString();
    let end = moment(endDate).toISOString();

    return this.http.get(`http://localhost:8080/resource/status/${environment}/${resourceId}?startDate=${start}&endDate=${end}`)
      .map((resp: Response) => {
        return resp.json();
      })
      .map(statuses => {
        statuses.forEach(resStatus => {
          resStatus.status = Status[resStatus.status];
          resStatus.updated = new Date(resStatus.updated)
        });
        return statuses;
      });
  }

}
