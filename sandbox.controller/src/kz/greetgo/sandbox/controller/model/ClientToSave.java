package kz.greetgo.sandbox.controller.model;

import kz.greetgo.sandbox.controller.model.CharacterType;
import kz.greetgo.sandbox.controller.model.GenderType;

import java.util.List;

public class ClientToSave {
  public int id;
  public String name;
  public String surname;
  public String patronymic;
  public int charm;
  public List<ClientPhone> phones;
  public long birthDate;
  public GenderType gender;
  public ClientAddress reg, fact;

  @Override
  public String toString() {
    return "ClientToSave{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", surname='" + surname + '\'' +
            ", patronymic='" + patronymic + '\'' +
            ", charm=" + charm +
            ", phones=" + phones +
            ", birthDate=" + birthDate +
            ", gender=" + gender +
            ", reg=" + reg +
            ", fact=" + fact +
            '}';
  }
}
