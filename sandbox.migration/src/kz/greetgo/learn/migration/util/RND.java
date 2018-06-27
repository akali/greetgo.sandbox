package kz.greetgo.learn.migration.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

public class RND {
  public static final String ENG_STR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  public static final String eng_str = "abcdefghijklmnopqrstuvwxyz";
  public static final String DEG_STR = "0123456789";
  public static final char[] ALL_ENG = (ENG_STR + eng_str + DEG_STR).toCharArray();

  public static final Random RAND = new Random();

  public static String str(int len) {
    char ret[] = new char[len];
    for (int i = 0; i < len; i++) {
      ret[i] = ALL_ENG[_int(ALL_ENG.length)];
    }
    return new String(ret);
  }

  public static int _int(int boundValue) {
    int sign = 1;
    if (boundValue < 0) {
      sign = -1;
      boundValue = -boundValue;
    }

    return sign * RAND.nextInt(boundValue);
  }

  public static Date date(int fromDays, int toDays) {
    int days = fromDays + _int(toDays - fromDays);
    Calendar cal = new GregorianCalendar();
    cal.add(Calendar.DAY_OF_YEAR, days);
    return cal.getTime();
  }

  public static boolean bool(float truePercent) {
    return _int(100_000) < truePercent * 1000f;
  }

}
