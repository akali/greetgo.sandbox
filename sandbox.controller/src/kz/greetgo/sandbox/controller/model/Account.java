package kz.greetgo.sandbox.controller.model;

import java.sql.Timestamp;

public class Account {
  public Integer id;
  public Integer clientId;
  public Float money;
  public String number;
  public Timestamp registeredAt;
  public Boolean isActive = true;

  public Account() { }

  public Account(int id, int clientId, Float money, String number, Timestamp registeredAt) {
    this.id = id;
    this.clientId = clientId;
    this.money = money;
    this.number = number;
    this.registeredAt = registeredAt;
  }
}
