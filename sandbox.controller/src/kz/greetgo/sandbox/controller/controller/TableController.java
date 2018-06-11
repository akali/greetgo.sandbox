package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.sandbox.controller.model.Charm;
import kz.greetgo.sandbox.controller.model.ClientDetail;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.register.AuthRegister;
import kz.greetgo.sandbox.controller.register.TableRegister;
import kz.greetgo.sandbox.controller.util.Controller;

import java.util.List;

@Bean
@Mapping("/table")
public class TableController implements Controller {
    public BeanGetter<TableRegister> tableRegister;


    @ToJson
    @Mapping("/getCharms")
    public List<Charm> getCharms() {
        return tableRegister.get().getCharms();
    }

    @ToJson
    @Mapping("/get")
    public List<ClientRecord> getRecordTable(@Par("start") int start,
                                             @Par("limit") int limit,
                                             @Par("sort") String direction,
                                             @Par("active") String active) {
        return tableRegister.get().getRecordTable(start, limit, direction, active);
    }

    @ToJson
    @Mapping("/detail")
    public ClientDetail getClientDetail(@Par("clientId") int clientId) {
        return tableRegister.get().getClientDetail(clientId);
    }

    @ToJson
    @Mapping("/remove")
    public void removeClient(@Par("clientId") int clientId) {
        tableRegister.get().removeClient(clientId);
    }

    @ToJson
    @Mapping("/add")
    public ClientRecord addClient(@Par("clientToSave") @Json ClientToSave client) {
        return tableRegister.get().addClient(client);
    }

    @ToJson
    @Mapping("/edit")
    public ClientRecord editClient(@Par("clientToSave") @Json ClientToSave clientToSave) {
        return tableRegister.get().editClient(clientToSave);
    }
}
