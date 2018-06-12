package kz.greetgo.sandbox.controller.model;


import java.sql.Timestamp;
import java.util.Date;

public class ClientAccount {
  public int id;
  public int client;
  public float money;
  public String number;
  public Timestamp registeredAt;

  public ClientAccount(int id, int client, float money, String number, long registeredAt) {
    this.id = id;
    this.client = client;
    this.money = money;
    this.number = number;
    this.registeredAt = new Timestamp(registeredAt);
  }

  public ClientAccount() { }

  public static ClientAccount parse(String[] line) {
    ClientAccount account = new ClientAccount();
    account.id = Integer.parseInt(line[0]);
    account.client = Integer.parseInt(line[1]);
    account.money = Float.parseFloat(line[2]);
    account.number = line[3];
    account.registeredAt = new Timestamp(Long.parseLong(line[4]));
    return account;
  }
}
