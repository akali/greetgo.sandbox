package kz.greetgo.sandbox.controller.register.client;


import kz.greetgo.sandbox.controller.model.*;

public interface ClientRegister {
  ClientDetails getClientDetails(int clientId);

  ClientAccountRecord createNewClient(ClientToSave clientToSave);

  ClientAccountRecord editClient(ClientToSave clientToSave);

  ClientAccountRecordPage deleteClient(int clientId, TableRequestDetails requestDetails);
}
