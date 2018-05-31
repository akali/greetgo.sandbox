package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.Account;
import kz.greetgo.sandbox.controller.model.Address;
import kz.greetgo.sandbox.db.stand.model.AccountDot;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AccountTestDao {

  @Insert("insert into Accounts ( id, clientId, money, number, registeredAt ) " +
    "values ( #{id}, #{clientId}, #{money}, #{number}, #{registeredAt} )")
  void insertAccountDot(AccountDot accountDot);

  @Insert("insert into Accounts ( id, clientId, money, number, registeredAt, isActive ) " +
    "values ( #{id}, #{clientId}, #{money}, #{number}, #{registeredAt}, #{isActive} )")
  void insertAccount(Account account);

  @Select("select * from accounts where clientId = #{clientId} and isActive = true")
  List<Account> getClientActiveAccounts(@Param("clientId") int clientId);

  @Delete("Truncate Accounts cascade")
  void truncateTable();

}
