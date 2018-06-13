package kz.greetgo.sandbox.db.test.dao.postgres;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.db.test.dao.AuthTestDao;
import kz.greetgo.sandbox.db.test.dao.TableTestDao;
import liquibase.structure.core.Table;

@Bean
public interface AuthTestDaoPostgres extends AuthTestDao {}

