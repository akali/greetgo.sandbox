package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.Charm;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ClientsDao {
  @Select("select (id, name, description, energy) from Charm")
  List<Charm> getCharms();
}
