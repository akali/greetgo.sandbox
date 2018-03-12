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

  @Description("Имя ssh сервера")
  @DefaultStrValue("zateyev")
  String sshUser();

  @Description("Пароль ssh сервера")
  @DefaultStrValue("111")
  String sshPassword();

  @Description("IP адрес ssh сервера")
  @DefaultStrValue("192.168.11.166")
  String sshHost();

  @Description("Номер порта ssh сервера")
  @DefaultIntValue(22)
  int sshPort();

  @Description("Путь к директории где лежат файлы")
  @DefaultStrValue("/home/zateyev/git/greetgo.sandbox/build/files_to_send/")
  String sshHomePath();
}
