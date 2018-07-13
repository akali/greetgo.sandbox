package kz.greetgo.learn.migration.core;

import com.jcraft.jsch.*;
import kz.greetgo.learn.migration.__prepare__.DropCreateMgrSrcDb;
import kz.greetgo.learn.migration.__prepare__.DropCreateOperDb;
import kz.greetgo.learn.migration.core.innerMigration.FrsMigration;
import kz.greetgo.learn.migration.core.innerMigration.Migration;
import kz.greetgo.learn.migration.core.outerMigration.*;
import kz.greetgo.learn.migration.interfaces.ConnectionConfig;
import kz.greetgo.learn.migration.interfaces.StreamHandler;
import kz.greetgo.learn.migration.util.ConfigFiles;
import kz.greetgo.learn.migration.util.Configs;
import kz.greetgo.learn.migration.util.ConnectionUtils;
import kz.greetgo.learn.migration.util.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class LaunchMigration {

  public static void main(String[] args) throws Exception {
    clearMgrSrcDb();
    createOperDb();
    readArchives();

    final File file = new File("build/__migration__");
    file.getParentFile().mkdirs();
    file.createNewFile();

    System.out.println("To stop next migration portion delete file " + file);
    System.out.println("To stop next migration portion delete file " + file);
    System.out.println("To stop next migration portion delete file " + file);

    ConnectionConfig operCC = ConnectionUtils.fileToConnectionConfig(ConfigFiles.homeDb());
    ConnectionConfig ciaCC = ConnectionUtils.fileToConnectionConfig(ConfigFiles.migrationDb());

    try (Migration migration = new FrsMigration(operCC, ciaCC)) {

      migration.chunkSize =  250_000;
      migration.uploadMaxBatchSize = 50_000;
      migration.downloadMaxBatchSize = 50_000;

      while (true) {
        int count = migration.migrate();
        System.err.println("MIGRATION COUNT = " + count);
        // if (count > 0) break;
        if (!file.exists()) break;
        System.out.println("Migrated " + count + " records");
        System.out.println("------------------------------------------------------------------");
        System.out.println("------------------------------------------------------------------");
        System.out.println("------------------------------------------------------------------");
        System.out.println("------------------------------------------------------------------");
      }
    }

    file.delete();

    System.out.println("Finish migration");
  }

  private static void createOperDb() throws Exception {
    Logger.log("LaunchMigration", "creating migrsrc db");
    DropCreateOperDb.execute();
  }

  private static void clearMgrSrcDb() throws Exception {
    Logger.log("LaunchMigration", "creating oper db");
    DropCreateMgrSrcDb.execute();
  }

  private static void readArchives() throws IOException {
//    File file = new File("migration_data/from_cia_2018-02-21-154929-1-300.xml.tar.bz2");
//    File file = new File("migration_data/from_cia_2018-02-21-154955-5-1000000.xml.tar.bz2");
//    File file = new File("migration_data/from_frs_2018-02-21-155112-1-30002.json_row.txt.tar.bz2");
    File file = new File("migration_data/from_frs_2018-02-21-155121-3-10000007.json_row.txt.tar.bz2");
    StreamHandler handler = new JsonHandler(), xmlHandler = new XmlHandler();
    ((JsonHandler) handler).setParser(new JsonParser());
    handler.setParentHandler(xmlHandler);
    ((XmlHandler) xmlHandler).setRowParser(new XmlParser());

    ArchiveParser parser = new ArchiveParser(new FileInputStream(file));
    parser.run(new ArchiveParser.MyObservable().withObserver((observable1, o) -> {
      if (o == null) return;
      ArchiveParser.StreamBundle sb = (ArchiveParser.StreamBundle) o;
      handler.handle(sb.streamReader, sb.filename);
    }));
  }

  public static void runFrsMigration() throws JSchException, SftpException, IOException {
    JSch jSch = new JSch();
    Session session = jSch.getSession(Configs.username, Configs.host);
    session.setPassword(Configs.password);
    session.setConfig(new Properties() {
      {
        put("StrictHostKeyChecking", "no");
      }
    });
    session.connect();
    ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
    sftp.connect();
    sftp.cd(Configs.downloadDirectory);

    for (Object o : sftp.ls("*.tar.bz2")) {
      System.err.println("/----------------------------/");
      System.err.println(o);
      System.err.println("/----------------------------/");
      ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) o;
      String filename = Configs.downloadDirectory + "/" + entry.getFilename();
      FrsMigrationWorker worker = new FrsMigrationWorker(sftp.get(filename));
      worker.migrate();
    }
  }

  public static void runCiaMigration() throws JSchException, SftpException, IOException {
    JSch jSch = new JSch();
    Session session = jSch.getSession(Configs.username, Configs.host);
    session.setPassword(Configs.password);
    session.setConfig(new Properties() {
      {
        put("StrictHostKeyChecking", "no");
      }
    });
    session.connect();
    ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
    sftp.connect();
    sftp.cd(Configs.downloadDirectory);

    for (Object o : sftp.ls("*.tar.bz2")) {
      System.err.println("/----------------------------/");
      System.err.println(o);
      System.err.println("/----------------------------/");
      ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) o;
      String filename = Configs.downloadDirectory + "/" + entry.getFilename();
      CiaMigrationWorker worker = new CiaMigrationWorker(sftp.get(filename));
      worker.migrate();
    }
  }
}
