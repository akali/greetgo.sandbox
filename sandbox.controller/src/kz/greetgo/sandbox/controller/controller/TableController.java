package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.AuthRegister;
import kz.greetgo.sandbox.controller.register.TableRegister;
import kz.greetgo.sandbox.controller.util.Controller;

import java.util.List;

//TODO: переименую контроллер. Непонятно за что контроллер отвечает
//TODO: переименую маппинг соответственно.
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
    //TODO: подправь наименование маппинга метода.
    //TODO: по наименованию не понятно, рекорд чего ты достаешь
    //TODO: если параметров больше 3-х, передавай одним объектом.
    // А то код получается громоздким.
    @Mapping("/get")
    public TableResponse getRecordTable(@Par("start") int start,
                                        @Par("limit") int limit,
                                        @Par("sort") String direction,
                                        @Par("active") String active,
                                        @Par("filter") String filter) {
        return tableRegister.get().getRecordTable(start, limit, direction, active, filter);
    }

    @ToJson
    //TODO: подправь наименование маппинга метода.
    //TODO: по наименованию не понятно, detail чего ты достаешь
    //TODO: используй detail во множественном числе -> details
    @Mapping("/detail")
    public ClientDetail getClientDetail(@Par("clientId") int clientId) {
        return tableRegister.get().getClientDetail(clientId);
    }

    @ToJson
    //TODO: подправь наименование маппинга метода.
    //TODO: по наименованию не понятно, что удаляешь
    @Mapping("/remove")
    public void removeClient(@Par("clientId") int clientId) {
        tableRegister.get().removeClient(clientId);
    }

    @ToJson
    @Mapping("/add")
    //TODO: подправь наименование маппинга метода.
    //TODO: по наименованию не понятно, что добавляешь
    public ClientRecord addClient(@Par("clientToSave") @Json ClientToSave client) {
        return tableRegister.get().addClient(client);
    }

    @ToJson
    //TODO: подправь наименование маппинга метода.
    //TODO: по наименованию не понятно, что редактируешь
    @Mapping("/edit")
    public ClientRecord editClient(@Par("clientToSave") @Json ClientToSave clientToSave) {
        return tableRegister.get().editClient(clientToSave);
    }
}
