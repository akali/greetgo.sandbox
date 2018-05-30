package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.InvalidClientData;
import kz.greetgo.sandbox.controller.errors.NotFound;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.account.AccountRegister;
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
  public BeanGetter<JdbcSandbox> jdbc;

  @Override
  public ClientDetails getClientDetails(int clientId) {
    ClientDetails clientDetails = new ClientDetails();

    Client client = clientDao.get().getClientById(clientId);

    if (client == null) throw new NotFound();

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
    if (!isValidClientData(clientToSave)) throw new InvalidClientData("invalid data");

    Client client = new Client();
    client.id = clientToSave.id;
    client.name = clientToSave.name.trim();
    client.surname = clientToSave.surname.trim();
    client.patronymic = clientToSave.patronymic != null ? clientToSave.patronymic.trim() : null;

    client.gender = clientToSave.gender;
    client.birthDate = clientToSave.birthDate;
    client.charmId = clientToSave.charmId;

    clientDao.get().updateClient(client);

    addressDao.get().updateAddress(clientToSave.regAddress);

    if(clientToSave.factAddress != null)
      addressDao.get().insertAddress(clientToSave.factAddress);

    return null;
  }

  @Override
  public ClientAccountRecordPage deleteClient(int clientId, TableRequestDetails requestDetails) {
    return null;
  }
}
