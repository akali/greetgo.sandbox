package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.*;

import java.util.List;

public interface ClientsRegister {
    List<Charm> getCharms();
    TableResponse getClientRecords(QueryFilter queryFilter);
    ClientDetail getClientDetailsById(int clientId);

    ClientRecord addClientToSave(ClientToSave clientToSave);
    ClientRecord editClientToSave(ClientToSave client);

    void removeClientById(int clientId);
}
