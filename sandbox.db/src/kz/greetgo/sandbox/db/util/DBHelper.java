package kz.greetgo.sandbox.db.util;

import liquibase.exception.DatabaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBHelper <T> {

  private boolean noCommits = false;

  public DBHelper() {
  }

  public DBHelper(boolean noCommits) {
    this.noCommits = noCommits;
  }

  public interface Runnable <T> {
    T run(Connection connection) throws SQLException, DatabaseException;
  }

  public T run(Runnable<T> runnable) throws DatabaseException, SQLException {
    try (Connection conn = DriverManager.getConnection(
      "jdbc:postgresql://localhost/aqali_sandbox",
      "aqali_sandbox",
      "111"
    )) {
      return runnable.run(conn);
    } catch (SQLException | DatabaseException e) {
      throw e;
    }
  }
}
