package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientsRegister;
import kz.greetgo.sandbox.db.test.dao.ClientsTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.sandbox.db.util.DBHelper;
import liquibase.exception.DatabaseException;
import org.apache.ibatis.jdbc.SQL;
import org.testng.annotations.Test;

import java.sql.*;
import java.util.*;
import java.util.stream.IntStream;

import static org.fest.assertions.api.Assertions.assertThat;


public class ClientsRegisterImplTest extends ParentTestNg {

  public BeanGetter<ClientsTestDao> clientsTestDao;
  public BeanGetter<ClientsRegister> clientsRegister;

  @Test
  public void testGetCharms() {
    List<Charm> charms = new ArrayList<>();
    charms.add(new Charm("HAPPY", "VERY HAPPY PERSON", (float) 0.5));
    clientsTestDao.get().clearCharm();
    charms.forEach(clientsTestDao.get()::insertCharm);
    List<Charm> testCharms = clientsRegister.get().getCharms();
    assertThat(testCharms).isNotNull();
    assertThat(testCharms).containsAll(charms);
    System.out.println(testCharms);
  }

  @Test
  public void testGetClientRecords() {

    //
    //
    TableResponse result = clientsRegister.get()
      .getClientRecords(new QueryFilter(0, 100, "DESC", "name", ""));
    //
    //

    System.out.println(result.list);

    assertThat(result.list).isNotNull();
    assertThat(result.list).isNotEmpty();
  }

   @Test
  public void testGetClientDetailsById() {
  }

