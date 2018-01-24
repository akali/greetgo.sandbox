package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.InvalidParameter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.util.Util;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.fest.assertions.api.Assertions.assertThat;

public class ClientRegisterImplTest extends ParentTestNg {

  public BeanGetter<ClientRegister> clientRegister;
  public BeanGetter<ClientTestDao> clientTestDao;

  @Test
  public void method_getCount_filterEmpty() {
    this.resetTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    long expectedClientCount = 40;
    for (int i = 0; i < expectedClientCount; i++)
      this.insertClient(charmHelperList);

    long realCount = clientRegister.get().getCount("");

    assertThat(realCount).isEqualTo(expectedClientCount);
  }

  @Test
  public void method_getCount_filter() {
    this.resetTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    String dummyName = "null";
    Set<Long> expectedIdSet = new HashSet<>();
    expectedIdSet.add(this.insertClient("Нурбакыт", "Айбек", "Смагулович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 3; i++)
      this.insertClient(dummyName, charmHelperList);
    this.insertClient("Исаков", "Владимир", "Вячеславович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id);
    expectedIdSet.add(this.insertClient("Яковлева", "Нургиза", "Андреевна", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 3; i++)
      this.insertClient(dummyName, charmHelperList);
    expectedIdSet.add(this.insertClient("Яковлева", "Татьяна", "Нурлановна", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    this.insertClient("Игорев", "Айдану", "Игоревич", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id);

    long realFilteredCount = clientRegister.get().getCount("Нур");

    assertThat(realFilteredCount).isEqualTo(expectedIdSet.size());
  }

  @Test
  public void method_getCount_filterWithIgnoredCase() {
    this.resetTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    String dummyName = "null";
    Set<Long> expectedIdSet = new HashSet<>();
    for (int i = 0; i < 3; i++)
      this.insertClient(dummyName, charmHelperList);
    expectedIdSet.add(this.insertClient("Нурбакыт", "Айбек", "Смагулович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    this.insertClient("Исаков", "Владимир", "Вячеславович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id);
    for (int i = 0; i < 3; i++)
      this.insertClient(dummyName, charmHelperList);
    expectedIdSet.add(this.insertClient("АйланУр", "Лалка", "Иванов", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("Яковлева", "Гизанур", "Андреевна", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 3; i++)
      this.insertClient(dummyName, charmHelperList);
    expectedIdSet.add(this.insertClient("Яковлева", "Татьяна", "Нурлановна", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));

    long realFilteredCount = clientRegister.get().getCount("нУР");

    assertThat(realFilteredCount).isEqualTo(expectedIdSet.size());
  }

  @Test
  public void method_getRecordList_default() {
    this.resetTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    Set<Long> expectedIdSet = new HashSet<>();
    expectedIdSet.add(this.insertClient("Нурбакыт", "Айбек", "Смагулович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("а", "б", "в", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 5; i++)
      expectedIdSet.add(this.insertClient(charmHelperList));
    expectedIdSet.add(this.insertClient("Игорев", "Игорь", "Игоревич", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("Нурланов", "Нурлан", "Нурланович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));

    ClientRecordListRequest clientRecordListRequest =
      this.clientRecordListRequestBuilder(0, expectedIdSet.size() + 5, ColumnSortType.NONE, false, "");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordListRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedIdSet.size());
    for (ClientRecord clientRecord : realRecordList)
      assertThat(clientRecord.id).isIn(expectedIdSet);
  }

  @Test
  public void method_getRecordList_defaultWithPagination_atBeginning() {
    this.resetTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    Set<Long> expectedIdSet = new HashSet<>();
    expectedIdSet.add(this.insertClient("Нурбакыт", "Айбек", "Смагулович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("а", "б", "в", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("Игорев", "Игорь", "Игоревич", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 10; i++)
      this.insertClient(charmHelperList);

    ClientRecordListRequest clientRecordListRequest =
      this.clientRecordListRequestBuilder(0, expectedIdSet.size(), ColumnSortType.NONE, false, "");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordListRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedIdSet.size());
    for (ClientRecord clientRecord : realRecordList)
      assertThat(clientRecord.id).isIn(expectedIdSet);
  }

  @Test
  public void method_getRecordList_defaultWithPagination_atMiddle() {
    this.resetTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    Set<Long> skippedIdSet = new HashSet<>();
    Set<Long> expectedIdSet = new HashSet<>();
    for (int i = 0; i < 10; i++)
      skippedIdSet.add(this.insertClient(charmHelperList));
    expectedIdSet.add(this.insertClient("ПУСТО", "ПУСТО", "ПУСТО", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("ч", "т", "о", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("Нурланов", "Нурлан", "Нурланович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 10; i++)
      this.insertClient(charmHelperList);

    ClientRecordListRequest clientRecordListRequest =
      this.clientRecordListRequestBuilder(skippedIdSet.size(), expectedIdSet.size(), ColumnSortType.NONE, false, "");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordListRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedIdSet.size());
    for (ClientRecord clientRecord : realRecordList)
      assertThat(clientRecord.id).isIn(expectedIdSet);
  }

  @Test
  public void method_getRecordList_defaultWithPagination_atEnd() {
    this.resetTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    Set<Long> skippedIdSet = new HashSet<>();
    Set<Long> expectedIdSet = new HashSet<>();
    for (int i = 0; i < 10; i++)
      skippedIdSet.add(this.insertClient(charmHelperList));
    expectedIdSet.add(this.insertClient("ПУСТО", "ПУСТО", "ПУСТО", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("ч", "т", "о", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("Нурланов", "Нурлан", "Нурланович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));

    ClientRecordListRequest clientRecordListRequest =
      this.clientRecordListRequestBuilder(skippedIdSet.size(), expectedIdSet.size(), ColumnSortType.NONE, false, "");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordListRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedIdSet.size());
    for (ClientRecord clientRecord : realRecordList)
      assertThat(clientRecord.id).isIn(expectedIdSet);
  }

  @Test
  public void method_getRecordList_defaultWithPagination_onCut() {
    this.resetTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    Set<Long> skippedIdSet = new HashSet<>();
    Set<Long> expectedIdSet = new HashSet<>();
    for (int i = 0; i < 10; i++)
      skippedIdSet.add(this.insertClient(charmHelperList));
    expectedIdSet.add(this.insertClient("ПУСТО", "ПУСТО", "ПУСТО", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("ч", "т", "о", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("Нурланов", "Нурлан", "Нурланович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));

    ClientRecordListRequest clientRecordListRequest = this.clientRecordListRequestBuilder(skippedIdSet.size(),
      expectedIdSet.size() + 2, ColumnSortType.NONE, false, "");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordListRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedIdSet.size());
    for (ClientRecord clientRecord : realRecordList)
      assertThat(clientRecord.id).isIn(expectedIdSet);
  }

  @Test
  public void method_getRecordList_defaultWithPagination_atCountExceed() {
    this.resetTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    Set<Long> skippedIdSet = new HashSet<>();
    for (int i = 0; i < 12; i++)
      skippedIdSet.add(this.insertClient(charmHelperList));
    skippedIdSet.add(this.insertClient("ПУСТО", "ПУСТО", "ПУСТО", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    skippedIdSet.add(this.insertClient("ч", "т", "о", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    skippedIdSet.add(this.insertClient("Нурланов", "Нурлан", "Нурлы", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));

    ClientRecordListRequest clientRecordListRequest =
      this.clientRecordListRequestBuilder(skippedIdSet.size(), 10, ColumnSortType.NONE, false, "");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordListRequest);

    assertThat(realRecordList).isEmpty();
  }

  @Test
  public void method_getRecordList_sortAgeAscend() {
    this.resetTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    List<Integer> expectedAgeList = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      int age = RND.plusInt(50) + 10;
      expectedAgeList.add(age);
      this.insertClient("", "", "", Gender.EMPTY.name(), LocalDate.now().minusYears(age), charmHelperList.get(0).id);
    }
    expectedAgeList.sort((o1, o2) -> Integer.compare(o1, o2));

    ClientRecordListRequest clientRecordListRequest =
      this.clientRecordListRequestBuilder(0, expectedAgeList.size() + 10, ColumnSortType.AGE, true, "");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordListRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedAgeList.size());
    for (int i = 0; i < realRecordList.size(); i++)
      assertThat(realRecordList.get(i).age).isEqualTo(expectedAgeList.get(i));
  }

  @Test
  public void method_getRecordList_sortAgeDescend() {
    this.resetTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    List<Integer> expectedAgeList = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      int age = RND.plusInt(50) + 10;
      expectedAgeList.add(age);
      this.insertClient("", "", "", Gender.EMPTY.name(), LocalDate.now().minusYears(age), charmHelperList.get(0).id);
    }
    expectedAgeList.sort((o1, o2) -> Integer.compare(o2, o1));

    ClientRecordListRequest clientRecordListRequest =
      this.clientRecordListRequestBuilder(0, expectedAgeList.size() + 10, ColumnSortType.AGE, false, "");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordListRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedAgeList.size());
    for (int i = 0; i < realRecordList.size(); i++)
      assertThat(realRecordList.get(i).age).isEqualTo(expectedAgeList.get(i));
  }

  @Test
  public void method_getRecordList_sortTotalAccountBalanceAscend() {
    this.resetTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    List<Float> expectedTotalMoneyList = new ArrayList<>();
    long id;
    for (int i = 0; i < 10; i++) {
      id = this.insertClient(charmHelperList);
      float totalMoney = 0;
      for (int j = 0; j < RND.plusInt(4) + 1; j++) {
        float money = RND.plusInt(100000) - 50000 + RND.rnd.nextFloat();
        totalMoney += money;
        this.insertClientAccount(id, money, "", new Timestamp(0));
      }
      expectedTotalMoneyList.add(totalMoney);
    }
    expectedTotalMoneyList.sort((o1, o2) -> Float.compare(o1, o2));

    ClientRecordListRequest clientRecordListRequest =
      this.clientRecordListRequestBuilder(0, expectedTotalMoneyList.size() + 10,
        ColumnSortType.TOTALACCOUNTBALANCE, true, "");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordListRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedTotalMoneyList.size());
    for (int i = 0; i < realRecordList.size(); i++)
      assertThat(Util.stringToFloat(realRecordList.get(i).totalAccountBalance))
        .isEqualTo(expectedTotalMoneyList.get(i));
  }

  @Test
  public void method_getRecordList_sortTotalAccountBalanceDescend() {
    this.resetTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    List<Float> expectedTotalMoneyList = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      long id = this.insertClient(charmHelperList);
      float totalMoney = 0;
      for (int j = 0; j < RND.plusInt(4) + 1; j++) {
        float money = RND.plusInt(200000) - 100000 + RND.rnd.nextFloat();
        totalMoney += money;
        this.insertClientAccount(id, money, "", new Timestamp(0));
      }
      expectedTotalMoneyList.add(totalMoney);
    }
    expectedTotalMoneyList.sort((o1, o2) -> Float.compare(o2, o1));

    ClientRecordListRequest clientRecordListRequest = this.clientRecordListRequestBuilder(0,
      expectedTotalMoneyList.size() + 10, ColumnSortType.TOTALACCOUNTBALANCE, false, "");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordListRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedTotalMoneyList.size());
    for (int i = 0; i < realRecordList.size(); i++)
      assertThat(Util.stringToFloat(realRecordList.get(i).totalAccountBalance))
        .isEqualTo(expectedTotalMoneyList.get(i));
  }

  @Test
  public void method_getRecordList_sortMaxAccountBalanceAscend() {
    this.resetTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    List<Float> expectedMaxMoneyList = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      long id = this.insertClient(charmHelperList);
      List<Float> moneyList = new ArrayList<>();
      for (int j = 0; j < RND.plusInt(5) + 1; j++) {
        float money = RND.plusInt(100000) - 50000 + RND.rnd.nextFloat();
        moneyList.add(money);
        this.insertClientAccount(id, money, "", new Timestamp(0));
      }
      expectedMaxMoneyList.add((float) moneyList.stream().mapToDouble(m -> m).max().getAsDouble());
    }
    expectedMaxMoneyList.sort((o1, o2) -> Float.compare(o1, o2));

    ClientRecordListRequest clientRecordListRequest = this.clientRecordListRequestBuilder(0,
      expectedMaxMoneyList.size() + 10, ColumnSortType.MAXACCOUNTBALANCE, true, "");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordListRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedMaxMoneyList.size());
    for (int i = 0; i < realRecordList.size(); i++)
      assertThat(Util.stringToFloat(realRecordList.get(i).maxAccountBalance))
        .isEqualTo(expectedMaxMoneyList.get(i));
  }

  @Test
  public void method_getRecordList_sortMaxAccountBalanceDescend() {
    this.resetTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    List<Float> expectedMaxMoneyList = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      long id = this.insertClient(charmHelperList);
      List<Float> moneyList = new ArrayList<>();
      for (int j = 0; j < RND.plusInt(4) + 1; j++) {
        float money = RND.plusInt(200000) - 100000 + RND.rnd.nextFloat();
        moneyList.add(money);
        this.insertClientAccount(id, money, "", new Timestamp(0));
      }
      expectedMaxMoneyList.add((float) moneyList.stream().mapToDouble(m -> m).max().getAsDouble());
    }
    expectedMaxMoneyList.sort((o1, o2) -> Float.compare(o2, o1));

    ClientRecordListRequest clientRecordListRequest = this.clientRecordListRequestBuilder(0,
      expectedMaxMoneyList.size() + 10, ColumnSortType.MAXACCOUNTBALANCE, false, "");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordListRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedMaxMoneyList.size());
    for (int i = 0; i < realRecordList.size(); i++)
      assertThat(Util.stringToFloat(realRecordList.get(i).maxAccountBalance))
        .isEqualTo(expectedMaxMoneyList.get(i));
  }

  @Test
  public void method_getRecordList_sortMinAccountBalanceAscend() {
    this.resetTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    List<Float> expectedMinMoneyList = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      long id = this.insertClient(charmHelperList);
      List<Float> moneyList = new ArrayList<>();
      for (int j = 0; j < RND.plusInt(6) + 1; j++) {
        float money = RND.plusInt(100000) - 50000 + RND.rnd.nextFloat();
        moneyList.add(money);
        this.insertClientAccount(id, money, "", new Timestamp(0));
      }
      expectedMinMoneyList.add((float) moneyList.stream().mapToDouble(m -> m).min().getAsDouble());
    }
    expectedMinMoneyList.sort((o1, o2) -> Float.compare(o1, o2));

    ClientRecordListRequest clientRecordListRequest = this.clientRecordListRequestBuilder(0,
      expectedMinMoneyList.size() + 10, ColumnSortType.MINACCOUNTBALANCE, true, "");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordListRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedMinMoneyList.size());
    for (int i = 0; i < realRecordList.size(); i++)
      assertThat(Util.stringToFloat(realRecordList.get(i).minAccountBalance))
        .isEqualTo(expectedMinMoneyList.get(i));
  }

  @Test
  public void method_getRecordList_sortMinAccountBalanceDescend() {
    this.resetTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    List<Float> expectedMinMoneyList = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      long id = this.insertClient(charmHelperList);
      List<Float> moneyList = new ArrayList<>();
      for (int j = 0; j < RND.plusInt(3) + 1; j++) {
        float money = RND.plusInt(200000) - 100000 + RND.rnd.nextFloat();
        moneyList.add(money);
        this.insertClientAccount(id, money, "", new Timestamp(0));
      }
      expectedMinMoneyList.add((float) moneyList.stream().mapToDouble(m -> m).min().getAsDouble());
    }
    expectedMinMoneyList.sort((o1, o2) -> Float.compare(o2, o1));

    ClientRecordListRequest clientRecordListRequest = this.clientRecordListRequestBuilder(0,
      expectedMinMoneyList.size() + 10, ColumnSortType.MINACCOUNTBALANCE, false, "");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordListRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedMinMoneyList.size());
    for (int i = 0; i < realRecordList.size(); i++)
      assertThat(Util.stringToFloat(realRecordList.get(i).minAccountBalance))
        .isEqualTo(expectedMinMoneyList.get(i));
  }

  @Test
  public void method_getRecordList_filter() {
    this.resetTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    String dummyName = "null";
    Set<Long> expectedIdSet = new HashSet<>();
    for (int i = 0; i < 3; i++)
      this.insertClient(dummyName, charmHelperList);
    expectedIdSet.add(this.insertClient("Далана", "квала", "Смук", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 3; i++)
      this.insertClient(dummyName, charmHelperList);
    expectedIdSet.add(this.insertClient("русская", "буква", "Игоревич", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("Квентин", "джон", "Нурланович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 3; i++)
      this.insertClient(dummyName, charmHelperList);

    ClientRecordListRequest clientRecordListRequest =
      this.clientRecordListRequestBuilder(0, expectedIdSet.size(), ColumnSortType.NONE, false, "кв");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordListRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedIdSet.size());
    for (ClientRecord clientRecord : realRecordList) {
      assertThat(clientRecord.id).isIn(expectedIdSet);
      assertThat(clientRecord.fullName)
        .isIn("Далана квала Смук", "русская буква Игоревич", "Квентин джон Нурланович");
    }
  }

  @Test
  public void method_getRecordList_filterWithIgnoredCase() {
    this.resetTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    String dummyName = "null";
    Set<Long> expectedIdSet = new HashSet<>();
    for (int i = 0; i < 3; i++)
      this.insertClient(dummyName, charmHelperList);
    expectedIdSet.add(this.insertClient("игорев", "квала", "Смук", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 3; i++)
      this.insertClient(dummyName, charmHelperList);
    expectedIdSet.add(this.insertClient("русская", "Игорь", "Игоревич", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("Квентин", "джон", "Пигоревич", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 3; i++)
      this.insertClient(dummyName, charmHelperList);

    ClientRecordListRequest clientRecordListRequest =
      this.clientRecordListRequestBuilder(0, expectedIdSet.size() + 3, ColumnSortType.NONE, false, "иГоР");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordListRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedIdSet.size());
    for (ClientRecord clientRecord : realRecordList) {
      assertThat(clientRecord.id).isIn(expectedIdSet);
      assertThat(clientRecord.fullName)
        .isIn("игорев квала Смук", "русская Игорь Игоревич", "Квентин джон Пигоревич");
    }
  }


  @Test
  public void method_getRecordList_filterWithPagination_atBeginning() {
    this.resetTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    String dummyName = "null";
    Set<Long> expectedIdSet = new HashSet<>();
    for (int i = 0; i < 3; i++)
      this.insertClient(dummyName, charmHelperList);
    expectedIdSet.add(this.insertClient("Айнур", "Айбек", "Смагулович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 3; i++)
      this.insertClient(dummyName, charmHelperList);
    expectedIdSet.add(this.insertClient("а", "Нургиза", "в", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 3; i++)
      this.insertClient(dummyName, charmHelperList);
    expectedIdSet.add(this.insertClient("Игорев", "Игорь", "Нурик", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    this.insertClient("Байконур", "Игорь", "Вячеслав", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id);

    ClientRecordListRequest clientRecordListRequest =
      this.clientRecordListRequestBuilder(0, expectedIdSet.size(), ColumnSortType.NONE, false, "нур");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordListRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedIdSet.size());
    for (ClientRecord clientRecord : realRecordList) {
      assertThat(clientRecord.id).isIn(expectedIdSet);
      assertThat(clientRecord.fullName)
        .isIn("Айнур Айбек Смагулович", "а Нургиза в", "Игорев Игорь Нурик", "Байконур Игорь Вячеслав");
    }
  }

  @Test
  public void method_getRecordList_filterWithPagination_atMiddle() {
    this.resetTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    String dummyName = "null";
    Set<Long> skippedIdSet = new HashSet<>();
    Set<Long> expectedIdSet = new HashSet<>();
    skippedIdSet.add(this.insertClient("айбек", "Айбек", "айбековна", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 4; i++)
      this.insertClient(dummyName, charmHelperList);
    skippedIdSet.add(this.insertClient("Айбек", "ПУСТО", "ПУСТО", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("ч", "тнур", "Айбек", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 4; i++)
      this.insertClient(dummyName, charmHelperList);
    expectedIdSet.add(this.insertClient("Нурланов", "Айбек", "Нурланович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    this.insertClient("айбек", "нулл", "Нурланович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id);

    ClientRecordListRequest clientRecordListRequest = this.clientRecordListRequestBuilder(skippedIdSet.size(),
      expectedIdSet.size(), ColumnSortType.NONE, false, "айбек");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordListRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedIdSet.size());
    for (ClientRecord clientRecord : realRecordList) {
      assertThat(clientRecord.id).isIn(expectedIdSet);
      assertThat(clientRecord.fullName)
        .isIn("ч тнур Айбек", "Нурланов Айбек Нурланович");
    }
  }

  @Test
  public void method_getRecordList_filterWithPagination_atEnd() {
    this.resetTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    String dummyName = "null";
    Set<Long> skippedIdSet = new HashSet<>();
    Set<Long> expectedIdSet = new HashSet<>();
    for (int i = 0; i < 4; i++)
      this.insertClient(dummyName, charmHelperList);
    skippedIdSet.add(this.insertClient("кваулы", "ПУСТО", "ПУСТО", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    skippedIdSet.add(this.insertClient("ч", "кваулификация", "о", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 4; i++)
      this.insertClient(dummyName, charmHelperList);
    skippedIdSet.add(this.insertClient("ч", "Квауентин", "о", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    skippedIdSet.add(this.insertClient("ч", "фвф", "Кваута", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("кваукер", "Нурлан", "Нурланович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("Нурланов", "квауква", "Нурланович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));

    ClientRecordListRequest clientRecordListRequest = this.clientRecordListRequestBuilder(skippedIdSet.size(),
      expectedIdSet.size(), ColumnSortType.NONE, false, "квау");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordListRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedIdSet.size());
    for (ClientRecord clientRecord : realRecordList) {
      assertThat(clientRecord.id).isIn(expectedIdSet);
      assertThat(clientRecord.fullName)
        .isIn("кваукер Нурлан Нурланович", "Нурланов квауква Нурланович");
    }
  }

  @Test
  public void method_getRecordList_filterWithPagination_onCut() {
    this.resetTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    String dummyName = "null";
    Set<Long> skippedIdSet = new HashSet<>();
    Set<Long> expectedIdSet = new HashSet<>();
    for (int i = 0; i < 4; i++)
      this.insertClient(dummyName, charmHelperList);
    skippedIdSet.add(this.insertClient("ПУСТО", "ПУСТОп", "ПУСТО", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    skippedIdSet.add(this.insertClient("стоп", "т", "о", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 4; i++)
      this.insertClient(dummyName, charmHelperList);
    skippedIdSet.add(this.insertClient("Нурланов", "стопльник", "Нурланович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("стопка", "кваква", "Нурланович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("Нурланов", "кваква", "СТОп", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 4; i++)
      this.insertClient(dummyName, charmHelperList);

    ClientRecordListRequest clientRecordListRequest = this.clientRecordListRequestBuilder(skippedIdSet.size(),
      expectedIdSet.size() + 10, ColumnSortType.NONE, false, "стоп");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordListRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedIdSet.size());
    for (ClientRecord clientRecord : realRecordList) {
      assertThat(clientRecord.id).isIn(expectedIdSet);
      assertThat(clientRecord.fullName).isIn("стопка кваква Нурланович", "Нурланов кваква СТОп");
    }
  }

  @Test
  public void method_getRecordList_filterWithPagination_atCountExceed() {
    this.resetTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    String dummyName = "null";
    Set<Long> skippedIdSet = new HashSet<>();
    for (int i = 0; i < 4; i++)
      this.insertClient(dummyName, charmHelperList);
    skippedIdSet.add(this.insertClient("Боборис", "ПУСТО", "ПУСТО", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    skippedIdSet.add(this.insertClient("ч", "бобош", "о", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 4; i++)
      this.insertClient(dummyName, charmHelperList);
    skippedIdSet.add(this.insertClient("бобоевик", "Нурлан", "Нурлы", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    skippedIdSet.add(this.insertClient("null", "Нурлан", "Бобово", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));

    ClientRecordListRequest clientRecordListRequest =
      this.clientRecordListRequestBuilder(skippedIdSet.size() + 5, 10, ColumnSortType.NONE, false, "бобо");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordListRequest);

    assertThat(realRecordList).isEmpty();
  }

  @Test
  public void method_removeDetails_default_single() {
    this.resetTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    Set<Long> expectedIdSet = new HashSet<>();
    Long removingId;
    for (int i = 0; i < 4; i++)
      expectedIdSet.add(this.insertClient(charmHelperList));
    removingId = this.insertClient(charmHelperList);
    for (int i = 0; i < 4; i++)
      expectedIdSet.add(this.insertClient(charmHelperList));

    assertThat(clientTestDao.get().selectCountTableClient()).isEqualTo(expectedIdSet.size() + 1);
    assertThat(clientTestDao.get().selectExistSingleTableClient(removingId)).isEqualTo(true);

    clientRegister.get().removeRecord(removingId);

    assertThat(clientTestDao.get().selectCountTableClient()).isEqualTo(expectedIdSet.size());
    assertThat(clientTestDao.get().selectExistSingleTableClient(removingId)).isEqualTo(false);
  }

  @Test
  public void method_removeDetails_default_several() {
    this.resetTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    Set<Long> expectedIdSet = new HashSet<>();
    List<Long> removingIdSet = new ArrayList<>();
    removingIdSet.add(this.insertClient(charmHelperList));
    for (int i = 0; i < 4; i++)
      expectedIdSet.add(this.insertClient(charmHelperList));
    removingIdSet.add(this.insertClient(charmHelperList));
    for (int i = 0; i < 4; i++)
      expectedIdSet.add(this.insertClient(charmHelperList));
    removingIdSet.add(this.insertClient(charmHelperList));
    removingIdSet.add(this.insertClient(charmHelperList));

    assertThat(clientTestDao.get().selectCountTableClient()).isEqualTo(expectedIdSet.size() + removingIdSet.size());
    for (Long removingId : removingIdSet)
      assertThat(clientTestDao.get().selectExistSingleTableClient(removingId)).isEqualTo(true);

    for (Long removingId : removingIdSet)
      clientRegister.get().removeRecord(removingId);

    assertThat(clientTestDao.get().selectCountTableClient()).isEqualTo(expectedIdSet.size());
    for (Long removingId : removingIdSet)
      assertThat(clientTestDao.get().selectExistSingleTableClient(removingId)).isEqualTo(false);
  }

  @Test
  public void method_getDetails_addOperation() {
    this.resetTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    clientTestDao.get().updateDisableSingleTableCharm(2);
    Set<Integer> expectedCharmIdSet = charmHelperList.stream().map(ch -> ch.id).skip(2).collect(Collectors.toSet());

    ClientDetails realClientDetails = clientRegister.get().getDetails(null);
    Set<Integer> realCharmIdSet =
      realClientDetails.charmList.stream().map(c -> c.id).collect(Collectors.toSet());

    assertThat(realClientDetails.id).isNull();
    assertThat(realClientDetails.surname).isEmpty();
    assertThat(realClientDetails.lastname).isEmpty();
    assertThat(realClientDetails.patronymic).isEmpty();
    assertThat(realClientDetails.gender.name()).isEqualTo(Gender.EMPTY.name());
    assertThat(realClientDetails.birthdate).isEmpty();
    assertThat(realClientDetails.charmId).isEqualTo(charmHelperList.get(0).id);
    assertThat(realCharmIdSet.size()).isEqualTo(expectedCharmIdSet.size());
    for (Integer realCharmId : realCharmIdSet)
      assertThat(realCharmId).isIn(expectedCharmIdSet);
    assertThat(realClientDetails.registrationAddressInfo.street).isEmpty();
    assertThat(realClientDetails.registrationAddressInfo.home).isEmpty();
    assertThat(realClientDetails.registrationAddressInfo.flat).isEmpty();
    assertThat(realClientDetails.residentialAddressInfo.street).isEmpty();
    assertThat(realClientDetails.residentialAddressInfo.home).isEmpty();
    assertThat(realClientDetails.residentialAddressInfo.flat).isEmpty();
    assertThat(realClientDetails.phones).isEmpty();
  }

  @Test
  public void method_getDetails_editOperation() {
    this.resetTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    clientTestDao.get().updateDisableSingleTableCharm(3);
    Set<Integer> expectedCharmIdSet = charmHelperList.stream().map(ch -> ch.id).skip(3).collect(Collectors.toSet());
    long expectedId;
    String dummyName = "test";
    for (int i = 0; i < 4; i++)
      this.insertClient(charmHelperList);
    expectedId = this.insertClient(dummyName, dummyName, dummyName, Gender.EMPTY.name(),
      Date.valueOf(LocalDate.ofEpochDay(0)), charmHelperList.get(0).id);
    for (int i = 0; i < 4; i++)
      this.insertClient(charmHelperList);

    this.insertClientAddress(expectedId, AddressType.REG, "Шевченко", "45а", "3");
    this.insertClientAddress(expectedId, AddressType.FACT, "Абай", "21б", "52");

    this.insertClientPhone(expectedId, "+72822590121", PhoneType.HOME);
    this.insertClientPhone(expectedId, "+77071112233", PhoneType.MOBILE);
    this.insertClientPhone(expectedId, "+77471234567", PhoneType.MOBILE);
    this.insertClientPhone(expectedId, "+77770001155", PhoneType.OTHER);

    Date expectedDate = Date.valueOf(LocalDate.now());
    this.updateClient(expectedId, "Фамилия", "Имя", "Отчество", Gender.MALE.name(), expectedDate,
      charmHelperList.get(1).id);

    assertThat(clientTestDao.get().selectExistSingleTableClient(expectedId)).isEqualTo(true);

    ClientDetails realClientDetails = clientRegister.get().getDetails(expectedId);
    Set<Integer> realCharmIdSet =
      realClientDetails.charmList.stream().map(c -> c.id).collect(Collectors.toSet());

    assertThat(realClientDetails.id).isEqualTo(expectedId);
    assertThat(realClientDetails.surname).isEqualTo("Фамилия");
    assertThat(realClientDetails.lastname).isEqualTo("Имя");
    assertThat(realClientDetails.patronymic).isEqualTo("Отчество");
    assertThat(realClientDetails.gender.name()).isEqualTo(Gender.MALE.name());
    assertThat(realClientDetails.birthdate).isEqualTo(expectedDate.toString());
    assertThat(realClientDetails.charmId).isEqualTo(charmHelperList.get(1).id);
    assertThat(realCharmIdSet.size()).isEqualTo(expectedCharmIdSet.size());
    for (Integer realCharmId : realCharmIdSet)
      assertThat(realCharmId).isIn(expectedCharmIdSet);
    assertThat(realClientDetails.registrationAddressInfo.street).isEqualTo("Шевченко");
    assertThat(realClientDetails.registrationAddressInfo.home).isEqualTo("45а");
    assertThat(realClientDetails.registrationAddressInfo.flat).isEqualTo("3");
    assertThat(realClientDetails.residentialAddressInfo.street).isEqualTo("Абай");
    assertThat(realClientDetails.residentialAddressInfo.home).isEqualTo("21б");
    assertThat(realClientDetails.residentialAddressInfo.flat).isEqualTo("52");
    assertThat(realClientDetails.phones).hasSize(4);
    assertThat(realClientDetails.phones.stream().anyMatch(phone ->
      phone.number.equals("+72822590121") && phone.type == PhoneType.HOME)).isEqualTo(true);
    assertThat(realClientDetails.phones.stream().anyMatch(phone ->
      phone.number.equals("+77071112233") && phone.type == PhoneType.MOBILE)).isEqualTo(true);
    assertThat(realClientDetails.phones.stream().anyMatch(phone ->
      phone.number.equals("+77471234567") && phone.type == PhoneType.MOBILE)).isEqualTo(true);
    assertThat(realClientDetails.phones.stream().anyMatch(phone ->
      phone.number.equals("+77770001155") && phone.type == PhoneType.OTHER)).isEqualTo(true);
  }

  @Test
  public void method_saveDetails_addOperation() {
    this.resetTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    long expectedId;
    Date expectedDate = Date.valueOf(LocalDate.now());

    for (int i = 0; i < 4; i++)
      this.insertClient(charmHelperList);
    for (int i = 0; i < 4; i++)
      this.insertClient(charmHelperList);
    expectedId = clientTestDao.get().selectSeqIdNextValueTableCharm() + 1;

    ClientDetailsToSave expectedClientDetailsToSave = new ClientDetailsToSave();
    expectedClientDetailsToSave.id = null;
    expectedClientDetailsToSave.surname = "surname";
    expectedClientDetailsToSave.lastname = "lastname";
    expectedClientDetailsToSave.patronymic = "patronymic";
    expectedClientDetailsToSave.gender = Gender.FEMALE;
    expectedClientDetailsToSave.birthdate = expectedDate.toString();
    expectedClientDetailsToSave.charmId = charmHelperList.get(3).id;
    expectedClientDetailsToSave.registrationAddressInfo = new RegistrationAddressInfo();
    expectedClientDetailsToSave.registrationAddressInfo.street = "street";
    expectedClientDetailsToSave.registrationAddressInfo.home = "home";
    expectedClientDetailsToSave.registrationAddressInfo.flat = "flat";
    expectedClientDetailsToSave.residentialAddressInfo = new ResidentialAddressInfo();
    expectedClientDetailsToSave.residentialAddressInfo.street = "street-res";
    expectedClientDetailsToSave.residentialAddressInfo.home = "home-res";
    expectedClientDetailsToSave.residentialAddressInfo.flat = "flat-res";
    expectedClientDetailsToSave.phones.add(this.phoneBuilder("+71111", PhoneType.HOME));
    expectedClientDetailsToSave.phones.add(this.phoneBuilder("+77071230011", PhoneType.EMBEDDED));
    expectedClientDetailsToSave.phones.add(this.phoneBuilder("+70000", PhoneType.HOME));
    expectedClientDetailsToSave.phones.add(this.phoneBuilder("+00000", PhoneType.OTHER));
    expectedClientDetailsToSave.phones.add(this.phoneBuilder("111111", PhoneType.WORK));
    expectedClientDetailsToSave.phones.add(this.phoneBuilder("222222", PhoneType.OTHER));

    clientRegister.get().saveDetails(expectedClientDetailsToSave);

    assertThat(clientTestDao.get().selectExistSingleTableClient(expectedId)).isEqualTo(true);
    assertThat(expectedClientDetailsToSave.id).isEqualTo(expectedId);
    assertThat(expectedClientDetailsToSave.surname).isEqualTo("surname");
    assertThat(expectedClientDetailsToSave.lastname).isEqualTo("lastname");
    assertThat(expectedClientDetailsToSave.patronymic).isEqualTo("patronymic");
    assertThat(expectedClientDetailsToSave.gender.name()).isEqualTo(Gender.FEMALE.name());
    assertThat(expectedClientDetailsToSave.birthdate).isEqualTo(expectedDate.toString());
    assertThat(expectedClientDetailsToSave.charmId).isEqualTo(charmHelperList.get(3).id);
    assertThat(expectedClientDetailsToSave.registrationAddressInfo.street).isEqualTo("street");
    assertThat(expectedClientDetailsToSave.registrationAddressInfo.home).isEqualTo("home");
    assertThat(expectedClientDetailsToSave.registrationAddressInfo.flat).isEqualTo("flat");
    assertThat(expectedClientDetailsToSave.residentialAddressInfo.street).isEqualTo("street-res");
    assertThat(expectedClientDetailsToSave.residentialAddressInfo.home).isEqualTo("home-res");
    assertThat(expectedClientDetailsToSave.residentialAddressInfo.flat).isEqualTo("flat-res");
    assertThat(expectedClientDetailsToSave.phones).hasSize(6);
    assertThat(expectedClientDetailsToSave.phones.stream().anyMatch(phone ->
      phone.number.equals("+71111") && phone.type == PhoneType.HOME)).isEqualTo(true);
    assertThat(expectedClientDetailsToSave.phones.stream().anyMatch(phone ->
      phone.number.equals("+77071230011") && phone.type == PhoneType.EMBEDDED)).isEqualTo(true);
    assertThat(expectedClientDetailsToSave.phones.stream().anyMatch(phone ->
      phone.number.equals("+70000") && phone.type == PhoneType.HOME)).isEqualTo(true);
    assertThat(expectedClientDetailsToSave.phones.stream().anyMatch(phone ->
      phone.number.equals("+00000") && phone.type == PhoneType.OTHER)).isEqualTo(true);
    assertThat(expectedClientDetailsToSave.phones.stream().anyMatch(phone ->
      phone.number.equals("111111") && phone.type == PhoneType.WORK)).isEqualTo(true);
    assertThat(expectedClientDetailsToSave.phones.stream().anyMatch(phone ->
      phone.number.equals("222222") && phone.type == PhoneType.OTHER)).isEqualTo(true);
  }

  @Test
  public void method_saveDetails_editOperation() {
    this.resetTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    long expectedId;
    Date expectedDate = Date.valueOf(LocalDate.now());
    String dummyName = "test";
    for (int i = 0; i < 4; i++)
      this.insertClient(charmHelperList);
    expectedId = this.insertClient(dummyName, dummyName, dummyName, Gender.EMPTY.name(),
      Date.valueOf(LocalDate.ofEpochDay(0)), charmHelperList.get(0).id);
    for (int i = 0; i < 4; i++)
      this.insertClient(charmHelperList);

    this.insertClientAddress(expectedId, AddressType.REG, "Шевченко", "45а", "3");

    this.insertClientPhone(expectedId, "+72822590121", PhoneType.HOME);
    this.insertClientPhone(expectedId, "111111", PhoneType.OTHER);

    ClientDetailsToSave expectedClientDetailsToSave = new ClientDetailsToSave();
    expectedClientDetailsToSave.id = null;
    expectedClientDetailsToSave.surname = "surname";
    expectedClientDetailsToSave.lastname = "lastname";
    expectedClientDetailsToSave.patronymic = "patronymic";
    expectedClientDetailsToSave.gender = Gender.MALE;
    expectedClientDetailsToSave.birthdate = expectedDate.toString();
    expectedClientDetailsToSave.charmId = charmHelperList.get(1).id;
    expectedClientDetailsToSave.registrationAddressInfo = new RegistrationAddressInfo();
    expectedClientDetailsToSave.registrationAddressInfo.street = "street";
    expectedClientDetailsToSave.registrationAddressInfo.home = "home";
    expectedClientDetailsToSave.registrationAddressInfo.flat = "flat";
    expectedClientDetailsToSave.residentialAddressInfo = new ResidentialAddressInfo();
    expectedClientDetailsToSave.residentialAddressInfo.street = "street-res";
    expectedClientDetailsToSave.residentialAddressInfo.home = "home-res";
    expectedClientDetailsToSave.residentialAddressInfo.flat = "flat-res";
    expectedClientDetailsToSave.phones.add(this.phoneBuilder("+71111", PhoneType.HOME));
    expectedClientDetailsToSave.phones.add(this.phoneBuilder("+77071230011", PhoneType.EMBEDDED));
    expectedClientDetailsToSave.phones.add(this.phoneBuilder("+70000", PhoneType.HOME));
    expectedClientDetailsToSave.phones.add(this.phoneBuilder("+00000", PhoneType.OTHER));
    expectedClientDetailsToSave.phones.add(this.phoneBuilder("111111", PhoneType.WORK));
    expectedClientDetailsToSave.phones.add(this.phoneBuilder("222222", PhoneType.OTHER));

    clientRegister.get().saveDetails(expectedClientDetailsToSave);

    assertThat(clientTestDao.get().selectExistSingleTableClient(expectedId)).isEqualTo(true);
    assertThat(expectedClientDetailsToSave.id).isEqualTo(expectedId);
    assertThat(expectedClientDetailsToSave.surname).isEqualTo("surname");
    assertThat(expectedClientDetailsToSave.lastname).isEqualTo("lastname");
    assertThat(expectedClientDetailsToSave.patronymic).isEqualTo("patronymic");
    assertThat(expectedClientDetailsToSave.gender.name()).isEqualTo(Gender.MALE.name());
    assertThat(expectedClientDetailsToSave.birthdate).isEqualTo(expectedDate.toString());
    assertThat(expectedClientDetailsToSave.charmId).isEqualTo(charmHelperList.get(1).id);
    assertThat(expectedClientDetailsToSave.registrationAddressInfo.street).isEqualTo("street");
    assertThat(expectedClientDetailsToSave.registrationAddressInfo.home).isEqualTo("home");
    assertThat(expectedClientDetailsToSave.registrationAddressInfo.flat).isEqualTo("flat");
    assertThat(expectedClientDetailsToSave.residentialAddressInfo.street).isEqualTo("street-res");
    assertThat(expectedClientDetailsToSave.residentialAddressInfo.home).isEqualTo("home-res");
    assertThat(expectedClientDetailsToSave.residentialAddressInfo.flat).isEqualTo("flat-res");
    assertThat(expectedClientDetailsToSave.phones).hasSize(7);
    assertThat(expectedClientDetailsToSave.phones.stream().anyMatch(phone ->
      phone.number.equals("+71111") && phone.type == PhoneType.HOME)).isEqualTo(true);
    assertThat(expectedClientDetailsToSave.phones.stream().anyMatch(phone ->
      phone.number.equals("+77071230011") && phone.type == PhoneType.EMBEDDED)).isEqualTo(true);
    assertThat(expectedClientDetailsToSave.phones.stream().anyMatch(phone ->
      phone.number.equals("+70000") && phone.type == PhoneType.HOME)).isEqualTo(true);
    assertThat(expectedClientDetailsToSave.phones.stream().anyMatch(phone ->
      phone.number.equals("+00000") && phone.type == PhoneType.OTHER)).isEqualTo(true);
    assertThat(expectedClientDetailsToSave.phones.stream().anyMatch(phone ->
      phone.number.equals("111111") && phone.type == PhoneType.WORK)).isEqualTo(true);
    assertThat(expectedClientDetailsToSave.phones.stream().anyMatch(phone ->
      phone.number.equals("222222") && phone.type == PhoneType.OTHER)).isEqualTo(true);
    assertThat(expectedClientDetailsToSave.phones.stream().anyMatch(phone ->
      phone.number.equals("+72822590121") && phone.type == PhoneType.HOME)).isEqualTo(true);
  }

  @Test(expectedExceptions = InvalidParameter.class)
  public void method_getCount_filterNull() {
    clientRegister.get().getCount(null);
  }

  @Test(expectedExceptions = InvalidParameter.class)
  public void method_getRecordList_countToSkipNegative() {
    ClientRecordListRequest clientRecordListRequest =
      clientRecordListRequestBuilder(-10, 0, ColumnSortType.NONE, false, "");

    clientRegister.get().getRecordList(clientRecordListRequest);
  }

  @Test(expectedExceptions = InvalidParameter.class)
  public void method_getRecordList_countZero() {
    ClientRecordListRequest clientRecordListRequest =
      clientRecordListRequestBuilder(0, 0, ColumnSortType.NONE, false, "");

    clientRegister.get().getRecordList(clientRecordListRequest);
  }

  @Test(expectedExceptions = InvalidParameter.class)
  public void method_getRecordList_countNegative() {
    ClientRecordListRequest clientRecordListRequest =
      clientRecordListRequestBuilder(0, -10, ColumnSortType.NONE, false, "");

    clientRegister.get().getRecordList(clientRecordListRequest);
  }

  @Test(expectedExceptions = InvalidParameter.class)
  public void method_removeRecord_idNegative() {
    clientRegister.get().removeRecord(-100);
  }

  @Test(expectedExceptions = InvalidParameter.class)
  public void method_getDetails_idNull() {
    clientRegister.get().getDetails(null);
  }

  @Test(expectedExceptions = InvalidParameter.class)
  public void method_getDetails_idNegative() {
    clientRegister.get().getDetails(-10L);
  }

  @Test(expectedExceptions = InvalidParameter.class)
  public void method_saveDetails_detailsNull() {
    clientRegister.get().saveDetails(null);
  }

  @Test(expectedExceptions = InvalidParameter.class)
  public void method_saveDetails_detailsMemberNull() {
    ClientDetailsToSave clientDetailsToSave = new ClientDetailsToSave();
    clientDetailsToSave.id = null;
    clientDetailsToSave.surname = null;
    clientDetailsToSave.lastname = null;
    clientDetailsToSave.patronymic = null;
    clientDetailsToSave.gender = Gender.EMPTY;
    clientDetailsToSave.birthdate = null;
    clientDetailsToSave.charmId = -10;
    clientDetailsToSave.registrationAddressInfo = null;
    clientDetailsToSave.residentialAddressInfo = null;

    clientRegister.get().saveDetails(clientDetailsToSave);
  }

  private static class CharmHelper {
    int id;
    String name;
    String description;
    float energy;
  }

  private CharmHelper declareAndInsertCharm(String name, String description, float energy) {
    int id = clientTestDao.get().selectSeqIdNextValueTableCharm();
    clientTestDao.get().insertCharm(id, name, description, energy);

    CharmHelper charmHelper = new CharmHelper();
    charmHelper.id = id;
    charmHelper.name = name;
    charmHelper.description = description;
    charmHelper.energy = energy;

    return charmHelper;
  }

  private List<CharmHelper> declareAndInsertCharms() {
    List<CharmHelper> charmHelperList = new ArrayList<>();

    charmHelperList.add(this.declareAndInsertCharm("Не указан", "Неизвестно", 0f));
    charmHelperList.add(this.declareAndInsertCharm("Спокойный", "Само спокойствие", 10f));
    charmHelperList.add(this.declareAndInsertCharm("Буйный", "Лучше лишний раз не трогать", 30f));
    charmHelperList.add(this.declareAndInsertCharm("Загадочный", "О чем он думает?", 8f));
    charmHelperList.add(this.declareAndInsertCharm("Открытый", "С ним приятно общаться!", 20f));
    charmHelperList.add(this.declareAndInsertCharm("Понимающий", "Он всегда выслушает", 15f));
    charmHelperList.add(this.declareAndInsertCharm("Консервативный", "Скучно...", 5f));

    return charmHelperList;
  }

  private void insertClientPhone(long client, String number, PhoneType type) {
    clientTestDao.get().insertClientPhone(client, number, type.name());
  }

  private void insertClientAddress(long client, AddressType addressType, String street, String house, String flat) {
    clientTestDao.get().insertClientAddr(client, addressType.name(), street, house, flat);
  }

  private long insertClientAccount(long clientId, float money, String code, Timestamp timestamp) {
    long id = clientTestDao.get().selectSeqIdNextValueTableClientAccount();
    clientTestDao.get().insertClientAccount(id, clientId, money, code, timestamp);
    return id;
  }

  private long insertClient(List<CharmHelper> charmHelperList) {
    long id = clientTestDao.get().selectSeqIdNextValueTableClientAccount();
    clientTestDao.get().insertClient(id, RND.str(RND.plusInt(5) + 5), RND.str(RND.plusInt(5) + 5),
      RND.str(RND.plusInt(5) + 5), Gender.values()[RND.plusInt(Gender.values().length)].name(), Util.generateDate(),
      charmHelperList.get(RND.plusInt(charmHelperList.size())).id);
    return id;
  }

  private long insertClient(String dummyName, List<CharmHelper> charmHelperList) {
    long id = clientTestDao.get().selectSeqIdNextValueTableClientAccount();
    clientTestDao.get().insertClient(id, dummyName, dummyName, dummyName,
      Gender.values()[RND.plusInt(Gender.values().length)].name(), Util.generateDate(),
      charmHelperList.get(RND.plusInt(charmHelperList.size())).id);
    return id;
  }

  private long insertClient(String surname, String name, String patronymic, String gender, LocalDate date, int charmId) {
    long id = clientTestDao.get().selectSeqIdNextValueTableClientAccount();
    clientTestDao.get().insertClient(id, surname, name, patronymic, gender, Date.valueOf(date), charmId);
    return id;
  }

  private long insertClient(String surname, String name, String patronymic, String gender, Date date, int charmId) {
    long id = clientTestDao.get().selectSeqIdNextValueTableClientAccount();
    clientTestDao.get().insertClient(id, surname, name, patronymic, gender, date, charmId);
    return id;
  }

  private void updateClient(long id, String surname, String name, String patronymic, String gender, Date date, int charmId) {
    clientTestDao.get().updateClient(id, surname, name, patronymic, gender, date, charmId);
  }

  private Phone phoneBuilder(String number, PhoneType type) {
    Phone phone = new Phone();
    phone.number = number;
    phone.type = type;

    return phone;
  }

  private ClientRecordListRequest clientRecordListRequestBuilder(long clientRecordCountToSkip, long clientRecordCount,
                                                                 ColumnSortType columnSortType, boolean sortAscend,
                                                                 String nameFilter) {
    ClientRecordListRequest clientRecordListRequest = new ClientRecordListRequest();
    clientRecordListRequest.clientRecordCountToSkip = clientRecordCountToSkip;
    clientRecordListRequest.clientRecordCount = clientRecordCount;
    clientRecordListRequest.columnSortType = columnSortType;
    clientRecordListRequest.sortAscend = sortAscend;
    clientRecordListRequest.nameFilter = nameFilter;

    return clientRecordListRequest;
  }

  private void resetTablesAll() {
    clientTestDao.get().deleteAllTableClientAccount();
    clientTestDao.get().deleteAllTableClientAddr();
    clientTestDao.get().deleteAllTableClientPhone();
    clientTestDao.get().deleteAllTableClient();
    clientTestDao.get().deleteAllTableCharm();
  }
}
