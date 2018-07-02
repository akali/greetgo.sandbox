package kz.greetgo.learn.migration.__prepare__;

import kz.greetgo.learn.migration.__prepare__.core.DbWorker;
import kz.greetgo.learn.migration.__prepare__.db.oper.OperDDL;
import kz.greetgo.learn.migration.util.ConfigFiles;

import java.io.IOException;

public class DropCreateOperDb {
  public static void main(String[] args) throws Exception {
    execute();
  }

  public static void execute() throws Exception {
    DbWorker dbWorker = new DbWorker();

    dbWorker.prepareConfigFiles();

    dbWorker.dropOperDb();
    dbWorker.createOperDb();

    dbWorker.applyDDL(ConfigFiles.homeDb(), OperDDL.get());
  }
}
