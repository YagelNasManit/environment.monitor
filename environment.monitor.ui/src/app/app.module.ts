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
import {StatusTimeRangePicker} from "./shared/components/statusrangepicker/statusrange-picker.component";
import {ResourceTimescaleDashboardComponent} from "./resource/timescale/dashboard/resource-timescale-dashboard.component";
import {ResourceTimescaleTableComponent} from "./resource/timescale/table/resource-timescale-table.component";
import {ResourceTimescaleChartComponent} from "./resource/timescale/chart/resource-timescale-chart.component";
import {EnvironmentTimescaleAggregatedPanelComponent} from "./environment/timescale/aggregatedpanel/env-timescale-aggregated-panel.component";
import {StatusLegendComponent} from "./shared/components/statuslegend/status-legend.component";
import {ResourceTimescaleDetailsPanel} from "./resource/timescale/detailspanel/resource-timescale-details-panel.component";
import {ResourceTimescaleDetailsTimeline} from "./resource/timescale/detailstimeline/resource-timescale-details-timeline.component";


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
    EnvironmentTimescaleAggregatedPanelComponent,
    EnvironmentTimescaleAggregatedChartComponent,

    // resource
    ResourceTimescaleDetailsPanel,
    ResourceTimescaleDashboardComponent,
    ResourceTimescaleTableComponent,
    ResourceTimescaleChartComponent,
    ResourceTimescaleDetailsTimeline,

    //shared
    StatusTimeRangePicker,
    StatusLegendComponent

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
