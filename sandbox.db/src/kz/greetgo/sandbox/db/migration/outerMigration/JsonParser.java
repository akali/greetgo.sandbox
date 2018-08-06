package kz.greetgo.sandbox.db.migration.outerMigration;

import kz.greetgo.sandbox.db.interfaces.RowParser;

public class JsonParser implements RowParser {

  private RowParser.OnParse onParse;

  @Override
  public void parseRow(String row) {
    if (onParse != null) {
      onParse.onParse(row);
    }
  }

  @Override
  public void setOnParse(OnParse onParse) {
    this.onParse = onParse;
  }
}
