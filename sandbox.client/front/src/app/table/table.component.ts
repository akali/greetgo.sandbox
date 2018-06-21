import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {MatDialog, MatDialogRef, MatPaginator, MatSort, MatTable} from '@angular/material';
import {TableDatasource} from './table-datasource';
import {HttpService} from "../HttpService";
import {ClientRecord} from "../../model/ClientRecord";
import {debounceTime, distinctUntilChanged, tap} from "rxjs/operators";
import {BehaviorSubject, fromEvent, merge} from "rxjs";
import {ClientDialogComponent} from "./client-dialog/clientDialog.component";
import {ClientDetail} from "../../model/ClientDetail";
import {PhoneType} from "../../model/PhoneType";
import {ClientPhone} from "../../model/ClientPhone";
import {ActionType} from "./client-dialog/actionType";
import {Charm} from "../../model/Charm";

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

  private charmsStorage: Charm[] = undefined;

  private toReload = new BehaviorSubject(undefined);

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

    this.toReload.asObservable().pipe(
      tap(
        (result) => {
          if (result === {} || result === undefined || result === null) return;
          console.log(result);
          if (result.hasOwnProperty('action')
            && result.hasOwnProperty(('client')))
            this.fakeLoad(result.action as ActionType, result.client as ClientRecord);
        }
      )
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
  selectedClient: ClientRecord;
  loading = false;

  onClientAdd() {
    let client = new ClientDetail();
    client.id = -1;
    client.phones = [];
    client.phones.push(new ClientPhone(-1, "", PhoneType.MOBILE));

    this.loading = true;

    if (this.charmsStorage !== undefined) {
      client.setCharms(this.charmsStorage);
      this.openDialog(ActionType.CREATE, client);
    } else {
      this.httpService.get("/clients/getCharms").toPromise().then(value => {
        this.charmsStorage = value.json();
        client.setCharms(this.charmsStorage);

        this.openDialog(ActionType.CREATE, client);
      }, error => {
        console.log(error);
      });
    }
  }

  private openDialog(type: ActionType, client: ClientDetail) {
    this.loading = false;
    if (type == ActionType.CREATE) {
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

        this.httpService.post("/clients/addClientToSave", {
          clientToSave: JSON.stringify(clientToSave)
        }).subscribe(result => {
          console.log(result.json());
          this.toReload.next({action: ActionType.CREATE, client: result.json()});
        }, error => {
          console.log(error.json());
        });
      });
    }
    if (type == ActionType.EDIT) {
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

        this.httpService.post("/clients/editClientToSave", {
          clientToSave: JSON.stringify(clientToSave)
        }).subscribe(result => {
          console.log(result.json());
          this.toReload.next({action: ActionType.EDIT, client: result.json()});
        }, error => {
          console.log(error.json());
        });

      });
    }
  }

  onClientRemove() {
    this.httpService.post("/clients/removeClientById", {
      clientId: this.selectedClientId
    }).toPromise().then(value => {
      console.log(this.selectedClient);
      this.toReload.next({action: ActionType.REMOVE, client: this.selectedClient});
    }).catch(reason => {
      console.log(reason);
    });
  }

  onClientEdit() {
    this.loading = true;
    this.httpService.post("/clients/getClientDetailsById", {
      clientId: this.selectedClientId
    }).subscribe(value => {
      console.log(value.json());
      this.openDialog(ActionType.EDIT, ClientDetail.copy(value.json()));
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
    else {
      this.selectedClientId = row.id;
      this.selectedClient = row;
    }
  }

  private fakeLoad(action: ActionType, client: ClientRecord) {
    this.dataSource.fakeLoad(action, client);
  }
}
