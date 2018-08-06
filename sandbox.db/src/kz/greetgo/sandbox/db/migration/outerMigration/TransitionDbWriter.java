package kz.greetgo.sandbox.db.migration.outerMigration;

import kz.greetgo.sandbox.db.interfaces.ConnectionConfig;
import kz.greetgo.sandbox.db.util.ConnectionUtils;
import kz.greetgo.sandbox.db.util.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.lang.String.format;

public class TransitionDbWriter {

  private static final int CHUNK_SIZE = 25_000;

  private int batchSize = 0;
  private boolean open = false;

  private Connection connection;
  private ConnectionConfig connectionConfig;

  private PreparedStatement preparedStatement;
  protected String tableName = "transition_client";

  public TransitionDbWriter(ConnectionConfig connectionConfig) {
    this.connectionConfig = connectionConfig;
  }

  private int rowsCount = 0;

  protected void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public void start() throws Exception {
    Logger.d(getClass().getSimpleName(), "method start");
    createConnection();
    createStatement();
    open = true;
  }

  private void closeStatement() {
    if (preparedStatement == null) return;
    try {
      preparedStatement.close();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      preparedStatement = null;
    }
  }

  private void createStatement() throws SQLException {
    Logger.d(getClass().getSimpleName(), "method createStatement");
    preparedStatement = connection.prepareStatement(
      format("insert into %s (record_data) values (?)", this.tableName)
    );
  }

  private void closeConnection() {
    if (connection != null) {
      try {
        connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
      connection = null;
    }
  }

  private void createConnection() throws Exception {
    Logger.d(getClass().getSimpleName(), "method createConnection");
    this.connection = ConnectionUtils.create(connectionConfig);
    connection.setAutoCommit(false);
  }

  public void writeRow(String xml) throws Exception {
    if (!open)
      throw new Exception("Writer is not opened");
    try {
      preparedStatement.setString(1, xml);
      preparedStatement.addBatch();

      ++batchSize;
      if (batchSize >= CHUNK_SIZE) {
        rowsCount += batchSize;
        logUpload();
        batchSize = 0;
        preparedStatement.executeBatch();
        connection.commit();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void finish() {
    Logger.d(getClass().getSimpleName(), "method finish");
    if (connection != null && preparedStatement != null) {
      try {
        if (batchSize > 0) {
          rowsCount += batchSize;
          logUpload();
          batchSize = 0;
          preparedStatement.executeBatch();
          connection.commit();
        }
        connection.setAutoCommit(false);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    closeStatement();
    closeConnection();
    open = false;
  }

  private void logUpload() {
    Logger.d(getClass().getSimpleName(), "executing batch in size " + batchSize);
    Logger.d(getClass().getSimpleName(), "total uploaded " + rowsCount);
  }

}
