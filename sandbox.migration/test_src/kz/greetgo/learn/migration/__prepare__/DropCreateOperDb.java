package kz.greetgo.learn.migration.__prepare__;

import kz.greetgo.sandbox.db.test.util.TestsBeanContainer;
import kz.greetgo.sandbox.db.test.util.TestsBeanContainerCreator;

public class DropCreateOperDb {
  public static void main(String[] args) throws Exception {
    execute();
  }

  public static void execute() throws Exception {
    // Using liquibase instead of own workers

    TestsBeanContainer bc = TestsBeanContainerCreator.create();

    bc.dbWorker().recreateAll();

//    DbWorker dbWorker = new DbWorker();
//
//    dbWorker.prepareConfigFiles();
//
//    dbWorker.dropOperDb();
//    dbWorker.createOperDb();
//
//    dbWorker.applyDDL(ConfigFiles.homeDb(), OperDDL.get());
  }
}
