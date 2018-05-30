package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.Phone;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PhoneDao {
  @Select("select * from Phones where clientId = #{clientId} and isActive = true")
  List<Phone> getClientPhones(@Param("clientId") int clientId);

  @Insert("insert into phones ( clientId, number, type ) values ( #{clientId}, #{number}, #{type}::PhoneType )")
  void insertPhone(Phone phone);
}
