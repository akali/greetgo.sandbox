package kz.greetgo.learn.migration.__prepare__;

import kz.greetgo.learn.migration.__prepare__.core.DbWorker;
import kz.greetgo.learn.migration.__prepare__.db.cia.CiaDDL;
import kz.greetgo.learn.migration.util.ConfigFiles;

import java.io.IOException;

public class DropCreateCiaDb {
  public static void main(String[] args) throws Exception {
    execute();
  }
  public static void execute() throws Exception {
    DbWorker dbWorker = new DbWorker();

    dbWorker.prepareConfigFiles();

    dbWorker.dropCiaDb();
    dbWorker.createCiaDb();

    dbWorker.applyDDL(ConfigFiles.migrationDb(), CiaDDL.get());
  }
}
