package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.db.stand.model.AddressDot;
import org.apache.ibatis.annotations.Insert;

public interface AddressTestDao {

  @Insert("insert into Addresses ( clientId, type, street, house, flat ) " +
    "values ( #{clientId}, #{type}::AddressType, #{street}, #{house}, #{flat} )")
  void insertAddressDot(AddressDot addressDot);
}
