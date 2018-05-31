package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.Address;
import kz.greetgo.sandbox.controller.model.AddressType;
import org.apache.ibatis.annotations.*;

public interface AddressDao {

  @Select("select * from Addresses where clientId = #{clientId} and type = #{type}::AddressType and isActive = true")
  Address getAddressByClientIdAndType(@Param("clientId") int clientId,
                                      @Param("type")AddressType type);

  @Insert("insert into addresses ( clientId, type, street, house, flat )" +
"                               values ( #{clientId}, #{type}::AddressType, #{street}, #{house}, #{flat} )")
  void insertAddress(Address address);

  @Update("update addresses set street = #{street}, house = #{house}, flat = #{flat}, isActive = #{isActive}" +
    " where id = #{id}")
  void updateAddress(Address address);

  @Insert("insert into addresses ( id, clientId, type, street, house, flat )" +
    " values (coalesce(#{id}, nextval('address_id_seq')), #{clientId}, #{type}::AddressType, #{street}, #{house}, #{flat} )" +
    " on conflict (id) do update set street = #{street}, house = #{house}, flat = #{flat}, isActive = #{isActive}" +
    " where addresses.id = #{id}")
  void insertOrUpdateAddress(Address address);

  @Delete("update addresses set isActive = false where id = #{id}")
  void deleteAddress(@Param("id") int addressId);
}
