package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.Client;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ClientDao {

  @Select("select * from Clients where id = #{clientId} and isActive = true")
  Client getClientById(@Param("clientId") int clientId);

  @Insert("insert into Clients (id, surname, name, patronymic, gender, birthDate, charmId )" +
    "values ( #{id}, #{surname}, #{name}, #{patronymic}, #{gender}::Gender, #{birthDate}, #{charmId} )")
  void insertClient(Client client);

  @Select("select id from Clients order by id desc limit 1")
  int getLastId();

}
