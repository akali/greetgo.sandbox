package kz.greetgo.sandbox.db.migration.outerMigration;

import kz.greetgo.sandbox.db.interfaces.ConnectionConfig;
import kz.greetgo.sandbox.db.util.Logger;

public class CiaTransitionWriter extends TransitionDbWriter {
  public static final String TABLE_NAME = "transition_cia";

  public CiaTransitionWriter(ConnectionConfig connectionConfig) {
    super(connectionConfig);
    setTableName(TABLE_NAME);
    Logger.d(getClass().getSimpleName(), "constructor with table name " + TABLE_NAME);
  }
}
