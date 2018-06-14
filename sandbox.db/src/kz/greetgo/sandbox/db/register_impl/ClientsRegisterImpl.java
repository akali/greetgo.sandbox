package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.AuthRegister;
import kz.greetgo.sandbox.controller.register.TableRegister;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

@Bean
public class ClientsRegisterImpl implements TableRegister {

  @Override
  public List<Charm> getCharms() {
    throw new NotImplementedException();
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
    throw new NotImplementedException();
  }
}
