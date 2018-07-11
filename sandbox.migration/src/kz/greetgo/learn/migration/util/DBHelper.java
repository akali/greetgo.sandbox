package kz.greetgo.learn.migration.util;

import com.itextpdf.text.DocumentException;
import liquibase.exception.DatabaseException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBHelper<T> {

  private boolean noCommits = false;
  private boolean useSrcDb = false;

  public DBHelper() {
  }

  public DBHelper(boolean noCommits) {
    this.noCommits = noCommits;
  }

  public DBHelper useSrcDb(boolean useSrcDb) {
    this.useSrcDb = useSrcDb;
    return this;
  }

  public T run(Runnable<T> runnable, String url, String username, String password) {
    try (Connection conn = DriverManager.getConnection(
      url,
      username,
      password
    )) {
      return runnable.run(conn);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public T run(Runnable<T> runnable) {
    if (!useSrcDb) {
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
    } else {
      return run(runnable,
        "jdbc:postgresql://localhost/aqali_sandbox_migration_source",
        "aqali_sandbox_migration_source",
        "111");
    }
  }

  public interface Runnable<T> {
    T run(Connection connection) throws SQLException, DatabaseException, DocumentException, IOException, CloneNotSupportedException;
  }
}
