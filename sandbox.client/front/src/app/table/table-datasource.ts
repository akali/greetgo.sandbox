import { DataSource } from '@angular/cdk/collections';
import { MatPaginator, MatSort } from '@angular/material';
import { map } from 'rxjs/operators';
import {Observable, merge, BehaviorSubject} from 'rxjs';
import {ClientRecord} from "../../model/ClientRecord";
import {HttpService} from "../HttpService";

export class TableDatasource extends DataSource<ClientRecord> {
  data: ClientRecord[] = [];
  // ClientRecord.copy({
  //   age: 48436,
  //   charm: "HAPPY",
  //   id: 1,
  //   max: 500,
  //   min: 500,
  //   name: "Vladimir",
  //   patronymic: "Sergeevich",
  //   surname:"Popov",
  //   total: 500
  // }),
  // ClientRecord.copy({
  //     age: 48436,
  //     charm: "HAPPY",
  //     id: 1,
  //     max: 500,
  //     min: 500,
  //     name: "Vladimir",
  //     patronymic: "Sergeevich",
  //     surname:"Popov",
  //     total: 500
  // }),
  // ClientRecord.copy({
  //     age: 48436,
  //     charm: "HAPPY",
  //     id: 1,
  //     max: 500,
  //     min: 500,
  //     name: "Vladimir",
  //     patronymic: "Sergeevich",
  //     surname:"Popov",
  //     total: 500
  //   },
  // )];

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

  public load(pageIndex=0, pageSize=1, sortDirection='ASC', active='name') {
    this.httpService.post("/table/get", {
      start: pageSize * pageIndex,
      limit: pageSize,
      sort: sortDirection,
      active: active
    }).subscribe(clients => this.clientsSubject.next(clients.json()));
  }
}

