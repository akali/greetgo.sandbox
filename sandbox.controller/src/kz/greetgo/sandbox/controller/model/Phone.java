package kz.greetgo.sandbox.controller.model;

import java.util.Objects;

public class Phone {
  public int id;
  public int clientId;
  public String number;
  public PhoneType type;
  public boolean isActive = true;

  public Phone() { }

  public Phone(int id, int clientId, String number, PhoneType type) {
    this.id = id;
    this.clientId = clientId;
    this.number = number;
    this.type = type;
  }

  public Phone(int clientId, String number, PhoneType type) {
    this.clientId = clientId;
    this.number = number;
    this.type = type;
  }

  public Phone(String number, PhoneType type) {
    this.number = number;
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Phone phone = (Phone) o;
    return id == phone.id &&
      clientId == phone.clientId &&
      isActive == phone.isActive &&
      Objects.equals(number, phone.number) &&
      type == phone.type;
  }
}
