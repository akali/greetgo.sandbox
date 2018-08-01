package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientsRegister;
import kz.greetgo.sandbox.db.test.dao.ClientsTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.fest.assertions.api.Assertions.assertThat;

public class CheckSortingClientsRegisterImplTest extends ParentTestNg {
  public BeanGetter<ClientsTestDao> clientsTestDao;
  public BeanGetter<ClientsRegister> clientsRegister;
  private List<RandomClientGenerator.ClientBundle> currentBundles;

  @DataProvider(name = "Filters")
  public Object[][] filters() {
    List<QueryFilter> filters = RandomClientGenerator.generateFilters(5, Arrays.asList("name", "total", "age", "total", "max", "min"));
    Object[][] obj = new Object[filters.size()][];
    final int[] sz = {0};
    filters.forEach(queryFilter -> obj[sz[0]++] = new Object[]{queryFilter});
    return obj;
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

  @BeforeClass
  public void insertBundles() {
    clearEntities();
    List<RandomClientGenerator.ClientBundle> clientBundles = RandomClientGenerator.generate(10);
    insertBundles(clientBundles);
    currentBundles = clientBundles;
  }

  private void insertTestingCharms(List<Charm> charms) {
    charms.forEach(clientsTestDao.get()::insertCharm);
  }

  private void insertTestingTransactionTypes(List<TransactionType> transactionTypes) {
    transactionTypes.forEach(clientsTestDao.get()::insertTransactionType);
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

  //FIXME(DONE) pompei Если тест упал, то непонятно какая сортировка не работает


  @Test(dataProvider = "Filters")
  public void getClientRecordsCheckSorting(QueryFilter filter) {
    //FIXME(DONE) pompei use DataProvider

    //
    //
    ClientRecordsListPage test = clientsRegister.get().getClientRecords(filter);
    //
    //

    Comparator cmp = (a, b) -> {
      ClientRecord t1 = (ClientRecord) a,
        t2 = (ClientRecord) b;
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
      if (result == 0) {
        result = Integer.compare(t1.id, t2.id);
      }
      if (filter.direction != null && filter.direction.toLowerCase().equals("desc")) {
        result = -result;
      }
      return result;
    };

    assertThat(test.list)
      .overridingErrorMessage("active = " + filter.active + ", order = " + filter.direction)
      .isSortedAccordingTo(cmp);

    List<ClientRecord> actualList =
      (List<ClientRecord>) currentBundles.stream().map(clientBundle -> clientBundle.getClientRecord()).sorted(cmp).skip(filter.start).limit(filter.limit).collect(Collectors.toList());

    int j = 0;

    System.out.println(test.list);
    System.out.println("/---------/");
    System.out.println(actualList);

    for (ClientRecord anActualList : actualList) {
      assertThat(anActualList).isEqualTo(test.list.get(j++));
    }

    // TODO(DONE): тебе надо проверить ещё правильные ли объекты метод выдаёт.
    // TODO(DONE): что с этим замечанием?
  }
}
