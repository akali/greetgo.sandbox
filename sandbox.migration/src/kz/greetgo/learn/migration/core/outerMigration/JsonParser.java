package kz.greetgo.learn.migration.core.outerMigration;

import kz.greetgo.learn.migration.interfaces.RowParser;

public class JsonParser implements RowParser {

  private OnParse onParse;

  @Override
  public void parseRow(String row) {
//     System.out.println(row);
    if (onParse != null) {
      onParse.onParse(row);
    }
  }

  @Override
  public void setOnParse(OnParse onParse) {
    this.onParse = onParse;
  }
}
