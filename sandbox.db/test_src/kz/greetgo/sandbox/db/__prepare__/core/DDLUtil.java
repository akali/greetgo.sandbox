package kz.greetgo.sandbox.db.__prepare__.core;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class DDLUtil {

  public static List<File> getSortedSqlFiles(Class<?> aClass) {
    URL url = aClass.getResource(".");
    System.err.println(aClass.getProtectionDomain().getCodeSource().getLocation());

    File[] files = new File(url.getFile())
      .listFiles(pn -> pn.getName().toLowerCase().endsWith(".sql"));

    new File(url.getFile())
      .listFiles((file, s) -> {
        System.err.println("~~~" + file + " " + s);
        return true;
      });

    return Arrays.asList(files != null ? files : new File[0]);
  }
}
