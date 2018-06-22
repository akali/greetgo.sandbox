package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.sandbox.controller.reports.ReportClientsRecord;
import kz.greetgo.sandbox.db.util.DBHelper;
import liquibase.exception.DatabaseException;
import org.apache.ibatis.jdbc.SQL;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GenerateReport {
  public static void execute(ReportClientsRecord generator) {
    try {
      new DBHelper<Void>().run(GetClientRecords.instance().run(connection, ));
    } catch (DatabaseException | SQLException e) {
      e.printStackTrace();
    }
  }
}
