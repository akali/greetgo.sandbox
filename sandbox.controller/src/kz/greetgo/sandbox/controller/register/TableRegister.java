package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.Charm;
import kz.greetgo.sandbox.controller.model.ClientDetail;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToSave;

import java.util.List;

public interface TableRegister {
    List<Charm> getCharms();
    List<ClientRecord> getRecordTable(int start, int offset, String direction, String active, String filter);
    ClientDetail getClientDetail(int clientId);

    ClientRecord addClient(ClientToSave clientToSave);
    ClientRecord editClient(ClientToSave client);

    void removeClient(int clientId);
}
