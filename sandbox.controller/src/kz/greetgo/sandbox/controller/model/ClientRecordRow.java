package kz.greetgo.sandbox.controller.model;

public class ClientRecordRow {
  public int no;
  public String name, charm;
  public int age;

  public ClientRecordRow(int no, String name, String charm, int age, float total, float min, float max) {
    this.no = no;
    this.name = name;
    this.charm = charm;
    this.age = age;
    this.total = total;
    this.min = min;
    this.max = max;
  }

  public ClientRecordRow(int no, ClientRecord record) {
    this(
      no,
      record.name + " " + record.surname + " " + record.patronymic,
      record.charm,
      record.age,
      record.total,
      record.min,
      record.max)
    ;
  }

  public float total, min, max;

  public ClientRecordRow() {
  }
}
