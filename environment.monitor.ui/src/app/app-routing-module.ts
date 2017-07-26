import {NgModule} from "@angular/core";
import {RouterModule, Routes} from "@angular/router";

const routes: Routes = [
  /*{path: '', pathMatch: 'full', redirectTo: 'current-dashboard'},
   {path: 'current-dashboard', component: CurrentStatusDashboardComponent},
   {path: 'period-dashboard', component: PeriodStatusDashboardComponent},
   {path: 'resource-dashboard', component: ResourceStatusDashboardComponent}*/
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
  }
)
export class AppRoutingModule {

}

export const routableComponents = [
  /*CurrentStatusDashboardComponent,
   PeriodStatusDashboardComponent,
   ResourceStatusDashboardComponent*/
];
