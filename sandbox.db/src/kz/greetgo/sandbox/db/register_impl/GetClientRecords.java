package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.FilteredTable;
import kz.greetgo.sandbox.controller.model.QueryFilter;
import org.apache.ibatis.jdbc.SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetClientRecords {
  public GetClientRecords() {
  }
  public static GetClientRecords instance() {
    return new GetClientRecords();
  }

  public FilteredTable run(Connection connection, QueryFilter queryFilter) throws SQLException {
    FilteredTable filteredTable = new FilteredTable();
    String active;
    switch(queryFilter.active) {
      case "age": active = "age"; break;
      case "max": active = "max"; break;
      case "min": active = "min"; break;
      case "charm": active = "charm"; break;
      case "total": active = "total"; break;
      default: active="name"; break;
    }
    PreparedStatement statement = connection.prepareStatement(
      new SQL()
        .SELECT(
          "count(*) over (partition by 1) total_rows",
          "client.id as id",
          "client.name as name",
          "client.surname as surname",
          "client.patronymic as patronymic",
          "(extract(year from age(birth_date))) as age",
          "max(c2.money) as max",
          "min(c2.money) as min",
          "sum(c2.money) as total",
          "c3.name as charm "
        )
        .FROM("client")
        .JOIN("ClientAccount c2 on client.id = c2.client", "charm c3 on client.charm = c3.id")
        .GROUP_BY("client.id", "c3.name")
        .WHERE("client.name || client.surname || client.patronymic like '%'||?||'%'")
        .ORDER_BY(active)
        .toString().concat(queryFilter.direction.toLowerCase().equals("asc") ? " asc " : " desc ")
        .concat(" LIMIT ? OFFSET ?")
    );

    statement.setString(1, queryFilter.filter);
    statement.setInt(2, queryFilter.limit);
    statement.setInt(3, queryFilter.start);

    ResultSet result = statement.executeQuery();
    while (result.next()) {
      filteredTable.list.add(new ClientRecord(result.getInt("id"),
        result.getString("name"),
        result.getString("surname"),
        result.getString("patronymic"),
        result.getFloat("total"),
        result.getFloat("max"),
        result.getFloat("min"),
        result.getString("charm"),
        result.getInt("age")
      ));
    }
    filteredTable.size = filteredTable.list.size();
    return filteredTable;
  }
}
