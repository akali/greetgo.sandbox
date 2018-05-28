package kz.greetgo.sandbox.db.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface AccountDao {

  @Select("select MIN(money) from Accounts where clientId = #{clientId} and isActive = true")
  Float getMinAccountBalance(@Param("clientId") int clientId);

  @Select("select MAX(money) from Accounts where clientId = #{clientId} and isActive = true")
  Float getMaxAccountBalance(@Param("clientId") int clientId);

  @Select("select SUM(money) from Accounts where clientId = #{clientId} and isActive = true")
  Float getTotalAccountBalance(@Param("clientId") int clientId);
}
