package kz.greetgo.sandbox.controller.model;

import java.util.Objects;

public class Charm {
  public Integer id;
  public String name;
  public String description;
  public Float energy;
  public Boolean isActive = true;

  public Charm(Integer id, String name, String description, Float energy) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.energy = energy;
  }

  public Charm() { }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Charm charm = (Charm) o;
    return id == charm.id &&
      isActive == charm.isActive &&
      Objects.equals(name, charm.name) &&
      Objects.equals(description, charm.description) &&
      Objects.equals(energy, charm.energy);
  }
}
