package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.db.stand.model.AccountDot;
import org.apache.ibatis.annotations.Insert;

public interface AccountTestDao {

  @Insert("insert into Accounts ( clientId, money, number, registeredAt ) " +
    "values ( #{clientId}, #{money}, #{number}, #{registeredAt} )")
  void insertAccountDot(AccountDot accountDot);

}
