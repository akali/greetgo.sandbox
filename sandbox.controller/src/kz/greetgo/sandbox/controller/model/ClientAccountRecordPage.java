package kz.greetgo.sandbox.controller.model;

import java.util.List;

public class ClientAccountRecordPage {
  public List<ClientAccountRecord> items;
  public int totalItemsCount;

  public ClientAccountRecordPage() { }

  public ClientAccountRecordPage(List<ClientAccountRecord> items, int totalItemsCount) {
    this.items = items;
    this.totalItemsCount = totalItemsCount;
  }
}
