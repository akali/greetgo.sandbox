package kz.greetgo.sandbox.db.test.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class RandomDate {

    public Date nextDate() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy");
        Date d1 = dateFormat.parse("01-01-1950");
        Date d2 = dateFormat.parse("01-01-2000");

        ThreadLocalRandom random = ThreadLocalRandom.current();

        return dateFormat.parse(dateFormat.format(new Date(random.nextLong(d1.getTime(), d2.getTime()))));
    }
}