package kz.greetgo.sandbox.controller.model;



public class Charm {
  public int id;
  public String name;
  public String description;
  public float energy;

  public Charm(int id, String name, String description, float energy) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.energy = energy;
  }

  public Charm() {

  }

  public static Charm parse(String[] line) {
    Charm charm = new Charm();
    charm.id = Integer.parseInt(line[0]);
    charm.name = line[1];
    charm.description = line[2];
    charm.energy = Float.parseFloat(line[3]);
    return charm;
  }
}
