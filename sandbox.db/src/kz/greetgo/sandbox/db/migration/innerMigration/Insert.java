package kz.greetgo.sandbox.db.migration.innerMigration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Insert {
  private final String tableName;
  private boolean onConflictDoNothing;

  public Insert onConflictDoNothing(boolean b) {
    onConflictDoNothing = b;
    return this;
  }

  public final List<InsertElement> elementList = new ArrayList<>();

  private static class InsertElement {
    final String name, value;
    String valueAfterInit;

    public InsertElement(String name, String value) {
      this.name = name;
      this.value = value;
    }

    public InsertElement(String name, String value, String valueAfterInit) {
      this.name = name;
      this.value = value;
      this.valueAfterInit = valueAfterInit;
    }
  }

  public Insert(String tableName) {
    this.tableName = tableName;
  }

  public void field(@SuppressWarnings("unused") int no, String name, String value) {
    elementList.add(new InsertElement(name, value));
  }

  @Override
  public String toString() {
    return "insert into " + tableName + " ("
      + elementList.stream().map(e -> e.name).collect(Collectors.joining(", "))
      + ") values ("
      + elementList.stream().map(e -> e.value).collect(Collectors.joining(", "))
      + ") " + (onConflictDoNothing ? "on conflict do nothing" : "");
  }
}