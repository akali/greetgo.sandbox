package kz.greetgo.sandbox.controller.model;

import java.util.Objects;

public class ClientAddress {
  public int client;
  public AddressType type;
  public String street;
  public String house;
  public String flat;

  public ClientAddress(int client, AddressType type, String street, String house, String flat) {
    this.client = client;
    this.type = type;
    this.street = street;
    this.house = house;
    this.flat = flat;
  }

  public ClientAddress(AddressType type, String street, String house, String flat) {
    this.type = type;
    this.street = street;
    this.house = house;
    this.flat = flat;
  }

  public ClientAddress(){}

  public static ClientAddress parse(String[] line) {
    ClientAddress clientAddress = new ClientAddress();
    clientAddress.client = Integer.parseInt(line[0]);
    if (line[1].equals(AddressType.FACT.toString()))
      clientAddress.type = AddressType.FACT;
    else
      clientAddress.type = AddressType.REG;
    clientAddress.street = line[2];
    clientAddress.house = line[3];
    clientAddress.flat = line[4];
    return clientAddress;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClientAddress that = (ClientAddress) o;
    return client == that.client &&
      type == that.type &&
      Objects.equals(street, that.street) &&
      Objects.equals(house, that.house) &&
      Objects.equals(flat, that.flat);
  }

  @Override
  public int hashCode() {
    return Objects.hash(client, type, street, house, flat);
  }

  @Override
  public String toString() {
    return "ClientAddress{" +
      "client=" + client +
      ", type=" + type +
      ", street='" + street + '\'' +
      ", house='" + house + '\'' +
      ", flat='" + flat + '\'' +
      '}';
  }

  public String getId() {
    return client + "_" + type;
  }
}
