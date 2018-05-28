package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.NotFound;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.client.ClientRegister;
import kz.greetgo.sandbox.db.dao.AddressDao;
import kz.greetgo.sandbox.db.dao.CharmDao;
import kz.greetgo.sandbox.db.dao.ClientDao;
import kz.greetgo.sandbox.db.dao.PhoneDao;

@Bean
public class ClientRegisterImpl implements ClientRegister {

  public BeanGetter<ClientDao> clientDao;
  public BeanGetter<CharmDao> charmDao;
  public BeanGetter<AddressDao> addressDao;
  public BeanGetter<PhoneDao> phoneDao;

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
  public ClientAccountInfo createNewClient(ClientToSave clientToSave) {
    return null;
  }

  @Override
  public ClientAccountInfo editClient(ClientToSave clientToSave) {
    return null;
  }

  @Override
  public ClientAccountInfoPage deleteClient(int clientId, TableRequestDetails requestDetails) {
    return null;
  }
}
