import {NgModule} from "@angular/core";
import {HttpModule, JsonpModule} from "@angular/http";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {BrowserModule} from "@angular/platform-browser";
import {RootComponent} from "./root.component";
import {LoginComponent} from "./input/login.component";
import {MainFormComponent} from "./main_form/main_form.component";
import {HttpService} from "./HttpService";
import {CdkTableModule} from "@angular/cdk/table";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {HttpClientModule} from "@angular/common/http";
import {NgbModal, NgbModule} from "@ng-bootstrap/ng-bootstrap";

@NgModule({
  imports: [
    BrowserModule,
    HttpModule,
    JsonpModule,
    FormsModule,
    CdkTableModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbModule.forRoot()
  ],
  declarations: [
    RootComponent, LoginComponent, MainFormComponent
  ],
  bootstrap: [RootComponent],
  providers: [HttpService, NgbModal],
  entryComponents: [],
})
export class AppModule{}
