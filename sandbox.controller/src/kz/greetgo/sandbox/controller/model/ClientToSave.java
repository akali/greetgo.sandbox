package kz.greetgo.sandbox.controller.model;

import java.util.Date;
import java.util.List;

public class ClientToSave {
  public Integer id;
  public String name;
  public String surname;
  public String patronymic;
  public Gender gender;
  public Date birthDate;
  public Integer charmId;
  public Address factAddress;
  public Address regAddress;
  public List<Phone> phones;
}
