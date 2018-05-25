package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.db.stand.model.ClientDot;
import org.apache.ibatis.annotations.Insert;

public interface ClientTestDao {

  @Insert("insert into Clients ( surname, name, patronymic, gender, charmId ) " +
    "values ( #{surname}, #{name}, #{patronymic}, #{gender}::Gender, #{charmId} )")
  void insertClientDot(ClientDot clientDot);

}
