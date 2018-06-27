package kz.greetgo.learn.migration.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
  public static void log(String tag, String message) {
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY:HH:MM:SS");
    String time = sdf.format(new Date(System.nanoTime()));

    message = "[" + time + "] " + tag + ": " + message;

    System.out.println(message);
  }
}
