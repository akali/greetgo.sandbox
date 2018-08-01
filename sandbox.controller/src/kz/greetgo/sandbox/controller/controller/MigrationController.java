package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.ParPath;
import kz.greetgo.mvc.interfaces.BinResponse;
import kz.greetgo.sandbox.controller.register.MigrationRegister;
import kz.greetgo.sandbox.controller.util.Controller;

@Bean
@Mapping("/migration")
public class MigrationController implements Controller {
  public BeanGetter<MigrationRegister> migrationRegister;

  @Mapping("/run/{migrationType}")
  public void run(@ParPath("migrationType") String migrationType, BinResponse binResponse) throws NotFound {
    migrationRegister.get().run(migrationType, binResponse);
  }
}
