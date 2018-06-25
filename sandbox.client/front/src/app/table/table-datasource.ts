import { DataSource } from '@angular/cdk/collections';
import { MatPaginator, MatSort } from '@angular/material';
import {Observable, BehaviorSubject} from 'rxjs';
import {ClientRecord} from "../../model/ClientRecord";
import {HttpService} from "../HttpService";
import {TableResponse} from "./TableResponse";
import {ActionType} from "./client-dialog/actionType";
import {QueryFilter} from "../../model/QueryFilter";

export class TableDatasource extends DataSource<ClientRecord> {
  data: ClientRecord[] = [];

  private clientsSubject = new BehaviorSubject<ClientRecord[]>([]);
  private localTable:ClientRecord[];

  constructor(private httpService: HttpService,
              private paginator: MatPaginator,
              private sort: MatSort) {
    super();
  }

  connect(): Observable<ClientRecord[]> {
    return this.clientsSubject.asObservable();
  }

  disconnect() {
    this.clientsSubject.complete();
  }

  public load(
    pageIndex = 0,
    pageSize = 1,
    sortDirection = 'ASC',
    active = 'name',
    filter: string): QueryFilter {
    this.httpService.post("/clients/getClientRecords", {
      queryFilter: JSON.stringify(new QueryFilter(pageSize * pageIndex, pageSize, sortDirection, active, filter))
    }).subscribe(clients => {
      let tableResponse = TableResponse.copy(clients.json());
      console.log(tableResponse.list);
      this.paginator.length = tableResponse.size;
      this.localTable = tableResponse.list;
      this.clientsSubject.next(tableResponse.list);
    });
    return new QueryFilter(pageSize * pageIndex, pageSize, sortDirection, active, filter);
  }

  public fakeLoad(action: ActionType, client: ClientRecord) {
    if (client === undefined) return;
    if (action === ActionType.CREATE) {
      this.localTable.push(client);
      this.clientsSubject.next(this.localTable);
    } else if (action === ActionType.EDIT) {
      let idx = this.localTable.findIndex(value => value.id === client.id);
      this.localTable[idx] = client;
      console.log(this.localTable);
      this.clientsSubject.next(this.localTable);
    } else {
      let idx = this.localTable.findIndex(value => value.id === client.id);
      this.localTable.splice(idx, 1);
      this.clientsSubject.next(this.localTable);
    }
  }
}
