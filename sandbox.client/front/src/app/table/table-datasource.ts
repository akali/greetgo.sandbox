import { DataSource } from '@angular/cdk/collections';
import { MatPaginator, MatSort } from '@angular/material';
import {Observable, BehaviorSubject} from 'rxjs';
import {ClientRecord} from "../../model/ClientRecord";
import {HttpService} from "../HttpService";
import {TableResponse} from "./TableResponse";

export class TableDatasource extends DataSource<ClientRecord> {
  data: ClientRecord[] = [];

  private clientsSubject = new BehaviorSubject<ClientRecord[]>([]);

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

  public load(pageIndex = 0, pageSize = 1, sortDirection = 'ASC', active = 'name', filter: string) {
    this.httpService.post("/table/get", {
      start: pageSize * pageIndex,
      limit: pageSize,
      sort: sortDirection,
      active: active,
      filter: filter
    }).subscribe(clients => {
      let tableResponse = TableResponse.copy(clients.json());
      this.paginator.length = tableResponse.size;
      this.clientsSubject.next(tableResponse.list);
    });
  }
}
