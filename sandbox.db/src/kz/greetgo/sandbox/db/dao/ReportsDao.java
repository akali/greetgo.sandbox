package kz.greetgo.sandbox.db.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ReportsDao {
  @Insert("insert into report(url, id_code) values(#{url}, #{id_code})")
  void putFile(@Param("url") String url, @Param("id_code") String idCode);

  @Select("select url from report where id_code = #{id_code}")
  String getFile(@Param("id_code") String idCode);
}
