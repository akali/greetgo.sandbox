package kz.greetgo.sandbox.controller.model;

import kz.greetgo.sandbox.controller.model.CharacterType;

public class ClientRecord {
  public int id;
  public String name;
  public String surname;
  public String patronymic;
  public int total;
  public float max;
  public float min;
  public String charm;
  public int age;

  public ClientRecord() { }

  public String getCombinedString() {
    return (name + "$" + surname + "$" + patronymic + "$" + total + "$" + max + "$" + min + "$" + charm + "$" + age)
      .toLowerCase();
  }

  public String toString() {
    return getCombinedString();
  }
}
