package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.Address;
import kz.greetgo.sandbox.controller.model.AddressType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface AddressDao {

  @Select("select * from Addresses where clientId = #{clientId} and type = #{type}::AddressType")
  Address getAddressByClientIdAndType(@Param("clientId") int clientId,
                                      @Param("type")AddressType type);

  @Insert("insert into addresses ( clientId, type, street, house, flat )" +
"                               values ( #{clientId}, #{type}::AddressType, #{street}, #{house}, #{flat} )")
  void insertAddress(Address address);

  @Update("update addresses set street = #{street}, house = #{house}, flat = #{flat}, isActive = #{isActive}" +
    " where id = #{id}")
  void updateAddress(Address address);

}
