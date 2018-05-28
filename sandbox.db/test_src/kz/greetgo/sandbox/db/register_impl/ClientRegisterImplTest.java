package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.NotFound;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.client.ClientRegister;
import kz.greetgo.sandbox.db.test.dao.AddressTestDao;
import kz.greetgo.sandbox.db.test.dao.CharmTestDao;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.dao.PhoneTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class ClientRegisterImplTest extends ParentTestNg {

  public BeanGetter<ClientRegister> clientRegister;

  public BeanGetter<ClientTestDao> clientTestDao;
  public BeanGetter<CharmTestDao> charmTestDao;
  public BeanGetter<AddressTestDao> addressTestDao;
  public BeanGetter<PhoneTestDao> phoneTestDao;

  private void recreateTables() {
    clientTestDao.get().recreateTable();
    charmTestDao.get().recreateTable();
    addressTestDao.get().recreateTable();
    phoneTestDao.get().recreateTable();
  }

  private void clearTables() {
    clientTestDao.get().clearTable();
    charmTestDao.get().clearTable();
    addressTestDao.get().clearTable();
    phoneTestDao.get().clearTable();
  }

  private Charm initCharm(int charmId, boolean isActive) {
    Charm charm = new Charm();
    charm.id = charmId;
    charm.name = RND.str(10);
    charm.isActive = isActive;

    charmTestDao.get().insertCharm(charm);

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
    clearTables();

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
    clearTables();

    //
    //
    clientRegister.get().getClientDetails(RND.plusInt(10));
    //
    //
  }

  @Test
  public void getClientDetails_charmsDictionary() {
    clearTables();

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
    clearTables();

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
    clearTables();

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
    clearTables();

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
    clearTables();

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
    clearTables();

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

}