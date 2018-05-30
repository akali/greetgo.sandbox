package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.NotFound;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.client.ClientRegister;
import kz.greetgo.sandbox.db.test.dao.*;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.sandbox.db.test.util.RandomDate;
import kz.greetgo.sandbox.db.util.JdbcSandbox;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class ClientRegisterImplTest extends ParentTestNg {

  public BeanGetter<ClientRegister> clientRegister;
  public BeanGetter<JdbcSandbox> jdbc;

  public BeanGetter<ClientTestDao> clientTestDao;
  public BeanGetter<CharmTestDao> charmTestDao;
  public BeanGetter<AddressTestDao> addressTestDao;
  public BeanGetter<PhoneTestDao> phoneTestDao;
  public BeanGetter<AccountTestDao> accountTestDao;


  private void truncateTables() {
    clientTestDao.get().truncateTable();
    charmTestDao.get().truncateTable();
    addressTestDao.get().truncateTable();
    phoneTestDao.get().truncateTable();

    jdbc.get().execute((connection)->{

      String restartCharmSeq = "ALTER SEQUENCE charm_id_seq RESTART WITH 1;";
      String restartClientSeq = "ALTER SEQUENCE client_id_seq RESTART WITH 1;";
      String restartAccountSeq = "ALTER SEQUENCE account_id_seq RESTART WITH 1;";
      String restartPhoneSeq = "ALTER SEQUENCE phone_id_seq RESTART WITH 1;";
      String restartAddressSeq = "ALTER SEQUENCE address_id_seq RESTART WITH 1;";

      connection.prepareStatement(restartCharmSeq).execute();
      connection.prepareStatement(restartClientSeq).execute();
      connection.prepareStatement(restartAccountSeq).execute();
      connection.prepareStatement(restartPhoneSeq).execute();
      connection.prepareStatement(restartAddressSeq).execute();

      return null;
    });
  }

  private Charm initCharm(int charmId, boolean isActive) {
    Charm charm = new Charm();
    charm.id = charmId;
    charm.name = RND.str(10);
    charm.isActive = isActive;

    charmTestDao.get().insertCharmWithId(charm);

    return charm;
  }

  private Client initClient(int clientId, int charmId) {
    Client expectedClient = new Client();
    expectedClient.id = clientId;
    expectedClient.name = RND.str(10);
    expectedClient.surname = RND.str(10);
    expectedClient.patronymic = RND.str(10);
    expectedClient.gender = Gender.MALE;
    try {
      expectedClient.birthDate = new SimpleDateFormat("dd-MM-yyyy").parse("28-05-2018");
    } catch (ParseException e) {
      e.printStackTrace();
    }
    expectedClient.charmId = charmId;
    expectedClient.isActive = true;

    clientTestDao.get().insertClient(expectedClient);

    return expectedClient;
  }

  private Address initAddress(int addressId, AddressType type, int clientId) {
    Address address = new Address();
    address.id = addressId;
    address.street = RND.str(20);
    address.house = RND.str(20);
    address.type = type;
    address.clientId = clientId;

    addressTestDao.get().insertAddress(address);

    return address;
  }

  private Phone initPhone(int phoneId, PhoneType type, int clientId, boolean isActive) {
    Phone phone = new Phone();
    phone.id = phoneId;
    phone.type = type;
    phone.clientId = clientId;
    phone.number = RND.str(10);
    phone.isActive = isActive;

    phoneTestDao.get().insertPhone(phone);

    return phone;
  }

  @Test
  public void getClientDetails_clientData() {
    truncateTables();

    Charm charm = initCharm(RND.plusInt(10), true);

    Client expectedClient = initClient(RND.plusInt(10), charm.id);

    //
    //
    ClientDetails details = clientRegister.get().getClientDetails(expectedClient.id);
    //
    //

    assertThat(details).isNotNull();
    assertThat(details.name).isNotNull();
    assertThat(details.name).isEqualToIgnoringCase(expectedClient.name);
    assertThat(details.surname).isNotNull();
    assertThat(details.surname).isEqualToIgnoringCase(expectedClient.surname);
    assertThat(details.patronymic).isNotNull();
    assertThat(details.patronymic).isEqualToIgnoringCase(expectedClient.patronymic);
    assertThat(details.gender).isNotNull();
    assertThat(details.gender).isEqualsToByComparingFields(expectedClient.gender);
    assertThat(details.birthDate).isNotNull();
    assertThat(details.birthDate).isEqualTo(expectedClient.birthDate);
    assertThat(details.charmId).isNotNull();
    assertThat(details.charmId).isEqualTo(expectedClient.charmId);
  }

  @Test(expectedExceptions = NotFound.class)
  public void getClientDetails_invalidId() {
    truncateTables();

    //
    //
    clientRegister.get().getClientDetails(RND.plusInt(10));
    //
    //
  }

  @Test
  public void getClientDetails_charmsDictionary() {
    truncateTables();

    List<Charm> expected = new ArrayList<>();
    expected.add(initCharm(1, true));
    expected.add(initCharm(2, true));
    expected.add(initCharm(3, true));


    Client expectedClient = initClient(RND.plusInt(10), 1);

    //
    //
    ClientDetails details = clientRegister.get().getClientDetails(expectedClient.id);
    List<Charm> actual = details.charmsDictionary;
    //
    //

    assertThat(details).isNotNull();
    assertThat(actual).isNotNull();
    assertThat(actual).hasSize(expected.size());
    assertThat(actual).contains(expected.get(0), expected.get(1), expected.get(2));
  }

  @Test
  public void getClientDetails_charmsDictionary_inActiveCharms() {
    truncateTables();

    List<Charm> expected = new ArrayList<>();
    expected.add(initCharm(1, true));
    expected.add(initCharm(2, false));
    expected.add(initCharm(3, true));


    Client expectedClient = initClient(RND.plusInt(10), 1);

    //
    //
    ClientDetails details = clientRegister.get().getClientDetails(expectedClient.id);
    List<Charm> actual = details.charmsDictionary;
    //
    //

    assertThat(details).isNotNull();
    assertThat(actual).isNotNull();
    assertThat(actual).hasSize(2);
    assertThat(actual).contains(expected.get(0));
    assertThat(actual).doesNotContain(expected.get(1));
    assertThat(actual).contains(expected.get(2));
  }

  @Test
  public void getClientDetails_factAddress() {
    truncateTables();

    Charm charm = initCharm(RND.plusInt(10), true);
    Client client = initClient(RND.plusInt(10), charm.id);

    Address expected = initAddress(RND.plusInt(10), AddressType.FACT, client.id);

    //
    //
    ClientDetails details = clientRegister.get().getClientDetails(client.id);
    Address actual = details.factAddress;
    //
    //

    assertThat(actual).isNotNull();
    assertThat(actual).isEqualsToByComparingFields(expected);
  }

  @Test
  public void getClientDetails_regAddress() {
    truncateTables();

    Charm charm = initCharm(RND.plusInt(10), true);
    Client client = initClient(RND.plusInt(10), charm.id);

    Address expected = initAddress(RND.plusInt(10), AddressType.REG, client.id);

    //
    //
    ClientDetails details = clientRegister.get().getClientDetails(client.id);
    Address actual = details.regAddress;
    //
    //

    assertThat(actual).isNotNull();
    assertThat(actual).isEqualsToByComparingFields(expected);
  }

  @Test
  public void getClientDetails_phones() {
    truncateTables();

    Charm charm = initCharm(RND.plusInt(10), true);
    Client client = initClient(RND.plusInt(100), charm.id);

    List<Phone> expected = new ArrayList<>();
    expected.add(initPhone(1, PhoneType.HOME, client.id, true));
    expected.add(initPhone(2, PhoneType.WORK, client.id, true));
    expected.add(initPhone(3, PhoneType.MOBILE, client.id, true));
    expected.add(initPhone(4, PhoneType.MOBILE, client.id, true));

    //
    //
    ClientDetails details = clientRegister.get().getClientDetails(client.id);
    List<Phone> actual = details.phones;
    //
    //

    assertThat(details).isNotNull();
    assertThat(actual).isNotNull();
    assertThat(actual).hasSize(expected.size());
    assertThat(actual).contains(expected.get(0), expected.get(1), expected.get(2), expected.get(3));
  }

  @Test
  public void getClientDetails_inActive_phones() {
    truncateTables();

    Charm charm = initCharm(RND.plusInt(10), true);
    Client client = initClient(RND.plusInt(100), charm.id);

    List<Phone> expected = new ArrayList<>();
    expected.add(initPhone(1, PhoneType.HOME, client.id, true));
    expected.add(initPhone(2, PhoneType.WORK, client.id, true));
    expected.add(initPhone(3, PhoneType.MOBILE, client.id, false));
    expected.add(initPhone(4, PhoneType.MOBILE, client.id, true));

    //
    //
    ClientDetails details = clientRegister.get().getClientDetails(client.id);
    List<Phone> actual = details.phones;
    //
    //

    assertThat(details).isNotNull();
    assertThat(actual).isNotNull();
    assertThat(actual).hasSize(3);
    assertThat(actual).contains(expected.get(0), expected.get(1), expected.get(3));
    assertThat(actual).doesNotContain(expected.get(2));
  }

  @Test
  public void createNewClient() throws ParseException {
    truncateTables();

    initCharm(1, true);

    RandomDate randomDate = new RandomDate();

    ClientToSave clientToSave = new ClientToSave();
    clientToSave.surname = RND.str(15);
    clientToSave.name = RND.str(15);
    clientToSave.patronymic = RND.str(15);
    clientToSave.gender = Gender.FEMALE;
    clientToSave.birthDate = randomDate.nextDate();
    clientToSave.charmId = 1;

    //
    //
    clientRegister.get().createNewClient(clientToSave);
    Client client = clientTestDao.get().getActiveClientById(1);
    //
    //

    assertThat(client).isNotNull();
    assertThat(client.name).isEqualToIgnoringCase(clientToSave.name);
    assertThat(client.surname).isEqualToIgnoringCase(clientToSave.surname);
    assertThat(client.patronymic).isEqualToIgnoringCase(clientToSave.patronymic);
    assertThat(client.gender).isEqualsToByComparingFields(clientToSave.gender);
    assertThat(client.birthDate).isEqualTo(clientToSave.birthDate);
    assertThat(client.charmId).isEqualTo(clientToSave.charmId);
  }

}