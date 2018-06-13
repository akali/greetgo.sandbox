package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.Charm;
import org.apache.ibatis.annotations.Insert;

public interface TableTestDao {
  @Insert("insert into Charm (id, name, energy, description) " +
    "                  values (#{id}, #{name}, #{energy}, #{description})")
  void insertCharm(Charm charm);
}
