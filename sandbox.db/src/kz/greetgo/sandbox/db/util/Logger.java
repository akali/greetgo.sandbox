package kz.greetgo.sandbox.db.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
  public static void d(String tag, String message) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    String time = sdf.format(new Date());

    message = time + " [" + tag + "]: " + message;

    System.out.println(message);
  }
}
