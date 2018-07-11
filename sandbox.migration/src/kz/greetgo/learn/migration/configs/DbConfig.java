package kz.greetgo.learn.migration.configs;

import kz.greetgo.conf.hot.DefaultStrValue;
import kz.greetgo.conf.hot.Description;

@Description("Параметры доступа к БД (используется только БД Postgresql)")
public interface DbConfig {
  @Description("URL доступа к БД")
  @DefaultStrValue("jdbc:postgresql://localhost/aqali_sandbox_migration_source")
  String url();

  @Description("Пользователь для доступа к БД")
  @DefaultStrValue("aqali_sandbox_migration_source")
  String username();

  @Description("Пароль для доступа к БД")
  @DefaultStrValue("111")
  String password();
}
