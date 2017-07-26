import {BrowserModule} from "@angular/platform-browser";
import {NgModule} from "@angular/core";
import {AppRoutingModule} from "./app-routing-module";
import {HttpModule} from "@angular/http";

import {AppComponent} from "./app.component";
import {AppHeaderComponent} from "./header/app-header.component";
import {AppFooterComponent} from "./footer/app-footer.component";
import {AppSideBarComponent} from "./sidebar/app-sidebar.component";
import {AppContentComponent} from "./content/app-content.component";
import {AppLayoutComponent} from "./layout/app-layout.component";
import {AppSidebarMenuComponent} from "./sidebar/app-sidebar-menu.component";


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

  ],
  imports: [
    BrowserModule,
    HttpModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
