package kz.greetgo.sandbox.controller.register;

import com.itextpdf.text.DocumentException;
import kz.greetgo.mvc.interfaces.BinResponse;
import kz.greetgo.sandbox.controller.model.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

//TODO(DONE): поменяй название интерфейся и соответственно всех классов, которые имплементируют его.
public interface ClientsRegister {
  List<Charm> getCharms();

  TableResponse getClientRecords(QueryFilter queryFilter);

  ClientDetail getClientDetailsById(int clientId);

  ClientRecord addClientToSave(ClientToSave clientToSave);

  ClientRecord editClientToSave(ClientToSave client);

  void removeClientById(int clientId);

  void generateReport(FileType fileType, int authorId, QueryFilter filter, BinResponse binResponse) throws IOException, DocumentException;
}
