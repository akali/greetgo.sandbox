package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.db.stand.model.PhoneDot;
import org.apache.ibatis.annotations.Insert;

public interface PhoneTestDao {

  @Insert("insert into Phones ( clientId, number, type ) values ( #{clientId}, #{number}, #{type}::PhoneType )")
  void insertPhoneDot(PhoneDot phoneDot);
}
