package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.*;

import java.util.List;

//TODO: поменяй название интерфейся и соответственно всех классов, которые имплементируют его.
public interface TableRegister {
    List<Charm> getCharms();
    TableResponse getRecordTable(int start, int offset, String direction, String active, String filter);
    ClientDetail getClientDetail(int clientId);

    ClientRecord addClient(ClientToSave clientToSave);
    ClientRecord editClient(ClientToSave client);

    void removeClient(int clientId);
}
