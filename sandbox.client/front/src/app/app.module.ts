import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TableComponent } from './table/table.component';
import {
  MatTableModule,
  MatPaginatorModule,
  MatSortModule,
  MatDialogModule,
  MatDatepickerModule,
  MatFormFieldModule,
  MatInputModule,
  MatButtonModule,
  MatCardModule,
  MatRippleModule,
  MatSelectModule,
  MatNativeDateModule,
  MatProgressSpinnerModule,
} from '@angular/material';
import {LoginComponent} from "./input/login.component";
import {HttpService} from "./HttpService";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HttpClientModule} from "@angular/common/http";
import {HttpModule, JsonpModule} from "@angular/http";
import {ClientDialogComponent} from "./table/client-dialog/clientDialog.component";

@NgModule({
  declarations: [
    AppComponent,
    TableComponent,
    LoginComponent,
    ClientDialogComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    MatTableModule,
    MatCardModule,
    MatNativeDateModule,
    MatDatepickerModule,
    MatButtonModule,
    MatPaginatorModule,
    MatSortModule,
    MatDialogModule,
    HttpClientModule,
    MatFormFieldModule,
    MatInputModule,
    HttpModule,
    JsonpModule,
    MatProgressSpinnerModule,
    FormsModule,
    ReactiveFormsModule,
    MatRippleModule,
    MatSelectModule
  ],
  providers: [HttpService],
  bootstrap: [AppComponent],
  entryComponents: [ClientDialogComponent]
})
export class AppModule { }
