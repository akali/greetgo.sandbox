package kz.greetgo.sandbox.controller.model;

import java.util.Date;

public class Client {
  public Integer id;
  public String name;
  public String surname;
  public String patronymic;
  public Gender gender;
  public Date birthDate;
  public Integer charmId;
  public Boolean isActive = true;

  public Client() { }

  public Client(int id, String name, String surname, String patronymic, Gender gender, Date birthDate, int charmId, boolean isActive) {
    this.id = id;
    this.name = name;
    this.surname = surname;
    this.patronymic = patronymic;
    this.gender = gender;
    this.birthDate = birthDate;
    this.charmId = charmId;
    this.isActive = isActive;
  }
}
