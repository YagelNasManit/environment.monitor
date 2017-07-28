import {NgModule} from "@angular/core";
import {RouterModule, Routes} from "@angular/router";
import {EnvironmentCurrentStatusDashboardComponent} from "./environment/currentstatus/dashboard/env-current-status-dashboard.component";
import {EnvironmentTimescaleDashboardComponent} from "./environment/timescale/dashboard/env-timescale-dashboard.component";

const routes: Routes = [
  {path: '', pathMatch: 'full', redirectTo: 'current-dashboard'},
  {path: 'current-dashboard', component: EnvironmentCurrentStatusDashboardComponent},
  {path: 'timescale-dashboard', component: EnvironmentTimescaleDashboardComponent},
  /* {path: 'resource-dashboard', component: ResourceStatusDashboardComponent}*/
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
  }
)
export class AppRoutingModule {

}

export const routableComponents = [
  EnvironmentCurrentStatusDashboardComponent,
  /*PeriodStatusDashboardComponent,
   ResourceStatusDashboardComponent*/
];
