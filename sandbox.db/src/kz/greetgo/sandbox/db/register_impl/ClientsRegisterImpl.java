package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.interfaces.BinResponse;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientsRegister;
import kz.greetgo.sandbox.db.dao.ClientsDao;
import kz.greetgo.sandbox.db.util.DBHelper;
import liquibase.exception.DatabaseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Bean
public class ClientsRegisterImpl implements ClientsRegister {

  public BeanGetter<ClientsDao> clientsDao;

  @Override
  public List<Charm> getCharms() {
    try {
      return new DBHelper<List<Charm>>().run(connection -> {
        ResultSet rs = connection.prepareStatement("select * from charm").executeQuery();
        List<Charm> charms = new ArrayList<>();
        while (rs.next()) {
          charms.add(new Charm(rs.getInt("id"), rs.getString("name"),
            rs.getString("description"), rs.getFloat("energy")));
        }
        return charms;
      });
    } catch (DatabaseException | SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public TableResponse getClientRecords(QueryFilter queryFilter) {
    try {
      return new DBHelper<TableResponse>().run(connection -> GetClientRecords.instance().run(connection, queryFilter));
    } catch (DatabaseException | SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public ClientDetail getClientDetailsById(int clientId) {
    return GetClientDetails.getClientDetailsById(clientId);
  }

  @Override
  public ClientRecord addClientToSave(ClientToSave clientToSave) {
    try {
      Client client = clientToSave.getClientCopy();
      client.id = new DBHelper<Integer>().run(connection -> {
        PreparedStatement statement = connection.prepareStatement(
          "INSERT INTO client (surname, name, patronymic, gender, birth_date, charm) " +
            "VALUES (?, ?, ?, ?, ?, ?) " +
            "RETURNING (id)"
        );
        statement.setString(1, client.surname);
        statement.setString(2, client.name);
        statement.setString(3, client.patronymic);
        statement.setString(4, client.gender.toString());
        statement.setDate(5, client.birth_date);
        statement.setInt(6, client.charm);

        ResultSet rs = statement.executeQuery();
        int result = -1;
        while (rs.next()){
          result = (rs.getInt(1));
        }
        return result;
      });
      clientToSave.set(client.id);
      clientsDao.get().addClientAddress(clientToSave.regAddress);
      clientToSave.phones.forEach(clientsDao.get()::addClientPhone);
      if (clientToSave.factAddress != null)
        clientsDao.get().addClientAddress(clientToSave.factAddress);
      return clientsDao.get().getClientRecordById(client.id);
    } catch (DatabaseException | SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public ClientRecord editClientToSave(ClientToSave clientToSave) {
    try {
      new DBHelper<Void>().run(connection -> {
        PreparedStatement statement = connection.prepareStatement(
          "Update client set surname=?, name=?, patronymic=?, gender=?, birth_date=?, charm=? " +
            "where id=?"
        );
        statement.setString(1, clientToSave.surname);
        statement.setString(2, clientToSave.name);
        statement.setString(3, clientToSave.patronymic);
        statement.setString(4, clientToSave.gender.toString());
        statement.setDate(5, new Date(clientToSave.birthDate));
        statement.setInt(6, clientToSave.charm);
        statement.setInt(7, clientToSave.id);
        System.out.println(statement.executeUpdate());
        return null;
      });

      Client client = clientsDao.get().getClient(clientToSave.id);
      clientToSave.set(clientToSave.id);
      clientsDao.get().editClientAddress(clientToSave.regAddress);
      clientToSave.phones.forEach(clientsDao.get()::addClientPhone);
      if (clientToSave.factAddress != null)
        clientsDao.get().editClientAddress(clientToSave.factAddress);
      return clientsDao.get().getClientRecordById(client.id);
    } catch (DatabaseException | SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public void removeClientById(int clientId) {
    clientsDao.get().removeClientById(clientId);
  }

  @Override
  public void generateReport(FileType fileType, int authorId, QueryFilter filter, BinResponse binResponse) throws IOException {
    binResponse.setFilename(("Report from: " + new SimpleDateFormat("dd-MM-yyyy")).concat(String.valueOf(fileType)));
    binResponse.setContentType("application/txt");
    binResponse.out().write(123);
    binResponse.out().flush();
  }
}
