package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.InvalidClientData;
import kz.greetgo.sandbox.controller.errors.NotFound;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.client.ClientRegister;
import kz.greetgo.sandbox.db.test.dao.*;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.sandbox.db.test.util.RandomDate;
import kz.greetgo.sandbox.db.util.JdbcSandbox;
import kz.greetgo.util.RND;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    jdbc.get().execute((connection) -> {

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

    phoneTestDao.get().insertPhoneWithId(phone);

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
  public void createNewClient_insertData() throws ParseException {
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

    clientToSave.factAddress = new Address(AddressType.FACT, RND.str(10), RND.str(10), RND.str(10));
    clientToSave.regAddress = new Address(AddressType.REG, RND.str(10), RND.str(10), RND.str(10));

    clientToSave.phones = new ArrayList<>();
    clientToSave.phones.add(new Phone(RND.intStr(10), PhoneType.HOME));
    clientToSave.phones.add(new Phone(RND.intStr(10), PhoneType.WORK));
    clientToSave.phones.add(new Phone(RND.intStr(10), PhoneType.MOBILE));
    clientToSave.phones.add(new Phone(RND.intStr(10), PhoneType.MOBILE));

    //
    //
    clientRegister.get().createNewClient(clientToSave);

    Client actualClient = clientTestDao.get().getActiveClientById(1);

    Address actualFactAddress = addressTestDao.get().getAddressByClientIdAndType(1, AddressType.FACT);
    Address actualRegAddress = addressTestDao.get().getAddressByClientIdAndType(1, AddressType.REG);

    Phone actualHomePhone = phoneTestDao.get().getPhoneByClientIdAndType(1, PhoneType.HOME);
    Phone actualWorkPhone = phoneTestDao.get().getPhoneByClientIdAndType(1, PhoneType.WORK);
    List<Phone> actualMobiles = phoneTestDao.get().getMobilesByClientId(1);
    //
    //

    assertThat(actualClient).isNotNull();
    assertThat(actualClient.name).isEqualToIgnoringCase(clientToSave.name);
    assertThat(actualClient.surname).isEqualToIgnoringCase(clientToSave.surname);
    assertThat(actualClient.patronymic).isEqualToIgnoringCase(clientToSave.patronymic);
    assertThat(actualClient.gender).isEqualsToByComparingFields(clientToSave.gender);
    assertThat(actualClient.birthDate).isEqualTo(clientToSave.birthDate);
    assertThat(actualClient.charmId).isEqualTo(clientToSave.charmId);

    assertThat(actualFactAddress).isNotNull();
    assertThat(actualFactAddress).isEqualsToByComparingFields(clientToSave.factAddress);

    assertThat(actualRegAddress).isNotNull();
    assertThat(actualRegAddress).isEqualsToByComparingFields(clientToSave.regAddress);

    assertThat(actualHomePhone).isNotNull();
    assertThat(actualHomePhone).isEqualsToByComparingFields(clientToSave.phones.get(0));

    assertThat(actualWorkPhone).isNotNull();
    assertThat(actualWorkPhone).isEqualsToByComparingFields(clientToSave.phones.get(1));

    assertThat(actualMobiles).isNotNull();
    assertThat(actualMobiles).hasSize(2);
    assertThat(actualMobiles.get(0)).isEqualsToByComparingFields(clientToSave.phones.get(2));
    assertThat(actualMobiles.get(1)).isEqualsToByComparingFields(clientToSave.phones.get(3));
  }

  @Test
  public void createNewClient_insertData_null_patronymic_and_factAddress() throws ParseException {
    truncateTables();

    initCharm(1, true);

    RandomDate randomDate = new RandomDate();

    ClientToSave clientToSave = new ClientToSave();
    clientToSave.surname = RND.str(15);
    clientToSave.name = RND.str(15);
    clientToSave.patronymic = null;
    clientToSave.gender = Gender.FEMALE;
    clientToSave.birthDate = randomDate.nextDate();
    clientToSave.charmId = 1;

    clientToSave.regAddress = new Address(AddressType.REG, RND.str(10), RND.str(10), RND.str(10));

    clientToSave.phones = new ArrayList<>();
    clientToSave.phones.add(new Phone(RND.intStr(10), PhoneType.HOME));
    clientToSave.phones.add(new Phone(RND.intStr(10), PhoneType.WORK));
    clientToSave.phones.add(new Phone(RND.intStr(10), PhoneType.MOBILE));
    clientToSave.phones.add(new Phone(RND.intStr(10), PhoneType.MOBILE));

    //
    //
    clientRegister.get().createNewClient(clientToSave);

    Client actualClient = clientTestDao.get().getActiveClientById(1);

    Address actualFactAddress = addressTestDao.get().getAddressByClientIdAndType(1, AddressType.FACT);
    Address actualRegAddress = addressTestDao.get().getAddressByClientIdAndType(1, AddressType.REG);

    Phone actualHomePhone = phoneTestDao.get().getPhoneByClientIdAndType(1, PhoneType.HOME);
    Phone actualWorkPhone = phoneTestDao.get().getPhoneByClientIdAndType(1, PhoneType.WORK);
    List<Phone> actualMobiles = phoneTestDao.get().getMobilesByClientId(1);
    //
    //

    assertThat(actualClient).isNotNull();
    assertThat(actualClient.name).isEqualToIgnoringCase(clientToSave.name);
    assertThat(actualClient.surname).isEqualToIgnoringCase(clientToSave.surname);
    assertThat(actualClient.patronymic).isNull();
    assertThat(actualClient.gender).isEqualsToByComparingFields(clientToSave.gender);
    assertThat(actualClient.birthDate).isEqualTo(clientToSave.birthDate);
    assertThat(actualClient.charmId).isEqualTo(clientToSave.charmId);

    assertThat(actualFactAddress).isNull();

    assertThat(actualRegAddress).isNotNull();
    assertThat(actualRegAddress).isEqualsToByComparingFields(clientToSave.regAddress);

    assertThat(actualHomePhone).isNotNull();
    assertThat(actualHomePhone).isEqualsToByComparingFields(clientToSave.phones.get(0));

    assertThat(actualWorkPhone).isNotNull();
    assertThat(actualWorkPhone).isEqualsToByComparingFields(clientToSave.phones.get(1));

    assertThat(actualMobiles).isNotNull();
    assertThat(actualMobiles).hasSize(2);
    assertThat(actualMobiles.get(0)).isEqualsToByComparingFields(clientToSave.phones.get(2));
    assertThat(actualMobiles.get(1)).isEqualsToByComparingFields(clientToSave.phones.get(3));
  }

  @DataProvider
  public static Object[][] fullName_DP() {
    return new Object[][]{
      {"Вася", "Иван", "Пупкин", "Вася Иван Пупкин"},
      {" Вася ", " Иван  ", "Пупкин ", "Вася Иван Пупкин"},
      {"Вася", "Иван", null, "Вася Иван"},
    };
  }

  @Test(dataProvider = "fullName_DP")
  public void createNewClient_record_fullName(String surname,
                                              String name,
                                              String patronymic,
                                              String expected) throws ParseException {
    truncateTables();

    initCharm(1, true);

    RandomDate randomDate = new RandomDate();

    ClientToSave clientToSave = new ClientToSave();
    clientToSave.gender = Gender.FEMALE;
    clientToSave.birthDate = randomDate.nextDate();
    clientToSave.charmId = 1;
    clientToSave.regAddress = new Address(AddressType.REG, RND.str(10), RND.str(10), RND.str(10));
    clientToSave.phones = new ArrayList<>();
    clientToSave.phones.add(new Phone(RND.intStr(10), PhoneType.HOME));
    clientToSave.phones.add(new Phone(RND.intStr(10), PhoneType.WORK));
    clientToSave.phones.add(new Phone(RND.intStr(10), PhoneType.MOBILE));


    clientToSave.surname = surname;
    clientToSave.name = name;
    clientToSave.patronymic = patronymic;

    //
    //
    ClientAccountRecord actual = clientRegister.get().createNewClient(clientToSave);
    //
    //

    assertThat(actual).isNotNull();
    assertThat(actual.clientFullName).isEqualToIgnoringCase(expected);
  }

  @Test
  public void createNewClient_record_accountBalances() throws ParseException {
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
    clientToSave.regAddress = new Address(AddressType.REG, RND.str(10), RND.str(10), RND.str(10));
    clientToSave.phones = new ArrayList<>();
    clientToSave.phones.add(new Phone(RND.intStr(10), PhoneType.HOME));
    clientToSave.phones.add(new Phone(RND.intStr(10), PhoneType.WORK));
    clientToSave.phones.add(new Phone(RND.intStr(10), PhoneType.MOBILE));


    //
    //
    ClientAccountRecord actual = clientRegister.get().createNewClient(clientToSave);
    //
    //

    assertThat(actual).isNotNull();
    assertThat(actual.minAccBalance).isEqualTo(0f);
    assertThat(actual.maxAccBalance).isEqualTo(0f);
    assertThat(actual.totalAccBalance).isEqualTo(0f);
  }


  @DataProvider
  public static Object[][] create_invalidValues_DP() throws ParseException {
    return new Object[][]{
      {
        null,
        RND.str(15),
        Gender.MALE,
        new SimpleDateFormat("dd-MM-yyyy").parse("12-12-2012"),
        new Address(AddressType.REG, RND.str(15), RND.str(15), RND.str(15)),
        new ArrayList<Phone>() {{
          add(new Phone(RND.intStr(10), PhoneType.HOME));
          add(new Phone(RND.intStr(10), PhoneType.WORK));
          add(new Phone(RND.intStr(10), PhoneType.MOBILE));
        }}
      },
      {
        "",
        RND.str(15),
        Gender.MALE,
        new SimpleDateFormat("dd-MM-yyyy").parse("12-12-2012"),
        new Address(AddressType.REG, RND.str(15), RND.str(15), RND.str(15)),
        new ArrayList<Phone>() {{
          add(new Phone(RND.intStr(10), PhoneType.HOME));
          add(new Phone(RND.intStr(10), PhoneType.WORK));
          add(new Phone(RND.intStr(10), PhoneType.MOBILE));
        }}
      },
      {
        "  ",
        RND.str(15),
        Gender.MALE,
        new SimpleDateFormat("dd-MM-yyyy").parse("12-12-2012"),
        new Address(AddressType.REG, RND.str(15), RND.str(15), RND.str(15)),
        new ArrayList<Phone>() {{
          add(new Phone(RND.intStr(10), PhoneType.HOME));
          add(new Phone(RND.intStr(10), PhoneType.WORK));
          add(new Phone(RND.intStr(10), PhoneType.MOBILE));
        }}
      },
      {
        RND.str(15),
        null,
        Gender.MALE,
        new SimpleDateFormat("dd-MM-yyyy").parse("12-12-2012"),
        new Address(AddressType.REG, RND.str(15), RND.str(15), RND.str(15)),
        new ArrayList<Phone>() {{
          add(new Phone(RND.intStr(10), PhoneType.HOME));
          add(new Phone(RND.intStr(10), PhoneType.WORK));
          add(new Phone(RND.intStr(10), PhoneType.MOBILE));
        }}
      },
      {
        RND.str(15),
        "",
        Gender.MALE,
        new SimpleDateFormat("dd-MM-yyyy").parse("12-12-2012"),
        new Address(AddressType.REG, RND.str(15), RND.str(15), RND.str(15)),
        new ArrayList<Phone>() {{
          add(new Phone(RND.intStr(10), PhoneType.HOME));
          add(new Phone(RND.intStr(10), PhoneType.WORK));
          add(new Phone(RND.intStr(10), PhoneType.MOBILE));
        }}
      },
      {
        RND.str(15),
        "  ",
        Gender.MALE,
        new SimpleDateFormat("dd-MM-yyyy").parse("12-12-2012"),
        new Address(AddressType.REG, RND.str(15), RND.str(15), RND.str(15)),
        new ArrayList<Phone>() {{
          add(new Phone(RND.intStr(10), PhoneType.HOME));
          add(new Phone(RND.intStr(10), PhoneType.WORK));
          add(new Phone(RND.intStr(10), PhoneType.MOBILE));
        }}
      },
      {
        RND.str(15),
        RND.str(15),
        null,
        new SimpleDateFormat("dd-MM-yyyy").parse("12-12-2012"),
        new Address(AddressType.REG, RND.str(15), RND.str(15), RND.str(15)),
        new ArrayList<Phone>() {{
          add(new Phone(RND.intStr(10), PhoneType.HOME));
          add(new Phone(RND.intStr(10), PhoneType.WORK));
          add(new Phone(RND.intStr(10), PhoneType.MOBILE));
        }}
      },
      {
        RND.str(15),
        RND.str(15),
        Gender.MALE,
        null,
        new Address(AddressType.REG, RND.str(15), RND.str(15), RND.str(15)),
        new ArrayList<Phone>() {{
          add(new Phone(RND.intStr(10), PhoneType.HOME));
          add(new Phone(RND.intStr(10), PhoneType.WORK));
          add(new Phone(RND.intStr(10), PhoneType.MOBILE));
        }}
      },
      {
        RND.str(15),
        RND.str(15),
        Gender.MALE,
        new SimpleDateFormat("dd-MM-yyyy").parse("12-12-2012"),
        null,
        new ArrayList<Phone>() {{
          add(new Phone(RND.intStr(10), PhoneType.HOME));
          add(new Phone(RND.intStr(10), PhoneType.WORK));
          add(new Phone(RND.intStr(10), PhoneType.MOBILE));
        }}
      },
      {
        RND.str(15),
        RND.str(15),
        Gender.MALE,
        new SimpleDateFormat("dd-MM-yyyy").parse("12-12-2012"),
        new Address(AddressType.REG, RND.str(15), RND.str(15), RND.str(15)),
        null,
      },
      {
        RND.str(15),
        RND.str(15),
        Gender.MALE,
        new SimpleDateFormat("dd-MM-yyyy").parse("12-12-2012"),
        new Address(AddressType.REG, RND.str(15), RND.str(15), RND.str(15)),
        new ArrayList<Phone>() {{
          add(new Phone(RND.intStr(10), PhoneType.WORK));
          add(new Phone(RND.intStr(10), PhoneType.MOBILE));
        }}
      },
      {
        RND.str(15),
        RND.str(15),
        Gender.MALE,
        new SimpleDateFormat("dd-MM-yyyy").parse("12-12-2012"),
        new Address(AddressType.REG, RND.str(15), RND.str(15), RND.str(15)),
        new ArrayList<Phone>() {{
          add(new Phone(RND.intStr(10), PhoneType.HOME));
          add(new Phone(RND.intStr(10), PhoneType.MOBILE));
        }}
      },
      {
        RND.str(15),
        RND.str(15),
        Gender.MALE,
        new SimpleDateFormat("dd-MM-yyyy").parse("12-12-2012"),
        new Address(AddressType.REG, RND.str(15), RND.str(15), RND.str(15)),
        new ArrayList<Phone>() {{
          add(new Phone(RND.intStr(10), PhoneType.HOME));
          add(new Phone(RND.intStr(10), PhoneType.WORK));
        }}
      }
    };
  }

  @Test(dataProvider = "create_invalidValues_DP", expectedExceptions = InvalidClientData.class)
  public void createNewClient_invalidValues(String surname, String name, Gender gender,
                                         Date birthDate, Address regAddress, List<Phone> phones) throws ParseException {
    truncateTables();

    initCharm(1, true);

    ClientToSave clientToSave = new ClientToSave();
    clientToSave.surname = surname;
    clientToSave.name = name;
    clientToSave.gender = gender;
    clientToSave.birthDate = birthDate;
    clientToSave.charmId = 1;

    clientToSave.regAddress = regAddress;
    clientToSave.phones = phones;

    //
    //
    clientRegister.get().createNewClient(clientToSave);
    //
    //
  }

  @Test
  public void editClient_clientData() throws ParseException {
    truncateTables();

    initCharm(1, true);

    Client client = initClient(1, 1);
    initAddress(0, AddressType.REG, 1);
    initPhone(0, PhoneType.HOME, 1, true);
    initPhone(1, PhoneType.WORK, 1, true);
    initPhone(2, PhoneType.MOBILE, 1, true);

    RandomDate randomDate = new RandomDate();

    ClientToSave expected = new ClientToSave();
    expected.id = client.id;
    expected.surname = RND.str(15);
    expected.name = RND.str(15);
    expected.patronymic = RND.str(15);
    expected.gender = Gender.FEMALE;
    expected.birthDate = randomDate.nextDate();
    expected.charmId = 1;

    //
    //
    clientRegister.get().editClient(expected);

    ClientDetails actual = clientRegister.get().getClientDetails(client.id);
    //
    //

    assertThat(actual).isNotNull();
    assertThat(actual.name).isEqualToIgnoringCase(expected.name);
    assertThat(actual.surname).isEqualToIgnoringCase(expected.surname);
    assertThat(actual.patronymic).isEqualToIgnoringCase(expected.patronymic);
    assertThat(actual.gender).isEqualsToByComparingFields(expected.gender);
    assertThat(actual.birthDate).isEqualTo(expected.birthDate);
    assertThat(actual.charmId).isEqualTo(expected.charmId);
  }

  @Test
  public void editClient_addresses() throws ParseException {
    truncateTables();

    initCharm(1, true);

    Client client = initClient(1, 1);
    initAddress(0, AddressType.REG, 1);

    phoneTestDao.get().insertPhone(new Phone(1, RND.intStr(10), PhoneType.HOME));
    phoneTestDao.get().insertPhone(new Phone(1, RND.intStr(10), PhoneType.WORK));
    phoneTestDao.get().insertPhone(new Phone(1, RND.intStr(10), PhoneType.MOBILE));
    phoneTestDao.get().insertPhone(new Phone(1, RND.intStr(10), PhoneType.MOBILE));

    RandomDate randomDate = new RandomDate();

    ClientToSave expected = new ClientToSave();
    expected.id = client.id;
    expected.surname = RND.str(15);
    expected.name = RND.str(15);
    expected.patronymic = RND.str(15);
    expected.gender = Gender.FEMALE;
    expected.birthDate = randomDate.nextDate();
    expected.charmId = 1;

    expected.regAddress = new Address(0, client.id, AddressType.REG, RND.str(10), RND.str(10), RND.str(10));
    expected.factAddress = new Address(client.id, AddressType.FACT, RND.str(10), RND.str(10), RND.str(10));

    expected.phones = new ArrayList<>();
    expected.phones.add(new Phone(RND.intStr(10), PhoneType.HOME));
    expected.phones.add(new Phone(RND.intStr(10), PhoneType.WORK));
    expected.phones.add(new Phone(RND.intStr(10), PhoneType.MOBILE));

    //
    //
    clientRegister.get().editClient(expected);

    ClientDetails actual = clientRegister.get().getClientDetails(client.id);
    //
    //

    assertThat(actual.regAddress).isNotNull();
    assertThat(actual.regAddress.type).isEqualsToByComparingFields(expected.regAddress.type);
    assertThat(actual.regAddress.house).isEqualToIgnoringCase(expected.regAddress.house);
    assertThat(actual.regAddress.street).isEqualToIgnoringCase(expected.regAddress.street);
    assertThat(actual.regAddress.flat).isEqualToIgnoringCase(expected.regAddress.flat);

    assertThat(actual.factAddress).isNotNull();
    assertThat(actual.factAddress.type).isEqualsToByComparingFields(expected.factAddress.type);
    assertThat(actual.factAddress.house).isEqualToIgnoringCase(expected.factAddress.house);
    assertThat(actual.factAddress.street).isEqualToIgnoringCase(expected.factAddress.street);
    assertThat(actual.factAddress.flat).isEqualToIgnoringCase(expected.factAddress.flat);
  }

  @Test
  public void editClient_phones() throws ParseException {
    truncateTables();

    initCharm(1, true);

    Client client = initClient(1, 1);

    phoneTestDao.get().insertPhone(new Phone(1, RND.intStr(10), PhoneType.HOME));
    phoneTestDao.get().insertPhone(new Phone(1, RND.intStr(10), PhoneType.WORK));
    phoneTestDao.get().insertPhone(new Phone(1, RND.intStr(10), PhoneType.MOBILE));
    phoneTestDao.get().insertPhone(new Phone(1, RND.intStr(10), PhoneType.MOBILE));

    ClientToSave expected = new ClientToSave();

    expected.phones = new ArrayList<>();
    expected.phones.add(new Phone(RND.intStr(10), PhoneType.HOME));
    expected.phones.add(new Phone(RND.intStr(10), PhoneType.WORK));
    expected.phones.add(new Phone(RND.intStr(10), PhoneType.MOBILE));

    //
    //
    clientRegister.get().editClient(expected);

    ClientDetails actual = clientRegister.get().getClientDetails(client.id);
    //
    //

    assertThat(actual).isNotNull();
    assertThat(actual.phones).isNotNull();
    assertThat(actual.phones).hasSize(expected.phones.size());

    for(Phone phone : expected.phones) {
      assertThat(actual.phones.stream().anyMatch(phone::equals));
    }
  }


  @DataProvider
  public static Object[][] edit_invalidValues_DP() throws ParseException {
    return new Object[][]{
      {
        1,
        null,
        RND.str(15),
        Gender.MALE,
        new SimpleDateFormat("dd-MM-yyyy").parse("12-12-2012"),
        new Address(AddressType.REG, RND.str(15), RND.str(15), RND.str(15)),
        new ArrayList<Phone>() {{
          add(new Phone(RND.intStr(10), PhoneType.HOME));
          add(new Phone(RND.intStr(10), PhoneType.WORK));
          add(new Phone(RND.intStr(10), PhoneType.MOBILE));
        }}
      },
      {
        1,
        RND.str(15),
        null,
        Gender.MALE,
        new SimpleDateFormat("dd-MM-yyyy").parse("12-12-2012"),
        new Address(AddressType.REG, RND.str(15), RND.str(15), RND.str(15)),
        new ArrayList<Phone>() {{
          add(new Phone(RND.intStr(10), PhoneType.HOME));
          add(new Phone(RND.intStr(10), PhoneType.WORK));
          add(new Phone(RND.intStr(10), PhoneType.MOBILE));
        }}
      },
      {
        1,
        RND.str(15),
        RND.str(15),
        null,
        new SimpleDateFormat("dd-MM-yyyy").parse("12-12-2012"),
        new Address(AddressType.REG, RND.str(15), RND.str(15), RND.str(15)),
        new ArrayList<Phone>() {{
          add(new Phone(RND.intStr(10), PhoneType.HOME));
          add(new Phone(RND.intStr(10), PhoneType.WORK));
          add(new Phone(RND.intStr(10), PhoneType.MOBILE));
        }}
      },
      {
        1,
        RND.str(15),
        RND.str(15),
        Gender.MALE,
        null,
        new Address(AddressType.REG, RND.str(15), RND.str(15), RND.str(15)),
        new ArrayList<Phone>() {{
          add(new Phone(RND.intStr(10), PhoneType.HOME));
          add(new Phone(RND.intStr(10), PhoneType.WORK));
          add(new Phone(RND.intStr(10), PhoneType.MOBILE));
        }}
      },
      {
        1,
        RND.str(15),
        RND.str(15),
        Gender.MALE,
        new SimpleDateFormat("dd-MM-yyyy").parse("12-12-2012"),
        null,
        new ArrayList<Phone>() {{
          add(new Phone(RND.intStr(10), PhoneType.HOME));
          add(new Phone(RND.intStr(10), PhoneType.WORK));
          add(new Phone(RND.intStr(10), PhoneType.MOBILE));
        }}
      },
      {
        1,
        RND.str(15),
        RND.str(15),
        Gender.MALE,
        new SimpleDateFormat("dd-MM-yyyy").parse("12-12-2012"),
        new Address(AddressType.REG, RND.str(15), RND.str(15), RND.str(15)),
        null,
      },
      {
        1,
        RND.str(15),
        RND.str(15),
        Gender.MALE,
        new SimpleDateFormat("dd-MM-yyyy").parse("12-12-2012"),
        new Address(AddressType.REG, RND.str(15), RND.str(15), RND.str(15)),
        new ArrayList<Phone>() {{
//          add(new Phone(RND.intStr(10), PhoneType.HOME));
          add(new Phone(RND.intStr(10), PhoneType.WORK));
          add(new Phone(RND.intStr(10), PhoneType.MOBILE));
        }}
      },
      {
        1,
        RND.str(15),
        RND.str(15),
        Gender.MALE,
        new SimpleDateFormat("dd-MM-yyyy").parse("12-12-2012"),
        new Address(AddressType.REG, RND.str(15), RND.str(15), RND.str(15)),
        new ArrayList<Phone>() {{
          add(new Phone(RND.intStr(10), PhoneType.HOME));
//          add(new Phone(RND.intStr(10), PhoneType.WORK));
          add(new Phone(RND.intStr(10), PhoneType.MOBILE));
        }}
      },
      {
        12,
        RND.str(15),
        RND.str(15),
        Gender.MALE,
        new SimpleDateFormat("dd-MM-yyyy").parse("12-12-2012"),
        new Address(AddressType.REG, RND.str(15), RND.str(15), RND.str(15)),
        new ArrayList<Phone>() {{
          add(new Phone(RND.intStr(10), PhoneType.HOME));
          add(new Phone(RND.intStr(10), PhoneType.WORK));
//          add(new Phone(RND.intStr(10), PhoneType.MOBILE));
        }}
      }
    };
  }

  @Test(dataProvider = "edit_invalidValues_DP", expectedExceptions = InvalidClientData.class)
  public void editClient_invalidValues(int clientId, String surname, String name, Gender gender,
                                         Date birthDate, Address regAddress, List<Phone> phones) throws ParseException {
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

    clientToSave.factAddress = new Address(AddressType.FACT, RND.str(10), RND.str(10), RND.str(10));
    clientToSave.regAddress = new Address(AddressType.REG, RND.str(10), RND.str(10), RND.str(10));

    clientToSave.phones = new ArrayList<>();
    clientToSave.phones.add(new Phone(RND.intStr(10), PhoneType.HOME));
    clientToSave.phones.add(new Phone(RND.intStr(10), PhoneType.WORK));
    clientToSave.phones.add(new Phone(RND.intStr(10), PhoneType.MOBILE));
    clientToSave.phones.add(new Phone(RND.intStr(10), PhoneType.MOBILE));

    //
    //
    clientRegister.get().createNewClient(clientToSave);

    ClientToSave expected = new ClientToSave();
    expected.id = clientId;
    expected.surname = surname;
    expected.name = name;
    expected.gender = gender;
    expected.birthDate = birthDate;
    expected.charmId = 1;

    expected.regAddress = regAddress;
    expected.phones = phones;

    //
    //
    clientRegister.get().editClient(expected);
    //
    //
  }
}