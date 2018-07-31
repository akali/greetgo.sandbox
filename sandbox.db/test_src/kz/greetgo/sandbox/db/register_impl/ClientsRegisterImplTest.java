package kz.greetgo.sandbox.db.register_impl;

import com.itextpdf.text.DocumentException;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.Charm;
import kz.greetgo.sandbox.controller.model.ClientDetail;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.model.FilteredTable;
import kz.greetgo.sandbox.controller.model.QueryFilter;
import kz.greetgo.sandbox.controller.model.ReportType;
import kz.greetgo.sandbox.controller.model.TransactionType;
import kz.greetgo.sandbox.controller.register.ClientsRegister;
import kz.greetgo.sandbox.db.dao.ReportsDao;
import kz.greetgo.sandbox.db.test.dao.ClientsTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;


public class ClientsRegisterImplTest extends ParentTestNg {

  public BeanGetter<ClientsTestDao> clientsTestDao;
  public BeanGetter<ClientsRegister> clientsRegister;
  public BeanGetter<ReportsDao> reportsDao;

  @Test
  public void inserting100RandomClients() {

    clearEntities();

    List<RandomClientGenerator.ClientBundle> clientBundles =
      RandomClientGenerator.generate(10);

    insertTestingCharms(clientBundles.get(0).getCharms());
    insertTestingTransactionTypes(clientBundles.get(0).getTransactionTypes());

    List<ClientRecord> testClientRecords = new ArrayList<>(), actualClientRecords = new ArrayList<>();

    // TODO(!DONE): простовляй такие пометки непосредственно в месте вызова проверяемого методы !!!
    //
    //
    for (RandomClientGenerator.ClientBundle clientBundle : clientBundles) {
      ClientToSave clientToSave = clientBundle.getClientToSave();
      ClientRecord e = clientsRegister.get().addClientToSave(clientToSave);
      testClientRecords.add(e);
      ClientRecord actualClientRecord = clientBundle.getClientRecord();
      actualClientRecord.total = actualClientRecord.max = actualClientRecord.min = 0;
      actualClientRecords.add(actualClientRecord);
    }
    //
    //

    assertThat(testClientRecords).containsAll(actualClientRecords);

    for (RandomClientGenerator.ClientBundle clientBundle : clientBundles) {
      assertThat(clientsTestDao.get().getRecordClientById(clientBundle.getClient().id)).isEqualTo(
        clientBundle.getClientRecord()
      );
    }
  }

  @Test
  public void testGetCharms() {
    List<Charm> charms = RandomClientGenerator.getCharms();
    clearEntities();

    charms.forEach(clientsTestDao.get()::insertCharm);

    //
    //
    List<Charm> testCharms = clientsRegister.get().getCharms();
    //
    //

    assertThat(testCharms).containsAll(charms);
  }

  @Test//FIXME При ошибке не понятно что заполняется не правильно
  public void testGetClientRecords() throws SQLException {
    clearEntities();
    List<RandomClientGenerator.ClientBundle> clientBundles = RandomClientGenerator.generate(10);
    insertBundles(clientBundles);

    QueryFilter filter = new QueryFilter(0, 5, "DESC", "name", "");

    //TODO(!DONE): TableResponse - поменяй название класса на более понятный !!!
    //TODO: всё ещё непонятное название.
    FilteredTable actual = RandomClientGenerator.getClientRecords(clientBundles, filter);

    //
    //
    FilteredTable test = clientsRegister.get()
      .getClientRecords(filter);
    //
    //

    assertThat(test.list).isEqualTo(actual.list);
  }


  //вот как правильно надо написать тест
  //должен проверять правильность заполнения полей
  @Test
  public void testGetClientRecords______RIGHT___checkFillingRecordFields() {//нужно правильно выбирать название теста
    clearEntities();
    List<RandomClientGenerator.ClientBundle> clientBundles = RandomClientGenerator.generate(1);//1 - будет достаточно
    insertBundles(clientBundles);

    QueryFilter filter = new QueryFilter(0, 5, "DESC", "name", "");//если элемент 1 то сортировка не нужна

    FilteredTable actual = RandomClientGenerator.getClientRecords(clientBundles, filter);

    //
    //
    FilteredTable test = clientsRegister.get()
      .getClientRecords(filter);
    //
    //

    assertThat(test.list.get(0).surname).isEqualTo(actual.list.get(0).surname);
    assertThat(test.list.get(0).name).isEqualTo(actual.list.get(0).name);
    //...
    // а поля нужно проверять отдельно
  }


