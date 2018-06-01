package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.Phone;
import kz.greetgo.sandbox.controller.model.PhoneType;

public class PhoneDot {
  public int id;
  public int clientId;
  public String number;
  public PhoneType type;

  public boolean isActive = true;

  public Phone toPhone() {
    return new Phone(this.id,this.clientId, this.number, this.type);
  }

  public PhoneDot() { }

  public PhoneDot(int id, int clientId, String number, PhoneType type) {
    this.id = id;
    this.clientId = clientId;
    this.number = number;
    this.type = type;
  }

  public void showInfo() {
    System.out.println(
      String.format("----------: Init Address { id:%2d, clientId:%2d, %s, %s }",
        this.id, this.clientId, this.type, this.number));
  }
}
