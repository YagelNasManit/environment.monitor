import {Injectable} from "@angular/core";
import {Http, Response} from "@angular/http";
import {Observable} from "rxjs/Observable";
import {EnvironmentStatus} from "../model/EnvironmentStatus";
import {Status} from "../model/Status";
import {DatePipe} from "@angular/common";

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

    // TODO think of locales?
    let datePipe = new DatePipe("en-US");
    let start = datePipe.transform(startDate, 'yyyy-MM-dd-hh-mm-ss');
    let end = datePipe.transform(endDate, 'yyyy-MM-dd-hh-mm-ss');

    return this.http.get(`http://localhost:8080/aggregated/${environment}?startDate=${start}&endDate=${end}`)
      .map((resp: Response) => {
        return resp.json();
      });
  }

  /*
   *  "type": "Res1",
   "unit": "Calls",
   "data": [
   {
   "cat": "Online",
   "val": 25.660336370794614
   },
   {
   "cat": "Unavailable",
   "val": 24.326156182248535
   },
   {
   "cat": "Unknown",
   "val": 8.269988175230445
   },
   {
   "cat": "Border Line",
   "val": 3.127329899459046
   },

   ],
   "total": 63.262302237546415
   },
   {
   "type": "Res2",
   "unit": "Calls",
   "data": [
   {
   "cat": "Online",
   "val": 17.248553931590425
   },
   {
   "cat": "Unavailable",
   "val": 13.72765038669414
   },
   {
   "cat": "Unknown",
   "val": 14.977993745798361
   },
   {
   "cat": "Border Line",
   "val": 17.260331552255536
   }
   ],
   "total": 74.13638376141843
   },
   {
   "type": "Res3",
   "unit": "Calls",
   "data": [
   {
   "cat": "Online",
   "val": 2.021445635148953
   },
   {
   "cat": "Unavailable",
   "val": 6.342238973815217
   },
   {
   "cat": "Unknown",
   "val": 4.361785647093132
   },
   {
   "cat": "Border Line",
   "val": 5.797068881767107
   }
   ],
   "total": 23.719431011719564
   }*/


}
