import {ResourceStatus} from "../model/ResourceStatus";
import {Resource} from "../model/Resource";
import {Status} from "../model/Status";
import {Injectable} from "@angular/core";
import {BehaviorSubject} from "rxjs/BehaviorSubject";

@Injectable()
export class EnvironmentStatusService {


  getLastResourceStatuses(environment: string) {

    return [
      new ResourceStatus(Status.Online, new Resource("Mock1", "Mock1"), "2017-11-12:00:00"),
      new ResourceStatus(Status.BorderLine, new Resource("Mock2", "Mock2"), "2017-11-12:00:00"),
      new ResourceStatus(Status.Unknown, new Resource("Mock3", "Mock3"), "2017-11-12:00:00"),
    ];
  }

  getAggregatedResourceStatuses(environment: string, startDate: Date, endDate: Date) {

    let data = [{
      "type": "Res1",
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
      }
    ];


    let dataSubject = new BehaviorSubject<any[]>(data);

    return dataSubject.asObservable();
    ;
  }
}
