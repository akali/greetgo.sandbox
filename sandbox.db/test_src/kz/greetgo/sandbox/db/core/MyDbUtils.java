package kz.greetgo.sandbox.db.core;

import kz.greetgo.sandbox.db.util.util.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MyDbUtils<T> {
  public void execute(OnConnected onConnected) {
    new DBHelper<>().useSrcDb(true).run(connection -> {
      PreparedStatement ps = onConnected.onConnected(connection);
      return ps.executeUpdate();
    });
  }

  public interface OnConnected {
    PreparedStatement onConnected(Connection connection) throws SQLException;
  }
}
