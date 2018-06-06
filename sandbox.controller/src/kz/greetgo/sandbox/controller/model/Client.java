package kz.greetgo.sandbox.controller.model;

import kz.greetgo.sandbox.controller.model.GenderType;

import java.util.Date;

public class Client {
  public int id;
  public String surname;
  public String name;
  public String patronymic;
  public GenderType gender;
  public long birthDate;
  public int charm;

  public Client(int id, String surname, String name, String patronymic, GenderType gender, long birthDate, int charm) {
    this.id = id;
    this.surname = surname;
    this.name = name;
    this.patronymic = patronymic;
    this.gender = gender;
    this.birthDate = birthDate;
    this.charm = charm;
  }

  public Client() {
  }

  public static Client parse(String[] line) {
    Client client = new Client();
    client.id = Integer.parseInt(line[0]);
    client.surname = line[1];
    client.name= line[2];
    client.patronymic = line[3];
    if (line[4].equals("MALE"))
      client.gender = GenderType.MALE;
    else
      client.gender = GenderType.FEMALE;
    client.birthDate = Long.parseLong(line[5]);
    client.charm = Integer.parseInt(line[6]);
    return client;
  }
}
