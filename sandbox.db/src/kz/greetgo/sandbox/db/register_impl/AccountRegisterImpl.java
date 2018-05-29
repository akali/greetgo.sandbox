package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.NoAccount;
import kz.greetgo.sandbox.controller.errors.NotFound;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.account.AccountRegister;
import kz.greetgo.sandbox.controller.register.client.ClientRegister;
import kz.greetgo.sandbox.db.dao.AccountDao;
import kz.greetgo.sandbox.db.dao.CharmDao;
import kz.greetgo.sandbox.db.dao.ClientDao;
import kz.greetgo.sandbox.db.util.YearDifference;

import java.time.Year;
import java.util.List;

@Bean
public class AccountRegisterImpl implements AccountRegister {

  public BeanGetter<ClientDao> clientDao;
  public BeanGetter<AccountDao> accountDao;
  public BeanGetter<CharmDao> charmDao;

  @Override
  public ClientAccountRecordPage getClientAccountRecordPage(TableRequestDetails requestDetails) {
    return null;
  }

  @Override
  public ClientAccountRecord getClientAccountRecord(int clientId) {
    ClientAccountRecord record = new ClientAccountRecord();

    Client client = clientDao.get().getClientById(clientId);
    if(client == null) throw new NotFound();

    Charm charm = charmDao.get().getCharm(client.charmId);

    record.clientId = client.id;
    record.clientFullName = String.format("%s %s %s", client.surname, client.name, client.patronymic).trim();
    record.clientCharmName = charm.name;
    record.clientAge = YearDifference.calculate(client.birthDate);

    int accountCount = accountDao.get().getAccountCount(clientId);
    if(accountCount == 0) {
      throw new NoAccount(clientId);
    }
    record.minAccBalance = getMinAccBalance(clientId);
    record.maxAccBalance = getMaxAccBalance(clientId);
    record.totalAccBalance = getTotalAccBalance(clientId);

    return record;
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
    return accountDao.get().getMinAccountBalance(clientId);
  }

  @Override
  public float getMaxAccBalance(int clientId) {
    return accountDao.get().getMaxAccountBalance(clientId);
  }

  @Override
  public float getTotalAccBalance(int clientId) {
    return accountDao.get().getTotalAccountBalance(clientId);
  }

  @Override
  public List<Account> getClientAccounts(int clientId) {
    return null;
  }
}
