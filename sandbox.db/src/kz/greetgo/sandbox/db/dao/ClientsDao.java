package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.*;
import org.apache.ibatis.annotations.*;

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
    "LEFT JOIN ClientAccount c2 on client.id = c2.client " +
    "LEFT JOIN Charm c3 on client.charm = c3.id " +
    "WHERE (client.name || client.surname || client.patronymic like '%'||#{filter}||'%') " +
    "GROUP BY client.id, c3.name " +
    "ORDER BY ${active} ${direction} LIMIT #{limit} OFFSET #{start}"
  )
  List<ClientRecord> getRecordClients(QueryFilter filter);

  @Select("select (id, surname, name, patronymic, gender, birth_date, charm) from client;")
  List<Client> getClients(QueryFilter filter);

  @Insert("insert into client (surname, name, patronymic, gender, birth_date, charm) " +
    "values (#{surname}, #{name}, #{patronymic}, #{gender}, #{birth_date}, #{charm}) " +
    "returning id")
  int addClient(Client client);

  @Insert("insert into ClientAddress (client, type, street, house, flat) " +
    "values (#{client}, #{type}, #{street}, #{house}, #{flat})")
  void addClientAddress(ClientAddress regAddress);

  @Insert("insert into ClientPhone (client, number, type) " +
    "values (#{client}, #{number}, #{type})")
  void addClientPhone(ClientPhone clientPhone);

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
      "LEFT JOIN ClientAccount c2 on client.id = c2.client " +
      "LEFT JOIN Charm c3 on client.charm = c3.id " +
      "WHERE client.id=#{id} " +
      "GROUP BY client.id, c3.name"
  )
  ClientRecord getClientRecordById(@Param("id") int id);

  @Delete(
    "delete from client where id=#{id}"
  )
  void removeClientById(@Param("id") int clientId);

  @Select(
    "select * from client where id=#{id}"
  )
  Client getClient(@Param("id") int id);

  @Update(
    "update ClientAddress set street=#{street}, house=#{house}, flat=#{flat} where client=#{client} and type=#{type}"
  )
  void editClientAddress(ClientAddress regAddress);
}
