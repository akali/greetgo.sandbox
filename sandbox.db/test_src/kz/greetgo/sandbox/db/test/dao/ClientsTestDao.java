package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.*;
import org.apache.ibatis.annotations.*;

import java.util.ArrayList;

public interface ClientsTestDao {

  //TODO: Delete очень ресурсо-ёмкая операция, желательно использовать параметр actual,
  // указывающий на действительность записи.
  // в тестах уже не меняй. Просто знай. На real надо будет именно так делать, через actual
  @Delete("delete from Charm")
  void clearCharm();

  @Insert("insert into Charm (id, name, description, energy) " +
    "                  values (#{id}, #{name}, #{description}, #{energy})")
  void insertCharm(Charm charm);

  @Delete("delete from Client")
  void clearClient();

  @Insert("insert into Client (surname, name, patronymic, gender, birth_date, charm)" +
    "values (#{surname}, #{name}, #{patronymic}, #{gender}, #{birth_date}, #{charm})")
  void insertClient(Client client);

  @Delete("delete from ClientAddress")
  void clearClientAddress();

  @Insert("insert into ClientAddress (client, type, street, house, flat)" +
    "values (#{client}, #{type}, #{street}, #{house}, #{flat})")
  void insertClientAddress(ClientAddress address);

  @Delete("delete from  ClientPhone")
  void clearClientPhone();

  @Insert("insert into ClientPhone (client, number, type)" +
    "values (#{client}, #{number}, #{type})")
  void insertClientPhone(ClientPhone phone);

  @Delete("delete from  ClientAccount")
  void clearClientAccount();

  @Insert("insert into ClientAccount (id, client, money, number, registered_at)" +
    "values (#{id}, #{client}, #{money}, #{number}, #{registered_at})")
  void insertClientAccount(ClientAccount account);

  @Delete("delete from  TransactionType")
  void clearTransactionType();

  @Insert("insert into TransactionType (id, code, name)" +
    "values (#{id}, #{code}, #{name})")
  void insertTransactionType(TransactionType type);

  @Delete("delete from  ClientAccountTransaction")
  void clearClientAccountTransaction();

  @Insert("insert into ClientAccountTransaction (id, account, money, finished_at, type)" +
    "values (#{id}, #{account}, #{money}, #{finished_at}, #{type})")
  void insertClientAccountTransaction(ClientAccountTransaction transaction);

  @Select("SELECT * from ClientRecord where id = #{id}")
  ClientRecord getRecordClientById(@Param("id") int id);

  @Select("SELECT * from ClientDetail where id = #{id}")
  ClientDetail getClientDetailsById(@Param("id") int id);

  @Update("ALTER SEQUENCE charm_id_seq RESTART WITH 1")
  void resetCharmIncrementor();

  @Update("ALTER SEQUENCE client_id_seq RESTART WITH 1")
  void resetClientIncrementor();

  @Update("ALTER SEQUENCE ClientAccount_id_seq RESTART WITH 1")
  void resetClientAccountIncrementor();

  @Update("ALTER SEQUENCE ClientAccountTransaction_id_seq RESTART WITH 1")
  void resetClientAccountTransactionIncrementor();

  @Update("ALTER SEQUENCE TransactionType_id_seq RESTART WITH 1")
  void resetTransactionTypeIncrementor();
}
