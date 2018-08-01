package kz.greetgo.sandbox.controller.register;

import kz.greetgo.mvc.interfaces.BinResponse;

public interface MigrationRegister {
  void run(String migrationType, BinResponse binResponse);
}
