package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Json;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientsRegister;
import kz.greetgo.sandbox.controller.util.Controller;
import kz.greetgo.util.RND;

import java.util.List;

@Bean
@Mapping("/clients")
public class ClientsController implements Controller {
  //TODO: Название переменной соответственно нужно поменять
  public BeanGetter<ClientsRegister> tableRegister;

  @ToJson
  @Mapping("/getCharms")
  public List<Charm> getCharms() throws InterruptedException {
    return tableRegister.get().getCharms();
  }

  @ToJson
  @Mapping("/getClientRecords")
  public TableResponse getClientRecords(@Par("queryFilter") @Json QueryFilter queryFilter) {
    return tableRegister.get().getClientRecords(queryFilter);
  }

  @ToJson
  @Mapping("/getClientDetailsById")
  public ClientDetail getClientDetailsById(@Par("clientId") int clientId) {
    return tableRegister.get().getClientDetailsById(clientId);
  }

  @ToJson
  @Mapping("/removeClientById")
  public void removeClientById(@Par("clientId") int clientId) {
    tableRegister.get().removeClientById(clientId);
  }

  @ToJson
  @Mapping("/addClientToSave")
  public ClientRecord addClientToSave(@Par("clientToSave") @Json ClientToSave client) {
    return tableRegister.get().addClientToSave(client);
  }

  @ToJson
  @Mapping("/editClientToSave")
  public ClientRecord editClientToSave(@Par("clientToSave") @Json ClientToSave clientToSave) {
    return tableRegister.get().editClientToSave(clientToSave);
  }
}
