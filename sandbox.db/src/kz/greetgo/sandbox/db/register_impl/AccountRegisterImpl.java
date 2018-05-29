package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.InvalidRequestDetails;
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Bean
public class AccountRegisterImpl implements AccountRegister {

  public BeanGetter<ClientDao> clientDao;
  public BeanGetter<AccountDao> accountDao;
  public BeanGetter<CharmDao> charmDao;
  public BeanGetter<JdbcSandbox> jdbc;

  @Override
  public ClientAccountRecordPage getClientAccountRecordPage(TableRequestDetails requestDetails) {

    if(requestDetails.pageSize <= 0 || requestDetails.pageIndex < 0) throw new InvalidRequestDetails();

    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append(
      "SELECT\n" +
        "Clients.id, Clients.surname, Clients.name, COALESCE(Clients.patronymic, '') as patronymic, Clients.gender,\n" +
        "DATE_PART('year', AGE(Clients.birthDate)) as age, Charms.name as charmName,\n" +
        "MIN(Accounts.money) minAccBalance, MAX(Accounts.money) maxAccBalance, SUM(Accounts.money) totalAccBalance,\n" +
        "count(*) OVER() AS totalItemsCount\n" +
      "FROM Clients\n" +
        "JOIN Accounts on Clients.id = Accounts.clientId\n" +
        "JOIN Charms on Clients.charmId = Charms.id");

    if(requestDetails.filter != null && !requestDetails.filter.toLowerCase().trim().isEmpty()) {

      String f = requestDetails.filter.toLowerCase().replaceAll("\\s+", "");
      queryBuilder.append(" WHERE LOWER(Clients.surname || Clients.name || COALESCE(Clients.patronymic, '')) LIKE '%");
      queryBuilder.append(f);
      queryBuilder.append("%'");
    }

    queryBuilder.append(" GROUP BY Clients.id, charmName ");

    if(requestDetails.sortBy != SortColumn.NONE) {
      queryBuilder.append(" ORDER BY ");

      switch (requestDetails.sortBy) {
        case FIO:
          queryBuilder.append("Clients.surname, Clients.name, Clients.patronymic ");
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
    } else {
      queryBuilder.append("ORDER BY Clients.id ASC");
    }

    queryBuilder.append(" LIMIT ");
    queryBuilder.append(requestDetails.pageSize);
    queryBuilder.append(" OFFSET ");
    queryBuilder.append(requestDetails.pageSize * requestDetails.pageIndex);
    queryBuilder.append(";");

    ClientAccountRecordPage page = new ClientAccountRecordPage();
    page.items = new ArrayList<>();

    System.out.println(queryBuilder.toString());

    jdbc.get().execute((connection)->{

      try(PreparedStatement ps = connection.prepareStatement(queryBuilder.toString())) {

        try(ResultSet rs = ps.executeQuery()) {
          while (rs.next()) {
            page.totalItemsCount = rs.getInt("totalItemsCount");

            ClientAccountRecord record = new ClientAccountRecord();
            record.clientId = rs.getInt("id");
            record.clientFullName = (rs.getString("surname")+" "
              +rs.getString("name")+" "+rs.getString("patronymic")).trim();
            record.clientAge = rs.getInt("age");
            record.clientCharmName = rs.getString("charmName");
            record.minAccBalance = rs.getFloat("minAccBalance");
            record.maxAccBalance = rs.getFloat("maxAccBalance");
            record.totalAccBalance = rs.getFloat("totalAccBalance");

            page.items.add(record);
          }
        }
      }

      return null;
    });

    return page;
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
