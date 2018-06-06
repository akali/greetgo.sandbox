package kz.greetgo.sandbox.controller.model;



public class TransactionType {
  public TransactionType(int id, String code, String name) {
    this.id = id;
    this.code = code;
    this.name = name;
  }

  public int id;
  public String code;
  public String name;

  public TransactionType() {

  }

  public static TransactionType parse(String[] line) {
    TransactionType type = new TransactionType();
    type.id = Integer.parseInt(line[0]);
    type.code = line[1];
    type.name = line[2];

    return type;
  }
}
