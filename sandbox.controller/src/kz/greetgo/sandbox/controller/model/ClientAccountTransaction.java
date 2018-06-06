package kz.greetgo.sandbox.controller.model;


import java.util.Date;

public class ClientAccountTransaction {
  public int id;
  public int account;
  public float money;
  public long finishedAt;
  public int type;

  public ClientAccountTransaction(int id, int account, float money, long finishedAt, int type) {
    this.id = id;
    this.account = account;
    this.money = money;
    this.finishedAt = finishedAt;
    this.type = type;
  }

  public ClientAccountTransaction() {

  }

  public static ClientAccountTransaction parse(String[] line) {
    ClientAccountTransaction transaction = new ClientAccountTransaction();
    transaction.id = Integer.parseInt(line[0]);
    transaction.account = Integer.parseInt(line[1]);
    transaction.money = Float.parseFloat(line[2]);
    transaction.finishedAt = Long.parseLong(line[3]);
    transaction.type = Integer.parseInt(line[4]);
    return transaction;
  }
}
