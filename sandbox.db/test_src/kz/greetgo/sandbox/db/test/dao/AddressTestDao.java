package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.Address;
import kz.greetgo.sandbox.controller.model.AddressType;
import kz.greetgo.sandbox.db.stand.model.AddressDot;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AddressTestDao {

  @Insert("insert into Addresses ( id, clientId, type, street, house, flat ) " +
    "values ( #{id}, #{clientId}, #{type}::AddressType, #{street}, #{house}, #{flat} )")
  void insertAddressDot(AddressDot addressDot);

  @Insert("insert into Addresses ( id, clientId, type, street, house, flat ) " +
    "values ( coalesce(#{id}, nextval('address_id_seq')), #{clientId}, #{type}::AddressType, #{street}, #{house}, #{flat} )")
  void insertAddress(Address address);

  @Select("select * from addresses where clientId = #{clientId} and type = #{type}::AddressType LIMIT 1")
  Address getAddressByClientIdAndType(@Param("clientId") int clientId,
                                      @Param("type") AddressType type);

  @Select("select * from addresses where clientId = #{clientId} and isActive = true")
  List<Address> getClientActiveAddresses(@Param("clientId") int clientId);

  @Delete("Truncate Addresses cascade")
  void truncateTable();

}
