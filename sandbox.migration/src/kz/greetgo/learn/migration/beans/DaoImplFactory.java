package kz.greetgo.learn.migration.beans;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.beans.all.DbSessionFactory;
import kz.greetgo.sandbox.db.util.AbstractMybatisDaoImplFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

public class DaoImplFactory extends AbstractMybatisDaoImplFactory {
  public BeanGetter<DbSessionFactory> dbSessionFactory;

  @Override
  protected SqlSession getSqlSession() {
    return dbSessionFactory.get().sqlSession();
  }

  @Override
  protected Configuration getConfiguration() {
    return dbSessionFactory.get().getConfiguration();
  }
}
