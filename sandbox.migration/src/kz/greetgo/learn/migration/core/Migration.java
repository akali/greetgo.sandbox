package kz.greetgo.learn.migration.core;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import kz.greetgo.learn.migration.interfaces.ConnectionConfig;
import kz.greetgo.learn.migration.util.ConnectionUtils;
import kz.greetgo.learn.migration.util.TimeUtils;
import org.xml.sax.SAXException;

import javax.xml.transform.Result;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static kz.greetgo.learn.migration.util.TimeUtils.recordsPerSecond;
import static kz.greetgo.learn.migration.util.TimeUtils.showTime;

public class Migration implements Closeable {

  private final ConnectionConfig operConfig;


  private final ConnectionConfig ciaConfig;
  private Connection operConnection = null, ciaConnection = null;
  private HashMap<String, String> tmpSqlVars;

  public Migration(ConnectionConfig operConfig, ConnectionConfig ciaConfig) {
    this.operConfig = operConfig;
    this.ciaConfig = ciaConfig;
  }

  @Override
  public void close() {
    closeOperConnection();
    closeCiaConnection();
  }

  private void closeCiaConnection() {
    if (ciaConnection != null) {
      try {
        ciaConnection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
      ciaConnection = null;
    }
  }

  private void closeOperConnection() {
    if (this.operConnection != null) {
      try {
        this.operConnection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
      this.operConnection = null;
    }
  }

  private void info(String message) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    System.out.println(sdf.format(new Date()) + " [" + getClass().getSimpleName() + "] " + message);
  }

  private String r(String sql) {
    for (Map.Entry<String, String> a: tmpSqlVars.entrySet()) {
      sql = sql.replace(a.getKey(), a.getValue());
    }
    return sql;
  }

  private void exec(String sql) throws SQLException {
    String executingSql = r(sql);

    System.err.println("EXECUTING: " + sql);

    long startedAt = System.nanoTime();
    try (Statement statement = operConnection.createStatement()) {
      int updates = statement.executeUpdate(executingSql);
      info("Updated " + updates
        + " records for " + showTime(System.nanoTime(), startedAt)
        + ", EXECUTED SQL : " + executingSql);
    } catch (SQLException e) {
      info("ERROR EXECUTE SQL for " + showTime(System.nanoTime(), startedAt)
        + ", message: " + e.getMessage() + ", SQL : " + executingSql);
      throw e;
    }
  }

  public int chunkSize = 1_000_000;
  public int downloadMaxBatchSize = 50_000;
  public int uploadMaxBatchSize = 50_000;
  public int showStatusPingMillis = 5000;

  private String tmpClientTable;

  public int migrate() throws Exception {
    long startedAt = System.nanoTime();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
    Date nowDate = new Date();
    tmpClientTable = "cia_migration_client_" + sdf.format(nowDate);
    tmpSqlVars = new HashMap<String, String>() {
      {
        put("TMP_CLIENT", "cia_migration_client_" + sdf.format(nowDate));
        put("TMP_CHARM", "cia_migration_charm_" + sdf.format(nowDate));
        put("TMP_ADDRESS", "cia_migration_address_" + sdf.format(nowDate));
        put("TMP_PHONE", "cia_migration_phone_" + sdf.format(nowDate));
      }
    };
    tmpSqlVars.forEach((key, value) -> info(key + " = " + value));

    createOperConnection();

    createTables();

    createCiaConnection();

    int chunkSize = download();

    info("Downloaded chunk " + chunkSize + " finished for " + TimeUtils.showTime(System.nanoTime(), startedAt));

    if (chunkSize == 0) return 0;

    closeCiaConnection();

    migrateFromTmp();

    info("Migration chunk " + chunkSize + " finished for " + TimeUtils.showTime(System.nanoTime(), startedAt));

    return chunkSize;
  }

  private void createTables() throws SQLException {
    //language=PostgreSQL
    exec("create table TMP_CLIENT (\n" +
      "  client_id int,\n" +
      "  status int not null default 0,\n" +
      "  error varchar(300),\n" +
      "  \n" +
      "  number bigint not null primary key,\n" +
      "  cia_id varchar(100) not null,\n" +
      "  surname varchar(300),\n" +
      "  name varchar(300),\n" +
      "  patronymic varchar(300),\n" +
      "  birth_date date,\n" +
      "  charm_name varchar(300)," +
      "  charm_id int" +
      ")");
//    //language=PostgreSQL
//    exec("create table TMP_CHARM (" +
//      " charm_id serial primary key," +
//      " name varchar(300) not null unique," +
//      " desciption varchar(300)," +
//      " energy numeric, " +
//      " number bigint not null primary key" +
//      ");");
    //language=PostgreSQL
    exec("create table TMP_ADDRESS (\n" +
      "  number bigint references TMP_CLIENT on delete cascade,\n" +
      "  client_id bigint,\n" +
      "  type varchar(10) not null,\n" +
      "  street varchar(300),\n" +
      "  house varchar(300),\n" +
      "  flat varchar(300),\n" +
      "  status int not null default 0,\n" +
      "  primary key(number, type)\n" +
      ");");

    //language=PostgreSQL
    exec("create table TMP_PHONE (\n" +
      "  client_id bigint,\n" +
      "  phone_number varchar(64) not null,\n" +
      "  type varchar(64) not null,\n" +
      "  number bigint references TMP_CLIENT on delete cascade,\n" +
      "  status int not null default 0,\n" +
      "  primary key(number, phone_number)\n" +
      ");\n");
    /*
    client bigint references Client on delete cascade,
    number varchar(64) not null,
    type varchar(64) not null,
    primary key(client, number)
    * */
  }

  private void createOperConnection() throws Exception {
    operConnection = ConnectionUtils.create(operConfig);
  }

  private void createCiaConnection() throws Exception {
    ciaConnection = ConnectionUtils.create(ciaConfig);
  }

  private int download() throws SQLException, IOException, SAXException {

    final AtomicBoolean working = new AtomicBoolean(true);
    final AtomicBoolean showStatus = new AtomicBoolean(false);

    final Thread see = new Thread(() -> {

      while (working.get()) {

        try {
          Thread.sleep(showStatusPingMillis);
        } catch (InterruptedException e) {
          break;
        }

        showStatus.set(true);

      }

    });
    see.start();

    try (PreparedStatement ciaPS = ciaConnection.prepareStatement(
      "select * from transition_client where status='JUST_INSERTED' order by number limit ?")) {

      info("Prepared statement for : select * from transition_client");

      ciaPS.setInt(1, chunkSize);

//      Insert insertCharm = new Insert("TMP_CHARM");
//      insertCharm.field(1, "number", "?");
//      insertCharm.field(2, "name", "?");

      Insert insert = new Insert("TMP_CLIENT");
      insert.field(1, "number", "?");
      insert.field(2, "cia_id", "?");
      insert.field(3, "surname", "?");
      insert.field(4, "name", "?");
      insert.field(5, "patronymic", "?");
      insert.field(6, "birth_date", "?");
      insert.field(7, "charm_name", "?");

      Insert insertAddress = new Insert("TMP_ADDRESS"); // TODO(DONE): write all inserts
      insertAddress.field(1, "number", "?");
      insertAddress.field(2, "type", "?");
      insertAddress.field(3, "street", "?");
      insertAddress.field(4, "house", "?");
      insertAddress.field(5, "flat", "?");

      Insert insertPhone = new Insert("TMP_PHONE"); // TODO: write all inserts
      insertPhone.field(1, "number", "?");
      insertPhone.field(2, "type", "?");
      insertPhone.field(3, "phone_number", "?");

      operConnection.setAutoCommit(false);
      try (PreparedStatement clientStatement = operConnection.prepareStatement(r(insert.toString()));
//           PreparedStatement charmStatement = operConnection.prepareStatement(r(insertCharm.toString()));
           PreparedStatement addressStatement = operConnection.prepareStatement(r(insertAddress.toString()));
           PreparedStatement phoneStatement = operConnection.prepareStatement(r(insertPhone.toString()))
      ) {

        try (ResultSet ciaRS = ciaPS.executeQuery()) {

          info("Got result set for : select * from transition_client");

          int batchSize = 0, recordsCount = 0;

          long startedAt = System.nanoTime();

          while (ciaRS.next()) {
            ClientRecord r = new ClientRecord();
            r.number = ciaRS.getLong("number");
            String line = ciaRS.getString("record_data");
//            System.out.println(line);
            r.parseRecordData(line);

            {
              clientStatement.setLong(1, r.number);
              clientStatement.setString(2, r.cia_id);
              clientStatement.setString(3, r.surname);
              clientStatement.setString(4, r.name);
              clientStatement.setString(5, r.patronymic);
              clientStatement.setDate(6, r.birthDate);
              clientStatement.setString(7, r.charm_name);

              clientStatement.addBatch();
            }

            {
              addressStatement.setLong(1, r.number);
              addressStatement.setString(2, "REG");
              addressStatement.setString(3, r.regAddress.street);
              addressStatement.setString(4, r.regAddress.house);
              addressStatement.setString(5, r.regAddress.flat);

              addressStatement.addBatch();

              addressStatement.setLong(1, r.number);
              addressStatement.setString(2, "FACT");
              addressStatement.setString(3, r.factAddress.street);
              addressStatement.setString(4, r.factAddress.house);
              addressStatement.setString(5, r.factAddress.flat);

              addressStatement.addBatch();
            }

            {
              for (String homePhone: r.homePhones) {
                phoneStatement.setLong(1, r.number);
                phoneStatement.setString(2, "HOME");
                phoneStatement.setString(3, homePhone);
                phoneStatement.addBatch();
              }
              for (String homePhone: r.workPhones) {
                phoneStatement.setLong(1, r.number);
                phoneStatement.setString(2, "WORK");
                phoneStatement.setString(3, homePhone);
                phoneStatement.addBatch();
              }
              for (String homePhone: r.mobilePhones) {
                phoneStatement.setLong(1, r.number);
                phoneStatement.setString(2, "MOBILE");
                phoneStatement.setString(3, homePhone);
                phoneStatement.addBatch();
              }
            }

            batchSize++;
            recordsCount++;

            if (batchSize >= downloadMaxBatchSize) {
//              charmStatement.executeBatch();
              clientStatement.executeBatch();
              addressStatement.executeBatch();
              phoneStatement.executeBatch();

              operConnection.commit();
              batchSize = 0;
            }

            if (showStatus.get()) {
              showStatus.set(false);

              long now = System.nanoTime();
              info(" -- downloaded records " + recordsCount + " for " + showTime(now, startedAt)
                + " : " + recordsPerSecond(recordsCount, now - startedAt));
            }

          }

          if (batchSize > 0) {
            clientStatement.executeBatch();
            addressStatement.executeBatch();
            phoneStatement.executeBatch();
            operConnection.commit();
          }

          {
            long now = System.nanoTime();
            info("TOTAL Downloaded records " + recordsCount + " for " + showTime(now, startedAt)
              + " : " + recordsPerSecond(recordsCount, now - startedAt));
          }

          return recordsCount;
        }
      } finally {
        operConnection.setAutoCommit(true);
        working.set(false);
        see.interrupt();
      }
    }
  }

  private void uploadAndDropErrors() throws Exception {
    info("uploadAndDropErrors goes : maxBatchSize = " + uploadMaxBatchSize);

    final AtomicBoolean working = new AtomicBoolean(true);

    createCiaConnection();
    ciaConnection.setAutoCommit(false);
    try {

      try (PreparedStatement inPS = operConnection.prepareStatement(r(
        "select number, error from TMP_CLIENT where error is not null"))) {

        info("Prepared statement for : select number, error from TMP_CLIENT where error is not null");

        try (ResultSet inRS = inPS.executeQuery()) {
          info("Query executed for : select number, error from TMP_CLIENT where error is not null");

          try (PreparedStatement outPS = ciaConnection.prepareStatement(
            "update transition_client set status = 'ERROR', error = ? where number = ?")) {

            int batchSize = 0, recordsCount = 0;

            final AtomicBoolean showStatus = new AtomicBoolean(false);

            new Thread(() -> {

              while (working.get()) {

                try {
                  Thread.sleep(showStatusPingMillis);
                } catch (InterruptedException e) {
                  break;
                }

                showStatus.set(true);

              }

            }).start();

            long startedAt = System.nanoTime();

            while (inRS.next()) {

              outPS.setString(1, inRS.getString("error"));
              outPS.setLong(2, inRS.getLong("number"));
              outPS.addBatch();
              batchSize++;
              recordsCount++;

              if (batchSize >= uploadMaxBatchSize) {
                outPS.executeBatch();
                ciaConnection.commit();
                batchSize = 0;
              }

              if (showStatus.get()) {
                showStatus.set(false);

                long now = System.nanoTime();
                info(" -- uploaded errors " + recordsCount + " for " + TimeUtils.showTime(now, startedAt)
                  + " : " + TimeUtils.recordsPerSecond(recordsCount, now - startedAt));
              }
            }

            if (batchSize > 0) {
              outPS.executeBatch();
              ciaConnection.commit();
            }

            {
              long now = System.nanoTime();
              info("TOTAL Uploaded errors " + recordsCount + " for " + TimeUtils.showTime(now, startedAt)
                + " : " + TimeUtils.recordsPerSecond(recordsCount, now - startedAt));
            }
          }
        }
      }

    } finally {
      closeCiaConnection();
      working.set(false);
    }

    //language=PostgreSQL
    exec("delete from TMP_CLIENT where error is not null");
  }

  private void uploadAllOk() throws Exception {

    info("uploadAllOk goes: maxBatchSize = " + uploadMaxBatchSize);

    final AtomicBoolean working = new AtomicBoolean(true);

    createCiaConnection();
    ciaConnection.setAutoCommit(false);
    try {

      try (PreparedStatement inPS = operConnection.prepareStatement(r("select number from TMP_CLIENT"))) {

        info("Prepared statement for : select number from TMP_CLIENT");

        try (ResultSet inRS = inPS.executeQuery()) {
          info("Query executed for : select number from TMP_CLIENT");

          try (PreparedStatement outPS = ciaConnection.prepareStatement(
            "update transition_client set status = 'OK' where number = ?")) {

            int batchSize = 0, recordsCount = 0;

            final AtomicBoolean showStatus = new AtomicBoolean(false);

            new Thread(() -> {

              while (true) {

                if (!working.get()) break;

                try {
                  Thread.sleep(showStatusPingMillis);
                } catch (InterruptedException e) {
                  break;
                }

                showStatus.set(true);
              }

            }).start();

            long startedAt = System.nanoTime();

            while (inRS.next()) {

              outPS.setLong(1, inRS.getLong("number"));
              outPS.addBatch();
              batchSize++;
              recordsCount++;

              if (batchSize >= uploadMaxBatchSize) {
                outPS.executeBatch();
                ciaConnection.commit();
                batchSize = 0;
              }

              if (showStatus.get()) {
                showStatus.set(false);

                long now = System.nanoTime();
                info(" -- uploaded ok records " + recordsCount + " for " + TimeUtils.showTime(now, startedAt)
                  + " : " + TimeUtils.recordsPerSecond(recordsCount, now - startedAt));
              }
            }

            if (batchSize > 0) {
              outPS.executeBatch();
              ciaConnection.commit();
            }

            {
              long now = System.nanoTime();
              info("TOTAL Uploaded ok records " + recordsCount + " for " + TimeUtils.showTime(now, startedAt)
                + " : " + TimeUtils.recordsPerSecond(recordsCount, now - startedAt));
            }
          }
        }
      }

    } finally {
      closeCiaConnection();
      working.set(false);
    }

  }

  private void migrateFromTmp() throws Exception {

    //language=PostgreSQL
    exec("update TMP_CLIENT set error = 'surname is not defined'\n" +
      "where error is null and surname is null");
    //language=PostgreSQL
    exec("update TMP_CLIENT set error = 'name is not defined'\n" +
      "where error is null and name is null");
    //language=PostgreSQL
    exec("update TMP_CLIENT set error = 'birth_date is not defined'\n" +
      "where error is null and birth_date is null");
    //language=PostgreSQL
    exec("update TMP_CLIENT set error = 'age must be in interval[4, 1000]'\n" +
      "where error is null and birth_date is not null and date_part('year', age(birth_date)) not between 4 and 1000");

    uploadAndDropErrors();

    //language=PostgreSQL
    exec("with num_ord as (\n" +
      "  select number, cia_id, row_number() over(partition by cia_id order by number desc) as ord \n" +
      "  from TMP_CLIENT\n" +
      ")\n" +
      "\n" +
      "update TMP_CLIENT set status = 2\n" +
      "where status = 0 and number in (select number from num_ord where ord > 1)");

    // createCiaConnection();

    //language=PostgreSQL
    exec("update TMP_CLIENT t set client_id = c.id\n" +
      "  from client c\n" +
      "  where c.cia_id = t.cia_id\n");

    //language=PostgreSQL
    exec("update TMP_CLIENT set status = 3 where client_id is not null and status = 0");

    //language=PostgreSQL
    exec("update TMP_CLIENT set client_id = nextval('s_client') where status = 0");

    //language=PostgreSQL
    exec("update TMP_CLIENT set charm_id = f.id from charm f where f.name = charm_name and status = 0");

    //language=PostgreSQL
    exec("update TMP_CLIENT set charm_id = nextval('s_charm') where status = 0 and charm_id is null");

    //language=PostgreSQL
    exec("" +
      "update TMP_ADDRESS a set status = f.status, client_id = f.client_id from TMP_CLIENT f where a.number = f.number");

    //language=PostgreSQL
    exec("" +
      "update TMP_PHONE a set status = f.status, client_id = f.client_id from TMP_CLIENT f where a.number = f.number");

    //language=PostgreSQL
    exec("insert into charm(id, name)\n" +
      "select charm_id, charm_name from TMP_CLIENT where status = 0;");

//    printTmpTable();

    //language=PostgreSQL
    exec("insert into client (id, cia_id, surname, name, patronymic, birth_date, charm)\n" +
      "select client_id, cia_id, surname, name, patronymic, birth_date, charm_id " +
      "from TMP_CLIENT where status = 0");

    //language=PostgreSQL
    exec("update client c set surname = s.surname\n" +
      "                 , name = s.name\n" +
      "                 , patronymic = s.patronymic\n" +
      "                 , birth_date = s.birth_date\n" +
      "                 , charm = s.charm_id " +
      "from TMP_CLIENT s\n" +
      "where c.id = s.client_id\n" +
      "and s.status = 3");

    //language=PostgreSQL
    exec("insert into ClientAddress(client, type, street, house, flat)\n" +
      "select client_id, type, street, house, flat from TMP_ADDRESS where status = 0;");

    //language=PostgreSQL
    exec("insert into ClientPhone(client, type, number) select client_id, type, phone_number from TMP_PHONE where status=0");

    //language=PostgreSQL
    exec("update client set actual = 1 where id in (\n" +
      "  select client_id from TMP_CLIENT where status = 0\n" +
      ")");

    uploadAllOk();
  }

  private void printTmpTable() {
    {
      System.err.println("-----------------------------------");
      System.err.println("-----------------------------------");
      System.err.println("-----------------------------------");
      List<String> q = Arrays.asList(
        "client_id",
        "status",
        "error",
        "number",
        "cia_id",
        "surname",
        "name",
        "patronymic",
        "birth_date",
        "charm_name",
        "charm_id");
      System.err.println(String.join(", ", q));
      try (PreparedStatement test =
             operConnection.prepareStatement(r("select * from TMP_CLIENT where status = 0"))) {
        ResultSet set = test.executeQuery();
        while (set.next()) {
          System.err.println(
            q.stream().map(s -> {
              try {
                return set.getString(s);
              } catch (SQLException e) {
                e.printStackTrace();
                return null;
              }
            }).collect(Collectors.joining(", "))
          );
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      System.err.println("-----------------------------------");
      System.err.println("-----------------------------------");
      System.err.println("-----------------------------------");
    }
  }

//  public static void main(String[] args) throws JSchException, IOException {
//    JSch jsch = new JSch();
//    String key = "";
//    FileInputStream fis = new FileInputStream(new File("/home/aqali/Загрузки/greetgo_ssh.pem"));
//    BufferedReader br = new BufferedReader(fis);
//    jsch.addIdentity("");
//    fis.close();
//    Session session = jsch.getSession("ubuntu", "18.219.75.117\n", 22);
////    session.setPassword("aklenaz123");
//    session.connect();
//    Channel channel = session.openChannel("shell");
//    channel.setInputStream(System.in);
//    channel.setOutputStream(System.out);
//    channel.connect();
//  }
}
