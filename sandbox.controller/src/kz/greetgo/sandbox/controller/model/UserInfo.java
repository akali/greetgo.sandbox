package kz.greetgo.sandbox.controller.model;

import kz.greetgo.sandbox.controller.model.PhoneType;

public class UserInfo {
  public String id;
  public String accountName;
  public String surname;
  public String name;
  public String patronymic;
  public boolean yellow;
  public PhoneType phoneType;

  @Override
  public String toString() {
    return "UserInfo{" +
      "id='" + id + '\'' +
      ", accountName='" + accountName + '\'' +
      ", surname='" + surname + '\'' +
      ", name='" + name + '\'' +
      ", patronymic='" + patronymic + '\'' +
      ", yellow=" + yellow +
      ", phoneType=" + phoneType +
      '}';
  }
}
