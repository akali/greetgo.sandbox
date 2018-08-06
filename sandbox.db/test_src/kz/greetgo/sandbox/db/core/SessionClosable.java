package kz.greetgo.sandbox.db.core;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.Closeable;
import java.util.Properties;

public class SessionClosable implements Closeable {
  Session session;

  public SessionClosable(Session session) {
    this.session = session;
  }

  public void setPassword(String password) {
    session.setPassword(password);
  }


  @Override
  public void close() {
    session.disconnect();
  }

  public void setConfig(Properties properties) {
    session.setConfig(properties);
  }


  public void connect() throws JSchException {
    session.connect();
  }

  public Object openChannel(String sftp) throws JSchException {
    return session.openChannel(sftp);
  }
}
