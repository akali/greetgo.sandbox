package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.sandbox.controller.model.*;
import org.apache.ibatis.jdbc.SQL;
import org.postgresql.util.PGobject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GetClientDetails {
  public static ClientDetail getClientDetailsById(int id) {
    try (Connection conn = DriverManager.getConnection(
      "jdbc:postgresql://localhost/aqali_sandbox",
      "aqali_sandbox",
      "111"
    )) {
      ClientDetail result = getClientDetail(conn, id);
      if (result != null) result.setPhones(getPhones(conn, id));
      if (result != null) result.setCharms(getCharms(conn));
      // System.out.println(result);
      return result;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  private static List<Charm> getCharms(Connection conn) throws SQLException {
    List<Charm> charms = new ArrayList<>();

    String sql = "select id, name, description, energy from charm";
    PreparedStatement statement = conn.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      charms.add(
        new Charm(
          resultSet.getInt("id"),
          resultSet.getString("name"),
          resultSet.getString("description"),
          resultSet.getFloat("energy")
        )
      );
    }

    return charms;
  }

  private static List<ClientPhone> getPhones(Connection conn, int id) throws SQLException {
    List<ClientPhone> phones = new ArrayList<>();

    String sql = "select client, number, type from clientphone where client = ?";
    PreparedStatement statement = conn.prepareStatement(sql);
    statement.setInt(1, id);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      phones.add(
        new ClientPhone(
          resultSet.getInt("client"),
          resultSet.getString("number"),
          PhoneType.valueOf(resultSet.getString("type"))
        )
      );
    }

    return phones;
  }

  private static ClientDetail getClientDetail(Connection conn, int id) throws SQLException {
    String sql = prepareSql(id);
    PreparedStatement statement = conn.prepareStatement(sql);
    statement.setInt(1, id);

    ResultSet resultSet = statement.executeQuery();
    ClientDetail result = null;
    while (resultSet.next()) {
      result = new ClientDetail();
      result.setId(resultSet.getInt("id"));
      result.setName(resultSet.getString("name"));
      result.setSurname(resultSet.getString("surname"));
      result.setPatronymic(resultSet.getString("patronymic"));
      result.setGender(GenderType.valueOf(resultSet.getString("gender")));
      result.setBirthDate(resultSet.getDate("birthDate").getTime());
      result.setRegAddress(new ClientAddress(result.id, AddressType.REG,
        resultSet.getString("regStreet"),
        resultSet.getString("regHouse"),
        resultSet.getString("regFlat")));
      String factStreet = resultSet.getString("factStreet");
      if (factStreet != null && !factStreet.equals("null")) {
        result.setFactAddress(new ClientAddress(result.id, AddressType.FACT,
          factStreet,
          resultSet.getString("factHouse"),
          resultSet.getString("factFlat")));
      }
      result.setCharm(new Charm(
        resultSet.getInt("charmid"),
        resultSet.getString("charmname"),
        resultSet.getString("charmdescription"),
        resultSet.getFloat("charmEnergy")));

    }
    return result;
  }

  private static String prepareSql(int id) {
    return "SELECT c1.id as id, c1.surname as surname, c1.name as name, c1.patronymic as patronymic,\n" +
      "    c1.gender as gender, c1.birth_date as birthDate,\n" +
      "    c2.flat as regFlat, c2.house as regHouse, c2.street as regStreet,\n" +
      "    c3.flat as factFlat, c3.house as factHouse, c3.street as factStreet,\n" +
      "    c4.id as charmId, c4.name as charmName, c4.description as charmDescription,\n" +
      "    c4.energy as charmEnergy\n" +
      "FROM Client c1\n" +
      "JOIN ClientAddress c2 ON c2.client=c1.id AND c2.type='REG'\n" +
      "LEFT JOIN ClientAddress c3 ON c3.client=c1.id AND c3.type='FACT'\n" +
      "JOIN Charm c4 ON c4.id=c1.charm\n" +
      "WHERE c1.id = ?;";
  }

  /**
   * SELECT c1.id as id, c1.surname as surname, c1.name as name, c1.patronymic as patronymic,
   *     c1.gender as gender, c1.birth_date as birthDate,
   *     to_json(c2) as regAddress,
   *     to_json(c3) as factAddress,
   *     to_json(c4) as Charm
   * FROM Client c1
   *   JOIN ClientAddress c2 ON c2.client = c1.id AND c2.type = 'REG'
   *   LEFT JOIN ClientAddress c3 ON c3.client = c1.id AND c3.type = 'FACT'
   *   JOIN Charm c4 ON c4.id = c1.charm
   * WHERE c1.id = 3;
   *
   * Can be used with json too
   *
   */
}
