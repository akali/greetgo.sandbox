package kz.greetgo.sandbox.db.__prepare__;

import kz.greetgo.sandbox.db.__prepare__.core.DbWorker;
import kz.greetgo.sandbox.db.__prepare__.db.oper.OperDDL;
import kz.greetgo.sandbox.db.util.ConfigFiles;

public class DropCreateOperDb {
  public static void main(String[] args) throws Exception {
    execute();
  }

  public static void execute() throws Exception {
//    TestsBeanContainer bc = TestsBeanContainerCreator.create();
//
//    bc.dbWorker().recreateAll();

    DbWorker dbWorker = new DbWorker();

    dbWorker.prepareConfigFiles();

    dbWorker.dropOperDb();
    dbWorker.createOperDb();

    dbWorker.applyDDL(ConfigFiles.homeDb(), OperDDL.get());
  }
}