  //FIXME pompei Если тест упал, то непонятно какая сортировка не работает
  @Test
  public void getClientRecordsCheckSorting() {
    clearEntities();
    List<RandomClientGenerator.ClientBundle> clientBundles = RandomClientGenerator.generate(10);
    insertBundles(clientBundles);

    //FIXME pompei use DataProvider
    List<QueryFilter> filters = RandomClientGenerator.generateFilters(10, Arrays.asList("name", "total", "age", "total", "max", "min"));

    filters.forEach(filter -> {

      //
      //
      FilteredTable test = clientsRegister.get().getClientRecords(filter);
      //
      //

      assertThat(test.list).isSortedAccordingTo(
        (t1, t2) -> {
          int result = 0;
          switch (filter.active.toLowerCase()) {
            case "name":
              result = t1.name.compareTo(t2.name);
              break;
            case "total":
              result = Float.compare(t1.total, t2.total);
              break;
            case "max":
              result = Float.compare(t1.max, t2.max);
              break;
            case "min":
              result = Float.compare(t1.min, t2.min);
              break;
            case "charm":
              result = t1.charm.compareTo(t2.charm);
              break;
            case "age":
              result = Integer.compare(t1.age, t2.age);
              break;
            default:
              result = Integer.compare(t1.id, t2.id);
          }
          if (filter.direction != null && filter.direction.toLowerCase().equals("desc")) {
            result = -result;
          }
          return result;
        });

      // TODO: тебе надо проверить ещё правильные ли объекты метод выдаёт.
      // TODO: что с этим замечанием?
    });
  }

  private void insertBundles(List<RandomClientGenerator.ClientBundle> clientBundles) {
    insertTestingCharms(RandomClientGenerator.getCharms());
    insertTestingTransactionTypes(clientBundles.get(0).getTransactionTypes());

    clientBundles.forEach(clientBundle -> {
      clientsTestDao.get().insertClient(clientBundle.getClient());
      clientsTestDao.get().insertClientAddress(clientBundle.getRegAddress());
      if (clientBundle.getFactAddress() != null)
        clientsTestDao.get().insertClientAddress(clientBundle.getFactAddress());

      clientBundle.getPhones().forEach(clientsTestDao.get()::insertClientPhone);
      clientBundle.getAccounts().forEach(clientsTestDao.get()::insertClientAccount);
      clientBundle.getTransactions().forEach(clientsTestDao.get()::insertClientAccountTransaction);
    });
  }

  @Test
  public void testClientDetailIsEqualByAge() {
    clearEntities();
    List<RandomClientGenerator.ClientBundle> bundles = RandomClientGenerator.generate(10);
    ClientDetail detail = bundles.get(0).getClientDetail();
    ClientDetail copy = detail.getCopy();
    copy.birthDate++;
    assertThat(detail.equals(copy)).isTrue();
  }

  @Test
  public void testGetClientDetailsById() {

    clearEntities();
    List<RandomClientGenerator.ClientBundle> bundles = RandomClientGenerator.generate(3);
    List<RandomClientGenerator.ClientBundle> fakeBundles = RandomClientGenerator.generate(3);
    insertBundles(bundles);

//    bundles.forEach(clientBundle -> {
//      System.out.println("X " + clientsRegister.get().getClientDetailsById(clientBundle.getClient().id).getCharm());
//      System.out.println("Y " + clientBundle.getClientDetail().getCharm());
//    });

    bundles.forEach(clientBundle -> assertThat(
      clientsRegister.get().getClientDetailsById(clientBundle.getClient().id).equals(clientBundle.getClientDetail())
    ).isTrue());

    fakeBundles.forEach(clientBundle -> assertThat(
      clientsRegister.get().getClientDetailsById(clientBundle.getClient().id).equals(clientBundle.getClientDetail())
    ).isFalse());
  }

