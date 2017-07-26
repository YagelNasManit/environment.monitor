import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs/Observable";

@Injectable()
export class EnvironmentsService {

  constructor(private http: HttpClient) {
  }

  getEnvironments(): Observable<string[]> {
    return this.http.get('http://localhost:8080/environments');
  }
}
