import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {MatDialog, MatDialogRef, MatPaginator, MatSort, MatTable} from '@angular/material';
import { TableDatasource } from './table-datasource';
import { HttpService} from "../HttpService";
import {ClientRecord} from "../../model/ClientRecord";
import {tap} from "rxjs/operators";
import {merge} from "rxjs";
import {ClientDialogComponent} from "./client-dialog/clientDialog.component";

@Component({
  selector: 'table-component',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.css']
})

export class TableComponent implements OnInit, AfterViewInit {
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatTable) table: MatTable<ClientRecord>;

  dataSource: TableDatasource;

  displayedColumns = ['name', 'charm', 'age', 'total', 'max', 'min'];
  INIT_PAGE_SIZE = 5;
  PAGE_SIZE_OPTIONS = [1, 2, 5, 50];

  constructor(private httpService: HttpService, private dialog: MatDialog) {}

  ngOnInit() {
    this.dataSource = new TableDatasource(this.httpService, this.paginator, this.sort);
    this.load();
  }

  ngAfterViewInit(): void {
    const mutates = [
      this.sort.sortChange, this.paginator.page
    ];

    merge(...mutates).pipe(
      tap(() => {
        this.load();
      })
    ).subscribe();
  }

  clientDialogRef: MatDialogRef<ClientDialogComponent>;

  onClientAdd() {
    this.clientDialogRef = this.dialog.open(ClientDialogComponent, {
      width: "960px",
      height: "850px",
      hasBackDrop: false
    });
  }

  private load() {
    if (this.paginator.pageSize === undefined)
      this.paginator.pageSize = this.INIT_PAGE_SIZE;

    this.dataSource.load(this.paginator.pageIndex,
      this.paginator.pageSize,
      this.sort.direction,
      this.sort.active);
  }
}
