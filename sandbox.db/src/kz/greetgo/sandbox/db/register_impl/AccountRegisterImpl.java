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
import kz.greetgo.sandbox.db.util.JdbcSandbox;
import kz.greetgo.sandbox.db.util.YearDifference;

import java.time.Year;
import java.util.List;

@Bean
public class AccountRegisterImpl implements AccountRegister {

  public BeanGetter<ClientDao> clientDao;
  public BeanGetter<AccountDao> accountDao;
  public BeanGetter<CharmDao> charmDao;
  public BeanGetter<JdbcSandbox> jdbc;

  @Override
  public ClientAccountRecordPage getClientAccountRecordPage(TableRequestDetails requestDetails) {

    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append(
      "SELECT " +
        "Clients.id, Clients.surname, Clients.name, Clients.patronymic, Clients.gender, " +
        "DATE_PART('year', AGE(Clients.birthDate)) as age, Charms.name as charmName, " +
      "MIN(Accounts.money) minAccBalance, MAX(Accounts.money) maxAccBalance, SUM(Accounts.money) totalAccBalance " +
      "FROM Clients, Charms, Accounts");

    String f = requestDetails.filter.toLowerCase().trim();
    if(!f.isEmpty()) {
      queryBuilder.append(" WHERE name LIKE %");
      queryBuilder.append(f);
      queryBuilder.append(" OR name LIKE %");
      queryBuilder.append(f);
      queryBuilder.append(" OR"+ "name LIKE %");
      queryBuilder.append(f);
    }

    queryBuilder.append(" GROUP BY Clients.id, charmName ");

    if(requestDetails.sortBy != SortColumn.NONE) {
      queryBuilder.append(" ORDER BY ");

      switch (requestDetails.sortBy) {
        case FIO:
          queryBuilder.append("Clients.name, Clients.surname, Clients.patronymic ");
          break;
        case AGE:
          queryBuilder.append("age ");
          break;
        case MIN:
          queryBuilder.append("minAccBalance ");
          break;
        case MAX:
          queryBuilder.append("maxAccBalance ");
          break;
        case TOTAL:
          queryBuilder.append("totalAccBalance ");
          break;
      }
      queryBuilder.append(requestDetails.sortDirection);
    }

    queryBuilder.append(" LIMIT ");
    queryBuilder.append(requestDetails.pageSize);
    queryBuilder.append(" OFFSET ");
    queryBuilder.append(requestDetails.pageSize * requestDetails.pageIndex);
    queryBuilder.append(";");

    //  jdbc.get().execute((connection)->{
    //
    //    String testSql = "select * from client;";
    //
    //    try(PreparedStatement ps = connection.prepareStatement(testSql)) {
    //
    //    try(ResultSet rs = ps.executeQuery()) {
    //
    //    while (rs.next()){
    //    Client client = new Client();
    //    client.name = rs.getString("name");
    //    }
    //    }
    //
    //    }
    //
    //    return null;
    //    });



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
    int accountCount = accountDao.get().getAccountCount(clientId);
    if(accountCount == 0) {
      throw new NoAccount(clientId);
    }

    return accountDao.get().getMinAccountBalance(clientId);
  }

  @Override
  public float getMaxAccBalance(int clientId) {
    int accountCount = accountDao.get().getAccountCount(clientId);
    if(accountCount == 0) {
      throw new NoAccount(clientId);
    }

    return accountDao.get().getMaxAccountBalance(clientId);
  }

  @Override
  public float getTotalAccBalance(int clientId) {
    int accountCount = accountDao.get().getAccountCount(clientId);
    if(accountCount == 0) {
      throw new NoAccount(clientId);
    }

    return accountDao.get().getTotalAccountBalance(clientId);
  }

}
