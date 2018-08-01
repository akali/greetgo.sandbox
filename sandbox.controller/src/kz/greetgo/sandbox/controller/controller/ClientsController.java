package kz.greetgo.sandbox.controller.controller;

import com.itextpdf.text.DocumentException;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.mvc.interfaces.BinResponse;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientsRegister;
import kz.greetgo.sandbox.controller.security.NoSecurity;
import kz.greetgo.sandbox.controller.util.Controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

//TODO: не собирается war. Исправить

@Bean
@Mapping("/clients")
public class ClientsController implements Controller {

  public BeanGetter<ClientsRegister> clientsRegister;

  @ToJson
  @Mapping("/getCharms")
  public List<Charm> getCharms() {
    return clientsRegister.get().getCharms();
  }

  @ToJson
  @Mapping("/getClientRecords")
  public FilteredTable getClientRecords(@Par("queryFilter") @Json QueryFilter queryFilter) throws SQLException {
    return clientsRegister.get().getClientRecords(queryFilter);
  }

  @ToJson
  @Mapping("/getClientDetailsById")
  public ClientDetail getClientDetailsById(@Par("clientId") int clientId) {
    return clientsRegister.get().getClientDetailsById(clientId);
  }

  @ToJson
  @Mapping("/removeClientById")
  public void removeClientById(@Par("clientId") int clientId) {
    clientsRegister.get().removeClientById(clientId);
  }

  @ToJson
  @Mapping("/addClientToSave")
  public ClientRecord addClientToSave(@Par("clientToSave") @Json ClientToSave client) {
    return clientsRegister.get().addClientToSave(client);
  }

  @ToJson
  @Mapping("/editClientToSave")
  public ClientRecord editClientToSave(@Par("clientToSave") @Json ClientToSave clientToSave) {
    return clientsRegister.get().editClientToSave(clientToSave);
  }

  // TODO: 2.03.1. Входные параметры должны быть в одном классе-аргументе;
  @AsIs
  @Mapping("/generateReport")
  public String generateReport(
    @ParSession("personId") String token,
    @Par("reportType") @Json ReportType reportType,
    @Par("queryFilter") @Json QueryFilter filter) throws IOException, DocumentException {
    System.out.println("Token: " + token);
    return clientsRegister.get().generateReport(reportType, filter, token);
  }

  @NoSecurity
  @Mapping("/downloadReport")
  public void downloadReport(@Par("id") String id, BinResponse binResponse) throws IOException {
    clientsRegister.get().downloadReport(id, binResponse);
  }
}
