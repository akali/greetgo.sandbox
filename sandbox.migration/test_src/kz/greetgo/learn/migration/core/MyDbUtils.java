package kz.greetgo.learn.migration.core;

import kz.greetgo.learn.migration.util.DBHelper;
import liquibase.exception.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MyDbUtils<T> {
  public void execute(OnConnected onConnected) throws DatabaseException, SQLException {
    new DBHelper<>().useSrcDb(true).run(connection -> {
      PreparedStatement ps = onConnected.onConnected(connection);
      return ps.executeUpdate();
    });
  }

  public interface OnConnected {
    PreparedStatement onConnected(Connection connection) throws SQLException;
  }
}
