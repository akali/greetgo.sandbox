package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.sandbox.controller.model.Charm;
import kz.greetgo.sandbox.db.dao.CharmDao;
import kz.greetgo.sandbox.db.stand.model.CharmDot;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface CharmTestDao {

  @Insert("insert into Charms (id, name, description, energy, isActive) " +
"                                                   values ( coalesce(#{id}, nextval('charm_id_seq')), #{name}, #{description}, #{energy}, #{isActive} )")
  void insertCharmWithId(Charm charm);

  @Insert("insert into Charms ( name, description, energy ) values ( #{name}, #{description}, #{energy} )")
  void insertCharmDot(CharmDot charmDot);

  @Select("select COUNT(*) from Charms")
  int getRecordsCount();

  @Select("select * from Charms where id = #{charmId} and isActive = true")
  Charm getCharmById(@Param("charmId") int charmId);

  @Delete("drop table if exists Charms cascade;" +
    "create table Accounts (\n" +
    "        id serial primary key,\n" +
    "        clientId integer references Clients(id) on delete cascade,\n" +
    "        money float(4),\n" +
    "        number varchar(30),\n" +
    "        registeredAt timestamp,\n" +
    "        isActive boolean default true\n" +
    "      )")
  void recreateTable();

  @Delete("Truncate Charms cascade")
  void truncateTable();

}
