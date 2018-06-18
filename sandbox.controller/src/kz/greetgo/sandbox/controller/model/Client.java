package kz.greetgo.sandbox.controller.model;

import java.sql.Timestamp;
import java.util.Date;

public class Client {
  public int id;
  public String surname;
  public String name;
  public String patronymic;
  public GenderType gender;
  public Date birth_date;
  public int charm;


  public Client(int id, String surname, String name, String patronymic, GenderType gender, Date birth_date, int charm) {
    this.id = id;
    this.surname = surname;
    this.name = name;
    this.patronymic = patronymic;
    this.gender = gender;
    this.birth_date = birth_date;
    this.charm = charm;
  }

  public Client(int id, String surname, String name, String patronymic, GenderType gender, Timestamp birth_date, int charm) {
    this.id = id;
    this.surname = surname;
    this.name = name;
    this.patronymic = patronymic;
    this.gender = gender;
    this.birth_date = birth_date;
    this.charm = charm;
  }

  public Client(int id, String surname, String name, String patronymic, GenderType gender, long birth_date, int charm) {
    this.id = id;
    this.surname = surname;
    this.name = name;
    this.patronymic = patronymic;
    this.gender = gender;
    this.birth_date = new Timestamp(birth_date);
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
    client.birth_date = new Date(Long.parseLong(line[5]));
    client.charm = Integer.parseInt(line[6]);

    return client;
  }

  @Override
  public String toString() {
    return "Client{" +
      "id=" + id +
      ", surname='" + surname + '\'' +
      ", name='" + name + '\'' +
      ", patronymic='" + patronymic + '\'' +
      ", gender=" + gender +
      ", birth_date=" + birth_date +
      ", charm=" + charm +
      '}';
  }
}
