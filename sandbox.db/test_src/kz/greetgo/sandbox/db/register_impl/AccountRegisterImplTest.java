package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.InvalidRequestDetails;
import kz.greetgo.sandbox.controller.errors.NoAccount;
import kz.greetgo.sandbox.controller.errors.NotFound;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.account.AccountRegister;
import kz.greetgo.sandbox.db.test.dao.AccountTestDao;
import kz.greetgo.sandbox.db.test.dao.CharmTestDao;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.sandbox.db.test.util.RandomDate;
import kz.greetgo.sandbox.db.util.JdbcSandbox;
import kz.greetgo.sandbox.db.util.YearDifference;
import kz.greetgo.util.RND;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.fest.assertions.api.Assertions.assertThat;


public class AccountRegisterImplTest extends ParentTestNg {

  public BeanGetter<AccountRegister> accountRegister;
  public BeanGetter<JdbcSandbox> jdbc;

  public BeanGetter<ClientTestDao> clientTestDao;
  public BeanGetter<AccountTestDao> accountTestDao;
  public BeanGetter<CharmTestDao> charmTestDao;

  private void truncateTables() {
    clientTestDao.get().truncateTable();
    accountTestDao.get().truncateTable();
    charmTestDao.get().truncateTable();

    jdbc.get().execute((connection)->{

      String restartCharmSeq = "ALTER SEQUENCE charm_id_seq RESTART WITH 1;";
      String restartClientSeq = "ALTER SEQUENCE client_id_seq RESTART WITH 1;";
      String restartAccountSeq = "ALTER SEQUENCE account_id_seq RESTART WITH 1;";

      connection.prepareStatement(restartCharmSeq).execute();
      connection.prepareStatement(restartClientSeq).execute();
      connection.prepareStatement(restartAccountSeq).execute();

      return null;
    });
  }

  private Charm initCharm(int charmId) {
    Charm charm = new Charm();
    charm.id = charmId;
    charm.name = RND.str(10);

    charmTestDao.get().insertCharm(charm);

    return charm;
  }

  private Client initClient(int clientId, int charmId, boolean isActive) {

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
    expectedClient.isActive = isActive;

    clientTestDao.get().insertClient(expectedClient);

    return expectedClient;
  }

  private Account initAccount(int accountId, int clientId, float money, boolean isActive) {
    Account account = new Account();
    account.id = accountId;
    account.clientId = clientId;
    account.money = money;
    account.number = RND.str(20);
    account.isActive = isActive;

    try {
      Date parsedDate = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").parse("28-06-2018 00:00:00");
      account.registeredAt = new java.sql.Timestamp(parsedDate.getTime());
    } catch (ParseException e) {
      e.printStackTrace();
    }

    accountTestDao.get().insertAccount(account);

    return account;
  }

  @Test
  public void getClientAccountRecord_clientData() {
    truncateTables();

    Charm charm = initCharm(RND.plusInt(20));
    Client client = initClient(RND.plusInt(10), charm.id, true);
    initAccount(1, client.id, 100f, true);

    //
    //
    ClientAccountRecord actual = accountRegister.get().getClientAccountRecord(client.id);
    //
    //

    assertThat(actual).isNotNull();
    assertThat(actual.clientFullName)
      .isEqualToIgnoringCase(String.format("%s %s %s", client.surname, client.name, client.patronymic));
    assertThat(actual.clientId).isEqualTo(client.id);
    assertThat(actual.clientAge).isEqualTo(YearDifference.calculate(client.birthDate));
    assertThat(actual.clientCharmName)
      .isEqualToIgnoringCase(charmTestDao.get().getCharmById(client.charmId).name);
  }

  @Test(expectedExceptions = NotFound.class)
  public void getClientAccountRecord_invalidClient() {
    truncateTables();

    //
    //
    accountRegister.get().getClientAccountRecord(RND.plusInt(10));
    //
    //
  }

  @Test(expectedExceptions = NotFound.class)
  public void getClientAccountRecord_inActiveClient() {
    truncateTables();

    Charm charm = initCharm(RND.plusInt(20));
    Client client = initClient(RND.plusInt(10), charm.id, false);

    //
    //
    accountRegister.get().getClientAccountRecord(client.id);
    //
    //
  }

