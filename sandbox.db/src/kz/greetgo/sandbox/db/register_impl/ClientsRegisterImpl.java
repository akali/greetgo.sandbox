package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientsRegister;
import kz.greetgo.sandbox.db.dao.ClientsDao;
import kz.greetgo.sandbox.db.util.DBHelper;
import org.apache.ibatis.jdbc.SQL;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Bean
public class ClientsRegisterImpl implements ClientsRegister {

  public BeanGetter<ClientsDao> clientsDao;

  @Override
  public List<Charm> getCharms() {
    List<Charm> charms = new ArrayList<>();
    DBHelper.run(connection -> {
      ResultSet rs = connection.prepareStatement("select * from charm").executeQuery();
      while (rs.next()) {
        charms.add(new Charm(rs.getInt("id"), rs.getString("name"),
          rs.getString("description"), rs.getFloat("energy")));
      }
    });
    return charms;
  }

  @Override
  public TableResponse getClientRecords(QueryFilter queryFilter) {
    AtomicReference<TableResponse> tableResponse = new AtomicReference<>();
    DBHelper.run(connection -> {
      PreparedStatement statement = connection.prepareStatement(
        new SQL()
          .SELECT(
            "count(*) over (partition by 1) total_rows",
            "client.id as id",
            "client.name as name",
            "client.surname as surname",
            "client.patronymic as patronymic",
            "(extract(year from age(birth_date))) as age",
            "max(c2.money) as max",
            "min(c2.money) as min",
            "sum(c2.money) as total",
            "c3.name as charm "
            )
          .FROM("client")
          .JOIN("ClientAccount c2 on client.id = c2.client", "charm c3 on client.charm = c3.id")
          .GROUP_BY("client.id", "c3.name")
          .WHERE("client.name like ?", "client.surname like ?", "client.patronymic like ?")
          .ORDER_BY("?", "?")
        .toString()
          .concat(" LIMIT ? OFFSET ?")
      );

      System.out.println(statement.toString());

      statement.setString(1, "%" + queryFilter.filter + "%");
      statement.setString(2, "%" + queryFilter.filter + "%");
      statement.setString(3, "%" + queryFilter.filter + "%");
      statement.setString(4, queryFilter.active);
      statement.setString(5, queryFilter.direction);
      statement.setInt(6, queryFilter.limit);
      statement.setInt(7, queryFilter.start);

      ResultSet result = statement.executeQuery();
      tableResponse.set(new TableResponse());
      while (result.next()) {
        tableResponse.get().list.add(new ClientRecord(result.getInt("id"),
          result.getString("name"),
          result.getString("surname"),
          result.getString("patronymic"),
          result.getFloat("total"),
          result.getFloat("max"),
          result.getFloat("min"),
          result.getString("charm"),
          result.getInt("age")
          ));
      }
      tableResponse.get().size = tableResponse.get().list.size();
    });

    System.out.println(queryFilter);

    return tableResponse.get();
  }

  @Override
  public ClientDetail getClientDetailsById(int clientId) {
    throw new NotImplementedException();
  }

  @Override
  public ClientRecord addClientToSave(ClientToSave clientToSave) {
    throw new NotImplementedException();
  }

  @Override
  public ClientRecord editClientToSave(ClientToSave client) {
    throw new NotImplementedException();
  }

  @Override
  public void removeClientById(int clientId) {
  }
}
