package kz.greetgo.sandbox.controller.model;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

public class ClientDetail implements Serializable {
    public int id;
    public String name;
    public String surname;
    public String patronymic;
    public GenderType gender;
    public long birthDate;
    public ClientAddress regAddress, factAddress;
    public List<ClientPhone> phones;
    public List<Charm> charms;
    public Charm charm;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public GenderType getGender() {
        return gender;
    }

    public void setGender(GenderType gender) {
        this.gender = gender;
    }

    public long getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(long birthDate) {
        this.birthDate = birthDate;
    }

    public ClientAddress getRegAddress() {
        return regAddress;
    }

    public void setRegAddress(ClientAddress regAddress) {
        this.regAddress = regAddress;
    }

    public ClientAddress getFactAddress() {
        return factAddress;
    }

    public void setFactAddress(ClientAddress factAddress) {
        this.factAddress = factAddress;
    }

    public List<ClientPhone> getPhones() {
        return phones;
    }

    public void setPhones(List<ClientPhone> phones) {
        this.phones = phones;
    }

    public List<Charm> getCharms() {
        return charms;
    }

    public void setCharms(List<Charm> charms) {
        this.charms = charms;
    }

    public Charm getCharm() {
        return charm;
    }

    public void setCharm(Charm charm) {
        this.charm = charm;
    }

    @Override
    public String toString() {
        return "ClientDetail{" +
          "id=" + id +
          ", name='" + name + '\'' +
          ", surname='" + surname + '\'' +
          ", patronymic='" + patronymic + '\'' +
          ", gender=" + gender +
          ", birthDate=" + birthDate +
          ", regAddress=" + regAddress +
          ", factAddress=" + factAddress +
          ", phones=" + phones +
          ", charms=" + charms +
          ", charm=" + charm +
          '}';
    }

    public ClientDetail() { }
}