  @Test(expectedExceptions = NoAccount.class)
  public void getClientAccountRecord_noAccount() {
    truncateTables();

    Charm charm = initCharm(RND.plusInt(20));
    Client client = initClient(RND.plusInt(10), charm.id, true);

    //
    //
    accountRegister.get().getClientAccountRecord(client.id);
    //
    //
  }

  @DataProvider
  public static Object[][] minAccBalance_DP() {
    return new Object[][] {
      {100f, -123f, 44f, -123f},
      {100f, 66f, 100f, 66f},
      {1111f, 6542f, 44f, 44f},
      {12312f, 0f, 0f, 0f},
      {20.0001f, 20.0002f, 20.00012f, 20.0001f},
    };
  }

  @Test(dataProvider = "minAccBalance_DP")
  public void getMinAccBalance(float m1, float m2, float m3, float expected) {
    truncateTables();

    Charm charm = initCharm(RND.plusInt(20));
    Client client = initClient(RND.plusInt(10), charm.id, true);
    initAccount(1, client.id, m1, true);
    initAccount(2, client.id, m2, true);
    initAccount(3, client.id, m3, true);

    //
    //
    float actual = accountRegister.get().getMinAccBalance(client.id);
    //
    //

    assertThat(actual).isNotNull();
    assertThat(actual).isEqualTo(expected);
  }

  @Test(expectedExceptions = NoAccount.class)
  public void getMinAccBalance_noAccount() {
    truncateTables();

    Charm charm = initCharm(RND.plusInt(20));
    Client client = initClient(RND.plusInt(10), charm.id, true);

    //
    //
    accountRegister.get().getMinAccBalance(client.id);
    //
    //
  }

  @DataProvider
  public static Object[][] maxAccBalance_DP() {
    return new Object[][] {
      {100f, -123f, 44f, 100f},
      {100f, 66f, 100f, 100f},
      {1111f, 6542f, 44f, 6542f},
      {12312f, 0f, 0f, 12312f},
      {20.0001f, 20.000f, 20.00012f, 20.00012f},
    };
  }

  @Test(dataProvider = "maxAccBalance_DP")
  public void getMaxAccBalance(float m1, float m2, float m3, float expected) {
    truncateTables();

    Charm charm = initCharm(RND.plusInt(20));
    Client client = initClient(RND.plusInt(10), charm.id, true);
    initAccount(1, client.id, m1, true);
    initAccount(2, client.id, m2, true);
    initAccount(3, client.id, m3, true);

    //
    //
    float actual = accountRegister.get().getMaxAccBalance(client.id);
    //
    //

    assertThat(actual).isNotNull();
    assertThat(actual).isEqualTo(expected);
  }

  @Test(expectedExceptions = NoAccount.class)
  public void getMaxAccBalance_noAccount() {
    truncateTables();

    Charm charm = initCharm(RND.plusInt(20));
    Client client = initClient(RND.plusInt(10), charm.id, true);

    //
    //
    accountRegister.get().getMaxAccBalance(client.id);
    //
    //
  }

  @DataProvider
  public static Object[][] totalAccBalance_DP() {
    return new Object[][] {
      {100f, -123f, 44f, 21f},
      {100f, 66f, 100f, 266f},
      {1111f, 6542f, 44f, 7697},
      {12312f, 0f, 0f, 12312f},
      {20.0001f, 20.000f, 20.00012f, 60.00022f},
    };
  }

  @Test(dataProvider = "totalAccBalance_DP")
  public void getTotalAccBalance(float m1, float m2, float m3, float expected) {
    truncateTables();

    Charm charm = initCharm(RND.plusInt(20));
    Client client = initClient(RND.plusInt(10), charm.id, true);
    initAccount(1, client.id, m1, true);
    initAccount(2, client.id, m2, true);
    initAccount(3, client.id, m3, true);

    //
    //
    float actual = accountRegister.get().getTotalAccBalance(client.id);
    //
    //

    assertThat(actual).isNotNull();
    assertThat(actual).isEqualTo(expected);
  }

