import {Injectable} from "@angular/core";
import {Http, Response} from "@angular/http";
import {Observable} from "rxjs/Observable";
import {EnvironmentStatus} from "../model/EnvironmentStatus";
import {Status} from "../model/Status";
import * as moment from "moment";

@Injectable()
export class EnvironmentStatusService {

  private poling_interval: number = 20000;

  constructor(private http: Http) {
  }

  getOverallStatus(): Observable<EnvironmentStatus[]> {
    return this.http.get('http://localhost:8080/current')
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

    return this.http.get(`http://localhost:8080/aggregated/${environment}?startDate=${start}&endDate=${end}`)
      .map((resp: Response) => {
        return resp.json();
      });
  }

}
