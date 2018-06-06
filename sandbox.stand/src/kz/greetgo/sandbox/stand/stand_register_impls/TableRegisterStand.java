package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.TableRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Bean
public class TableRegisterStand implements TableRegister {
    public BeanGetter<StandDb> standDb;

    private Client getClient(int clientId) {
        return standDb.get().clientStorage.get(String.valueOf(clientId));
    }

    @Override
    public List<Charm> getCharms() {
        List<Charm> list = new ArrayList<>();
        Map<String, Charm> map = standDb.get().charmStorage;
        for (String key : map.keySet())
            list.add(map.get(key));
        return list;
    }

    private List<ClientAccount> getClientAccounts(int id) {
        Map<String, ClientAccount> map = standDb.get().accountStorage;
        return map.values().stream()
                .filter(clientAccount -> clientAccount.id == id)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClientRecord> getRecordTable(int start, int offset) {
        List<ClientRecord> list = new ArrayList<>();

        Map<String, Client> map = standDb.get().clientStorage;

        for (String key : map.keySet()) {
            Client client = map.get(key);
            ClientRecord result = getClientRecord(client.id);
            list.add(result);
        }

        return list.stream().skip(start).limit(offset).collect(Collectors.toList());
    }

    public static int calculateAge(long birthDateTs, long currentDateTs) {
        if ((birthDateTs != 0) && (currentDateTs != 0)) {
            LocalDate birthDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(birthDateTs),
                    TimeZone.getDefault().toZoneId()).toLocalDate();
            LocalDate currentDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(currentDateTs),
                    TimeZone.getDefault().toZoneId()).toLocalDate();

            return Period.between(birthDate, currentDate).getYears();
        } else {
            return 0;
        }
    }

    @Override
    public ClientDetail getClientDetail(int clientId) {
        Client client = getClient(clientId);

        ClientDetail clientDetail = new ClientDetail();
        clientDetail.id = client.id;
        clientDetail.name = client.name;
        clientDetail.surname = client.surname;
        clientDetail.patronymic = client.patronymic;
        clientDetail.gender = client.gender;
        clientDetail.birthDate = client.birthDate;
        standDb.get().addressStorage.values().stream()
                .filter(clientAddress -> clientAddress.client == client.id)
                .forEach(clientAddress -> {
                    if (clientAddress.type == AddressType.REG)
                        clientDetail.regAddress = clientAddress;
                    else
                        clientDetail.factAddress = clientAddress;
                });

        clientDetail.phones = standDb.get().phoneStorage.values().stream()
                .filter(clientPhone -> clientPhone.client == clientId)
                .collect(Collectors.toList());

        clientDetail.charms = new ArrayList<>(standDb.get().charmStorage.values());

        return clientDetail;
    }

    @Override
    public ClientRecord addClient(ClientToSave clientToSave) {
        System.err.println(clientToSave);
        if (!verify(clientToSave))
            throw new RuntimeException("Incorrect data");

        Client client = new Client(); // getClient(clientToSave.id);
        client.id = ++standDb.get().clientId;
        client.name = clientToSave.name;
        client.birthDate = clientToSave.birthDate;
        client.gender = clientToSave.gender;
        client.patronymic = clientToSave.patronymic;
        client.surname = clientToSave.surname;
        client.charm = clientToSave.charm;

        ClientAddress reg = clientToSave.reg;
        reg.client = client.id;
        standDb.get().addressStorage.put(reg.getId(), reg);

        ClientAddress fact = clientToSave.fact;
        if (fact != null) {
            fact.client = client.id;
            standDb.get().addressStorage.put(fact.getId(), fact);
        }

        standDb.get().clientStorage.put(String.valueOf(client.id), client);

        return getClientRecord(client.id);
    }

    private ClientRecord getClientRecord(int id) {
        Client client = getClient(id);
        ClientRecord result = new ClientRecord();
        result.id = client.id;
        result.name = client.name;
        result.surname = client.surname;
        result.patronymic = client.patronymic;
        Charm charm = (Charm) getCharms().stream().filter(ch -> ch.id == client.charm).toArray()[0];
        result.charm = charm.name;
        result.total = 0;
        List<ClientAccount> accounts = getClientAccounts(client.id);
        if (accounts != null && !accounts.isEmpty()) {
            accounts.forEach(clientAccount -> result.total += clientAccount.money);
            result.min = accounts.stream().min((a, b) -> Float.compare(a.money, b.money)).get().money;
            result.max = accounts.stream().max((a, b) -> Float.compare(a.money, b.money)).get().money;
        }
        result.age = calculateAge(client.birthDate, new Date().getTime());
        return result;
    }

    private boolean verify(ClientToSave clientToSave) {
        if (clientToSave == null) return false;
        if (clientToSave.name == null || clientToSave.name.isEmpty()) return false;
        if (clientToSave.surname == null || clientToSave.surname.isEmpty()) return false;
        if (clientToSave.patronymic == null || clientToSave.patronymic.isEmpty()) return false;
        if (clientToSave.charm > getCharms().size()) return false;
        if (clientToSave.fact == null) return false;
        if (clientToSave.phones.stream().noneMatch(clientPhone -> clientPhone.type == PhoneType.MOBILE)) return false;
        return true;
    }

    @Override
    public ClientRecord editClient(ClientToSave clientToSave) {
        // System.err.println(clientToSave);
        if (!verify(clientToSave))
            throw new RuntimeException("Incorrect data");

        Client client = getClient(clientToSave.id);
        client.name = clientToSave.name;
        client.birthDate = clientToSave.birthDate;
        client.gender = clientToSave.gender;
        client.patronymic = clientToSave.patronymic;
        client.charm = clientToSave.charm;
        client.surname = clientToSave.surname;

        ClientAddress fact = standDb.get().addressStorage.get(clientToSave.fact.getId());
        ClientAddress reg = standDb.get().addressStorage.get(clientToSave.reg.getId());

        fact.street = clientToSave.fact.street;
        fact.house = clientToSave.fact.house;
        fact.flat = clientToSave.fact.flat;

        reg.street = clientToSave.reg.street;
        reg.house = clientToSave.reg.house;
        reg.flat = clientToSave.reg.flat;

        return getClientRecord(client.id);
    }

    @Override
    public void removeClient(int clientId) {
        standDb.get().clientStorage.remove(String.valueOf(clientId));
    }
}
