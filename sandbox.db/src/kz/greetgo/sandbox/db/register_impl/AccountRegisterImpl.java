package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.account.AccountRegister;

import java.util.List;

public class AccountRegisterImpl implements AccountRegister {

  @Override
  public ClientAccountInfoPage getClientAccountInfo(TableRequestDetails requestDetails) {
    return null;
  }

  @Override
  public ClientAccountInfo getAccountInfo(int clientId) {
    return null;
  }

  @Override
  public List<ClientAccountInfo> filter(List<ClientAccountInfo> list, String filterValue) {
    return null;
  }

  @Override
  public List<ClientAccountInfo> sort(List<ClientAccountInfo> list, SortColumn column, SortDirection direction) {
    return null;
  }

  @Override
  public List<ClientAccountInfo> paginate(List<ClientAccountInfo> list, int pageIndex, int pageSize) {
    return null;
  }

  @Override
  public float getMinAccBalance(int clientId) {
    return 0;
  }

  @Override
  public float getMaxAccBalance(int clientId) {
    return 0;
  }

  @Override
  public float getTotalAccBalance(int clientId) {
    return 0;
  }

  @Override
  public List<Account> getClientAccounts(int clientId) {
    return null;
  }
}
