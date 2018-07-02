package kz.greetgo.learn.migration.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
  public static void log(String tag, String message) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    String time = sdf.format(new Date());

    message = time + " [" + tag + "] " + message;

    System.out.println(message);
  }
}
