package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.Address;
import kz.greetgo.sandbox.controller.model.AddressType;
import kz.greetgo.sandbox.db.stand.model.AddressDot;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface AddressTestDao {

  @Insert("insert into Addresses ( id, clientId, type, street, house, flat ) " +
    "values ( #{id}, #{clientId}, #{type}::AddressType, #{street}, #{house}, #{flat} )")
  void insertAddressDot(AddressDot addressDot);

  @Insert("insert into Addresses ( id, clientId, type, street, house, flat ) " +
    "values ( #{id,}, #{clientId}, #{type}::AddressType, #{street}, #{house}, #{flat} )")
  void insertAddress(Address address);

  @Select("select * from addresses where clientId = #{clientId} and type = #{type}::AddressType")
  Address getAddressByClientIdAndType(@Param("clientId") int clientId,
                                      @Param("type") AddressType type);

  @Delete("Truncate Addresses cascade")
  void truncateTable();

}
