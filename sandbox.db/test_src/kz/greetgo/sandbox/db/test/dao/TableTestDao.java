package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.*;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;

public interface TableTestDao {
  @Delete("delete from Charm")
  void clearCharm();

  @Insert("insert into Charm (id, name, description, energy) " +
    "                  values (#{id}, #{name}, #{description}, #{energy})")
  void insertCharm(Charm charm);

  @Insert("insert into Client (id, surname, name, patronymic, gender, birth_date, charm)" +
    "values (#{id}, #{surname}, #{name}, #{patronymic}, #{gender}, #{birth_date}, #{charm})")
  void insertClient(Client client);

  @Insert("insert into ClientAddress (client, type, street, house, flat)" +
    "values (#{client}, #{type}, #{street}, #{house}, #{flat})")
  void insertClientAddress(ClientAddress address);

  @Insert("insert into ClientPhone (client, number, type)" +
    "values (#{client}, #{number}, #{type})")
  void insertClientPhone(ClientPhone phone);

  @Insert("insert into ClientAccount (id, client, money, number, registered_at)" +
    "values (#{id}, #{client}, #{money}, #{number}, #{registered_at})")
  void insertClientAccount(ClientAccount account);

  @Insert("insert into TransactionType (id, code, name)" +
    "values (#{id}, #{code}, #{name})")
  void insertTransactionType(TransactionType type);

  @Insert("insert into ClientAccountTransaction (id, account, money, finished_at, type)" +
    "values (#{id}, #{account}, #{money}, #{finished_at}, #{type})")
  void insertClientAccountTransation(ClientAccountTransaction transaction);
}
