import {Injectable} from "@angular/core";
import {Http, Response} from "@angular/http";
import {Observable} from "rxjs/Observable";
import {Environment} from "../model/Environment";
import {environment} from "../../../environments/environment";

@Injectable()
export class EnvironmentsService {

  constructor(private http: Http) {
  }

  getEnvironments(): Observable<Environment[]> {
    return this.http.get(`${environment.apiBaseUrl}/config/environments`)
      .map((resp: Response) => {
        return resp.json();
      });
  }
}
