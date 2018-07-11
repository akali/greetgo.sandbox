package kz.greetgo.learn.migration.test.dao.postgres;

import kz.greetgo.depinject.core.BeanConfig;
import kz.greetgo.depinject.core.BeanScanner;
import kz.greetgo.learn.migration.beans.DaoImplFactory;

@BeanScanner
@BeanConfig(defaultFactoryClass = DaoImplFactory.class)
public class BeanConfigTestDao {
}
