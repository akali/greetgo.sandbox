import {NgModule} from "@angular/core";
import {HttpModule, JsonpModule} from "@angular/http";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {BrowserModule} from "@angular/platform-browser";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";

import {RootComponent} from "./root.component";
import {LoginComponent} from "./input/login.component";
import {ModalClientDetailsComponent} from "./main-form/components/client-records/components/modal-client-details/modal-client-details";
import {MainFormComponent} from "./main-form/main-form";
import {ClientAccountInfoTableComponent} from "./main-form/components/client-records/components/client-account-info-table/client-account-info-table";
import {ClientRecordsComponent} from "./main-form/components/client-records/client-records";
import {OnlyNumber} from "../utils/OnlyNumber";

import {HttpService} from "./HttpService";
import {AccountService} from "./services/AccountService";

import 'core-js/es6/reflect';
import 'hammerjs'


import {
  MatAutocompleteModule,
  MatButtonModule,
  MatButtonToggleModule,
  MatCardModule,
  MatCheckboxModule,
  MatChipsModule,
  MatDatepickerModule,
  MatDialogModule,
  MatDividerModule,
  MatExpansionModule,
  MatFormFieldModule,
  MatGridListModule,
  MatIconModule,
  MatInputModule,
  MatListModule,
  MatMenuModule,
  MatNativeDateModule,
  MatPaginatorModule,
  MatProgressBarModule,
  MatProgressSpinnerModule,
  MatRadioModule,
  MatRippleModule,
  MatSelectModule,
  MatSidenavModule,
  MatSliderModule,
  MatSlideToggleModule,
  MatSnackBarModule,
  MatSortModule,
  MatStepperModule,
  MatTableModule,
  MatTabsModule,
  MatToolbarModule,
  MatTooltipModule,
} from '@angular/material';


@NgModule({
  exports: [
    MatAutocompleteModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatCardModule,
    MatCheckboxModule,
    MatChipsModule,
    MatStepperModule,
    MatDatepickerModule,
    MatDialogModule,
    MatDividerModule,
    MatExpansionModule,
    MatGridListModule,
    MatIconModule,
    MatInputModule,
    MatListModule,
    MatMenuModule,
    MatNativeDateModule,
    MatPaginatorModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    MatRadioModule,
    MatRippleModule,
    MatSelectModule,
    MatSidenavModule,
    MatSliderModule,
    MatSlideToggleModule,
    MatSnackBarModule,
    MatSortModule,
    MatTableModule,
    MatTabsModule,
    MatToolbarModule,
    MatTooltipModule,
    MatFormFieldModule
  ]
})
export class MaterialModule {
}

@NgModule({
  imports: [
    BrowserModule,
    HttpModule,
    JsonpModule,
    FormsModule,
    BrowserAnimationsModule,
    MaterialModule,
    ReactiveFormsModule,
  ],
  declarations: [
    RootComponent,
    LoginComponent,
    MainFormComponent,
    OnlyNumber,
    ClientRecordsComponent,
    ModalClientDetailsComponent,
    ClientAccountInfoTableComponent,
  ],
  bootstrap: [RootComponent],
  providers: [HttpService, AccountService],
  entryComponents: [ModalClientDetailsComponent],
})
export class AppModule {
}