package kz.greetgo.sandbox.controller.register.account;

import kz.greetgo.sandbox.controller.model.*;

import java.util.List;

public interface AccountRegister {
  ClientAccountInfoPage getClientAccountInfo(TableRequestDetails requestDetails);

  ClientAccountInfo getAccountInfo(int clientId);

  List<ClientAccountInfo> filter(List<ClientAccountInfo> list, String filterValue);

  List<ClientAccountInfo> sort(List<ClientAccountInfo> list, SortColumn column, SortDirection direction);

  List<ClientAccountInfo> paginate(List<ClientAccountInfo> list, int pageIndex, int pageSize);

  float getMinAccBalance(int clientId);

  float getMaxAccBalance(int clientId);

  float getTotalAccBalance(int clientId);

  List<Account> getClientAccounts(int clientId);
}
