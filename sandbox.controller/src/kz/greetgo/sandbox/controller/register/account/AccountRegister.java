package kz.greetgo.sandbox.controller.register.account;

import kz.greetgo.sandbox.controller.model.*;

import java.util.List;

public interface AccountRegister {
  ClientAccountRecordPage getClientAccountRecordPage(TableRequestDetails requestDetails);

  ClientAccountRecord getClientAccountRecord(int clientId);

  List<ClientAccountRecord> filter(List<ClientAccountRecord> list, String filterValue);

  List<ClientAccountRecord> sort(List<ClientAccountRecord> list, SortColumn column, SortDirection direction);

  List<ClientAccountRecord> paginate(List<ClientAccountRecord> list, int pageIndex, int pageSize);

  float getMinAccBalance(int clientId);

  float getMaxAccBalance(int clientId);

  float getTotalAccBalance(int clientId);
}
