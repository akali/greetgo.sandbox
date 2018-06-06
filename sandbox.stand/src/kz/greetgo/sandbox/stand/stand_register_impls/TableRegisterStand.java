package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.model.CharacterType;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.model.GenderType;
import kz.greetgo.sandbox.controller.register.TableRegister;

import java.util.ArrayList;
import java.util.List;

@Bean
public class TableRegisterStand implements TableRegister {
//    public BeanGetter<>

    @Override
    public List<ClientToSave> getTable(String personId) {
        ArrayList<ClientToSave> test = new ArrayList<>();
        ClientToSave testClient = new ClientToSave();

        testClient.name = "Aisultan";
        testClient.surname = "Kali";
        testClient.patronymic = "AA";
        testClient.total = 520;
        testClient.max = 520;
        testClient.min = 50;
        testClient.character = CharacterType.CHOLERIC;
        testClient.gender = GenderType.MALE;
        testClient.age = 20;
        testClient.factAddress = "Akha";
        testClient.regAddress = "AKHA";

        test.add(testClient);
        test.add(testClient);
        test.add(testClient);
        test.add(testClient);
        return test;
    }

    @Override
    public int changeClient(String personId, int clientId) {
        return 0;
    }

    @Override
    public int addClient(String personId, ClientToSave client) {
        return 0;
    }

    @Override
    public int removeClient(String personId, int clientId) {
        return 0;
    }
}
