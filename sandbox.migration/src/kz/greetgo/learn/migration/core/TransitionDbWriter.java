package kz.greetgo.learn.migration.core;

import kz.greetgo.learn.migration.interfaces.ConnectionConfig;
import kz.greetgo.learn.migration.util.ConnectionUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.lang.String.format;

public class TransitionDbWriter {

  private static final int CHUNK_SIZE = 250_000;

  private int batchSize = 0;
  private boolean open = false;

  private Connection connection;
  private ConnectionConfig connectionConfig;

  private PreparedStatement preparedStatement;
  protected String tableName = "transition_client";

  public TransitionDbWriter(ConnectionConfig connectionConfig) {
    this.connectionConfig = connectionConfig;
  }

  public void start() throws Exception {
    createConnection();
    createStatement();
    open = true;
  }

  protected void setTableName(String tableName) {
    this.tableName = tableName;
  }

  private void createStatement() throws SQLException {
    preparedStatement = connection.prepareStatement(
      format("insert into %s (record_data) values (?)", this.tableName)
    );
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

  private void createConnection() throws Exception {
    this.connection = ConnectionUtils.create(connectionConfig);
    connection.setAutoCommit(false);
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

  public void writeRow(String xml) throws Exception {
    if (!open)
      throw new Exception("Writer is not opened");
    try {
      preparedStatement.setString(1, xml);
      preparedStatement.addBatch();

      ++batchSize;
      if (batchSize >= CHUNK_SIZE) {
        batchSize = 0;
        preparedStatement.executeBatch();
        connection.commit();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void finish() {
    if (connection != null && preparedStatement != null) {
      try {
        if (batchSize > 0) {
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

}
