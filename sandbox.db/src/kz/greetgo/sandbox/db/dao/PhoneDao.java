package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.Phone;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface PhoneDao {
  @Select("select * from Phones where clientId = #{clientId} and isActive = true")
  List<Phone> getClientPhones(@Param("clientId") int clientId);

  @Insert("insert into phones ( clientId, number, type ) values ( #{clientId}, #{number}, #{type}::PhoneType )")
  void insertPhone(Phone phone);

  @Insert("insert into phones ( id, clientId, number, type ) " +
    "values ( coalesce(#{id}, nextval('phone_id_seq')), #{clientId}, #{number}, #{type}::PhoneType )" +
    " on conflict(id) do update set number = #{number} where phones.id = #{id}")
  void insertOrUpdatePhone(Phone phone);

  @Update("update phones set isActive = false where id = #{id}")
  void deletePhone(@Param("id") int phoneId);

  @Select("select * from phones where clientId = #{clientId}")
  List<Phone> selectAllPhones(@Param("clientId") int clientId);

  @Update("update phones set isActive = false where clientId = #{clientId}")
  void deleteClientPhones(@Param("clientId") Integer id);
}
