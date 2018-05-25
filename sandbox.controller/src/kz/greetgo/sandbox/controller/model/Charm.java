package kz.greetgo.sandbox.controller.model;

public class Charm {
  public int id;
  public String name;
  public String description;
  public Float energy;
  public boolean isActive = true;

  public Charm(Integer id, String name, String description, Float energy) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.energy = energy;
  }

  public Charm() { }
}
