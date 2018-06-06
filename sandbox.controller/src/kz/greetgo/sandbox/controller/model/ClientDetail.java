package kz.greetgo.sandbox.controller.model;

import java.util.List;
import java.util.Vector;

public class ClientDetail {
    public int id;
    public String name;
    public String surname;
    public String patronymic;
    public GenderType gender;
    public long birthDate;
    public ClientAddress regAddress, factAddress;
    public List<ClientPhone> phones;
    public List<Charm> charms;

    public ClientDetail() { }
}
