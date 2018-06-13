import {ClientRecord} from "../../model/ClientRecord";

export class TableResponse {
  size: number;
  list: ClientRecord[];

  public static copy(o: any): TableResponse {
    let result = new TableResponse();
    result.assign(o);
    return result;
  }

  assign(o: any) {
    this.size = o.size;
    this.list = [];
    for (let key in o.list) {
      if (o.list.hasOwnProperty(key)) {
        this.list.push(ClientRecord.copy(o.list[key]));
      }
    }
  }
}
