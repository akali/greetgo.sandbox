package kz.greetgo.learn.migration.core;

import kz.greetgo.learn.migration.interfaces.ConnectionConfig;

public class CiaTransitionWriter extends TransitionDbWriter {
  public static final String TABLE_NAME = "transition_cia";

  public CiaTransitionWriter(ConnectionConfig connectionConfig) {
    super(connectionConfig);
    setTableName(TABLE_NAME);
  }
}
