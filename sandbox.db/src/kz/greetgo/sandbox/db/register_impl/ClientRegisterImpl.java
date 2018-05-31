package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.InvalidClientData;
import kz.greetgo.sandbox.controller.errors.NotFound;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.account.AccountRegister;
import kz.greetgo.sandbox.controller.register.charm.CharmRegister;
import kz.greetgo.sandbox.controller.register.client.ClientRegister;
import kz.greetgo.sandbox.db.dao.AddressDao;
import kz.greetgo.sandbox.db.dao.CharmDao;
import kz.greetgo.sandbox.db.dao.ClientDao;
import kz.greetgo.sandbox.db.dao.PhoneDao;
import kz.greetgo.sandbox.db.util.JdbcSandbox;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

@Bean
public class ClientRegisterImpl implements ClientRegister {

  public BeanGetter<ClientDao> clientDao;
  public BeanGetter<CharmDao> charmDao;
  public BeanGetter<AddressDao> addressDao;
  public BeanGetter<PhoneDao> phoneDao;

  public BeanGetter<AccountRegister> accountRegister;
  public BeanGetter<CharmRegister> charmRegister;
  public BeanGetter<JdbcSandbox> jdbc;

  @Override
  public ClientDetails getClientDetails(int clientId) {
    ClientDetails clientDetails = new ClientDetails();

    Client client = clientDao.get().getClientById(clientId);

    if (client == null) throw new NotFound();

    clientDetails.id = clientId;
    clientDetails.name = client.name;
    clientDetails.surname = client.surname;
    clientDetails.patronymic = client.patronymic;
    clientDetails.gender = client.gender;
    clientDetails.birthDate = client.birthDate;
    clientDetails.charmId = client.charmId;
    clientDetails.charmsDictionary = charmDao.get().getAllCharms();
    clientDetails.factAddress = addressDao.get().getAddressByClientIdAndType(clientId, AddressType.FACT);
    clientDetails.regAddress = addressDao.get().getAddressByClientIdAndType(clientId, AddressType.REG);
    clientDetails.phones = phoneDao.get().getClientPhones(client.id);

    return clientDetails;
  }

  @Override
  public ClientAccountRecord createNewClient(ClientToSave clientToSave) {
    if (!isValidClientData(clientToSave)) throw new InvalidClientData("invalid client data");

    Client client = new Client();
    client.name = clientToSave.name.trim();
    client.surname = clientToSave.surname.trim();

    if (clientToSave.patronymic != null) client.patronymic = clientToSave.patronymic.trim();

    client.gender = clientToSave.gender;
    client.birthDate = clientToSave.birthDate;
    client.charmId = clientToSave.charmId;

    jdbc.get().execute((connection) -> {

      String query = "SELECT nextval('client_id_seq');";

      try (PreparedStatement ps = connection.prepareStatement(query)) {
        try (ResultSet rs = ps.executeQuery()) {
          rs.next();
          client.id = rs.getInt("nextval");
        }
      } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException();
      }

      return null;
    });

    clientDao.get().insertClient(client);

    if (clientToSave.factAddress != null) {
      clientToSave.factAddress.clientId = client.id;
      addressDao.get().insertAddress(clientToSave.factAddress);
    }

    clientToSave.regAddress.clientId = client.id;
    addressDao.get().insertAddress(clientToSave.regAddress);

    for (Phone phone : clientToSave.phones) {
      phone.clientId = client.id;
      phoneDao.get().insertPhone(phone);
    }

