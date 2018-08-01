package kz.greetgo.sandbox.db.register_impl;

import com.itextpdf.text.DocumentException;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientsRegister;
import kz.greetgo.sandbox.db.dao.ReportsDao;
import kz.greetgo.sandbox.db.test.dao.ClientsTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

    // TODO(DONE): простовляй такие пометки непосредственно в месте вызова проверяемого методы !!!
    //
    //
    for (RandomClientGenerator.ClientBundle clientBundle : clientBundles) {
      ClientToSave clientToSave = clientBundle.getClientToSave();
      //
      //
      ClientRecord e = clientsRegister.get().addClientToSave(clientToSave);
      //
      //
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

  @Test//FIXME DONE При ошибке не понятно что заполняется не правильно
  public void testGetClientRecords() {
    clearEntities();
    List<RandomClientGenerator.ClientBundle> clientBundles = RandomClientGenerator.generate(10);
    insertBundles(clientBundles);

    QueryFilter filter = new QueryFilter(0, 5, "DESC", "name", "");

    //TODO(DONE): TableResponse - поменяй название класса на более понятный !!!
    //TODO(DONE): всё ещё непонятное название.
    ClientRecordsListPage actual = RandomClientGenerator.getClientRecords(clientBundles, filter);

    //
    //
    ClientRecordsListPage test = clientsRegister.get()
      .getClientRecords(filter);
    //
    //

    int j = 0;
    test.list.forEach(record -> {
      assertThat(record).as(j + "th element").isEqualTo(actual.list.get(j));
    });
  }


  //вот как правильно надо написать тест
  //должен проверять правильность заполнения полей
  @Test
  public void testGetClientRecords______RIGHT___checkFillingRecordFields() {//нужно правильно выбирать название теста
    clearEntities();
    List<RandomClientGenerator.ClientBundle> clientBundles = RandomClientGenerator.generate(1);//1 - будет достаточно
    insertBundles(clientBundles);

    QueryFilter filter = new QueryFilter(0, 5, "DESC", "name", "");//если элемент 1 то сортировка не нужна

    ClientRecordsListPage actual = RandomClientGenerator.getClientRecords(clientBundles, filter);

    //
    //
    ClientRecordsListPage test = clientsRegister.get()
      .getClientRecords(filter);
    //
    //

    assertThat(test.list.get(0).surname).isEqualTo(actual.list.get(0).surname);
    assertThat(test.list.get(0).name).isEqualTo(actual.list.get(0).name);
    //...
    // а поля нужно проверять отдельно
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

    // TODO(DONE): Называй переменные со смыслом, понятным для всех.
    ClientRecord actualClientRecord = clientsTestDao.get().getRecordClientById(newBundle.getClient().id);

    assertThat(newClientRecord).isNotEqualTo(old);
    assertThat(newClientRecord).isEqualTo(newBundle.getClientRecord());
    assertThat(newClientRecord).isEqualTo(actualClientRecord);
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