  private Calendar getCalendar(int day, int month, int year) {
    Calendar cal = new GregorianCalendar();
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month - 1);
    cal.set(Calendar.DATE, day);
    return cal;
  }

  public long getTimestamp(int day, int month, int year) {
    Calendar cal = getCalendar(day, month, year);
    return cal.getTimeInMillis();
  }

  private void insertTestingCharms() {
    clientsTestDao.get().clearCharm();
    for (int i = 1; i <= 10; ++i) {
      Charm charm = new Charm(i, generateString(10), generateString(24), (float) 0.9);
      clientsTestDao.get().insertCharm(charm);
    }
  }

  private ArrayList<ClientToSave> insertTestingClients() {
    ClientToSave clientToSave =
      new ClientToSave(1, "Yerbolat", "Ablemetov", "Askarovich", 1, GenderType.MALE,
        new ClientAddress(
          1, AddressType.REG, "Seyfullina", "13a", "23"
        ),
        new ClientAddress(
          1, AddressType.FACT, "Bekturova", "23", null
        ),
        getTimestamp(12, 1, 1998),
        Arrays.asList(
          new ClientPhone(1, "+77473105484", PhoneType.MOBILE),
          new ClientPhone(1, "+77273518547", PhoneType.HOME)
        )
      );
    clientsTestDao.get().clearClient();
    clientsTestDao.get().clearClientAddress();
    clientsTestDao.get().clearClientPhone();

    clientsTestDao.get().insertClient(clientToSave.getClientCopy());
    clientsTestDao.get().insertClientAddress(clientToSave.regAddress);
    clientsTestDao.get().insertClientAddress(clientToSave.factAddress);
    clientToSave.phones.forEach(clientsTestDao.get()::insertClientPhone);
    ArrayList<ClientToSave> clients = new ArrayList<>();
    clients.add(clientToSave);
    return clients;
  }

  @Test
  void testest() {
    clientsTestDao.get().clearClient();
    clientsTestDao.get().clearClientAddress();
    clientsTestDao.get().clearClientPhone();

    insertTestingCharms();

    ClientToSave clientToSave =
      new ClientToSave(1, "Yerbolat", "Ablemetov", "Askarovich", 1, GenderType.MALE,
        new ClientAddress(
          1, AddressType.REG, "Seyfullina", "13a", "23"
        ),
        new ClientAddress(
          1, AddressType.FACT, "Bekturova", "23", null
        ),
        getTimestamp(12, 1, 1998),
        Arrays.asList(
          new ClientPhone(1, "+77473105484", PhoneType.MOBILE),
          new ClientPhone(1, "+77273518547", PhoneType.HOME)
        )
      );

    System.out.println(clientsRegister.get().addClientToSave(clientToSave));
  }

  private final String sigma = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890";

  private String generateString(int len) {
    Random rnd = new Random();
    StringBuilder sb = new StringBuilder();
    IntStream.range(0, len).forEach(i -> sb.append(sigma.charAt(rnd.nextInt(sigma.length()))));
    return sb.toString();
  }

  @Test
  public void testAddClientToSave() {

    clientsTestDao.get().clearClientPhone();
    clientsTestDao.get().clearClient();
    clientsTestDao.get().clearClientAddress();
    clientsTestDao.get().clearCharm();

    insertTestingCharms();
    ArrayList<ClientToSave> clients = insertTestingClients();
    ArrayList<ClientRecord> recordClients = new ArrayList<>();

    //
    //
    clients.forEach(client -> recordClients.add(clientsRegister.get().addClientToSave(client)));
    //
    //

    {
      for (int i = 0; i < recordClients.size(); i++) {
        ClientRecord client = recordClients.get(i);

        assertThat(client).equals(clientsTestDao.get().getRecordClientById(client.id));
      }
    }
  }

  @Test
  public void test() {
    clientsTestDao.get().clearClient();
    clientsTestDao.get().clearClientAddress();
    clientsTestDao.get().clearClientPhone();

    insertTestingCharms();

    ClientToSave clientToSave =
      new ClientToSave(1, "Yerbolat", "Ablemetov", "Askarovich", 1, GenderType.MALE,
        new ClientAddress(
          1, AddressType.REG, "Seyfullina", "13a", "23"
        ),
        new ClientAddress(
          1, AddressType.FACT, "Bekturova", "23", null
        ),
        getTimestamp(12, 1, 1998),
        Arrays.asList(
          new ClientPhone(1, "+77473105484", PhoneType.MOBILE),
          new ClientPhone(1, "+77273518547", PhoneType.HOME)
        )
      );

    ClientRecord record = clientsRegister.get().addClientToSave(clientToSave);

    System.out.println(GetClientDetails.getClientDetailsById(record.id));

    ClientToSave clientToEdit =
      new ClientToSave(record.id, "Aisultan", "Kali", "Amanzholuly", 2, GenderType.MALE,
        new ClientAddress(
          record.id, AddressType.REG, "AkhanSeri", "13a", "32"
        ),
        new ClientAddress(
          record.id, AddressType.FACT, "Timeriyazeva", "44", "33"
        ),
        getTimestamp(12, 1, 1965),
        Arrays.asList(
          new ClientPhone(1, "+77772233636", PhoneType.MOBILE),
          new ClientPhone(1, "+77051472585", PhoneType.HOME),
          new ClientPhone(1, "+77271472585", PhoneType.WORK)
        )
      );

    clientsRegister.get().editClientToSave(clientToEdit);

    System.out.println(GetClientDetails.getClientDetailsById(record.id));
  }

  @Test
  public void testEditClientToSave() {
  }

  @Test
  public void testRemoveClientById() {

    List<Client> clients = new ArrayList<>(),
      toRemove = new ArrayList<>();

    for (Client client : clients)
      clientsTestDao.get().insertClient(client);

    Random rnd = new Random();

    clients.forEach(client -> {
      if (rnd.nextInt(1) == 0)
        toRemove.add(client);
    });

    //
    //
    toRemove.forEach(client -> clientsRegister.get().removeClientById(client.id));
    //
    //

    toRemove.forEach(client -> {
//      assertThat(clientsTestDao.get().getClient(client.id)).isNullOrEmpty();
    });

  }
}