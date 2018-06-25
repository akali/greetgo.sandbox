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
import kz.greetgo.util.RND;

import java.io.IOException;
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

  @AsIs
  @Mapping("/generateReport")
  public String generateReport(
    @ParSession("personId") String token,
    @Par("reportType") @Json ReportType reportType,
    @Par("queryFilter") @Json QueryFilter filter) throws IOException, DocumentException {
    System.out.println("Token: " + token);
    return tableRegister.get().generateReport(reportType, filter, token);
  }

  @NoSecurity
  @Mapping("/downloadReport")
  public void downloadReport(@Par("id") String id, BinResponse binResponse) throws IOException, DocumentException {
    tableRegister.get().downloadReport(id, binResponse);
//    tableRegister.get().generateReport(ReportType.XLSX, 1,
//      new QueryFilter(0, 0, "ASC", "name", ""),
//      binResponse);

//    binResponse.setFilename(filename + ".txt");
//    binResponse.setContentType("application/txt");
//    PrintWriter pr = new PrintWriter(binResponse.out());
//    pr.println("Hello, world!");
//    pr.flush();
//    binResponse.flushBuffers();
//    binResponse.out().flush();
  }
}
