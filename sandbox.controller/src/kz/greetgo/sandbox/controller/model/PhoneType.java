package kz.greetgo.sandbox.controller.model;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum PhoneType {
  HOME,
  WORK,
  MOBILE;

  private static final List<PhoneType> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
  private static final int SIZE = VALUES.size();

  public static PhoneType getRandomType(Random rnd)  {
    return VALUES.get(rnd.nextInt(SIZE));
  }
}
