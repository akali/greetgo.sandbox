package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientsRegister;
import kz.greetgo.sandbox.db.test.dao.TableTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.sandbox.db.util.DBHelper;
import liquibase.database.PreparedStatementFactory;
import liquibase.database.jvm.JdbcConnection;
import org.apache.ibatis.jdbc.SQL;
import org.testng.annotations.Test;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

import static org.fest.assertions.api.Assertions.assertThat;


public class ClientsRegisterImplTest extends ParentTestNg {

  public BeanGetter<TableTestDao> tableTestDao;
  public BeanGetter<ClientsRegister> clientsRegister;

  @Test
  public void testGetCharms() {
    DBHelper.run(connection -> {
      List<Charm> charms = new ArrayList<>();

      charms.add(new Charm("HAPPY", "VERY HAPPY PERSON", (float) 0.5));

      for (Charm charm : charms) {
        PreparedStatement st = new PreparedStatementFactory(new JdbcConnection(connection)).create(new SQL()
          .DELETE_FROM("charm")
          .WHERE("name=?", "description=?", "energy=?").toString()
        );
        st.setString(1, charm.name);
        st.setString(2, charm.description);
        st.setFloat(3, charm.energy);

        System.out.println(st.executeUpdate());

        PreparedStatement stat = connection.prepareStatement(
          new SQL()
            .INSERT_INTO("charm")
            .VALUES("name, description, energy", "?,?,?")
            .toString()
        );
        stat.setString(1, charm.name);
        stat.setString(2, charm.description);
        stat.setFloat(3, charm.energy);
        stat.execute();
      }

      //
      //
      List<Charm> testCharms = clientsRegister.get().getCharms();
      //
      //

      assertThat(testCharms).isNotNull();
      assertThat(testCharms).containsAll(charms);
    });
  }

  @Test
  public void testGetClientRecords() {
  }

  @Test
  public void testGetClientDetailsById() {
  }

  private Calendar getCalendar(int day, int month, int year) {
    Calendar cal = new GregorianCalendar();
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month - 1);
    cal.set(Calendar.DATE, day);
    return cal;
  }

  public long getTimestamp(int day, int month, int year) {
    Calendar cal = getCalendar(day, month, year);
    return cal.getTimeInMillis();
  }

  @Test
  public void testAddClientToSave() {
    ClientToSave clientToSave =
      new ClientToSave("Yerbolat", "Ablemetov", "Askarovich", 1, GenderType.MALE,
        new ClientAddress(
          AddressType.REG, "Seyfullina", "13a", "23"
        ),
        new ClientAddress(
          AddressType.FACT, "Bekturova", "23", null
        ),
        getTimestamp(12, 1, 1998),
        Arrays.asList(
          new ClientPhone("+77473105484", PhoneType.MOBILE),
          new ClientPhone("+77273518547", PhoneType.HOME)
        )
      );
    Client clientToSaveClientCopy =
      new Client(
        -1,
        clientToSave.surname,
        clientToSave.name,
        clientToSave.patronymic,
        clientToSave.gender,
        clientToSave.birthDate,
        clientToSave.charm
      );

    //
    //
    ClientRecord clientRecord = clientsRegister.get().addClientToSave(clientToSave);
    //
    //

    DBHelper.run(connection -> {
      PreparedStatement statement = connection.prepareStatement(
        new SQL()
          .SELECT("id", "surname", "name", "patronymic", "gender", "birth_date", "charm")
          .FROM("client")
          .WHERE("id=?")
          .toString()
      );
      statement.setInt(1, clientRecord.id);

      ResultSet rs = statement.executeQuery();
      List<Client> clients = new ArrayList<>();
      while (rs.next()) {
        clients.add(new Client(rs.getInt("id"), rs.getString("surname"), rs.getString("name"),
          rs.getString("patronymic"), GenderType.valueOf(rs.getString("gender")), rs.getLong("birth_date"), rs.getInt("charm")));
      }
      assertThat(clients).hasSize(1);
      assertThat(clients.get(0)).isNotNull();
      assertThat(clients.get(0)).equals(clientToSaveClientCopy);
    });
  }

  @Test
  public void testEditClientToSave() {
  }

  @Test
  public void testRemoveClientById() {

    List<Client> clients = new ArrayList<>(),
      toRemove = new ArrayList<>();

    for (Client client : clients)
      tableTestDao.get().insertClient(client);

    Random rnd = new Random();

    clients.forEach(client -> {
      if (rnd.nextInt(1) == 0)
        toRemove.add(client);
    });

    //
    //
    toRemove.forEach(client -> clientsRegister.get().removeClientById(client.id));
    //
    //

    toRemove.forEach(client -> {
//      assertThat(tableTestDao.get().getClient(client.id)).isNullOrEmpty();
    });

  }

  @Test
  public void test() {
    try (Connection conn = DriverManager.getConnection(
      "jdbc:postgresql://localhost/aqali_sandbox",
      "aqali_sandbox",
      "111"
    )) {

      conn.setAutoCommit(false);
      PreparedStatement statement =
        conn.prepareStatement("insert into charm values (?,?,?,?);");

      statement.setInt(1, 7);
      statement.setString(2, "GOOD");
      statement.setString(3, "SODOOG");
      statement.setDouble(4, 2.5);

      statement.execute();

      ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM Charm");

      while (rs.next()) {
        System.out.println(rs.getRow() + ". " + rs.getString("id")
          + "\t" + rs.getString("name")
          + "\t" + rs.getString("description")
          + "\t" + rs.getString("energy")
        );
      }

//      statement = conn.createStatement();
      System.out.println(statement.execute("delete from charm where id=3"));

//      statement = conn.createStatement();
      rs = statement.executeQuery("SELECT * FROM Charm");

      while (rs.next()) {
        System.out.println(rs.getRow() + ". " + rs.getString("id")
          + "\t" + rs.getString("name")
          + "\t" + rs.getString("description")
          + "\t" + rs.getString("energy")
        );
      }

      // statement.execute("delete * from Charm;");
      conn.commit();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}