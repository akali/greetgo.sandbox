package kz.greetgo.sandbox.controller.model;

public class QueryFilter {
  public int start, limit;
  public String direction, active, filter;

  public QueryFilter(int start, int limit, String direction, String active, String filter) {
    this.start = start;
    this.limit = limit;
    this.direction = direction;
    this.active = active;
    this.filter = filter;
  }

  public QueryFilter() {
  }
}
