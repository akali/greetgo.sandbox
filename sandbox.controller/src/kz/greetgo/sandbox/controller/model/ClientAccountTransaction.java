package kz.greetgo.sandbox.controller.model;


import java.sql.Timestamp;

public class ClientAccountTransaction {
  public int id;
  public int account;
  public float money;
  public Timestamp finished_at;
  public int type;

  public ClientAccountTransaction(int id, int account, float money, long finished_at, int type) {
    this.id = id;
    this.account = account;
    this.money = money;
    this.finished_at = new Timestamp(finished_at);
    this.type = type;
  }

  public ClientAccountTransaction() {

  }

  public ClientAccountTransaction(int id, int account, float money, Timestamp finished_at, int type) {
    this.id = id;
    this.account = account;
    this.money = money;
    this.finished_at = finished_at;
    this.type = type;
  }

  public static ClientAccountTransaction parse(String[] line) {
    ClientAccountTransaction transaction = new ClientAccountTransaction();
    transaction.id = Integer.parseInt(line[0]);
    transaction.account = Integer.parseInt(line[1]);
    transaction.money = Float.parseFloat(line[2]);
    transaction.finished_at = new Timestamp(Long.parseLong(line[3]));
    transaction.type = Integer.parseInt(line[4]);
    return transaction;
  }
}
