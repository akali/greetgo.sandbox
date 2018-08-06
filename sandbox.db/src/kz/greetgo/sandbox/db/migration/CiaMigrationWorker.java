package kz.greetgo.sandbox.db.migration;

import kz.greetgo.sandbox.db.interfaces.ConnectionConfig;
import kz.greetgo.sandbox.db.migration.innerMigration.CiaMigration;
import kz.greetgo.sandbox.db.migration.innerMigration.Migration;
import kz.greetgo.sandbox.db.migration.outerMigration.ArchiveParser;
import kz.greetgo.sandbox.db.migration.outerMigration.XmlHandler;
import kz.greetgo.sandbox.db.migration.outerMigration.XmlParser;
import kz.greetgo.sandbox.db.util.ConfigFiles;
import kz.greetgo.sandbox.db.util.ConnectionUtils;
import kz.greetgo.sandbox.db.util.Logger;

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

    XmlHandler xmlHandler = new XmlHandler();
    xmlHandler.setRowParser(new XmlParser());

    ConnectionConfig operCC = ConnectionUtils.fileToConnectionConfig(ConfigFiles.homeDb());
    ConnectionConfig ciaCC = ConnectionUtils.fileToConnectionConfig(ConfigFiles.migrationDb());

    xmlHandler.setOnChunkLoaded(() -> {
      Thread t = new Thread(() -> {
        try (Migration migration = new CiaMigration(operCC, ciaCC)) {
          long count;
          while ((count = migration.migrate()) > 0) {
            Logger.d(CiaMigrationWorker.class.getSimpleName(), "Migrated: " + count + " rows");
          }
          Logger.d(CiaMigrationWorker.class.getSimpleName(), "Finished cia migration");
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
      t.start();
    });

    ArchiveParser.MyObservable observable =
      new ArchiveParser.MyObservable().withObserver((observable1, o) -> {
        ArchiveParser.StreamBundle bundle = (ArchiveParser.StreamBundle) o;
        xmlHandler.handle(bundle.streamReader, bundle.filename);
      });

    ap.run(observable);
  }
}