    return accountRegister.get().getClientAccountRecord(client.id);
  }

  private boolean isValidClientData(ClientToSave clientToSave) {
    if (clientToSave.name == null) {
      throw new InvalidClientData("'name' cannot be a null");
    }

    if (clientToSave.surname == null) {
      throw new InvalidClientData("'surname' cannot be a null");
    }

    if (clientToSave.birthDate == null) {
      throw new InvalidClientData("'birthDate' cannot be a null");
    }

    if (clientToSave.gender == null) {
      throw new InvalidClientData("'gender' cannot be a null");
    }

    if (clientToSave.regAddress == null) {
      throw new InvalidClientData("'regAddress' cannot be a null");

    }

    if (clientToSave.regAddress.street == null || clientToSave.regAddress.house == null) {
      throw new InvalidClientData("'street' and 'house' of regAddress cannot be a null");
    }

    if (clientToSave.factAddress != null
      && (clientToSave.factAddress.street == null || clientToSave.factAddress.house == null)) {
      throw new InvalidClientData("'street' and 'house' of factAddress cannot be a null");
    }

    if (clientToSave.phones == null) {
      throw new InvalidClientData("'phones' cannot be a null");
    }

    for (Phone phone : clientToSave.phones) {
      if (!phone.number.matches("[0-9]+")) {
        throw new InvalidClientData("phone should contain only numbers");
      }
    }

    if (clientToSave.phones.isEmpty()) {
      throw new InvalidClientData("no phones are specified");
    }

    if (clientToSave.phones.stream().noneMatch(p -> p.type == PhoneType.HOME)) {
      throw new InvalidClientData("client should have at least one HOME phone");
    }

    if (clientToSave.phones.stream().noneMatch(p -> p.type == PhoneType.WORK)) {
      throw new InvalidClientData("client should have at least one WORK phone");
    }

    if (clientToSave.phones.stream().noneMatch(p -> p.type == PhoneType.MOBILE)) {
      throw new InvalidClientData("client should have at least one MOBILE phone");
    }

    return true;
  }

  @Override
  public ClientAccountRecord editClient(ClientToSave clientToSave) {
    if(clientToSave.id == null) throw new InvalidClientData("client id cannot be null");

    ClientDetails clientDetails = getClientDetails(clientToSave.id);

    Client client = new Client();
    client.id = clientDetails.id;
    client.name = clientToSave.name != null ? clientToSave.name.trim() : clientDetails.name;
    client.surname = clientToSave.surname != null ? clientToSave.surname.trim() : clientDetails.surname;
    client.patronymic = clientToSave.patronymic != null ? clientToSave.patronymic : clientDetails.patronymic;
    client.gender = clientToSave.gender != null ? clientToSave.gender : clientDetails.gender;
    client.birthDate = clientToSave.birthDate != null ? clientToSave.birthDate : clientDetails.birthDate;

    Charm charm = charmRegister.get().getCharm(clientToSave.charmId != null ? clientToSave.charmId : clientDetails.charmId);
    client.charmId =  charm.id;

    clientDao.get().updateClient(client);

    if (clientToSave.regAddress != null && clientToSave.regAddress.isActive) {
      Address regAddress = new Address();

      if(clientDetails.regAddress != null) regAddress.id = clientDetails.regAddress.id;

      regAddress.type = AddressType.REG;
      regAddress.clientId = clientDetails.id;
      regAddress.street =
        clientToSave.regAddress.street != null ? clientToSave.regAddress.street : clientDetails.regAddress.street;

      regAddress.house =
        clientToSave.regAddress.house != null ? clientToSave.regAddress.house : clientDetails.regAddress.house;

      regAddress.flat =
        clientToSave.regAddress.flat != null ? clientToSave.regAddress.flat : clientDetails.regAddress.flat;

      addressDao.get().insertOrUpdateAddress(regAddress);

    } else if (clientToSave.regAddress != null && !clientToSave.regAddress.isActive) {
      throw new InvalidClientData("registration address cannot be deleted");
    }

    if (clientToSave.factAddress != null && clientToSave.factAddress.isActive) {
      Address factAddress = new Address();

      if(clientDetails.factAddress != null) factAddress.id = clientDetails.factAddress.id;

      factAddress.type = AddressType.FACT;
      factAddress.clientId = clientDetails.id;
      factAddress.street =
        clientToSave.factAddress.street != null ? clientToSave.factAddress.street : clientDetails.factAddress.street;

      factAddress.house =
        clientToSave.factAddress.house != null ? clientToSave.factAddress.house : clientDetails.factAddress.house;

      factAddress.flat =
        clientToSave.factAddress.flat != null ? clientToSave.factAddress.flat : clientDetails.factAddress.flat;

      addressDao.get().insertOrUpdateAddress(factAddress);

    } else if (clientToSave.factAddress != null && clientDetails.factAddress != null && !clientToSave.factAddress.isActive) {
      addressDao.get().deleteAddress(clientDetails.factAddress.id);
    }

    if (clientToSave.phones != null) {
      for (Phone phone : clientToSave.phones) {
        phoneDao.get().insertOrUpdatePhone(phone);
      }

      // All other phones should be disabled
      for (Phone phone: phoneDao.get().selectAllPhones(clientDetails.id)) {
        if (!containsNumber(clientToSave.phones, phone.number)) {
          phoneDao.get().deletePhone(phone.id);
        }
      }
    }

    return null;
  }

  private boolean containsNumber(final List<Phone> list, final String number) {
    return list.stream().anyMatch(o -> o.number.equals(number));
  }


  @Override
  public ClientAccountRecordPage deleteClient(int clientId, TableRequestDetails requestDetails) {
    return null;
  }
}
