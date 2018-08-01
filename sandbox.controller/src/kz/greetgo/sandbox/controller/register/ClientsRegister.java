package kz.greetgo.sandbox.controller.register;

import com.itextpdf.text.DocumentException;
import kz.greetgo.mvc.interfaces.BinResponse;
import kz.greetgo.sandbox.controller.model.*;

import java.io.IOException;
import java.util.List;

public interface ClientsRegister {
  List<Charm> getCharms();

  ClientRecordsListPage getClientRecords(QueryFilter queryFilter);

  ClientDetail getClientDetailsById(int clientId);

  ClientRecord addClientToSave(ClientToSave clientToSave);

  ClientRecord editClientToSave(ClientToSave client);

  void removeClientById(int clientId);

  String generateReport(ReportType reportType, QueryFilter filter, String token) throws IOException, DocumentException;

  void downloadReport(String id, BinResponse binResponse) throws IOException;
}
