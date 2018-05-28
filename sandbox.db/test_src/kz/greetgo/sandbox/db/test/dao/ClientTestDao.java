package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.Client;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;

public interface ClientTestDao {

  @Insert("insert into Clients ( surname, name, patronymic, gender, birthDate, charmId ) " +
    "values ( #{surname}, #{name}, #{patronymic}, #{gender}::Gender, #{birthDate}, #{charmId} )")
  void insertClientDot(ClientDot clientDot);

  @Insert("insert into Clients ( id, surname, name, patronymic, gender, birthDate, charmId, isActive ) " +
    "values ( #{id}, #{surname}, #{name}, #{patronymic}, #{gender}::Gender, #{birthDate}, #{charmId}, #{isActive} )")
  void insertClient(Client client);



  @Delete("drop table if exists Clients cascade;" +
    "create table Clients (\n" +
    "        id serial primary key,\n" +
    "        surname varchar(255) not null,\n" +
    "        name varchar(255) not null,\n" +
    "        patronymic varchar(255),\n" +
    "        gender Gender not null,\n" +
    "        birthDate Date not null,\n" +
    "        charmId integer references Charms(id) on delete cascade not null ,\n" +
    "        isActive boolean default true\n" +
    "      )")
  void recreateTable();

  @Delete("Truncate Clients cascade")
  void truncateTable();

}
