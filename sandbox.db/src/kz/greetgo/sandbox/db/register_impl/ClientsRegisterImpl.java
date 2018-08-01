package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.interfaces.BinResponse;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.AuthRegister;
import kz.greetgo.sandbox.controller.register.ClientsRegister;
import kz.greetgo.sandbox.controller.reports.ReportClientRecordPdfGenerator;
import kz.greetgo.sandbox.controller.reports.ReportClientRecordXlsxGenerator;
import kz.greetgo.sandbox.controller.reports.ReportClientsRecord;
import kz.greetgo.sandbox.db.dao.ClientsDao;
import kz.greetgo.sandbox.db.dao.ReportsDao;
import kz.greetgo.sandbox.db.util.DBHelper;
import kz.greetgo.sandbox.db.util.JdbcSandbox;
import kz.greetgo.util.RND;
import liquibase.exception.DatabaseException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Bean
public class ClientsRegisterImpl implements ClientsRegister {

  public BeanGetter<ClientsDao> clientsDao;
  public BeanGetter<ReportsDao> reportsDao;
  public BeanGetter<AuthRegister> authRegister;
  public BeanGetter<JdbcSandbox> jdbcBeanGetter;

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
  public ClientRecordsListPage getClientRecords(QueryFilter queryFilter) {

    return jdbcBeanGetter.get().execute(connection -> GetClientRecords.instance().run(connection, queryFilter));
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
            "RETURNING id"
        );
        statement.setString(1, client.surname);
        statement.setString(2, client.name);
        statement.setString(3, client.patronymic);
        statement.setString(4, client.gender.toString());
        statement.setDate(5, client.birth_date);
        statement.setInt(6, client.charm);

        ResultSet rs = statement.executeQuery();
        int result = -1;
        while (rs.next()) {
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
  public String generateReport(ReportType reportType, QueryFilter filter, String token) throws IOException {

    // TODO(DONE): не устанавливай так лимиты
    filter.start = 0;
    filter.noLimit = true;

    // TODO(DONE): если не используешь - убирай. Лишний код портит читабильность
    ReportClientsRecord reportClientsRecord = null;

    // TODO(DONE): не используй обсолютные пути
    String root = "reports/" + "Report_" + RND.str(10);

    if (!Files.exists(Paths.get(root)))
      Files.createDirectories(Paths.get(root));

    FileOutputStream fos;

    switch (reportType) {
      case XLSX:
        root += ".xlsx";
        fos = new FileOutputStream(root);
        reportClientsRecord = new ReportClientRecordXlsxGenerator(fos);
        break;
      case PDF:
        root += ".pdf";
        fos = new FileOutputStream(root);
        reportClientsRecord = new ReportClientRecordPdfGenerator(fos);
        break;
    }

    // TODO(DONE: если не используешь - убирай. Лишний код портит читабильность

    //TODO(DONE: у нас есть специальный класс RND. Посмотри там методы. Для строк тоже есть рандом
    String id = RND.str(5);

    GenerateReport report =
      new GenerateReport(
        reportClientsRecord,
        filter,
        authRegister.get().getUserInfo(token).accountName
      );

    report.execute();

    // TODO(DONE): запиши этот файл в бд после реального создания файла, а не до.
    reportsDao.get().putFile(root, id);

    return id;
  }

  @Override
  public void downloadReport(String id, BinResponse binResponse) throws IOException {
    // TODO(DONE): почему метод пустой?
    FileInputStream fileInputStream = new FileInputStream(reportsDao.get().getFile(id));
    BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(binResponse.out());
    String line;
    while ((line = br.readLine()) != null)
      outputStreamWriter.write(line);
  }
}
