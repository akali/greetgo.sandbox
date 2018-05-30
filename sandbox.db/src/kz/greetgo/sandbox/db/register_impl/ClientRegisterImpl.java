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
    if (!isValidClientData(clientToSave)) throw new InvalidClientData();

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
    return
      clientToSave.name != null
        && clientToSave.surname != null
        && clientToSave.birthDate != null
        && clientToSave.gender != null
        && clientToSave.regAddress != null
        && clientToSave.regAddress.street != null
        && clientToSave.regAddress.house != null
        && (clientToSave.factAddress == null
        || (clientToSave.factAddress.street != null && clientToSave.factAddress.house != null))
        && arePhonesValid(clientToSave.phones);
  }

  private boolean arePhonesValid(List<Phone> phones) {
    for (Phone phone : phones) {
      if (!phone.number.matches("[0-9]+")) {
        return false;
      }
    }

    return !phones.isEmpty()
      && phones.stream().anyMatch(p -> p.type == PhoneType.HOME)
      && phones.stream().anyMatch(p -> p.type == PhoneType.WORK)
      && phones.stream().anyMatch(p -> p.type == PhoneType.MOBILE);
  }

  @Override
  public ClientAccountRecord editClient(ClientToSave clientToSave) {
    return null;
  }

  @Override
  public ClientAccountRecordPage deleteClient(int clientId, TableRequestDetails requestDetails) {
    return null;
  }
}
