package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.ClientToSave;

import java.util.List;

public interface TableRegister {
    List<ClientToSave> getTable(String personId);
    int changeClient(String personId, int clientId);
    int addClient(String personId, ClientToSave client);
    int removeClient(String personId, int clientId);
}
