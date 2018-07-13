package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.ParPath;
import kz.greetgo.mvc.interfaces.BinResponse;
import kz.greetgo.sandbox.controller.util.Controller;

import java.io.PrintWriter;

@Bean
@Mapping("/migration")
public class MigrationController implements Controller {
//  public BeanGetter<MigrationRegister> migrationRegister;

  @Mapping("/run/{migrationType}")
  public void run(@ParPath("migrationType") String migrationType, BinResponse binResponse) throws NotFound {
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
