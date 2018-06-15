package kz.greetgo.sandbox.controller.model;

import kz.greetgo.sandbox.controller.model.CharacterType;

public class ClientRecord {
  public int id;
  public String name;
  public String surname;
  public String patronymic;
  public float total;
  public float max;
  public float min;
  public String charm;
  public int age;

  public ClientRecord(int id,
                      String name,
                      String surname,
                      String patronymic,
                      float total,
                      float max,
                      float min,
                      String charm,
                      int age) {
    this.id = id;
    this.name = name;
    this.surname = surname;
    this.patronymic = patronymic;
    this.total = total;
    this.max = max;
    this.min = min;
    this.charm = charm;
    this.age = age;
  }

  public ClientRecord() { }

  public String getCombinedString() {
    return (name + "$" + surname + "$" + patronymic + "$" + total + "$" + max + "$" + min + "$" + charm + "$" + age)
      .toLowerCase();
  }

  public String toString() {
    return getCombinedString();
  }
}
