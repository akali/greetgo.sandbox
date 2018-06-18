package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.Charm;
import kz.greetgo.sandbox.controller.model.Client;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.QueryFilter;
import org.apache.ibatis.annotations.Select;

import javax.management.Query;
import java.util.List;

public interface ClientsDao {
  @Select("select (id, name, description, energy) from Charm")
  List<Charm> getCharms();

  @Select(
    "SELECT " +
      "client.id as id, " +
      "client.name as name, " +
      "client.surname as surname, " +
      "client.patronymic as patronymic, " +
      "sum(c2.money) as total, " +
      "max(c2.money) as max, " +
      "min(c2.money) as min, " +
      "c3.name as charm, " +
      "(extract(year from age(birth_date))) as age " +
    "FROM client " +
    "JOIN ClientAccount c2 on client.id = c2.client " +
    "JOIN Charm c3 on client.charm = c3.id " +
    "WHERE (client.name || client.surname || client.patronymic like '%'||#{filter}||'%') " +
    "GROUP BY client.id, c3.name " +
    "ORDER BY ${active} ${direction} LIMIT #{limit} OFFSET #{start}"
  )
  List<ClientRecord> getRecordClients(QueryFilter filter);

  @Select("select (id, surname, name, patronymic, gender, birth_date, charm) from client;")
  List<Client> getClients(QueryFilter filter);
}
