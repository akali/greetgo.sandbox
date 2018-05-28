package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.account.AccountRegister;

import java.util.List;

public class AccountRegisterImpl implements AccountRegister {

  @Override
  public ClientAccountRecordPage getClientAccountRecordPage(TableRequestDetails requestDetails) {
    return null;
  }

  @Override
  public ClientAccountRecord getAccountInfo(int clientId) {
    return null;
  }

  @Override
  public List<ClientAccountRecord> filter(List<ClientAccountRecord> list, String filterValue) {
    return null;
  }

  @Override
  public List<ClientAccountRecord> sort(List<ClientAccountRecord> list, SortColumn column, SortDirection direction) {
    return null;
  }

  @Override
  public List<ClientAccountRecord> paginate(List<ClientAccountRecord> list, int pageIndex, int pageSize) {
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
