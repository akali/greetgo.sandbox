package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.Client;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface ClientDao {

  @Select("select * from Clients where id = #{clientId} and isActive = true")
  Client getClientById(@Param("clientId") int clientId);

  @Insert("insert into Clients (id, surname, name, patronymic, gender, birthDate, charmId )" +
    "values ( #{id}, #{surname}, #{name}, #{patronymic}, #{gender}::Gender, #{birthDate}, #{charmId} )")
  void insertClient(Client client);

  @Update("update clients set name = #{name}, surname = #{surname}, patronymic = #{patronymic}," +
    " gender = #{gender}::Gender, birthDate = #{birthDate}, charmId = #{charmId} where id = #{id} and isActive = true")
  void updateClient(Client client);

  @Update("update clients set isActive = false where id = #{id}")
  void deleteClient(@Param("id") int clientId);
}
