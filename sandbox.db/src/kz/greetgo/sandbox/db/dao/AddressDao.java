package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.Address;
import kz.greetgo.sandbox.controller.model.AddressType;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface AddressDao {

  @Select("select * from Addresses where clientId = #{clientId} and type = #{type}::AddressType")
  Address getAddressByClientIdAndType(@Param("clientId") int clientId,
                                      @Param("type")AddressType type);
}
