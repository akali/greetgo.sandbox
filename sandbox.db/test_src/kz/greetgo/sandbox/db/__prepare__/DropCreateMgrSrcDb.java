package kz.greetgo.sandbox.db.__prepare__;

import kz.greetgo.sandbox.db.__prepare__.core.DbWorker;
import kz.greetgo.sandbox.db.__prepare__.db.cia.CiaDDL;
import kz.greetgo.sandbox.db.util.ConfigFiles;

public class DropCreateMgrSrcDb {
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