  @Test(expectedExceptions = NoAccount.class)
  public void getTotalAccBalance_noAccount() {
    truncateTables();

    Charm charm = initCharm(RND.plusInt(20));
    Client client = initClient(RND.plusInt(10), charm.id, true);

    //
    //
    accountRegister.get().getTotalAccBalance(client.id);
    //
    //
  }

  private List<Client> init_test_clientList(int charmId) throws ParseException {
    List<Client> list = new ArrayList<>();
    RandomDate rd = new RandomDate();

    list.add(new Client(1, "Александр", "Сергеевич", "Пушкин",
      Gender.MALE, rd.nextDate(), charmId, true));
    list.add(new Client(2, "Михаил", "Юрьевич", "Лермонтов",
      Gender.MALE, rd.nextDate(), charmId, true));
    list.add(new Client(3, "Астафьев", "Пётр", "Евгеньевич",
      Gender.MALE, rd.nextDate(), charmId, true));
    list.add(new Client(4, "Аткинсон", "Уильям", "Уокер",
      Gender.MALE, rd.nextDate(), charmId, true));
    list.add(new Client(5, "Аттербум", "Даниель", "Амадеус", Gender.MALE,
      rd.nextDate(), charmId, true));
    list.add(new Client(6, "Базинер", "Фёдор", "Иванович",
      Gender.MALE, rd.nextDate(), charmId, true));
    list.add(new Client(7, "Бек", "Лео", null,
      Gender.MALE, rd.nextDate(), charmId, true));
    list.add(new Client(8, "Аббаньяно", "Никола", null,
      Gender.MALE, rd.nextDate(), charmId, true));
    list.add(new Client(9, "Анри", "Мишель", null,
      Gender.MALE, rd.nextDate(), charmId, true));
    list.add(new Client(10, "Аппиа", "Кваме", "Энтони",
      Gender.MALE, rd.nextDate(), charmId, true));
    list.add(new Client(11, "Аджита", "Кесакамбала", null,
      Gender.MALE, rd.nextDate(), charmId, true));
    list.add(new Client(12, "Александр", "Афродисийский", null,
      Gender.MALE, rd.nextDate(), charmId, true));
    list.add(new Client(13, "Мишель", "Мэнсон", null,
      Gender.FEMALE, rd.nextDate(), charmId, true));
    list.add(new Client(14, "Мария", "Шарапова", "Андреевна",
      Gender.MALE, rd.nextDate(), charmId, true));
    list.add(new Client(15, "Зайд", "Шейх", "Асланович",
      Gender.MALE, rd.nextDate(), charmId, true));

    for(Client client: list) {
      clientTestDao.get().insertClient(client);
    }

    return list;
  }

  private List<Account> initAccounts(int range) {
    List<Account> list = new ArrayList<>();
    Random random = new Random();

    int idCounter = 1;
    for(int i = 1; i <= range; i++) {
      Account account  = initAccount(idCounter++, i, random.nextFloat(), true);
      Account account2  = initAccount(idCounter++, i, random.nextFloat(), true);
      Account account3  = initAccount(idCounter++, i, random.nextFloat(), true);

      list.add(account);
    }

    return list;
  }

  private List<ClientAccountRecord> init_test_clientAccountRecordList(List<Client> clientList) {
    List<ClientAccountRecord> list = new ArrayList<>();

    for(Client client : clientList) {
      ClientAccountRecord record = accountRegister.get().getClientAccountRecord(client.id);
      list.add(record);
    }

    return list;
  }

  @DataProvider
  public static Object[][] filter_DP() {
    return new Object[][] {
      { "сон Уо", 1},
      { "", 15},
      { "    ", 15},
      { null, 15},
      { "а", 13},
      { "алекСАНДр", 2},
      { "АПП", 1},
      { " миш ", 2},
      { "r", 0},
      { "-1", 0},
    };
  }

  private TableRequestDetails initTableRequestDetails(String filter, int pageIndex, int pageSize,
                                                     SortColumn column, SortDirection direction) {
    TableRequestDetails details = new TableRequestDetails();
    details.filter = filter;
    details.pageIndex = pageIndex;
    details.pageSize = pageSize;
    details.sortBy = column;
    details.sortDirection = direction;

    return details;
  }

