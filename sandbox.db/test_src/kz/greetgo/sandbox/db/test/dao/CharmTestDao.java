package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.model.Charm;
import kz.greetgo.sandbox.db.dao.CharmDao;
import kz.greetgo.sandbox.db.stand.model.CharmDot;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface CharmTestDao {

  @Insert("insert into Charms ( name, description, energy, isActive) " +
"                                                   values ( #{name}, #{description}, #{energy}, #{isActive} )")
  void insertCharm(Charm charm);

  @Insert("insert into Charms ( name, description, energy ) values ( #{name}, #{description}, #{energy} )")
  void insertCharmDot(CharmDot charmDot);

  @Select("select COUNT(*) from Charms")
  int getRecordsCount();

  @Delete("delete from Charms")
  void clearTable();

}
