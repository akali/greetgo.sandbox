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

  @Override
  public String toString() {
    return "ClientToSave{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", surname='" + surname + '\'' +
      ", patronymic='" + patronymic + '\'' +
      ", charm=" + charm +
      ", phones=" + phones +
      ", birth_date=" + birthDate +
      ", gender=" + gender +
      ", regAddress=" + regAddress +
      ", factAddress=" + factAddress +
      '}';
  }

  public ClientToSave(String name,
                      String surname,
                      String patronymic,
                      int charm,
                      GenderType gender,
                      ClientAddress regAddress,
                      ClientAddress factAddress,
                      long birthDate,
                      List<ClientPhone> phones) {
    this.name = name;
    this.surname = surname;
    this.patronymic = patronymic;
    this.charm = charm;
    this.gender = gender;
    this.regAddress = regAddress;
    this.factAddress = factAddress;
    this.birthDate = birthDate;
    this.phones = phones;
  }

  public ClientToSave(int id,
                      String name,
                      String surname,
                      String patronymic,
                      int charm,
                      GenderType gender,
                      ClientAddress regAddress,
                      ClientAddress factAddress,
                      long birthDate,
                      List<ClientPhone> phones) {
    this.id = id;
    this.name = name;
    this.surname = surname;
    this.patronymic = patronymic;
    this.charm = charm;
    this.gender = gender;
    this.regAddress = regAddress;
    this.factAddress = factAddress;
    this.birthDate = birthDate;
    this.phones = phones;
  }

  public void set(int id) {
    this.id = id;
    for (ClientPhone p : phones) {
      p.client = id;
    }
    factAddress.client = id;
    regAddress.client = id;
  }
}
