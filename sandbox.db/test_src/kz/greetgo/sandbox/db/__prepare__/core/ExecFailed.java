package kz.greetgo.sandbox.db.__prepare__.core;

import java.sql.SQLException;

public class ExecFailed extends RuntimeException {
  public ExecFailed(SQLException e) {
    super(e.getMessage(), e);
  }
}
