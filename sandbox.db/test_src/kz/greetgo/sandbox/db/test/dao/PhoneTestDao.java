package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.Phone;
import kz.greetgo.sandbox.db.stand.model.PhoneDot;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;

public interface PhoneTestDao {

  @Insert("insert into Phones ( clientId, number, type ) values ( #{clientId}, #{number}, #{type}::PhoneType )")
  void insertPhoneDot(PhoneDot phoneDot);

  @Insert("insert into Phones ( id, clientId, number, type, isActive ) " +
  "                               values ( #{id}, #{clientId}, #{number}, #{type}::PhoneType, #{isActive} )")
  void insertPhone(Phone phone);

  @Delete("drop table if exists Phones cascade;" +
    "create table Phones (\n" +
    "        id serial primary key,\n" +
    "        clientId integer references Clients(id) on delete cascade,\n" +
    "        number varchar(30),\n" +
    "        type PhoneType,\n" +
    "        isActive boolean default true\n" +
    "      )")
  void recreateTable();

  @Delete("Delete from Phones")
  void clearTable();

}
