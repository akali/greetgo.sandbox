package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientsRegister;
import kz.greetgo.sandbox.db.dao.ClientsDao;
import kz.greetgo.sandbox.db.util.DBHelper;
import liquibase.exception.DatabaseException;
import org.apache.ibatis.jdbc.SQL;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Bean
public class ClientsRegisterImpl implements ClientsRegister {

  public BeanGetter<ClientsDao> clientsDao;

  @Override
  public List<Charm> getCharms() {
    try {
      return new DBHelper<List<Charm>>().run(connection -> {
        ResultSet rs = connection.prepareStatement("select * from charm").executeQuery();
        List<Charm> charms = new ArrayList<>();
        while (rs.next()) {
          charms.add(new Charm(rs.getInt("id"), rs.getString("name"),
            rs.getString("description"), rs.getFloat("energy")));
        }
        return charms;
      });
    } catch (DatabaseException | SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public TableResponse getClientRecords(QueryFilter queryFilter) {
    try {
      return new DBHelper<TableResponse>().run(connection -> GetClientRecords.instance().run(connection, queryFilter));
    } catch (DatabaseException | SQLException e) {
      e.printStackTrace();
      return null;
    }
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
