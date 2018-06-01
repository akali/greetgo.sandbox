package kz.greetgo.sandbox.db.util;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class YearDifference {
  public static int calculate (Date date) {
    Calendar dateNow = Calendar.getInstance();
    dateNow.setTime(Date.from(Instant.now()));

    Calendar dateBirth = Calendar.getInstance();
    dateBirth.setTime(date);

    int diff = dateNow.get(YEAR) - dateBirth.get(YEAR);
    if (dateNow.get(MONTH) > dateBirth.get(MONTH) ||
      (dateNow.get(MONTH) == dateBirth.get(MONTH) && dateNow.get(DATE) > dateBirth.get(DATE))) {
      diff--;
    }
    return diff;
  }
}
