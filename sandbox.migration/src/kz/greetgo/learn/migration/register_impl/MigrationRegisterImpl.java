package kz.greetgo.learn.migration.register_impl;

import kz.greetgo.mvc.interfaces.BinResponse;
import kz.greetgo.sandbox.controller.controller.NotFound;
import kz.greetgo.sandbox.controller.register.MigrationRegister;

import java.io.PrintWriter;

public class MigrationRegisterImpl implements MigrationRegister {
  @Override
  public void run(String migrationType, BinResponse binResponse) {
    migrationType = migrationType.toUpperCase();
    PrintWriter pr;
    binResponse.setFilename("hello.txt");
    switch (migrationType) {
      case "FRS":
        pr = new PrintWriter(binResponse.out());
        pr.println(migrationType);
        pr.flush();
        break;
      case "CIA":
        pr = new PrintWriter(binResponse.out());
        pr.println(migrationType);
        pr.flush();
        break;
      default:
        throw new NotFound();
    }
  }
}
