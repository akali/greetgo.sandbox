package kz.greetgo.sandbox.db.util;

import com.itextpdf.text.DocumentException;
import liquibase.exception.DatabaseException;

import java.io.IOException;
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
    T run(Connection connection) throws SQLException, DatabaseException, DocumentException, IOException;
  }

  public T run(Runnable<T> runnable) throws DatabaseException, SQLException {
    try (Connection conn = DriverManager.getConnection(
      "jdbc:postgresql://localhost/aqali_sandbox",
      "aqali_sandbox",
      "111"
    )) {
      return runnable.run(conn);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
