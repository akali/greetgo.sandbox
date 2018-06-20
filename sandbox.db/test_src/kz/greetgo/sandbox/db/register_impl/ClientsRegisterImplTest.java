package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientsRegister;
import kz.greetgo.sandbox.db.test.dao.ClientsTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.Test;

import java.util.*;

import static org.fest.assertions.api.Assertions.assertThat;


public class ClientsRegisterImplTest extends ParentTestNg {

  public BeanGetter<ClientsTestDao> clientsTestDao;
  public BeanGetter<ClientsRegister> clientsRegister;

  @Test
  public void inserting100RandomClients() {

    clearEntities();

    List<RandomClientGenerator.ClientBundle> clientBundles =
      RandomClientGenerator.generate(10);

    insertTestingCharms(clientBundles.get(0).getCharms());
    insertTestingTransactionTypes(clientBundles.get(0).getTransactionTypes());

    List<ClientRecord> testClientRecords = new ArrayList<>(), actualClientRecords = new ArrayList<>();

    //
    //
    for (RandomClientGenerator.ClientBundle clientBundle : clientBundles) {
      testClientRecords.add(clientsRegister.get().addClientToSave(clientBundle.getClientToSave()));
      ClientRecord actualClientRecord = clientBundle.getClientRecord();
      actualClientRecord.total = actualClientRecord.max = actualClientRecord.min = 0;
      actualClientRecords.add(actualClientRecord);
    }
    //
    //

    assertThat(testClientRecords).containsAll(actualClientRecords);
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

  @Test
  public void testGetClientRecords() {
    clearEntities();
    List<RandomClientGenerator.ClientBundle> clientBundles = RandomClientGenerator.generate(10);
    insertBundles(clientBundles, false, false);

    QueryFilter filter = new QueryFilter(0, 5, "DESC", "name", "");

    TableResponse actual = RandomClientGenerator.getClientRecords(clientBundles, filter);

    //
    //
    TableResponse test = clientsRegister.get()
      .getClientRecords(filter);
    //
    //

    assertThat(test.list).isEqualTo(actual.list);
  }

  @Test
  public void getClientRecordsCheckSorting() {
    clearEntities();
    List<RandomClientGenerator.ClientBundle> clientBundles = RandomClientGenerator.generate(10);
    insertBundles(clientBundles, false, false);

    List<QueryFilter> filters = RandomClientGenerator.generateFilters(10, Arrays.asList("name", "total", "age", "total", "max", "min"));

    filters.forEach(filter -> {
      //
      //
      TableResponse test = clientsRegister.get()
        .getClientRecords(filter);
      //
      //

      assertThat(test.list).isSortedAccordingTo(
        (t1, t2) -> {
          int result = 0;
          switch(filter.active.toLowerCase()) {
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
    });
  }

  private void insertBundles(List<RandomClientGenerator.ClientBundle> clientBundles, boolean insertCharms, boolean insertTransactionTypes) {
    if (insertCharms)
      insertTestingCharms(clientBundles.get(0).getCharms());
    if (insertTransactionTypes)
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
  public void testGetClientDetailsById() {
  }

  @Test
  public void testEditClientToSave() {
  }

  @Test
  public void testRemoveClientById() {
    int size = 3;
    List<RandomClientGenerator.ClientBundle> bundles = RandomClientGenerator.generate(size);

    for (int msk = 0; msk < (1 << size); ++msk) {
      clearEntities();
      insertBundles(bundles, true, true);
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
    insertBundles(clientBundles, false, false);
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
}
