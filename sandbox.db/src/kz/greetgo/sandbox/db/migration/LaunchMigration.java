package kz.greetgo.sandbox.db.migration;

import com.jcraft.jsch.*;
import kz.greetgo.sandbox.db.interfaces.ConnectionConfig;
import kz.greetgo.sandbox.db.interfaces.StreamHandler;
import kz.greetgo.sandbox.db.migration.innerMigration.FrsMigration;
import kz.greetgo.sandbox.db.migration.innerMigration.Migration;
import kz.greetgo.sandbox.db.migration.outerMigration.*;
import kz.greetgo.sandbox.db.util.ConfigFiles;
import kz.greetgo.sandbox.db.util.Configs;
import kz.greetgo.sandbox.db.util.ConnectionUtils;
import kz.greetgo.sandbox.db.util.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class LaunchMigration {

  public static boolean RUNNING = false;

//  private static void createOperDb() throws Exception {
//    Logger.d("LaunchMigration", "creating migrsrc db");
//    DropCreateOperDb.execute();
//  }

//  private static void clearMgrSrcDb() throws Exception {
//    Logger.d("LaunchMigration", "creating oper db");
//    DropCreateMgrSrcDb.execute();
//  }

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

  private static JSch jSch;
  private static Session sessionClosable;
  private static ChannelSftp sftp;

  public static void main(String[] args) throws Exception {
//    clearMgrSrcDb();
//    createOperDb();
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

  public static void runFrsMigration() throws JSchException, SftpException, IOException {
    while (migrateOneFrsFile()) ;
  }

  private static void openSession() throws JSchException, SftpException {
    jSch = new JSch();
    sessionClosable = (jSch.getSession(Configs.username, Configs.host));
    sessionClosable.setPassword(Configs.password);
    sessionClosable.setConfig(new Properties() {
      {
        put("StrictHostKeyChecking", "no");
      }
    });
    sessionClosable.connect();
    sftp = (ChannelSftp) sessionClosable.openChannel("sftp");
    sftp.connect();
    sftp.cd(Configs.downloadDirectory);
  }

  private static boolean migrateOneCiaFile() throws JSchException, IOException, SftpException {
    boolean ok = false;

    for (Object o : sftp.ls("*xml*.tar.bz2")) {
      ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) o;

      System.err.println("/--------Starting to migrate new file---------------/");
      Logger.d(LaunchMigration.class.getSimpleName(), "Launching migration for " + entry.getFilename());

      String filename = Configs.downloadDirectory + "/" + entry.getFilename();
      CiaMigrationWorker worker = new CiaMigrationWorker(sftp.get(filename));
      worker.migrate();
      moveToMigrated(sessionClosable, sftp, filename);
      ok = true;
      break;
    }
    return ok;
  }

  private static void moveToMigrated(Session sessionClosable, ChannelSftp sftp, String filename) throws SftpException {
    sftp.rm(filename);
  }

  public static void runCiaMigration() throws JSchException, SftpException, IOException {
    while (migrateOneCiaFile()) ;
  }

  private static boolean migrateOneFrsFile() throws IOException, SftpException, JSchException {
    boolean ok = false;

    for (Object o : sftp.ls("*json*.tar.bz2")) {
      ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) o;
      System.err.println("/--------Starting to migrate new file---------------/");
      Logger.d(LaunchMigration.class.getSimpleName(), "Launching migration for " + entry.getFilename());
      String filename = Configs.downloadDirectory + "/" + entry.getFilename();
      FrsMigrationWorker worker = new FrsMigrationWorker(sftp.get(filename));
      worker.migrate();
      moveToMigrated(sessionClosable, sftp, filename);
      ok = true;
      break;
    }
    return ok;
  }

  public static void runMigration() throws JSchException, SftpException, IOException {
    RUNNING = true;
    openSession();
    while (true) {
      if (!migrateOneCiaFile()) break;
      if (!migrateOneFrsFile()) break;
    }
    while (migrateOneCiaFile()) ;
    while (migrateOneFrsFile()) ;
    RUNNING = false;
    closeSession();
  }

  private static void closeSession() {
    sftp.disconnect();
    sessionClosable.disconnect();
  }
}
