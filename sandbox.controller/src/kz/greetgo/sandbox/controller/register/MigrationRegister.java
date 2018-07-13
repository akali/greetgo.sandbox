package kz.greetgo.sandbox.controller.register;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public interface MigrationRegister {
  default void runFrsMigration() {
    throw new NotImplementedException();
  }
}
