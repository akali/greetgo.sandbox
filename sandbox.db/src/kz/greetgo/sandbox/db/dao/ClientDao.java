package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.Client;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ClientDao {

  @Select("select * from Clients where id = #{clientId} and isActive = true")
  Client getClientById(@Param("clientId") int clientId);

}
