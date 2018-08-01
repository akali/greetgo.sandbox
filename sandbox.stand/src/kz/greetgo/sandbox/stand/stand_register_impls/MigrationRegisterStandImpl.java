package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.mvc.interfaces.BinResponse;
import kz.greetgo.sandbox.controller.register.MigrationRegister;

@Bean
public class MigrationRegisterStandImpl implements MigrationRegister {
  @Override
  public void run(String migrationType, BinResponse binResponse) {
    System.out.println("running migration!");
  }
}
