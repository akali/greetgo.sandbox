package kz.greetgo.sandbox.controller.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FilteredTable {
  public int size;
  public List<ClientRecord> list;

  public FilteredTable(List<ClientRecord> sample, int start, int offset, String direction, String action, String filter) {
    List<ClientRecord> total = sample.stream()
      .filter(clientRecord -> filter == null || clientRecord.getCombinedString().contains(filter))
      .sorted(
        (t1, t2) -> {
          int result;
          switch(action) {
            case "name":
              result = t1.name.compareTo(t2.name);
              break;
            case "total":
              result = Float.compare(t1.total, t2.total);
              break;
            case "max":
              result = Float.compare(t1.max, t2.max);
              break;
            case "min":
              result = Float.compare(t1.min, t2.min);
              break;
            case "charm":
              result = t1.charm.compareTo(t2.charm);
              break;
            case "age":
              result = Integer.compare(t1.age, t2.age);
              break;
            default:
              result = Integer.compare(t1.id, t2.id);
          }
          if (direction != null && direction.toLowerCase().equals("desc")) {
            result = -result;
          }
          return result;
        }).collect(Collectors.toList());
    this.size = total.size();
    this.list = total.stream().skip(start).limit(offset).collect(Collectors.toList());
  }

  public FilteredTable(List<ClientRecord> clientRecords) {
    list = new ArrayList<>();
  }

  public FilteredTable(List<ClientRecord> clientRecords, QueryFilter filter) {
    this(clientRecords, filter.start, filter.limit, filter.direction, filter.active, filter.filter);
  }

  public FilteredTable() {
    list = new ArrayList<>();

  }
}
