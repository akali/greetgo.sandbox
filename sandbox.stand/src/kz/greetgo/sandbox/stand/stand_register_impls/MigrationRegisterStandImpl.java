package kz.greetgo.sandbox.stand.stand_register_impls;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.register.MigrationRegister;
import kz.greetgo.sandbox.db.migration.LaunchMigration;

import java.io.IOException;

@Bean
public class MigrationRegisterStandImpl implements MigrationRegister {
  @Override
  public String run() throws JSchException, SftpException, IOException {
    if (!LaunchMigration.RUNNING)
      LaunchMigration.runMigration();
    return "<html><head></head><body>Migration is running/body></html>";
  }
}
