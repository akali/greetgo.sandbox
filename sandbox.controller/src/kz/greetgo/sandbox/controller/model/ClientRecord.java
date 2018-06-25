package kz.greetgo.sandbox.controller.model;

import java.util.Objects;

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
  private static final float EPS = (float) 1e-2;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClientRecord record = (ClientRecord) o;
    return id == record.id &&
      Math.abs(record.total - total) < EPS &&
      Math.abs(record.max - max) < EPS &&
      Math.abs(record.min - min) < EPS &&
      Math.abs(age - record.age) < 2 &&
      Objects.equals(name, record.name) &&
      Objects.equals(surname, record.surname) &&
      Objects.equals(patronymic, record.patronymic) &&
      Objects.equals(charm, record.charm);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, surname, patronymic, total, max, min, charm, age);
  }

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

  @Override
  public String toString() {
    return "ClientRecord{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", surname='" + surname + '\'' +
      ", patronymic='" + patronymic + '\'' +
      ", total=" + total +
      ", max=" + max +
      ", min=" + min +
      ", charm='" + charm + '\'' +
      ", age=" + age +
      '}';
  }
}
