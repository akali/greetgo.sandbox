package kz.greetgo.sandbox.db.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

public class Configs {
  public static String host;
  public static String username;
  public static String downloadDirectory;
  public static String password;

  static {
    try (FileInputStream fis = new FileInputStream("/home/aqali/sandbox.d/conf/SshConfig.hotconfig")) {
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
        String line;
        while ((line = reader.readLine()) != null) if (!"".equals(line.trim())) {
          String[] split = line.split("=");
          String key = split[0];
          String value = split[1];
          for (Field field : Configs.class.getFields()) {
            if (field.getName().equals(key)) {
              field.set(Configs.class, value);
              break;
            }
          }
        }
      }
    } catch (IOException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }
}
