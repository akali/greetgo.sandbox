package kz.greetgo.learn.migration.core;

import kz.greetgo.learn.migration.interfaces.ConnectionConfig;

public class CiaTransitionWriter extends TransitionDbWriter {
  public CiaTransitionWriter(ConnectionConfig connectionConfig) {
    super(connectionConfig);
    setTableName("transition_client");
  }
}
