import {BrowserModule} from "@angular/platform-browser";
import {NgModule} from "@angular/core";
import {AppRoutingModule} from "./app-routing-module";
import {HttpModule} from "@angular/http";
import "./rxjs-extensions";

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

  ],
  imports: [
    BrowserModule,
    HttpModule,
    AppRoutingModule
  ],
  providers: [EnvironmentsService, EnvironmentStatusService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
