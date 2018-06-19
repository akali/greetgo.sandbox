package kz.greetgo.sandbox.controller.model;

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
  public String toString() {
    return "ClientPhone{" +
      "client=" + client +
      ", number='" + number + '\'' +
      ", type=" + type +
      '}';
  }
}
