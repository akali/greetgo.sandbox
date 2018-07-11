package kz.greetgo.learn.migration.test.dao;

import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MigrationSourceTestDao {
  @Select("select number from transition_frs limit 500")
  List<Long> getNumbers();

  @Select("select 'Hello world'")
  String helloWorld();
}
