package kz.greetgo.sandbox.db.configs;

import kz.greetgo.conf.hot.DefaultIntValue;
import kz.greetgo.conf.hot.DefaultStrValue;
import kz.greetgo.conf.hot.Description;

@Description("Параметры миграции")
public interface MigrationConfig {

  @Description("Максимальный размер батча")
  @DefaultIntValue(50_000)
  int maxBatchSize();

  @Description("Имя файла с ошибками")
  @DefaultStrValue("build/files_to_send/errors.txt")
  String outErrorFile();
}
