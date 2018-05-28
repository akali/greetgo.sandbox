package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.db.stand.model.AccountDot;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;

public interface AccountTestDao {

  @Insert("insert into Accounts ( clientId, money, number, registeredAt ) " +
    "values ( #{clientId}, #{money}, #{number}, #{registeredAt} )")
  void insertAccountDot(AccountDot accountDot);

  @Delete("drop table if exists Accounts cascade;" +
    "create table Accounts (\n" +
    "        id serial primary key,\n" +
    "        clientId integer references Clients(id) on delete cascade,\n" +
    "        money float(4),\n" +
    "        number varchar(30),\n" +
    "        registeredAt timestamp,\n" +
    "        isActive boolean default true\n" +
    "      )")
  void recreateTable();

  @Delete("Truncate Accounts cascade")
  void truncateTable();

}
