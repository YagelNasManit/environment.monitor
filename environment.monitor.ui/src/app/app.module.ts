import {BrowserModule} from "@angular/platform-browser";
import {NgModule} from "@angular/core";
import {AppRoutingModule} from "./app-routing-module";
import {HttpModule} from "@angular/http";
import "./rxjs-extensions";
import {Daterangepicker} from "ng2-daterangepicker";

import {AppComponent} from "./app.component";
import {AppHeaderComponent} from "./header/app-header.component";
import {AppFooterComponent} from "./footer/app-footer.component";
import {AppSideBarComponent} from "./sidebar/app-sidebar.component";
import {AppContentComponent} from "./content/app-content.component";
import {AppLayoutComponent} from "./layout/app-layout.component";
import {AppSidebarMenuComponent} from "./sidebar/app-sidebar-menu.component";
import {EnvironmentCurrentStatusComponent} from "./environment/currentstatus/panel/env-current-status.component";
import {EnvironmentCurrentStatusDashboardComponent} from "./environment/currentstatus/dashboard/env-current-status-dashboard.component";
import {EnvironmentsService} from "./shared/service/environments.service";
import {EnvironmentStatusService} from "./shared/service/environment-status.service";
import {EnvironmentTimescaleDashboardComponent} from "./environment/timescale/dashboard/env-timescale-dashboard.component";
import {EnvironmentTimescaleAggregatedChartComponent} from "./environment/timescale/aggregatedchart/env-timescale-aggregated-chart.component";
import {PeriodDateRangePickerComponent} from "./shared/components/datepicker/period-daterange-picker.component";
import {EnvPickerComponent} from "./shared/components/envpicker/env-picker.component";


@NgModule({
  declarations: [
    AppComponent,

    // layout
    AppHeaderComponent,
    AppFooterComponent,
    AppSideBarComponent,
    AppSidebarMenuComponent,
    AppContentComponent,
    AppLayoutComponent,

    // current status
    EnvironmentCurrentStatusDashboardComponent,
    EnvironmentCurrentStatusComponent,

    //timescale
    EnvironmentTimescaleDashboardComponent,
    EnvironmentTimescaleAggregatedChartComponent,

    //shared
    PeriodDateRangePickerComponent,
    EnvPickerComponent

  ],
  imports: [
    BrowserModule,
    HttpModule,
    AppRoutingModule,
    Daterangepicker
  ],
  providers: [EnvironmentsService, EnvironmentStatusService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
