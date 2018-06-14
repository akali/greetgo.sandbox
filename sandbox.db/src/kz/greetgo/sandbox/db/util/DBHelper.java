package kz.greetgo.sandbox.db.util;

import liquibase.exception.DatabaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBHelper {

  public interface Runnable {
    void run(Connection connection) throws SQLException, DatabaseException;
  }

  public static void run(Runnable runnable) {
    try (Connection conn = DriverManager.getConnection(
      "jdbc:postgresql://localhost/aqali_sandbox",
      "aqali_sandbox",
      "111"
    )) {
//      conn.setAutoCommit(false);
      runnable.run(conn);
//      conn.commit();
    } catch (SQLException | DatabaseException e) {
      e.printStackTrace();
    }
  }
}