  @Test
  public void getClientAccountRecordPage() throws ParseException {
    truncateTables();

    Charm charm = initCharm(1);
    List<Client> clientList = init_test_clientList(charm.id);
    initAccounts(15);
    List<ClientAccountRecord> expected = init_test_clientAccountRecordList(clientList);

    TableRequestDetails details = initTableRequestDetails(null,0, 15, SortColumn.NONE, SortDirection.ASC);

    //
    //
    ClientAccountRecordPage actual = accountRegister.get().getClientAccountRecordPage(details);
    //
    //

    assertThat(actual).isNotNull();
    assertThat(actual.totalItemsCount).isEqualTo(expected.size());
    assertThat(actual.items.get(0)).isEqualsToByComparingFields(expected.get(0));
    assertThat(actual.items.get(1)).isEqualsToByComparingFields(expected.get(1));
    assertThat(actual.items.get(2)).isEqualsToByComparingFields(expected.get(2));
    assertThat(actual.items.get(3)).isEqualsToByComparingFields(expected.get(3));
    assertThat(actual.items.get(4)).isEqualsToByComparingFields(expected.get(4));
    assertThat(actual.items.get(5)).isEqualsToByComparingFields(expected.get(5));
    assertThat(actual.items.get(6)).isEqualsToByComparingFields(expected.get(6));
    assertThat(actual.items.get(7)).isEqualsToByComparingFields(expected.get(7));
    assertThat(actual.items.get(9)).isEqualsToByComparingFields(expected.get(9));
    assertThat(actual.items.get(10)).isEqualsToByComparingFields(expected.get(10));
    assertThat(actual.items.get(11)).isEqualsToByComparingFields(expected.get(11));
    assertThat(actual.items.get(12)).isEqualsToByComparingFields(expected.get(12));
    assertThat(actual.items.get(13)).isEqualsToByComparingFields(expected.get(13));
    assertThat(actual.items.get(14)).isEqualsToByComparingFields(expected.get(14));
  }

  @Test(dataProvider = "filter_DP")
  public void getClientAccountRecordPage_filter(String filterValue, int expectedSize) throws ParseException {
    truncateTables();

    Charm charm = initCharm(1);
    List<Client> clientList = init_test_clientList(charm.id);
    initAccounts(15);
    init_test_clientAccountRecordList(clientList);

    TableRequestDetails details =
      initTableRequestDetails(filterValue,0, 15, SortColumn.NONE, SortDirection.ASC);

    //
    //
    ClientAccountRecordPage actual = accountRegister.get().getClientAccountRecordPage(details);
    //
    //

    assertThat(actual).isNotNull();
    assertThat(actual.items).isNotNull();
    assertThat(actual.items).hasSize(expectedSize);
  }

  @DataProvider
  public static Object[][] sort_DP() {
    return new Object[][] {
      {
        SortColumn.FIO, SortDirection.ASC,
        (Comparator<ClientAccountRecord>) (o1, o2) -> o1.clientFullName.compareTo(o2.clientFullName)
      },
      {
        SortColumn.FIO, SortDirection.DESC,
        (Comparator<ClientAccountRecord>) (o1, o2) -> o1.clientFullName.compareTo(o2.clientFullName)
      },
      {
        SortColumn.AGE, SortDirection.ASC,
        (Comparator<ClientAccountRecord>) (o1, o2) -> Integer.compare(o1.clientAge, o2.clientAge)
      },
      {
        SortColumn.AGE, SortDirection.DESC,
        ((Comparator<ClientAccountRecord>) (o1, o2) ->  Integer.compare(o1.clientAge, o2.clientAge)).reversed()
      },
      {
        SortColumn.MIN, SortDirection.ASC,
        (Comparator<ClientAccountRecord>) (o1, o2) -> Float.compare(o1.minAccBalance, o2.minAccBalance)
      },
      {
        SortColumn.MIN, SortDirection.DESC,
        ((Comparator<ClientAccountRecord>) (o1, o2) -> Float.compare(o1.minAccBalance, o2.minAccBalance)).reversed()
      },
      {
        SortColumn.MAX, SortDirection.ASC,
        (Comparator<ClientAccountRecord>) (o1, o2) -> Float.compare(o1.maxAccBalance, o2.maxAccBalance)
      },
      {
        SortColumn.MAX, SortDirection.DESC,
        ((Comparator<ClientAccountRecord>) (o1, o2) -> Float.compare(o1.maxAccBalance, o2.maxAccBalance)).reversed()
      },
      {
        SortColumn.TOTAL, SortDirection.ASC,
        (Comparator<ClientAccountRecord>) (o1, o2) -> Float.compare(o1.totalAccBalance, o2.totalAccBalance)
      },
      {
        SortColumn.TOTAL, SortDirection.DESC,
        ((Comparator<ClientAccountRecord>) (o1, o2) -> Float.compare(o1.totalAccBalance, o2.totalAccBalance)).reversed()
      },
    };
  }

