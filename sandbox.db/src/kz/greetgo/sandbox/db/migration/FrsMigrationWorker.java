package kz.greetgo.sandbox.db.migration;

import kz.greetgo.sandbox.db.interfaces.ConnectionConfig;
import kz.greetgo.sandbox.db.migration.innerMigration.FrsMigration;
import kz.greetgo.sandbox.db.migration.innerMigration.Migration;
import kz.greetgo.sandbox.db.migration.outerMigration.ArchiveParser;
import kz.greetgo.sandbox.db.migration.outerMigration.JsonHandler;
import kz.greetgo.sandbox.db.migration.outerMigration.JsonParser;
import kz.greetgo.sandbox.db.util.ConfigFiles;
import kz.greetgo.sandbox.db.util.ConnectionUtils;
import kz.greetgo.sandbox.db.util.Logger;

import java.io.IOException;
import java.io.InputStream;

public class FrsMigrationWorker {
  private InputStream fileInputStream;

  public FrsMigrationWorker(InputStream fileInputStream) {
    this.fileInputStream = fileInputStream;
  }

  public void migrate() throws IOException {
    Logger.d(getClass().getSimpleName(), "method migrate");
    ArchiveParser ap = new ArchiveParser(fileInputStream);

    ArchiveParser.MyObservable observable =
      new ArchiveParser.MyObservable().withObserver((observable1, o) -> {
        try {
          JsonHandler jsonHandler = new JsonHandler();
          jsonHandler.setParser(new JsonParser());

          ArchiveParser.StreamBundle bundle = (ArchiveParser.StreamBundle) o;
          jsonHandler.handle(bundle.streamReader, bundle.filename);
        } catch (IOException e) {
          e.printStackTrace();
        }
      });

    ap.run(observable);

    ConnectionConfig operCC = ConnectionUtils.fileToConnectionConfig(ConfigFiles.homeDb());
    ConnectionConfig ciaCC = ConnectionUtils.fileToConnectionConfig(ConfigFiles.migrationDb());

    try (Migration migration = new FrsMigration(operCC, ciaCC)) {
      long count;
      while ((count = migration.migrate()) > 0) {
        Logger.d(FrsMigrationWorker.class.getSimpleName(), "Migrated: " + count + " rows");
      }
      Logger.d(FrsMigrationWorker.class.getSimpleName(), "Finished frs migration");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
