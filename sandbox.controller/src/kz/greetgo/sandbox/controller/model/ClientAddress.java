package kz.greetgo.sandbox.controller.model;

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

  public String getId() {
    return client + "_" + type;
  }
}
