package kz.greetgo.sandbox.controller.model;

public class ClientAccountRecord {
  public int clientId;
  public String clientFullName;
  public String clientCharmName;
  public int clientAge;
  public float totalAccBalance;
  public float maxAccBalance;
  public float minAccBalance;

  public ClientAccountRecord() { }

  @Override
  public String toString() {
    return "ClientAccountRecord{" +
      "clientId=" + clientId +
      ", clientFullName='" + clientFullName + '\'' +
      ", clientCharmName='" + clientCharmName + '\'' +
      ", clientAge=" + clientAge +
      '}';
  }
}
