package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.AuthRegister;
import kz.greetgo.sandbox.controller.register.TableRegister;

import java.util.List;

@Bean
public class TableRegisterImpl implements TableRegister {

  @Override
  public List<Charm> getCharms() {
    return null;
  }

  @Override
  public TableResponse getClientRecords(int start, int offset, String direction, String active, String filter) {
    return null;
  }

  @Override
  public ClientDetail getClientDetailsById(int clientId) {
    return null;
  }

  @Override
  public ClientRecord addClientToSave(ClientToSave clientToSave) {
    return null;
  }

  @Override
  public ClientRecord editClientToSave(ClientToSave client) {
    return null;
  }

  @Override
  public void removeClientById(int clientId) {

  }
}
