package kz.greetgo.sandbox.db.interfaces;

public interface RowParser {
  void parseRow(String row);
  interface OnParse {
    void onParse(String parsed);
  }
  void setOnParse(OnParse onParse);
}
