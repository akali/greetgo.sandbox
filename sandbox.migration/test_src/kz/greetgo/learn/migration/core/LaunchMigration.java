package kz.greetgo.learn.migration.core;

import kz.greetgo.learn.migration.__prepare__.DropCreateCiaDb;
import kz.greetgo.learn.migration.__prepare__.DropCreateOperDb;
import kz.greetgo.learn.migration.interfaces.ConnectionConfig;
import kz.greetgo.learn.migration.interfaces.StreamHandler;
import kz.greetgo.learn.migration.util.ConfigFiles;
import kz.greetgo.learn.migration.util.ConnectionUtils;
import kz.greetgo.learn.migration.util.Logger;

import java.io.File;
import java.io.IOException;

public class LaunchMigration {

  public static void main(String[] args) throws Exception {
    Logger.log("LaunchMigration", "creating cia db");
    clearCiaDb();
    Logger.log("LaunchMigration", "creating oper db");
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

    try (Migration migration = new Migration(operCC, ciaCC)) {

      migration.chunkSize =  250_000;
      migration.uploadMaxBatchSize = 50_000;
      migration.downloadMaxBatchSize = 50_000;

      while (true) {
        int count = migration.migrate();
        System.err.println("MIGRATION COUNT = " + count);
        if (count == 0) break;
//        if (count > 0) break;
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
    DropCreateOperDb.execute();
  }

  private static void clearCiaDb() throws Exception {
    DropCreateCiaDb.execute();
  }

  private static void readArchives() throws IOException {
    File file = new File("migration_data/from_cia_2018-02-21-154929-1-300.xml.tar.bz2");
//    File file = new File("migration_data/from_cia_2018-02-21-154955-5-1000000.xml.tar.bz2");
//    File file = new File("migration_data/from_frs_2018-02-21-155112-1-30002.json_row.txt.tar.bz2");
    StreamHandler handler = new JsonHandler(), xmlHandler = new XmlHandler();
    ((JsonHandler) handler).setParser(new JsonParser());
    handler.setParentHandler(xmlHandler);
    ((XmlHandler) xmlHandler).setRowParser(new XmlParser());

    ArchiveParser parser = new ArchiveParser(file);
    parser.run(new ArchiveParser.MyObservable().withObserver((observable1, o) -> {
      if (o == null) return;
      ArchiveParser.StreamBundle sb = (ArchiveParser.StreamBundle) o;
      handler.handle(sb.streamReader, sb.filename);
    }));
  }

}