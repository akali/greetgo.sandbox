package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientsRegister;
import kz.greetgo.sandbox.controller.util.Controller;

import java.util.List;

//TODO(DONE): переименую контроллер. Непонятно за что контроллер отвечает
//TODO(DONE): переименую маппинг соответственно.
@Bean
@Mapping("/clients")
public class ClientsController implements Controller {
    public BeanGetter<ClientsRegister> tableRegister;

    @ToJson
    //TODO(DONE): если здесь поток приостановить на 5 сек, то форма добавления отрабатывает неверно.
    @Mapping("/getCharms")
    public List<Charm> getCharms() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return tableRegister.get().getCharms();
    }

    @ToJson
    //TODO(DONE): подправь наименование маппинга метода.
    //TODO(DONE): по наименованию не понятно, рекорд чего ты достаешь
    //TODO(DONE): если параметров больше 3-х, передавай одним объектом.
    // А то код получается громоздким.
    @Mapping("/getClientRecords")
    public TableResponse getClientRecords(@Par("queryFilter") @Json QueryFilter queryFilter) {
        return tableRegister.get().getClientRecords(queryFilter);
    }

    @ToJson
    //TODO(DONE): подправь наименование маппинга метода.
    //TODO(DONE): по наименованию не понятно, detail чего ты достаешь
    //TODO(DONE): используй detail во множественном числе -> details
    @Mapping("/getClientDetailsById")
    public ClientDetail getClientDetailsById(@Par("clientId") int clientId) {
        return tableRegister.get().getClientDetailsById(clientId);
    }

    @ToJson
    //TODO(DONE): подправь наименование маппинга метода.
    //TODO(DONE): по наименованию не понятно, что удаляешь
    @Mapping("/removeClientById")
    public void removeClientById(@Par("clientId") int clientId) {
        tableRegister.get().removeClientById(clientId);
    }

    @ToJson
    @Mapping("/addClientToSave")
    //TODO(DONE): подправь наименование маппинга метода.
    //TODO(DONE): по наименованию не понятно, что добавляешь
    public ClientRecord addClientToSave(@Par("clientToSave") @Json ClientToSave client) {
        return tableRegister.get().addClientToSave(client);
    }

    @ToJson
    //TODO(DONE): подправь наименование маппинга метода.
    //TODO(DONE): по наименованию не понятно, что редактируешь
    @Mapping("/editClientToSave")
    public ClientRecord editClientToSave(@Par("clientToSave") @Json ClientToSave clientToSave) {
        return tableRegister.get().editClientToSave(clientToSave);
    }
}
