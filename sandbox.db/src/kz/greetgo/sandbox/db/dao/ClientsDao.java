package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.Charm;
import kz.greetgo.sandbox.controller.model.Client;
import kz.greetgo.sandbox.controller.model.QueryFilter;
import org.apache.ibatis.annotations.Select;

import javax.management.Query;
import java.util.List;

public interface ClientsDao {
  @Select("select (id, name, description, energy) from Charm")
  List<Charm> getCharms();

  @Select("" +
    "SELECT " +
      "client.id as id, " +
      "client.name as name, " +
      "client.surname as surname, " +
      "client.patronymic as patronymic, " +
      "(extract(year from age(birth_date))) as age, " +
      "max(c2.money) as max, " +
      "min(c2.money) as min, " +
      "sum(c2.money) as total, " +
      "c3.name as charm " +
    "FROM client " +
    "JOIN ClientAccount c2 on client.id = c2.client " +
    "JOIN Charm c3 on client.charm = c3.id " +
    "WHERE (client.name || client.surname || client.patronymic like #{filter}) " +
    "GROUP BY client.id, c3.name " +
    "ORDER BY #{active} #{direction} LIMIT #{limit} OFFSET #{start}"
  )
  List<Client> getClients(QueryFilter filter);
}
