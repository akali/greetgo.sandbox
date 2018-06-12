import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {MatDialog, MatDialogRef, MatPaginator, MatSort, MatTable} from '@angular/material';
import {TableDatasource} from './table-datasource';
import {HttpService} from "../HttpService";
import {ClientRecord} from "../../model/ClientRecord";
import {debounceTime, distinctUntilChanged, tap} from "rxjs/operators";
import {BehaviorSubject, fromEvent, merge} from "rxjs";
import {ClientDialogComponent} from "./client-dialog/clientDialog.component";
import {ClientDetail} from "../../model/ClientDetail";
import {ClientAddress} from "../../model/ClientAddress";
import {AddressType} from "../../model/AddressType";
import {PhoneType} from "../../model/PhoneType";
import {ClientPhone} from "../../model/ClientPhone";
import {ClientToSave} from "../../model/ClientToSave";
import {ActionType} from "./client-dialog/actionType";

@Component({
  selector: 'table-component',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.css']
})

export class TableComponent implements OnInit, AfterViewInit {
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatTable) table: MatTable<ClientRecord>;
  @ViewChild('input') input: ElementRef;

  dataSource: TableDatasource;

  displayedColumns = ['name', 'charm', 'age', 'total', 'max', 'min'];
  INIT_PAGE_SIZE = 5;
  PAGE_SIZE_OPTIONS = [1, 2, 5, 50];
  private toReload = new BehaviorSubject([]);

  constructor(private httpService: HttpService, private dialog: MatDialog) {}

  ngOnInit() {
    this.dataSource = new TableDatasource(this.httpService, this.paginator, this.sort);
    this.load();
  }

  ngAfterViewInit(): void {
    const mutates = [
      this.sort.sortChange, this.paginator.page, this.toReload.asObservable()
    ];

    merge(...mutates).pipe(
      tap(() => {
        this.load();
      })
    ).subscribe();

    fromEvent(this.input.nativeElement,'keyup')
      .pipe(
        debounceTime(150),
        distinctUntilChanged(),
        tap(() => {
          // console.log(this.input.nativeElement.value.toLowerCase());
          this.paginator.pageIndex = 0;
          this.load();
        })
      )
      .subscribe();
  }

  clientDialogRef: MatDialogRef<ClientDialogComponent>;
  selectedClientId = -1;

  onClientAdd() {
    let client = new ClientDetail();
    client.id = -1;
    client.phones = [];
    client.phones.push(new ClientPhone(-1, "", PhoneType.MOBILE));
    this.httpService.get("/table/getCharms").toPromise().then(value => {
      client.setCharms(value.json());
      this.clientDialogRef = this.dialog.open(ClientDialogComponent, {
        data: {
          client: ClientDetail.copy(client),
          action: ActionType.CREATE
        }
      });
      this.clientDialogRef.disableClose = true;

      this.clientDialogRef.afterClosed().subscribe(value => {
        if (value === undefined || value === null) return;
        let clientToSave = value;
        console.log(JSON.stringify(clientToSave));

        this.httpService.post("/table/add", {
          clientToSave: JSON.stringify(clientToSave)
        }).subscribe(result => {
          console.log(result.json());
          this.toReload.next([]);
        }, error => {
          console.log(error.json());
        });
      });
    }, error => {
      console.log(error);
    });
  }

  onClientRemove() {
    this.httpService.post("/table/remove", {
      clientId: this.selectedClientId
    }).toPromise().then(value => {
      this.toReload.next([]);
    }).catch(reason => {
      console.log(reason);
    });
  }

  onClientEdit() {
    this.httpService.post("/table/detail", {
      clientId: this.selectedClientId
    }).subscribe(value => {
      let client = ClientDetail.copy(value.json());
      this.clientDialogRef = this.dialog.open(ClientDialogComponent,
        {
          data: {
            client: client,
            action: ActionType.EDIT
          }
        });
      this.clientDialogRef.disableClose = true;
      this.clientDialogRef.afterClosed().subscribe(value => {
        if (!value) return;

        let clientToSave = value;

        console.log('sending to REST', JSON.stringify(clientToSave));

        this.httpService.post("/table/edit", {
          clientToSave: JSON.stringify(clientToSave)
        }).subscribe(result => {
          console.log(result.json());
          this.toReload.next([]);
        }, error => {
          console.log(error.json());
        });

      });
    }, error => {
      alert(error);
    });
  }

  private load() {
    if (this.paginator.pageSize === undefined)
      this.paginator.pageSize = this.INIT_PAGE_SIZE;
      console.log("Loading");
      this.dataSource.load(
        this.paginator.pageIndex,
        this.paginator.pageSize,
        this.sort.direction,
        this.sort.active,
        this.input.nativeElement.value.toLowerCase());
  }

  onRowSelect(row) {
    console.log(row);
    if (this.selectedClientId === row.id) this.selectedClientId = -1;
    else this.selectedClientId = row.id;
  }
}
