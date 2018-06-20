package kz.greetgo.sandbox.controller.model;

import java.util.Objects;

public class ClientPhone {
  public int client;
  public String number;
  public PhoneType type;

  public ClientPhone(int client, String number, PhoneType type) {
    this.client = client;
    this.number = number;
    this.type = type;
  }

  public ClientPhone(String number, PhoneType type) {
    this.number = number;
    this.type = type;
  }

  public ClientPhone() {

  }

  public static ClientPhone parse(String[] splitLine) {
    ClientPhone phone = new ClientPhone();

    phone.client = Integer.parseInt(splitLine[0]);
    phone.number = splitLine[1];
    if (splitLine[2].equals(PhoneType.MOBILE.toString())) {
      phone.type = PhoneType.MOBILE;
    } else if (splitLine[2].equals(PhoneType.HOME.toString())) {
        phone.type = PhoneType.HOME;
    } else phone.type = PhoneType.WORK;

    System.out.println("|"+splitLine[2]+"|" + " " + "|"+PhoneType.MOBILE.toString()+"|" + " " + splitLine[2].equals(PhoneType.MOBILE.toString()));
    System.out.println(phone);
    return phone;
  }

  public String getId() {
    return client + "_" + number;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClientPhone that = (ClientPhone) o;
    return client == that.client &&
      Objects.equals(number, that.number) &&
      type == that.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(client, number, type);
  }

  @Override
  public String toString() {
    return "ClientPhone{" +
      "client=" + client +
      ", number='" + number + '\'' +
      ", type=" + type +
      '}';
  }
}
