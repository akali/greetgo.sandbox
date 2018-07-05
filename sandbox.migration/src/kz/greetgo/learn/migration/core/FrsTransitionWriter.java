package kz.greetgo.learn.migration.core;

import kz.greetgo.learn.migration.interfaces.ConnectionConfig;

public class FrsTransitionWriter extends TransitionDbWriter {
  public static final String TABLE_NAME = "transition_frs";
  public FrsTransitionWriter(ConnectionConfig connectionConfig) {
    super(connectionConfig);
    setTableName(TABLE_NAME);
  }
}