  @Test(dataProvider = "sort_DP")
  public void getClientAccountRecordPage_sort(
    SortColumn column, SortDirection direction, Comparator<ClientAccountRecord> comparator) throws ParseException {
    truncateTables();

    Charm charm = initCharm(1);
    List<Client> clientList = init_test_clientList(charm.id);
    initAccounts(15);
    init_test_clientAccountRecordList(clientList);

    TableRequestDetails details =
      initTableRequestDetails(null,0, 15, column, direction);

    //
    //
    ClientAccountRecordPage actual = accountRegister.get().getClientAccountRecordPage(details);
    //
    //

    assertThat(actual).isNotNull();
    assertThat(actual.items).isNotNull();
    assertThat(actual.items).hasSize(15);
    assertThat(actual.items).isSortedAccordingTo(comparator);
  }

  @DataProvider
  public static Object[][] paginate_DP() {
    return new Object[][] {
      {
        0, 3,
        new ArrayList<Integer>(){{
            add(1);
            add(2);
            add(3);
          }
        }
      },
      {
        1, 3,
        new ArrayList<Integer>(){{
            add(4);
            add(5);
            add(6);
          }
        }
      },
      {
        2, 6,
        new ArrayList<Integer>(){{
            add(13);
            add(14);
            add(15);
          }
        }
      },
      {
        2, 7,
        new ArrayList<Integer>(){{
          add(15);
        }
        }
      },
      {
        2, 7,
        new ArrayList<Integer>(){{
          add(15);
          }
        }
      }
    };
  }

  @Test(dataProvider = "paginate_DP")
  public void getClientAccountRecordPage_paginate(int pageIndex, int pageSize, List<Integer> expectedIds) throws ParseException {
      truncateTables();

      Charm charm = initCharm(1);
      List<Client> clientList = init_test_clientList(charm.id);
      initAccounts(15);
      init_test_clientAccountRecordList(clientList);

      TableRequestDetails details =
        initTableRequestDetails(null,pageIndex, pageSize, SortColumn.NONE, SortDirection.ASC);

      //
      //
      ClientAccountRecordPage actual = accountRegister.get().getClientAccountRecordPage(details);
      //
      //

      assertThat(actual).isNotNull();
      assertThat(actual.items).isNotNull();
      assertThat(actual.items).hasSize(expectedIds.size());

      for(Integer expectedId : expectedIds) {
        assertThat(actual.items.stream().anyMatch(o -> o.clientId == expectedId)).isTrue();
      }
  }

  @DataProvider
  public static Object[][] paginate_invalid_DP() {
    return new Object[][] {
      { -1, 3 },
      { 0, -3 },
      { -34, -23 },
      { 0, 0 },
    };
  }

  @Test(dataProvider = "paginate_invalid_DP", expectedExceptions = InvalidRequestDetails.class)
  public void getClientAccountRecordPage_paginate_invalidRequest(int pageIndex, int pageSize) throws ParseException {
    truncateTables();

    Charm charm = initCharm(1);
    List<Client> clientList = init_test_clientList(charm.id);
    initAccounts(15);
    init_test_clientAccountRecordList(clientList);

    TableRequestDetails details =
      initTableRequestDetails(null,pageIndex, pageSize, SortColumn.NONE, SortDirection.ASC);

    //
    //
    accountRegister.get().getClientAccountRecordPage(details);
    //
    //
  }

}