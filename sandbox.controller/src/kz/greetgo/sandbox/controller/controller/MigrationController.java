package kz.greetgo.sandbox.controller.controller;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.AsIs;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.sandbox.controller.register.MigrationRegister;
import kz.greetgo.sandbox.controller.security.NoSecurity;
import kz.greetgo.sandbox.controller.util.Controller;

import java.io.IOException;

@Bean
@Mapping("/migration")
public class MigrationController implements Controller {
  public BeanGetter<MigrationRegister> migrationRegister;

  @AsIs
  @NoSecurity
  @Mapping("/run")
  public String run() throws NotFound, JSchException, SftpException, IOException {
    return migrationRegister.get().run();
  }
}
