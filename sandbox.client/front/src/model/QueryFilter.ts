export class QueryFilter {
  public start: number /* int */;
  public limit: number /* int */;
  public direction: string;
  public active: string;
  public filter: string;


  constructor(start: number = 0, limit: number = 1, direction: string, active: string, filter: string) {
    this.start = start;
    this.limit = limit;
    this.direction = direction;
    this.active = active;
    this.filter = filter;
  }
}
