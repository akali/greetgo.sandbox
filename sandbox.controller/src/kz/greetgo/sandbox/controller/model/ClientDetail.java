package kz.greetgo.sandbox.controller.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

public class ClientDetail implements Serializable, Cloneable {
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

    public ClientDetail(int id, String name, String surname, String patronymic, GenderType gender, long birthDate, ClientAddress regAddress, ClientAddress factAddress, List<ClientPhone> phones, List<Charm> charms, Charm charm) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.gender = gender;
        this.birthDate = birthDate;
        this.regAddress = regAddress;
        this.factAddress = factAddress;
        this.phones = phones;
        this.charms = charms;
        this.charm = charm;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientDetail that = (ClientDetail) o;

        if (id != that.id) return false;
        if (!checkIfSameDay(birthDate, that.birthDate)) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (surname != null ? !surname.equals(that.surname) : that.surname != null) return false;
        if (patronymic != null ? !patronymic.equals(that.patronymic) : that.patronymic != null) return false;
        if (gender != that.gender) return false;
        if (regAddress != null ? !regAddress.equals(that.regAddress) : that.regAddress != null) return false;
        if (factAddress != null ? !factAddress.equals(that.factAddress) : that.factAddress != null) return false;
        if (phones != null ? !phones.equals(that.phones) : that.phones != null) return false;
        if (charms != null ? !charms.equals(that.charms) : that.charms != null) return false;
        return charm != null ? charm.equals(that.charm) : that.charm == null;
    }

    private boolean checkIfSameDay(long date1, long date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(date1);
        cal2.setTimeInMillis(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
          cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname, patronymic, gender, birthDate, regAddress, factAddress, phones, charms, charm);
    }

    public ClientDetail getCopy() {
        try {
            return (ClientDetail) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getDay() {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(birthDate);
        return cal1.get(Calendar.YEAR) + " " + cal1.get(Calendar.DAY_OF_YEAR);
    }
}
