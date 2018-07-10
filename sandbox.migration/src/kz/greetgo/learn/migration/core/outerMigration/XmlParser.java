package kz.greetgo.learn.migration.core.outerMigration;

import kz.greetgo.learn.migration.interfaces.RowParser;

public class XmlParser implements RowParser {
  private StringBuilder sb = new StringBuilder();

  public XmlParser() {
  }

  @Override
  public void parseRow(String row) {
    if (row.contains("<cia>") || row.contains("</cia>")) return;
    sb.append(row);
    if (row.contains("</client>")) {
      parse(sb.toString());
      sb.setLength(0);
      sb = new StringBuilder();
    }
  }

  @Override
  public void setOnParse(OnParse onParse) {
    this.onParse = onParse;
  }

  public OnParse onParse;

  private void parse(String xml) {
    if (onParse != null)
      onParse.onParse(xml);
  }

}
