import {Injectable} from "@angular/core";
import {Http, Response} from "@angular/http";
import {Observable} from "rxjs/Observable";

@Injectable()
export class EnvironmentsService {

  constructor(private http: Http) {
  }

  getEnvironments(): Observable<string[]> {
    return this.http.get('http://localhost:8080/environments')
      .map((resp: Response) => {
        return resp.json();
      });
  }
}
