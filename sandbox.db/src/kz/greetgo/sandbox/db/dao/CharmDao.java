package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.Charm;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CharmDao {

  @Select("select * from Charms where isActive = true")
  List<Charm> getAllCharms();

  @Select("select * from Charms where id = #{charmId} and isActive = true")
  Charm getCharm(@Param("charmId") int charmId);

}
