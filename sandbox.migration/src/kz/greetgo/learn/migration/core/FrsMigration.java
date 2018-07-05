package kz.greetgo.learn.migration.core;

import kz.greetgo.learn.migration.interfaces.ConnectionConfig;
import org.fest.util.Lists;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static kz.greetgo.learn.migration.util.TimeUtils.recordsPerSecond;
import static kz.greetgo.learn.migration.util.TimeUtils.showTime;

public class FrsMigration extends Migration {
  public FrsMigration(ConnectionConfig operConfig, ConnectionConfig ciaConfig) {
    super(operConfig, ciaConfig);
  }

  @Override
  public int migrate() throws Exception {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
    Date now = new Date();
    tmpSqlVars = new HashMap<String, String>() {
      {
        put("TMP_TRANSACTION", "frs_migration_transaction_" + sdf.format(now));
        put("TMP_ACCOUNT", "frs_migration_account_" + sdf.format(now));
      }
    };
    tmpSqlVars.forEach((key, value) -> info(key + " = " + value));

    createOperConnection();
    createTables();
    createMigrationConnection();

    int chunkSize = download();

    if (chunkSize == 0) return 0;
    closeMigrationConnection();

    migrateFromTmp();

    return chunkSize;
  }

  private void migrateFromTmp() throws Exception {
    for (String column : Lists.newArrayList("account_number", "money", "finished_at"))
      exec(String.format(
        "update TMP_TRANSACTION set error = '%s is null, status = 1' where error is null and %s is null", column, column
      ));

    for (String column : Lists.newArrayList("client_id", "number", "registered_at"))
      exec(String.format(
        "update TMP_ACCOUNT set error = '%s is null', status = 1 where error is null and %s is null", column, column
      ));

    for (Map.Entry<String, String> entry : tmpSqlVars.entrySet()) {
      uploadAndDropErrors(entry.getKey(), "transition_frs", "row_number");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    exec("update TMP_ACCOUNT set id = f.id, client = f.client from ClientAccount f where number = f.number");

    exec("update TMP_ACCOUNT set client = f.id from Client f where f.cia_id = client_id and f.cia_id is not null");

    exec("update TMP_ACCOUNT set client = nextval('s_client') where client is null");

    exec("update TMP_ACCOUNT set id = nextval('s_client_account') where id is null");
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    exec("update TMP_TRANSACTION set transaction_type_id = f.id " +
      "from TransactionType f " +
      "where transaction_type = f.name");

    exec("update TMP_TRANSACTION set transaction_type_id = nextval('s_transaction_type') " +
      "where transaction_type_id is null");

    exec("update TMP_TRANSACTION set account = f.id from TMP_ACCOUNT f where account_number = f.number");

    exec("update TMP_TRANSACTION set id = nextval('s_client_account_transaction')");
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    exec("insert into TransactionType(id, name) " +
      "select transaction_type_id, transaction_type from TMP_TRANSACTION on conflict do nothing;");
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    exec("insert into client(id) select client from TMP_ACCOUNT on conflict do nothing;");

    exec("insert into ClientAccount(id, client, number, registered_at) " +
      "select id, client, number, registered_at from TMP_ACCOUNT where status = 0 on conflict do nothing;");
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    exec("insert into ClientAccountTransaction(id, account, money, finished_at, type) " +
      "select id, account, money, finished_at, transaction_type_id from TMP_TRANSACTION " +
      "where status = 0");
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    exec("update ClientAccount set money = money + (select sum(money) from TMP_TRANSACTION f where f.account = id)");

    for (Map.Entry<String, String> entry : tmpSqlVars.entrySet()) {
      uploadAllOk(entry.getKey(), "transition_frs", "row_number");
    }
  }

  private int download() throws SQLException {
    long startedAt = System.nanoTime();
    int batchSize = 0;
    int recordsCount = 0;
    try (PreparedStatement srcPS = migrationConnection.prepareStatement(
      "select * from transition_frs where status='JUST_INSERTED' order by number limit ?"
    )) {
      srcPS.setInt(1, chunkSize);

      operConnection.setAutoCommit(false);

      try (ResultSet srcRs = srcPS.executeQuery()) {

        Insert insertTransaction = new Insert("TMP_TRANSACTION");
        insertTransaction.field(1, "transaction_type", "?");
        insertTransaction.field(2, "account_number", "?");
        insertTransaction.field(3, "money", "?");
        insertTransaction.field(4, "finished_at", "?");
        insertTransaction.field(5, "row_number", "?");

        Insert insertAccount = new Insert("TMP_ACCOUNT");
        insertAccount.field(1, "registered_at", "?");
        insertAccount.field(2, "client_id", "?");
        insertAccount.field(3, "number", "?");
        insertAccount.field(4, "row_number", "?");


        try (
          PreparedStatement transactionPS = operConnection.prepareStatement(r(insertTransaction.toString()));
          PreparedStatement accountPS = operConnection.prepareStatement(r(insertAccount.toString()))
        ) {
          while (srcRs.next()) {
            String data = srcRs.getString("record_data");
            FrsRecord record = FrsRecord.parse(data);
            record.number = srcRs.getLong("number");
            if (record.type == FrsRecord.Type.NEW_ACCOUNT) {
              accountPS.setDate(1, record.registered_at);
              accountPS.setString(2, record.client_id);
              accountPS.setString(3, record.account_number);
              accountPS.setLong(4, record.number);

              accountPS.addBatch();
            } else {
              transactionPS.setString(1, record.transaction_type);
              transactionPS.setString(2, record.account_number);
              transactionPS.setFloat(3, record.money);
              transactionPS.setDate(4, record.finished_at);
              transactionPS.setLong(5, record.number);

              transactionPS.addBatch();
            }
            ++batchSize;
            ++recordsCount;
            if (batchSize >= downloadMaxBatchSize) {
              accountPS.executeBatch();
              transactionPS.executeBatch();
              batchSize = 0;
              operConnection.commit();
            }
          }
          if (batchSize > 0) {
            accountPS.executeBatch();
            transactionPS.executeBatch();
            operConnection.commit();
          }
        }
        {
          long now = System.nanoTime();
          info("TOTAL Downloaded records " + recordsCount + " for " + showTime(now, startedAt)
            + " : " + recordsPerSecond(recordsCount, now - startedAt));
        }
        return recordsCount;
      } catch (IOException e) {
        e.printStackTrace();
      }
    } finally {
      operConnection.setAutoCommit(true);
    }
    return 0;
  }

  private void createTables() throws SQLException {
    //language=PostgreSQL
    exec("create table TMP_ACCOUNT (\n" +
      "  id bigint,\n" +
      "  row_number bigint primary key,\n" +
      "  client_id varchar(300),\n" +
      "  client bigint,\n" +
      "  money float,\n" +
      "  number varchar(300),\n" +
      "  registered_at timestamp,\n" +
      "  status int not null default 0," +
      "  error varchar(300)" +
      ");");
    //language=PostgreSQL
    exec("create table TMP_TRANSACTION (\n" +
      "  row_number bigint primary key,\n" +
      "  id bigint,\n" +
      "  account bigint,\n" +
      "  account_number varchar(300),\n" +
      "  money float not null,\n" +
      "  finished_at timestamp,\n" +
      "  transaction_type_id bigint,\n" +
      "  transaction_type varchar(300),\n" +
      "  status int not null default 0," +
      "  error varchar(300)" +
      ");");
  }
}
