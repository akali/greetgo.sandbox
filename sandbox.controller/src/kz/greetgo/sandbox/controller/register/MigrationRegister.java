package kz.greetgo.sandbox.controller.register;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import java.io.IOException;

public interface MigrationRegister {
  String run() throws JSchException, SftpException, IOException;
}
