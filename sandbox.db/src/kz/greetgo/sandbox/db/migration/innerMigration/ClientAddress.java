package kz.greetgo.sandbox.db.migration.innerMigration;

public class ClientAddress {
  public String street, house, flat;

  public ClientAddress(String street, String house, String flat) {
    this.street = street;
    this.house = house;
    this.flat = flat;
  }

  @Override
  public String toString() {
    return "ClientAddress{" +
      "street='" + street + '\'' +
      ", house='" + house + '\'' +
      ", flat='" + flat + '\'' +
      '}';
  }
}
