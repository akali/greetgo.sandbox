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
  MatFormFieldModule, MatInputModule, MatButtonModule, MatCardModule, MatCardModule
} from '@angular/material';
import {LoginComponent} from "./input/login.component";
import {HttpService} from "./HttpService";
import {FormsModule} from "@angular/forms";
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
    MatButtonModule,
    MatPaginatorModule,
    MatSortModule,
    MatDialogModule,
    HttpClientModule,
    MatFormFieldModule,
    MatInputModule,
    HttpModule,
    JsonpModule,
    FormsModule,
  ],
  providers: [HttpService],
  bootstrap: [AppComponent],
  entryComponents: [ClientDialogComponent]
})
export class AppModule { }
