package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.InvalidRequestDetails;
import kz.greetgo.sandbox.controller.errors.NotFound;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.account.AccountRegister;
import kz.greetgo.sandbox.db.dao.AccountDao;
import kz.greetgo.sandbox.db.dao.CharmDao;
import kz.greetgo.sandbox.db.dao.ClientDao;
import kz.greetgo.sandbox.db.util.JdbcSandbox;
import kz.greetgo.sandbox.db.util.YearDifference;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

@Bean
public class AccountRegisterImpl implements AccountRegister {

  public BeanGetter<ClientDao> clientDao;
  public BeanGetter<AccountDao> accountDao;
  public BeanGetter<CharmDao> charmDao;
  public BeanGetter<JdbcSandbox> jdbc;

  @Override
  public ClientAccountRecordPage getClientAccountRecordPage(TableRequestDetails requestDetails) {

    if (requestDetails.pageSize <= 0 || requestDetails.pageIndex < 0) throw new InvalidRequestDetails();

    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append(initQueryHeader());

    if (requestDetails.filter != null && !requestDetails.filter.toLowerCase().trim().isEmpty()) {
      queryBuilder.append(" AND LOWER(clients.surname || clients.name || COALESCE(clients.patronymic, '')) LIKE ?");
    }

    queryBuilder.append(" GROUP BY clients.id, charmName ");

    if (requestDetails.sortBy != SortColumn.NONE) {
      queryBuilder.append(" ORDER BY ");

      switch (requestDetails.sortBy) {
        case FIO:
          queryBuilder.append("clients.surname, clients.name, clients.patronymic ");
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
      queryBuilder.append("ORDER BY clients.id ASC");
    }

    queryBuilder.append(" LIMIT ");
    queryBuilder.append(requestDetails.pageSize);
    queryBuilder.append(" OFFSET ");
    queryBuilder.append(requestDetails.pageSize * requestDetails.pageIndex);
    queryBuilder.append(";");

    ClientAccountRecordPage page = new ClientAccountRecordPage();
    page.items = new ArrayList<>();

    System.out.println(queryBuilder.toString());

    jdbc.get().execute((connection) -> {

      try (PreparedStatement ps = connection.prepareStatement(queryBuilder.toString())) {

        if(requestDetails.filter != null && !requestDetails.filter.toLowerCase().trim().isEmpty()) {
          String filter = requestDetails.filter.toLowerCase().replaceAll("\\s+", "");
          ps.setString(1, "%" + filter + "%");
        }

        try (ResultSet rs = ps.executeQuery()) {
          while (rs.next()) {
            page.totalItemsCount = rs.getInt("totalItemsCount");

            ClientAccountRecord record = new ClientAccountRecord();
            record.clientId = rs.getInt("id");
            record.clientFullName = (rs.getString("surname") + " "
              + rs.getString("name") + " " + rs.getString("patronymic")).trim();
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

  private String initQueryHeader() {
    return
      "WITH joined_clients_accounts AS (\n" +
      "    SELECT\n" +
      "      c.id client_id,\n" +
      "      a.id account_id,\n" +
      "      a.money money\n" +
      "    FROM clients c\n" +
      "      LEFT OUTER JOIN accounts a ON c.id = a.clientid\n" +
      "    WHERE c.isactive = TRUE AND a.isactive = TRUE\n" +
      "    GROUP BY c.id, a.id\n" +
      "    ORDER BY c.id\n" +
      ")\n" +
      "SELECT\n" +
      "  clients.id,\n" +
      "  clients.surname,\n" +
      "  clients.name,\n" +
      "  COALESCE(clients.patronymic, '') as patronymic,\n" +
      "  clients.gender,\n" +
      "  DATE_PART('year', AGE(clients.birthDate)) as age,\n" +
      "  charms.name as charmName,\n" +
      "  count(*) OVER() AS totalItemsCount,\n" +
      "  coalesce(MIN(money), 0) as minAccBalance,\n" +
      "  coalesce(MAX(money), 0) as maxAccBalance,\n" +
      "  coalesce(SUM(money), 0) as totalAccBalance\n" +
      "FROM clients\n" +
      "  LEFT OUTER JOIN joined_clients_accounts ON client_id = clients.id\n" +
      "  JOIN charms on clients.charmid = charms.id\n" +
      "WHERE clients.isactive = true";
  }

  @Override
  public ClientAccountRecord getClientAccountRecord(int clientId) {
    ClientAccountRecord record = new ClientAccountRecord();

    Client client = clientDao.get().getClientById(clientId);
    if (client == null) throw new NotFound();

    Charm charm = charmDao.get().getCharm(client.charmId);

    record.clientId = client.id;

    record.clientFullName = String.format("%s %s", client.surname, client.name).trim();
    if(client.patronymic != null) record.clientFullName += " " + client.patronymic;

    record.clientCharmName = charm.name;
    record.clientAge = YearDifference.calculate(client.birthDate);

    record.minAccBalance = getMinAccBalance(clientId);
    record.maxAccBalance = getMaxAccBalance(clientId);
    record.totalAccBalance = getTotalAccBalance(clientId);

    return record;
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

}
