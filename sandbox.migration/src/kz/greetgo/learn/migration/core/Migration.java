package kz.greetgo.learn.migration.core;

import kz.greetgo.learn.migration.interfaces.ConnectionConfig;
import kz.greetgo.learn.migration.util.ConnectionUtils;
import kz.greetgo.learn.migration.util.TimeUtils;

import java.io.Closeable;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static kz.greetgo.learn.migration.util.TimeUtils.showTime;

public abstract class Migration implements Closeable {

  protected final ConnectionConfig operConfig;
  protected final ConnectionConfig ciaConfig;
  public int chunkSize = 1_000_000;
  public int downloadMaxBatchSize = 50_000;
  public int uploadMaxBatchSize = 50_000;
  public int showStatusPingMillis = 5000;
  protected Connection operConnection = null, migrationConnection = null;

  public Migration(ConnectionConfig operConfig, ConnectionConfig ciaConfig) {
    this.operConfig = operConfig;
    this.ciaConfig = ciaConfig;
  }

  protected HashMap<String, String> tmpSqlVars;

  @Override
  public void close() {
    closeOperConnection();
    closeMigrationConnection();
  }

  protected void closeMigrationConnection() {
    if (migrationConnection != null) {
      try {
        migrationConnection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
      migrationConnection = null;
    }
  }

  protected void closeOperConnection() {
    if (this.operConnection != null) {
      try {
        this.operConnection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
      this.operConnection = null;
    }
  }

  protected void info(String message) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    System.out.println(sdf.format(new Date()) + " [" + getClass().getSimpleName() + "] " + message);
  }

  protected String r(String sql) {
    for (Map.Entry<String, String> a: tmpSqlVars.entrySet()) {
      sql = sql.replace(a.getKey(), a.getValue());
    }
    return sql;
  }

  public abstract int migrate() throws Exception;

  protected void exec(String sql) throws SQLException {
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

  protected void createOperConnection() throws Exception {
    operConnection = ConnectionUtils.create(operConfig);
  }

  protected void createMigrationConnection() throws Exception {
    migrationConnection = ConnectionUtils.create(ciaConfig);
  }

  protected void uploadAllOk(String operTableName, String migrationTableName, String rowNumberColumn) throws Exception {

    info("uploadAllOk goes: maxBatchSize = " + uploadMaxBatchSize);

    final AtomicBoolean working = new AtomicBoolean(true);

    createMigrationConnection();
    migrationConnection.setAutoCommit(false);
    try {

      try (PreparedStatement inPS = operConnection.prepareStatement(r("select " + rowNumberColumn + " from " + operTableName))) {
        info("Prepared statement for : select number from TMP_ACCOUNT");

        try (ResultSet inRS = inPS.executeQuery()) {
          info("Query executed for : select number from TMP_ACCOUNT");

          try (PreparedStatement outPS = migrationConnection.prepareStatement(r(
            "update " + migrationTableName + " set status = 'OK' where number = ?"))) {
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

              outPS.setLong(1, inRS.getLong(rowNumberColumn));
              outPS.addBatch();
              batchSize++;
              recordsCount++;

              if (batchSize >= uploadMaxBatchSize) {
                outPS.executeBatch();
                migrationConnection.commit();
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
              migrationConnection.commit();
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
      closeMigrationConnection();
      working.set(false);
    }
  }

  protected void uploadAndDropErrors(String operTableName, String migrationTableName, String rowNumberColumn) throws Exception {
    info("uploadAndDropErrors goes : maxBatchSize = " + uploadMaxBatchSize);

    final AtomicBoolean working = new AtomicBoolean(true);

    createMigrationConnection();
    migrationConnection.setAutoCommit(false);
    try {

      try (PreparedStatement inPS = operConnection.prepareStatement(r(
        "select " + rowNumberColumn + ", error from " + operTableName + " where error is not null"))) {
        info("Prepared statement for : select number, error from TMP_CLIENT where error is not null");

        try (ResultSet inRS = inPS.executeQuery()) {
          info("Query executed for : select number, error from TMP_CLIENT where error is not null");

          try (PreparedStatement outPS = migrationConnection.prepareStatement(
            "update " + migrationTableName + "set status = 'ERROR', error = ? where number = ?")) {

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
              outPS.setLong(2, inRS.getLong(rowNumberColumn));
              outPS.addBatch();
              batchSize++;
              recordsCount++;

              if (batchSize >= uploadMaxBatchSize) {
                outPS.executeBatch();
                migrationConnection.commit();
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
              migrationConnection.commit();
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
      closeMigrationConnection();
      working.set(false);
    }

    //language=PostgreSQL
    exec("delete from " + operTableName + " where error is not null");
  }
}
