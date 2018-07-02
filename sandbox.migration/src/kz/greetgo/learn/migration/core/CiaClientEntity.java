package kz.greetgo.learn.migration.core;

import java.sql.Date;

public class CiaClientEntity {
  public String id, name, surname, patrnymic, gender, charm;
  public Date birthDate;
  public class Address {
    public String street, house, flat;
  }
  Address fact, register;

}