  @Test
  public void testEditClientToSave() {
    clearEntities();
    List<RandomClientGenerator.ClientBundle> bundles = RandomClientGenerator.generate(5);
    insertBundles(bundles);
    ClientRecord old = bundles.get(0).getClientRecord();

    ClientToSave clientToSave = bundles.get(0).getClientToSave();

    RandomClientGenerator.ClientBundle newBundle = RandomClientGenerator.generateBundleById(clientToSave.id);
    newBundle.setAccounts(bundles.get(0).getAccounts());

    //
    //
    ClientRecord newClientRecord = clientsRegister.get().editClientToSave(newBundle.getClientToSave());
    //
    //

    // TODO: Называй переменные со смыслом, понятным для всех.
    ClientRecord my = clientsTestDao.get().getRecordClientById(newBundle.getClient().id);

    assertThat(newClientRecord).isNotEqualTo(old);
    assertThat(newClientRecord).isEqualTo(newBundle.getClientRecord());
    assertThat(newClientRecord).isEqualTo(my);
  }

  @Test
  public void testRemoveClientById() {
    int size = 3;
    List<RandomClientGenerator.ClientBundle> bundles = RandomClientGenerator.generate(size);

    for (int msk = 0; msk < (1 << size); ++msk) {
      clearEntities();
      insertBundles(bundles);
      List<Integer> toRemove = new ArrayList<>();
      //
      //
      for (int i = 0; i < size; ++i) {
        if (((1 << i) & msk) > 0) {
          int id = bundles.get(i).getClient().id;
          toRemove.add(i);
          clientsRegister.get().removeClientById(id);
        }
      }
      //
      //
      toRemove.forEach(
        f -> assertThat(clientsRegister.get()
          .getClientDetailsById(bundles.get(f).getClient().id))
          .isNull()
      );
      //
      //

      toRemove.forEach(f -> clientsTestDao.get().insertClient(bundles.get(f).getClient()));
    }
  }

  @Test
  public void generatorTest() {
    clearEntities();
    List<RandomClientGenerator.ClientBundle> clientBundles = RandomClientGenerator.generate(5);
    insertBundles(clientBundles);
  }

  private void clearEntities() {
    clientsTestDao.get().clearCharm();
    clientsTestDao.get().clearClientAddress();
    clientsTestDao.get().clearClient();

    clientsTestDao.get().clearClientPhone();
    clientsTestDao.get().clearClientAccount();
    clientsTestDao.get().clearClientAccountTransaction();

    clientsTestDao.get().clearTransactionType();

    clientsTestDao.get().resetCharmIncrementor();
    clientsTestDao.get().resetClientIncrementor();

    clientsTestDao.get().resetClientAccountIncrementor();
    clientsTestDao.get().resetClientAccountTransactionIncrementor();

    clientsTestDao.get().resetTransactionTypeIncrementor();
  }

  private void insertTestingTransactionTypes(List<TransactionType> transactionTypes) {
    transactionTypes.forEach(clientsTestDao.get()::insertTransactionType);
  }

  private void insertTestingCharms(List<Charm> charms) {
    charms.forEach(clientsTestDao.get()::insertCharm);
  }

  // TODO: Неверный тест на источник данных
  // Посмотри видео Жени. Внём всё рассказано.
  // TODO: Для тестирования нужно использовать тестовый рендерер, тоже есть видео.
  @Test()
  private void generateReportTest() {
    clearEntities();
    List<RandomClientGenerator.ClientBundle> bundles = RandomClientGenerator.generate(20);
    insertBundles(bundles);
    String id = null;

    try {
      id = clientsRegister.get().generateReport(
        ReportType.XLSX,
        new QueryFilter(0, 10, "ASC", "name", ""),
        "p1"
      );
    } catch (IOException | DocumentException e) {
      e.printStackTrace();
    }
    String path = reportsDao.get().getFile(id);
    assertThat(new File(path).exists()).isTrue();
  }
}
