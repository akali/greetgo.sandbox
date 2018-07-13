package kz.greetgo.learn.migration.core;

import kz.greetgo.learn.migration.core.innerMigration.CiaMigration;
import kz.greetgo.learn.migration.core.innerMigration.Migration;
import kz.greetgo.learn.migration.core.outerMigration.ArchiveParser;
import kz.greetgo.learn.migration.core.outerMigration.XmlHandler;
import kz.greetgo.learn.migration.core.outerMigration.XmlParser;
import kz.greetgo.learn.migration.interfaces.ConnectionConfig;
import kz.greetgo.learn.migration.util.ConfigFiles;
import kz.greetgo.learn.migration.util.ConnectionUtils;
import kz.greetgo.learn.migration.util.Logger;

import java.io.IOException;
import java.io.InputStream;

public class CiaMigrationWorker {
  private InputStream fileInputStream;

  public CiaMigrationWorker(InputStream fileInputStream) {
    this.fileInputStream = fileInputStream;
  }

  public void migrate() throws IOException {
    System.out.println("Migrating");
    ArchiveParser ap = new ArchiveParser(fileInputStream);

    ArchiveParser.MyObservable observable =
      new ArchiveParser.MyObservable().withObserver((observable1, o) -> {
        try {
          XmlHandler xmlHandler = new XmlHandler();
          xmlHandler.setRowParser(new XmlParser());

          ArchiveParser.StreamBundle bundle = (ArchiveParser.StreamBundle) o;
          xmlHandler.handle(bundle.streamReader, bundle.filename);
        } catch (IOException e) {
          e.printStackTrace();
        }
      });

    ap.run(observable);

    ConnectionConfig operCC = ConnectionUtils.fileToConnectionConfig(ConfigFiles.homeDb());
    ConnectionConfig ciaCC = ConnectionUtils.fileToConnectionConfig(ConfigFiles.migrationDb());

    try (Migration migration = new CiaMigration(operCC, ciaCC)) {
      long count;
      while ((count = migration.migrate()) > 0) {
        Logger.log(FrsMigrationWorker.class.getSimpleName(), "Migrated: " + count + " rows");
      }
      Logger.log(FrsMigrationWorker.class.getSimpleName(), "Finished cia migration");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
