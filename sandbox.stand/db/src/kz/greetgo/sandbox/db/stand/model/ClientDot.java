package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.Gender;

import java.util.Date;

public class ClientDot {
  public int id;
  public String name;
  public String surname;
  public String patronymic;
  public Gender gender;
  public Date birthDate;
  public int charmId;

  public boolean isActive = true;

  public ClientDot() { }

  public ClientDot(int id, String name, String surname, String patronymic, Gender gender, Date birthDate, int charmId) {
    this.id = id;
    this.name = name;
    this.surname = surname;
    this.patronymic = patronymic;
    this.gender = gender;
    this.birthDate = birthDate;
    this.charmId = charmId;
  }

  public void showInfo() {
    System.out.println(
      String.format("----------: Init Client { id:%2d, name:%s %s %s}",
        this.id, this.surname, this.name, this.patronymic));
  }
}
