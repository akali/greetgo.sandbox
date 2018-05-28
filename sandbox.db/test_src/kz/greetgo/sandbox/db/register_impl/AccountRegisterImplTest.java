package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.NoAccount;
import kz.greetgo.sandbox.controller.errors.NotFound;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.account.AccountRegister;
import kz.greetgo.sandbox.db.test.dao.AccountTestDao;
import kz.greetgo.sandbox.db.test.dao.CharmTestDao;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.sandbox.db.util.YearDifference;
import kz.greetgo.util.RND;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.fest.assertions.api.Assertions.assertThat;


public class AccountRegisterImplTest extends ParentTestNg {

  public BeanGetter<AccountRegister> accountRegister;

  public BeanGetter<ClientTestDao> clientTestDao;
  public BeanGetter<AccountTestDao> accountTestDao;
  public BeanGetter<CharmTestDao> charmTestDao;

  private void truncateTables() {
    clientTestDao.get().truncateTable();
    accountTestDao.get().truncateTable();
    charmTestDao.get().truncateTable();
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
    ClientAccountRecord actual = accountRegister.get().getClientAccountRecord(client.id);
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
    ClientAccountRecord record = accountRegister.get().getClientAccountRecord(client.id);
    //
    //

    assertThat(record).isNotNull();
    assertThat(record.minAccBalance).isEqualTo(expected);
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
    ClientAccountRecord record = accountRegister.get().getClientAccountRecord(client.id);
    //
    //

    assertThat(record).isNotNull();
    assertThat(record.maxAccBalance).isEqualTo(expected);
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
    ClientAccountRecord record = accountRegister.get().getClientAccountRecord(client.id);
    //
    //

    assertThat(record).isNotNull();
    assertThat(record.totalAccBalance).isEqualTo(expected);
  }

}