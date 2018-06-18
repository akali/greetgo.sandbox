package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.sandbox.controller.model.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.util.*;

public class RandomClientGenerator {
  public class ClientBundle {
    private Client client;
    private ClientAddress regAddress, factAddress;
    private List<ClientPhone> phones;

    private List<ClientAccount> accounts;
    private List<ClientAccountTransaction> transactions;

    private List<Charm> charms;
    private List<TransactionType> transactionTypes;

    public List<Charm> getCharms() {
      return charms;
    }

    public void setCharms(List<Charm> charms) {
      this.charms = charms;
    }

    public List<TransactionType> getTransactionTypes() {
      return transactionTypes;
    }

    public void setTransactionTypes(List<TransactionType> transactionTypes) {
      this.transactionTypes = transactionTypes;
    }

    @Override
    public String toString() {
      return "ClientBundle{" +
        "client=" + client +
        ", regAddress=" + regAddress +
        ", factAddress=" + factAddress +
        ", phones=" + phones +
        ", accounts=" + accounts +
        ", transactions=" + transactions +
        '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ClientBundle that = (ClientBundle) o;
      return Objects.equals(client, that.client) &&
        Objects.equals(regAddress, that.regAddress) &&
        Objects.equals(factAddress, that.factAddress) &&
        Objects.equals(phones, that.phones) &&
        Objects.equals(accounts, that.accounts) &&
        Objects.equals(transactions, that.transactions);
    }

    @Override
    public int hashCode() {
      return Objects.hash(client, regAddress, factAddress, phones, accounts, transactions);
    }

    public Client getClient() {
      return client;
    }

    public void setClient(Client client) {
      this.client = client;
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

    public List<ClientAccount> getAccounts() {
      return accounts;
    }

    public void setAccounts(List<ClientAccount> accounts) {
      this.accounts = accounts;
    }

    public List<ClientAccountTransaction> getTransactions() {
      return transactions;
    }

    public void setTransactions(List<ClientAccountTransaction> transactions) {
      this.transactions = transactions;
    }

    public ClientBundle() {
    }

    public ClientBundle(Client client, ClientAddress regAddress, ClientAddress factAddress, List<ClientPhone> phones, List<ClientAccount> accounts, List<ClientAccountTransaction> transactions) {
      this.client = client;
      this.regAddress = regAddress;
      this.factAddress = factAddress;
      this.phones = phones;
      this.accounts = accounts;
      this.transactions = transactions;
    }
  }
  private String sigma;

  private List<String> names = new ArrayList<>();
  private List<String> surnames = new ArrayList<>();
  private List<Charm> charms = new ArrayList<>();
  private List<TransactionType> transactionTypes = new ArrayList<>();
  private Random random = new Random();

  public RandomClientGenerator() {
    try {
      sigma = "";
      for (char c = 'a'; c <= 'z'; ++c) sigma = sigma.concat(String.valueOf(c));
      for (char c = 'A'; c <= 'Z'; ++c) sigma = sigma.concat(String.valueOf(c));
      names = load("first_names/all.txt");
      surnames = load("surnames/all.txt");
      generateCharms();
      generateTransactionTypes();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public ClientBundle generate(int id) {
    ClientBundle bundle = new ClientBundle();

    bundle.setClient(generateClient(id));
    bundle.setFactAddress(generateAddress(id, AddressType.FACT));
    bundle.setRegAddress(generateAddress(id, AddressType.REG));
    bundle.setPhones(generatePhones(id));
    bundle.setAccounts(generateAccounts(id));

    return bundle;
  }


  private void generateTransactionTypes() {
  }

  private void generateCharms() {
    charms = new ArrayList<>();
    for (int i = 0; i < 50; ++i) {
      charms.add(new Charm(generateRandomString(15), generateRandomString(64), random.nextFloat()));
    }
  }

  private String generateRandomString(int len) {
    StringBuilder string = new StringBuilder();
    for (int i = 0; i < len; ++i) {
      string.append(sigma.charAt(random.nextInt(sigma.length())));
    }
    return String.valueOf(string);
  }

  private List<ClientAccount> generateAccounts(int id) {
  }

  private List<ClientPhone> generatePhones(int id) {
  }

  private ClientAddress generateAddress(int id, AddressType fact) {
  }

  public long getTimestamp(int day, int month, int year) {
    Calendar cal = new GregorianCalendar();
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month - 1);
    cal.set(Calendar.DATE, day);
    return cal.getTimeInMillis();
  }

  private Client generateClient(int id) {
    Client client = new Client();
    client.id = id;
    client.surname = pick(surnames);
    client.name = pick(names);
    client.patronymic = pick(names);
    client.gender = GenderType.FEMALE;
    if (random.nextInt(1) > 0)
      client.gender = GenderType.MALE;
    client.birth_date = pickDate();
    return client;
  }

  private Date pickDate() {
    int year = 1890 + random.nextInt(120);
    int month = 1 + random.nextInt(12);
    int day = random.nextInt(getDays(month, year));
    return new Date(getTimestamp(day, month, year));
  }

  private int getDays(int month, int year) {
    int[] m = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    if (year % 4 == 0 || (year % 100 == 0 && year % 400 == 0)) {
      ++m[2];
    }
    return m[month];
  }

  private String pick(List<String> l) {
    return l.get(random.nextInt(l.size()));
  }

  private List<String> load(String name) throws IOException {
    List<String> objs = new ArrayList<>();
    BufferedReader bf =
      new BufferedReader(new FileReader("NameDatabases/NamesDatabases/" + name));
    String s;
    while ((s = bf.readLine()) != null) {
      s = s.trim();
      if (!s.equals("") && !s.isEmpty()) {
        objs.add(s);
      }
    }
    return objs;
  }

}
