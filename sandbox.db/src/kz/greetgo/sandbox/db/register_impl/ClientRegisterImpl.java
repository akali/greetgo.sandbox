package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
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

    if(client == null) throw new NotFound();

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

    Client client = new Client();
    client.name = clientToSave.name;
    client.surname = clientToSave.surname;
    client.patronymic = clientToSave.patronymic;
    client.gender = clientToSave.gender;
    client.birthDate = clientToSave.birthDate;
    client.charmId = clientToSave.charmId;

    jdbc.get().execute((connection)->{

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

    return accountRegister.get().getClientAccountRecord(client.id);
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
