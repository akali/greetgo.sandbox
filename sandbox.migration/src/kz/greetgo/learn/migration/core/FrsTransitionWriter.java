package kz.greetgo.learn.migration.core;

import kz.greetgo.learn.migration.interfaces.ConnectionConfig;

public class FrsTransitionWriter extends TransitionDbWriter {
  public FrsTransitionWriter(ConnectionConfig connectionConfig) {
    super(connectionConfig);
    setTableName("transition_transactions");
  }
}
