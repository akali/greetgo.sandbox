package kz.greetgo.sandbox.db.beans.all;

import kz.greetgo.depinject.core.BeanConfig;
import kz.greetgo.depinject.core.BeanScanner;
import kz.greetgo.depinject.core.Include;
import kz.greetgo.sandbox.controller.controller.BeanConfigControllers;
import kz.greetgo.sandbox.db.dao.postgres.BeanConfigPostgresDao;
import kz.greetgo.sandbox.db.in_service.InServiceBeanScanner;
import kz.greetgo.sandbox.db.register_impl.BeanConfigRegisterImpl;

@BeanConfig
@BeanScanner
@Include({InServiceBeanScanner.class,
  BeanConfigRegisterImpl.class,
  BeanConfigPostgresDao.class,
  BeanConfigControllers.class})
public class BeanConfigAll {}
