package kz.greetgo.sandbox.controller.model;



public class Charm {
  private static final float EPS = (float) 1e-3;
  public int id;
  public String name;
  public String description;
  public float energy;

  @Override
  public String toString() {
    return "Charm{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", description='" + description + '\'' +
      ", energy=" + energy +
      '}';
  }

  public Charm(int id, String name, String description, float energy) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.energy = energy;
  }

  public Charm() {

  }

  public Charm(String happy, String very_happy_person, float v) {
    this(-1, happy, very_happy_person, v);
  }

  public static Charm parse(String[] line) {
    Charm charm = new Charm();
    charm.id = Integer.parseInt(line[0]);
    charm.name = line[1];
    charm.description = line[2];
    charm.energy = Float.parseFloat(line[3]);
    return charm;
  }

  @Override
  public boolean equals(Object o) {
    Charm c = (Charm) o;
    if (!c.name.equals(name)) return false;
    if (!c.description.equals(description)) return false;
    return Math.abs(energy - c.energy) < EPS;
  }
}
