package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.Phone;
import kz.greetgo.sandbox.controller.model.PhoneType;
import kz.greetgo.sandbox.db.stand.model.PhoneDot;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface PhoneTestDao {

  @Insert("insert into Phones ( id, clientId, number, type ) values ( #{id}, #{clientId}, #{number}, #{type}::PhoneType )")
  void insertPhoneDot(PhoneDot phoneDot);

  @Insert("insert into Phones ( id, clientId, number, type, isActive ) " +
  "                               values ( #{id}, #{clientId}, #{number}, #{type}::PhoneType, #{isActive} )")
  void insertPhoneWithId(Phone phone);

  @Insert("insert into Phones ( clientId, number, type ) " +
    "                               values ( #{clientId}, #{number}, #{type}::PhoneType )")
  void insertPhone(Phone phone);

  @Select("select * from phones where clientId = #{clientId} and type = #{type}::PhoneType")
  Phone getPhoneByClientIdAndType(@Param("clientId") int clientId,
                                  @Param("type")PhoneType type);

  @Select("select * from phones where clientId = #{clientId} and type = 'MOBILE'")
  List<Phone> getMobilesByClientId(@Param("clientId") int clientId);

  @Delete("Truncate Phones cascade")
  void truncateTable();

}
