package kz.greetgo.learn.migration.core;

import kz.greetgo.learn.migration.__prepare__.DropCreateMgrSrcDb;
import kz.greetgo.learn.migration.__prepare__.DropCreateOperDb;
import kz.greetgo.learn.migration.core.innerMigration.CiaMigration;
import kz.greetgo.learn.migration.core.innerMigration.FrsMigration;
import kz.greetgo.learn.migration.core.innerMigration.Migration;
import kz.greetgo.learn.migration.interfaces.ConnectionConfig;
import kz.greetgo.learn.migration.util.ConfigFiles;
import kz.greetgo.learn.migration.util.ConnectionUtils;
import kz.greetgo.learn.migration.util.DBHelper;
import kz.greetgo.learn.migration.util.TimeUtils;
import kz.greetgo.sandbox.controller.model.AddressType;
import kz.greetgo.sandbox.controller.model.ClientAccount;
import kz.greetgo.sandbox.controller.model.ClientAccountTransaction;
import kz.greetgo.sandbox.controller.model.ClientAddress;
import kz.greetgo.sandbox.db.register_impl.RandomClientGenerator;
import kz.greetgo.sandbox.db.register_impl.RandomClientGenerator.ClientBundle;
import org.testng.annotations.Test;

import java.sql.Date;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.fest.assertions.api.Assertions.assertThat;

public class MigrationTest {
  HashMap<String, String> tmpSqlVars = new HashMap<>();

  @Test
  public void testCiaDuplicates() throws Exception {
    int bundlesCount = 50;
    List<ClientBundle> bundles = RandomClientGenerator.generate(bundlesCount);

    DropCreateMgrSrcDb.execute();
    DropCreateOperDb.execute();

    new DBHelper<>().useSrcDb(true).run(connection -> {
      PreparedStatement ps = connection.prepareStatement("insert into transition_cia(record_data) values (?)");

      for (ClientBundle clientBundle : bundles) {
        for (int i = 0; i < 3; ++i) {
          ClientBundle bundle = clientBundle.clone().swapNames();
          String data = bundle.toXml();
          ps.setString(1, data);
          ps.addBatch();
        }
        for (int i = 0; i < 3; ++i) {
          ClientBundle bundle = clientBundle.clone().swapAddress();
          String data = bundle.toXml();
          ps.setString(1, data);
          ps.addBatch();
        }
        String data = clientBundle.toXml();
        ps.setString(1, data);
        ps.addBatch();
      }

      ps.executeBatch();
      return null;
    });

    //
    //
    ConnectionConfig operCC = ConnectionUtils.fileToConnectionConfig(ConfigFiles.homeDb());
    ConnectionConfig ciaCC = ConnectionUtils.fileToConnectionConfig(ConfigFiles.migrationDb());

    try (Migration migration = new CiaMigration(operCC, ciaCC)) {
      migration.chunkSize = 250_000;
      migration.uploadMaxBatchSize = 50_000;
      migration.downloadMaxBatchSize = 50_000;

      migration.migrate();

      tmpSqlVars = migration.tmpSqlVars;
    }
    //
    //

    new DBHelper<>().useSrcDb(false).run(connection -> {
      for (ClientBundle bundle : bundles) {
        long id = 1;
        try (PreparedStatement ps = connection.prepareStatement(
          r("select id from client where cia_id = ?"))
        ) {
          ps.setString(1, String.valueOf(bundle.getClientDetail().getId()));
          ResultSet rs = ps.executeQuery();
          int cnt = 0;
          while (rs.next()) {
            ++cnt;
            id = rs.getLong("id");
          }
          assertThat(cnt).isEqualTo(1);
        }
        try (PreparedStatement ps = connection.prepareStatement(
          r("select cia_id, surname, name, patronymic, birth_date from client where id = ?"))
        ) {
          ps.setLong(1, id);
          ResultSet rs = ps.executeQuery();
          while (rs.next()) {
            String name = rs.getString("name");
            String surname = rs.getString("surname");
            String patronymic = rs.getString("patronymic");
            Date birthDate = rs.getDate("birth_date");

            assertThat(name).isEqualTo(bundle.getClient().name);
            assertThat(surname).isEqualTo(bundle.getClient().surname);
            assertThat(patronymic).isEqualTo(bundle.getClient().patronymic);
            assertThat(birthDate.toLocalDate().getYear()).isEqualTo(bundle.getClient().birth_date.toLocalDate().getYear());
            assertThat(birthDate.toLocalDate().getDayOfYear()).isEqualTo(bundle.getClient().birth_date.toLocalDate().getDayOfYear());
          }
        }
        try (PreparedStatement ps = connection.prepareStatement(
          r("select type, street, house, flat from clientAddress where client = ?"))
        ) {
          ps.setLong(1, id);
          ResultSet rs = ps.executeQuery();
          while (rs.next()) {
            String type = rs.getString("type");
            String street = rs.getString("street");
            String house = rs.getString("house");
            String flat = rs.getString("flat");

            assertThat(type.toLowerCase()).isIn("reg", "fact");

            AddressType addressType = AddressType.valueOf(type.toUpperCase());

            ClientAddress address = bundle.getClientDetail().getFactAddress();

            if (addressType == AddressType.REG) {
              address = bundle.getClientDetail().getRegAddress();
            }

            assertThat(street).isEqualToIgnoringCase(address.street);
            assertThat(house).isEqualToIgnoringCase(address.house);
            assertThat(flat).isEqualToIgnoringCase(address.flat);
          }
        }
        try (PreparedStatement ps = connection.prepareStatement(
          r("select number, type from clientPhone where client = ?"))) {
          ps.setLong(1, id);

          ResultSet rs = ps.executeQuery();
          while (rs.next()) {
            String type = rs.getString("type");
            String phoneNumber = rs.getString("number");

            assertThat(Arrays.asList("work", "mobile", "home")).contains(type.toLowerCase());

            assertThat(bundle.getClientDetail().phones.stream().
              anyMatch(
                clientPhone -> clientPhone.number.equalsIgnoreCase(phoneNumber) && clientPhone.type.toString().equalsIgnoreCase(type))
            ).isTrue();
          }
        }
      }
      return null;
    });
  }

