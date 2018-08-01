package kz.greetgo.sandbox.controller.model;

public class QueryFilter {
  public int start, limit;
  public String direction, active, filter;

  public boolean noLimit;

  public boolean isNoLimit() {
    return noLimit;
  }

  public void setNoLimit(boolean noLimit) {
    this.noLimit = noLimit;
  }

  public QueryFilter(int start, int limit, String direction, String active, String filter) {
    this.start = start;
    this.limit = limit;
    this.direction = direction;
    this.active = active;
    this.filter = filter;
  }

  @Override
  public String toString() {
    return "QueryFilter{" +
      "start=" + start +
      ", limit=" + limit +
      ", direction='" + direction + '\'' +
      ", active='" + active + '\'' +
      ", filter='" + filter + '\'' +
      '}';
  }

  public QueryFilter() {
  }
}
