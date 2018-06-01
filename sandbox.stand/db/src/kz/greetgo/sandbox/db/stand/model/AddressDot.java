package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.Address;
import kz.greetgo.sandbox.controller.model.AddressType;

public class AddressDot {
  public int id;
  public int clientId;
  public AddressType type;
  public String street;
  public String house;
  public String flat;

  public boolean isActive = true;

  public Address toAddress() {
    return new Address(id,clientId, type,street,house,flat);
  }

  public AddressDot() { }

  public AddressDot(int id, int clientId, AddressType type, String street, String house, String flat) {
    this.id = id;
    this.clientId = clientId;
    this.type = type;
    this.street = street;
    this.house = house;
    this.flat = flat;
  }

  public void showInfo() {
    System.out.println(
      String.format("----------: Init Address { id:%2d, clientId:%2d, %s, %s, %s }",
        this.id, this.clientId, this.type, this.street, this.house));
  }
}