  @Test
  public void testCiaErrorsWithIncorrectData() throws Exception {
    RandomClientGenerator gen = new RandomClientGenerator();

    int bundlesCount = 50;

    DropCreateMgrSrcDb.execute();
    DropCreateOperDb.execute();

    new DBHelper<>().useSrcDb(true).run(connection -> {
      PreparedStatement ps = connection.prepareStatement("insert into transition_cia(record_data) values (?)");
      HashMap<Integer, Function<ClientBundle, String>> mutate = new HashMap<>();
      mutate.put(0, clientBundle -> clientBundle.noName(true).toXml());
      mutate.put(1, clientBundle -> clientBundle.noSurname(true).toXml());
      mutate.put(2, clientBundle -> clientBundle.noPatrnymic(true).toXml());
      mutate.put(3, clientBundle -> clientBundle.noBirthDate(true).toXml());
      mutate.put(4, ClientBundle::toXml);
      Random random = new Random(1337);

      for (int i = 1; i <= bundlesCount; ++i) {
        int x = random.nextInt(5);
        if (x == 4) {
          RandomClientGenerator.incorrectDate = true;
        }
        String data = mutate.get(x).apply(gen.generateClientBundle(i));
        ps.setString(1, data);
        ps.addBatch();
        RandomClientGenerator.incorrectDate = false;
      }

      for (int i = bundlesCount + 1; i <= 2 * bundlesCount; ++i) {
        ps.setString(1, gen.generateClientBundle(i).toXml());
        ps.addBatch();
      }
      ps.executeBatch();
      return null;
    });

    //
    //

    ConnectionConfig operCC = ConnectionUtils.fileToConnectionConfig(ConfigFiles.homeDb());
    ConnectionConfig ciaCC = ConnectionUtils.fileToConnectionConfig(ConfigFiles.migrationDb());
    try (Migration migration = new CiaMigration(operCC, ciaCC)) {
      migration.chunkSize = 250_000;
      migration.uploadMaxBatchSize = 50_000;
      migration.downloadMaxBatchSize = 50_000;

      migration.migrate();
    }
    //
    //

    new DBHelper<>().useSrcDb(true).run(connection -> {
      PreparedStatement ps = connection.prepareStatement("select count(1) as count from transition_cia where error is not null");
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        assertThat(Integer.parseInt(rs.getString("count"))).isEqualTo(bundlesCount);
      }
      return ps;
    });
  }

  @Test
  public void testCiaTmpLoadedData() throws Exception {
    int bundlesCount = 50;
    List<ClientBundle> bundles = RandomClientGenerator.generate(bundlesCount);

    DropCreateMgrSrcDb.execute();
    DropCreateOperDb.execute();

    new DBHelper<>().useSrcDb(true).run(connection -> {
      PreparedStatement ps = connection.prepareStatement("insert into transition_cia(record_data) values (?)");

      for (ClientBundle clientBundle : bundles) {
        String data = clientBundle.toXml();
        ps.setString(1, data);
        ps.addBatch();
      }

      ps.executeBatch();
      return null;
    });

    //
    //
    ConnectionConfig operCC = ConnectionUtils.fileToConnectionConfig(ConfigFiles.homeDb());
    ConnectionConfig ciaCC = ConnectionUtils.fileToConnectionConfig(ConfigFiles.migrationDb());

    try (Migration migration = new CiaMigration(operCC, ciaCC)) {
      migration.chunkSize = 250_000;
      migration.uploadMaxBatchSize = 50_000;
      migration.downloadMaxBatchSize = 50_000;

      migration.migrate();

      tmpSqlVars = migration.tmpSqlVars;
    }
    //
    //

    new DBHelper<>().useSrcDb(false).run(connection -> {
      for (ClientBundle bundle : bundles) {
        long number = 1;
        try (PreparedStatement ps = connection.prepareStatement(
          r("select number from TMP_CLIENT where cia_id = ?"))
        ) {
          ps.setString(1, String.valueOf(bundle.getClientDetail().getId()));
          ResultSet rs = ps.executeQuery();
          int cnt = 0;
          while (rs.next()) {
            ++cnt;
            number = rs.getLong("number");
          }
          assertThat(cnt).isEqualTo(1);
        }
        try (PreparedStatement ps = connection.prepareStatement(
          r("select cia_id, surname, name, patronymic, birth_date, charm_name from TMP_CLIENT where number = ?"))
        ) {
          ps.setLong(1, number);
          ResultSet rs = ps.executeQuery();
          while (rs.next()) {
            String name = rs.getString("name");
            String surname = rs.getString("surname");
            String patronymic = rs.getString("patronymic");
            Date birthDate = rs.getDate("birth_date");
            String charmName = rs.getString("charm_name");

            assertThat(name).isEqualTo(bundle.getClient().name);
            assertThat(surname).isEqualTo(bundle.getClient().surname);
            assertThat(patronymic).isEqualTo(bundle.getClient().patronymic);
            assertThat(birthDate.toLocalDate().getYear()).isEqualTo(bundle.getClient().birth_date.toLocalDate().getYear());
            assertThat(birthDate.toLocalDate().getDayOfYear()).isEqualTo(bundle.getClient().birth_date.toLocalDate().getDayOfYear());
            assertThat(charmName).isEqualTo(bundle.getClientDetail().getCharm().name);
          }
        }
        try (PreparedStatement ps = connection.prepareStatement(
          r("select client_id, type, street, house, flat from TMP_ADDRESS where number = ?"))
        ) {
          ps.setLong(1, number);
          ResultSet rs = ps.executeQuery();
          while (rs.next()) {
            String type = rs.getString("type");
            String street = rs.getString("street");
            String house = rs.getString("house");
            String flat = rs.getString("flat");

            assertThat(type.toLowerCase()).isIn("reg", "fact");

            AddressType addressType = AddressType.valueOf(type.toUpperCase());

            ClientAddress address = bundle.getClientDetail().getFactAddress();

            if (addressType == AddressType.REG) {
              address = bundle.getClientDetail().getRegAddress();
            }

            assertThat(street).isEqualToIgnoringCase(address.street);
            assertThat(house).isEqualToIgnoringCase(address.house);
            assertThat(flat).isEqualToIgnoringCase(address.flat);
          }
        }
        try (PreparedStatement ps = connection.prepareStatement(
          r("select phone_number, type from TMP_PHONE where number = ?"))) {
          ps.setLong(1, number);

          ResultSet rs = ps.executeQuery();
          while (rs.next()) {
            String type = rs.getString("type");
            String phoneNumber = rs.getString("phone_number");

            assertThat(Arrays.asList("work", "mobile", "home")).contains(type.toLowerCase());

            assertThat(bundle.getClientDetail().phones.stream().
              anyMatch(
                clientPhone -> clientPhone.number.equalsIgnoreCase(phoneNumber) && clientPhone.type.toString().equalsIgnoreCase(type))
            ).isTrue();
          }
        }
      }
      return null;
    });
  }

  @Test
  public void testFrsTmpLoadedData() throws Exception {
    int bundlesCount = 50;
    List<ClientBundle> bundles = RandomClientGenerator.generate(bundlesCount);

    DropCreateMgrSrcDb.execute();
    DropCreateOperDb.execute();

    new DBHelper<>().useSrcDb(true).run(connection -> {
      PreparedStatement ps = connection.prepareStatement("insert into transition_frs(record_data) values (?)");

      for (ClientBundle clientBundle : bundles) {
        for (String data : clientBundle.getJsonAccounts()) {
          ps.setString(1, data);
          ps.addBatch();
        }
        for (String data : clientBundle.getJsonTransactions()) {
          ps.setString(1, data);
          ps.addBatch();
        }
      }

      ps.executeBatch();
      return null;
    });

    //
    //
    ConnectionConfig operCC = ConnectionUtils.fileToConnectionConfig(ConfigFiles.homeDb());
    ConnectionConfig ciaCC = ConnectionUtils.fileToConnectionConfig(ConfigFiles.migrationDb());

    try (Migration migration = new FrsMigration(operCC, ciaCC)) {
      migration.chunkSize = 250_000;
      migration.uploadMaxBatchSize = 50_000;
      migration.downloadMaxBatchSize = 50_000;

      migration.migrate();

      tmpSqlVars = migration.tmpSqlVars;
    }
    //
    //

    new DBHelper<>().useSrcDb(false).run(connection -> {
      for (ClientBundle bundle : bundles) {
        List<String> jsonAccounts = bundle.getJsonAccounts();
        Iterator it = jsonAccounts.iterator();
        for (ClientAccount account : bundle.getAccounts()) {
          long number = 1;
          try (PreparedStatement ps = connection.prepareStatement(
            r("select row_number from TMP_ACCOUNT where number = ?"))
          ) {
            ps.setString(1, account.number);
            ResultSet rs = ps.executeQuery();
            int cnt = 0;
            while (rs.next()) {
              ++cnt;
              number = rs.getLong("row_number");
            }
            assertThat(cnt).isEqualTo(1);
          }
          try (PreparedStatement ps = connection.prepareStatement(
            r("select client_id, money, number, registered_at from TMP_ACCOUNT where row_number = ?"))
          ) {
            ps.setLong(1, number);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
              String cia_id = rs.getString("client_id");
              float money = rs.getFloat("money");
              String accountNumber = rs.getString("number");
              Timestamp registeredAt = rs.getTimestamp("registered_at");

              assertThat(cia_id).isEqualTo(String.valueOf(account.client));

              assertThat(accountNumber).isEqualTo(account.number);
              assertThat(registeredAt).isEqualTo(account.registered_at);
            }
          }
        }

        for (ClientAccountTransaction transaction : bundle.getTransactions()) {
          long number = 1;
          String transactionAccountNumber = bundle.getAccounts().stream().filter(clientAccount -> clientAccount.id == transaction.account).findFirst().get().number;
          try (PreparedStatement ps = connection.prepareStatement(
            r("select row_number from TMP_TRANSACTION where account_number = ? AND finished_at = ?"))
          ) {
            ps.setString(1, transactionAccountNumber);
            ps.setTimestamp(2, transaction.finished_at);
            ResultSet rs = ps.executeQuery();
            int cnt = 0;
            while (rs.next()) {
              ++cnt;
              number = rs.getLong("row_number");
              assertThat(cnt).isEqualTo(1);
            }
          }
          try (PreparedStatement ps = connection.prepareStatement(
            r("select account_number, money, finished_at, transaction_type from TMP_TRANSACTION where row_number = ?"))
          ) {
            ps.setLong(1, number);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
              String accountNumber = rs.getString("account_number");
              float money = rs.getFloat("money");
              Timestamp finishedAt = rs.getTimestamp("finished_at");
              String transactionType = rs.getString("transaction_type");

              assertThat(accountNumber).isEqualTo(transactionAccountNumber);
              assertThat(money).isEqualTo(transaction.money);

              assertThat(finishedAt).hasTime(transaction.finished_at.getTime());
              assertThat(transactionType).isEqualTo(bundle.getTransactionTypes().stream().filter(type -> type.id == transaction.type).findFirst().get().name);
            }
          }
        }
      }
      return null;
    });
  }

  String r(String statement) {
    return Stream.of(statement).map(s -> {
      String[] ss = new String[]{s};
      tmpSqlVars.forEach((s1, s2) -> ss[0] = ss[0].replace(s1, s2));
      return ss[0];
    }).collect(Collectors.joining());
  }

  @Test
  void testFrsErrorsWithIncorrectData() throws Exception {
    RandomClientGenerator gen = new RandomClientGenerator();

    int bundlesCount = 2;

    DropCreateMgrSrcDb.execute();
    DropCreateOperDb.execute();

    AtomicInteger errorsCount = new AtomicInteger();

    new DBHelper<>().useSrcDb(true).run(connection -> {
      PreparedStatement ps = connection.prepareStatement("insert into transition_frs(record_data) values (?)");

      HashMap<Integer, Function<RandomClientGenerator.ClientBundle, RandomClientGenerator.ClientBundle>> mutate = new HashMap<>();

      mutate.put(0, clientBundle -> clientBundle.noCiaId(true));
      mutate.put(1, clientBundle -> clientBundle.noNumber(true));
      mutate.put(2, clientBundle -> clientBundle);

      Random random = new Random(1337);

      for (int i = 1; i <= 2 * bundlesCount; ++i) {
        int x = random.nextInt(2);
        if (i > bundlesCount)
          x = 2;
        RandomClientGenerator.ClientBundle bundle = gen.generateClientBundle(i);
        bundle = mutate.get(x).apply(bundle);
        int finalX = x;
        bundle.getJsonAccounts().forEach(data -> {
          try {
            if (finalX != 2)
              errorsCount.incrementAndGet();
            ps.setString(1, data);
            ps.addBatch();
          } catch (SQLException e) {
            e.printStackTrace();
          }
        });
        bundle.getJsonTransactions().forEach(data -> {
          try {
            if (finalX != 2)
              errorsCount.incrementAndGet();
            ps.setString(1, data);
            ps.addBatch();
          } catch (SQLException e) {
            e.printStackTrace();
          }
        });
      }
      ps.executeBatch();
      return null;
    });

    //
    //

    ConnectionConfig operCC = ConnectionUtils.fileToConnectionConfig(ConfigFiles.homeDb());
    ConnectionConfig ciaCC = ConnectionUtils.fileToConnectionConfig(ConfigFiles.migrationDb());
    try (Migration migration = new FrsMigration(operCC, ciaCC)) {
      migration.chunkSize = 250_000;
      migration.uploadMaxBatchSize = 50_000;
      migration.downloadMaxBatchSize = 50_000;

      migration.migrate();
    }
    //
    //

    new DBHelper<>().useSrcDb(true).run(connection -> {
      PreparedStatement ps = connection.prepareStatement("select count(1) as count from transition_frs where error is not null");
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        assertThat(Integer.parseInt(rs.getString("count"))).isEqualTo(errorsCount.get());
      }
      return ps;
    });
  }

  @Test
  void testCiaLargeMigration() throws Exception {
    long start = System.nanoTime();
    RandomClientGenerator gen = new RandomClientGenerator();

    int bundlesCount = 100000;

    DropCreateMgrSrcDb.execute();
    DropCreateOperDb.execute();

    new DBHelper<>().useSrcDb(true).run(connection -> {
      PreparedStatement ps = connection.prepareStatement("insert into transition_cia(record_data) values (?)");
      HashMap<Integer, Function<ClientBundle, String>> mutate = new HashMap<>();
      mutate.put(0, clientBundle -> clientBundle.noName(true).toXml());
      mutate.put(1, clientBundle -> clientBundle.noSurname(true).toXml());
      mutate.put(2, clientBundle -> clientBundle.noPatrnymic(true).toXml());
      mutate.put(3, clientBundle -> clientBundle.noBirthDate(true).toXml());
      mutate.put(4, ClientBundle::toXml);
      Random random = new Random(1337);

      int batchSize = 0;
      int recordsCount = 0;

      for (int i = 1; i <= bundlesCount; ++i) {
        int x = random.nextInt(5);
        if (x == 4) {
          RandomClientGenerator.incorrectDate = true;
        }
        String data = mutate.get(x).apply(gen.generateClientBundle(i));
        ps.setString(1, data);
        ps.addBatch();
        ++batchSize;
        ++recordsCount;

        if (batchSize >= 25000) {
          ps.executeBatch();
          System.err.println("Batch executed: " + recordsCount);
          batchSize = 0;
        }

        RandomClientGenerator.incorrectDate = false;
      }

      for (int i = bundlesCount + 1; i <= 2 * bundlesCount; ++i) {
        ps.setString(1, gen.generateClientBundle(i).toXml());
        ps.addBatch();
        ++batchSize;
        ++recordsCount;
        if (batchSize >= 25000) {
          ps.executeBatch();
          System.err.println("Batch executed: " + recordsCount);
          batchSize = 0;
        }
      }
      if (batchSize > 0) {
        ps.executeBatch();
        System.err.println("Batch executed: " + recordsCount);
      }
      ps.executeBatch();
      return null;
    });

    System.err.println("inserting done");

    //
    //

    ConnectionConfig operCC = ConnectionUtils.fileToConnectionConfig(ConfigFiles.homeDb());
    ConnectionConfig ciaCC = ConnectionUtils.fileToConnectionConfig(ConfigFiles.migrationDb());
    try (Migration migration = new CiaMigration(operCC, ciaCC)) {
      migration.chunkSize = 250_000;
      migration.uploadMaxBatchSize = 50_000;
      migration.downloadMaxBatchSize = 50_000;

      migration.migrate();
    }
    //
    //

    new DBHelper<>().useSrcDb(true).run(connection -> {
      PreparedStatement ps = connection.prepareStatement("select count(1) as count from transition_cia where error is not null");
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        assertThat(Integer.parseInt(rs.getString("count"))).isEqualTo(bundlesCount);
      }
      return ps;
    });

    {
      long now = System.nanoTime();
      System.err.println("Done in: " + TimeUtils.showTime(now, start));
    }
  }

}
