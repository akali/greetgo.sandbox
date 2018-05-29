package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.Client;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ClientTestDao {

  @Insert("insert into Clients ( id, surname, name, patronymic, gender, birthDate, charmId ) " +
    "values ( #{id}, #{surname}, #{name}, #{patronymic}, #{gender}::Gender, #{birthDate}, #{charmId} )")
  void insertClientDot(ClientDot clientDot);

  @Insert("insert into Clients ( id, surname, name, patronymic, gender, birthDate, charmId, isActive ) " +
    "values ( #{id}, #{surname}, #{name}, #{patronymic}, #{gender}::Gender, #{birthDate}, #{charmId}, #{isActive} )")
  void insertClient(Client client);

  @Select("select * from Clients where id = #{clientId} and isActive = true")
  Client getActiveClientById(@Param("clientId") int clientId);

  @Delete("Truncate Clients cascade")
  void truncateTable();
}
