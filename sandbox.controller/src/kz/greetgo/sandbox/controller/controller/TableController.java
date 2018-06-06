package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ParSession;
import kz.greetgo.mvc.annotations.ToJson;
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
    @Mapping("/get")
    public List<ClientToSave> getTable(@ParSession("personId") String personId) {
        return tableRegister.get().getTable(personId);
    }

    @ToJson
    @Mapping("/change")
    public int changeClient(@Par("personId") String personId, @Par("clientId") int clientId) {
        return tableRegister.get().changeClient(personId, clientId);
    }

    @ToJson
    @Mapping("/add")
    public int addClient(@Par("personId") String personId, @Par("client") ClientToSave client) {
        return tableRegister.get().addClient(personId, client);
    }
}
