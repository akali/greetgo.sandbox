package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientsRegister;
import kz.greetgo.sandbox.db.dao.ClientsDao;
import kz.greetgo.sandbox.db.util.DBHelper;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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
//    return clientsDao.get().getCharms();
  }

  @Override
  public TableResponse getClientRecords(QueryFilter queryFilter) {
    throw new NotImplementedException();
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
