package kz.greetgo.sandbox.db.register_impl;

import com.itextpdf.text.DocumentException;
import kz.greetgo.sandbox.controller.model.ClientRecordRow;
import kz.greetgo.sandbox.controller.model.QueryFilter;
import kz.greetgo.sandbox.controller.reports.ReportClientsRecord;
import kz.greetgo.sandbox.db.util.DBHelper;
import liquibase.exception.DatabaseException;
import org.apache.ibatis.jdbc.SQL;
import org.fest.util.Lists;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class GenerateReport {

  private String sql;
  private ReportClientsRecord generator;
  private QueryFilter queryFilter;
  private PreparedStatement statement;
  private int no = 1;
  private String client;

  public GenerateReport(ReportClientsRecord generator, QueryFilter queryFilter, String client) {
    System.out.println(generator);
    System.out.println(queryFilter);
    System.out.println(client);
    this.generator = generator;
    this.queryFilter = queryFilter;
    this.client = client;
  }

  private void prepareSQL(Connection connection) throws SQLException {
    String active;
    List<String> actives = Lists.newArrayList("age", "max", "min", "charm", "total", "name");
    if (!actives.contains(queryFilter.active))
      queryFilter.active = "name";
    sql = new SQL()
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
      .ORDER_BY(queryFilter.active)
      .toString().concat(queryFilter.direction.toLowerCase().equals("asc") ? " asc " : " desc ")
      .concat(" LIMIT ? OFFSET ?");
    statement = connection.prepareStatement(sql);
    statement.setString(1, queryFilter.filter);
    statement.setInt(2, queryFilter.limit);
    statement.setInt(3, queryFilter.start);
  }

  public void execute() {
    try {
      new DBHelper<Void>().run(connection -> {
        prepareSQL(connection);

        ResultSet result = statement.executeQuery();

        generator.start(client, new Date());

        while (result.next()) {
          collectData(result);
        }

        generator.finish();

        return null;
      });
    } catch (DatabaseException | SQLException e) {
      e.printStackTrace();
    }
  }

  private void collectData(ResultSet result) throws SQLException, DocumentException {
    generator.append(new ClientRecordRow(
      no++,
      result.getString("name") + " " +
        result.getString("surname") + " " +
        result.getString("patronymic"),
      result.getString("charm"),
      result.getInt("age"),
      result.getFloat("total"),
      result.getFloat("max"),
      result.getFloat("min"))
    );
  }
}
