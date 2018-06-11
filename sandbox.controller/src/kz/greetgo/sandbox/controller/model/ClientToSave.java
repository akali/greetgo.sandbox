package kz.greetgo.sandbox.controller.model;

import java.util.List;

public class ClientToSave {
  public int id;
  public String name;
  public String surname;
  public String patronymic;
  public int charm;
  public GenderType gender;
  public ClientAddress regAddress;
  public ClientAddress factAddress;
  public long birthDate;
  public List<ClientPhone> phones;
  public ClientPhone workPhone;
  public ClientPhone homePhone;

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
      ", regAddress=" + regAddress +
      ", factAddress=" + factAddress +
      ", workPhone=" + workPhone +
      ", homePhone=" + homePhone +
      '}';
  }
}
