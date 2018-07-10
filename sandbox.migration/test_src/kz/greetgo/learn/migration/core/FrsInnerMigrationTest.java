package kz.greetgo.learn.migration.core;

import kz.greetgo.learn.migration.__prepare__.DropCreateMgrSrcDb;
import kz.greetgo.learn.migration.__prepare__.DropCreateOperDb;
import kz.greetgo.learn.migration.core.innerMigration.FrsMigration;
import kz.greetgo.learn.migration.core.innerMigration.Migration;
import kz.greetgo.learn.migration.core.outerMigration.JsonHandler;
import kz.greetgo.learn.migration.core.outerMigration.JsonParser;
import kz.greetgo.learn.migration.interfaces.ConnectionConfig;
import kz.greetgo.learn.migration.util.ConfigFiles;
import kz.greetgo.learn.migration.util.ConnectionUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class FrsInnerMigrationTest {
  @Test
  public void test() throws Exception {

    DropCreateOperDb.execute();
    DropCreateMgrSrcDb.execute();

    JsonHandler jsonHandler = new JsonHandler();
    jsonHandler.setParser(new JsonParser());

    File file = new File("test.json");
    FileOutputStream fis = new FileOutputStream(file);
    PrintWriter pw = new PrintWriter(fis);

//    FrsRecord record = new FrsRecord().

    pw.println("");

    //
    //
    ConnectionConfig operCC = ConnectionUtils.fileToConnectionConfig(ConfigFiles.homeDb());
    ConnectionConfig ciaCC = ConnectionUtils.fileToConnectionConfig(ConfigFiles.migrationDb());
    try (Migration migration = new FrsMigration(operCC, ciaCC)) {
      migration.chunkSize = 250_000;
      migration.uploadMaxBatchSize = 50_000;
      migration.downloadMaxBatchSize = 50_000;

      int count = migration.migrate();

    }
    //
    //
  }
}
