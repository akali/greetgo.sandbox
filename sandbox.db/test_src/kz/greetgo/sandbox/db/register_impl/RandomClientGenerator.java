package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.sandbox.controller.model.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class RandomClientGenerator {
  private int accountId = 1;
  private int accountTransactionId = 1;

  public static List<Charm> getCharms() {
    return charms;
  }

  public static List<QueryFilter> generateFilters(int listSize, List<String> actives) {
    List<QueryFilter> filters = new ArrayList<>();
    List<String> directions = Arrays.asList("DESC", "ASC");
    for (int start = 0; start < listSize; ++start)
      for (int limit = 1; limit <= listSize; ++limit) {
        int finalI = start, finalJ = limit;
        directions.forEach(
          direction -> actives.forEach(
            active -> filters.add(
              new QueryFilter(finalI, finalJ, direction, active, "")
            )
          )
        );
      }
    return filters;
  }

  public static TableResponse getClientRecords(List<ClientBundle> clientBundles, QueryFilter queryFilter) {
    int start = queryFilter.start;
    int offset = queryFilter.limit;
    String direction = queryFilter.direction;
    String active = queryFilter.active;
    String filter = queryFilter.filter;
    List<ClientRecord> list = new ArrayList<>();

    for (ClientBundle clientBundle: clientBundles) {
      list.add(clientBundle.getClientRecord());
    }

    return new TableResponse(list, start, offset, direction, active, filter);
  }

  public class ClientBundle {
    private Client client;
    private ClientAddress regAddress, factAddress;
    private List<ClientPhone> phones;

    private List<ClientAccount> accounts;
    private List<ClientAccountTransaction> transactions;

    private List<Charm> charms;
    private List<TransactionType> transactionTypes;
    private float total;
    private float min;
    private float max;

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

    public ClientToSave getClientToSave() {
      return new ClientToSave(
        this.client.id,
        this.client.name,
        this.client.surname,
        this.client.patronymic,
        this.client.charm,
        this.client.gender,
        getRegAddress(),
        getFactAddress(),
        this.client.birth_date.getTime(),
        this.getPhones()
      );
    }

    public ClientRecord getClientRecord() {
      return new ClientRecord(
        this.client.id,
        this.client.name,
        this.client.surname,
        this.client.patronymic,
        getTotal(),
        getMax(),
        getMin(),
        this.charms.get(this.client.charm - 1).name,
        calculateAge(this.client.birth_date.getTime())
      );
    }

    public int calculateAge(long birthDateTs) {
      LocalDate date = Instant.ofEpochMilli(new java.util.Date().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
      LocalDate date1 = new Timestamp(birthDateTs).toLocalDateTime().toLocalDate();

      return date.minusYears(date1.getYear()).getYear();
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
      if (accounts != null && !accounts.isEmpty()) {
        accounts.forEach(clientAccount -> total += clientAccount.money);
        min = accounts.stream().min((a, b) -> Float.compare(a.money, b.money)).get().money;
        max = accounts.stream().max((a, b) -> Float.compare(a.money, b.money)).get().money;
      }
    }

    public List<ClientAccountTransaction> getTransactions() {
      return transactions;
    }

    public void setTransactions(List<ClientAccountTransaction> transactions) {
      this.transactions = transactions;
    }

    public float getTotal() {
      return total;
    }

    public void setTotal(float total) {
      this.total = total;
    }

    public float getMin() {
      return min;
    }

    public void setMin(float min) {
      this.min = min;
    }

    public float getMax() {
      return max;
    }

    public void setMax(float max) {
      this.max = max;
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
  private static String sigma;

  private static List<String> phoneCodes = new ArrayList<>();
  private static List<String> names = new ArrayList<>();
  private static List<String> surnames = new ArrayList<>();
  private static List<Charm> charms = new ArrayList<>();
  private static List<TransactionType> transactionTypes = new ArrayList<>();

  private static final long RANDOM_SEED = 1337;
  private static Random random = new Random(RANDOM_SEED);

  static {
    try {
      sigma = "";
      for (char c = 'a'; c <= 'z'; ++c) sigma = sigma.concat(String.valueOf(c));
      for (char c = 'A'; c <= 'Z'; ++c) sigma = sigma.concat(String.valueOf(c));
      names = load("first_names/all.txt");
      surnames = load("surnames/all.txt");
      phoneCodes = load("phonesCodes/all.txt");
      generateCharms();
      generateTransactionTypes();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static List<ClientBundle> generate(int count) {
    List<ClientBundle> bundle = new ArrayList<>();

    RandomClientGenerator generator = new RandomClientGenerator();

    for (int i = 0; i < count; ++i) {
      bundle.add(generator.generateClientBundle(i + 1));
    }

    return bundle;
  }

  public ClientBundle generateClientBundle(int id) {
    ClientBundle bundle = new ClientBundle();

    bundle.setClient(generateClient(id));
    bundle.setFactAddress(generateAddress(id, AddressType.FACT));
    bundle.setRegAddress(generateAddress(id, AddressType.REG));
    bundle.setPhones(generatePhones(id));
    bundle.setAccounts(generateAccounts(id));
    bundle.setTransactions(generateTransactions(bundle.getAccounts()));

    bundle.setTransactionTypes(transactionTypes);
    bundle.setCharms(charms);
    return bundle;
  }

  private List<ClientAccountTransaction> generateTransactions(List<ClientAccount> accounts) {
    List<ClientAccountTransaction> transactions = new ArrayList<>();

    int count = 20 + random.nextInt(100);

    int year = 2007, month = 1;

    for (int i = 0; i < count; ++i) {
      int num = random.nextInt(accounts.size());
      ClientAccount account = accounts.get(num);

      num = random.nextInt(transactionTypes.size());

      TransactionType type = transactionTypes.get(num);
      float money = nextFloat();

      if (!type.name.equals("in"))
        money = -money;

      transactions.add(new ClientAccountTransaction(
        generateAccountTransactionId(),
        account.id,
        money,
        getTimestamp(getDays(month, year), month, year),
        type.id
      ));
      ++month;
      if (month == 13) {
        ++year;
        month = 1;
      }
    }
    return transactions;
  }

  private static float nextFloat() {
    return Float.parseFloat(String.format(java.util.Locale.US, "%.2f", random.nextFloat()));
  }

  private int generateAccountTransactionId() {
    return ++accountTransactionId;
  }


  private static void generateTransactionTypes() {
    transactionTypes = new ArrayList<>();
    transactionTypes.add(new TransactionType(
      1,
      "IN",
      "income"
    ));
    transactionTypes.add(new TransactionType(2, "OUT", "outcome"));
  }

  private static void generateCharms() {
    charms = new ArrayList<>();
    for (int i = 0; i < 50; ++i) {
      charms.add(new Charm(i + 1, generateRandomString(15), generateRandomString(64), nextFloat()));
    }
  }

  private static String generateRandomString(int len) {
    StringBuilder string = new StringBuilder();
    for (int i = 0; i < len; ++i) {
      string.append(sigma.charAt(random.nextInt(sigma.length())));
    }
    return String.valueOf(string);
  }

  private String generateRandomString(int len, String sigma) {
    StringBuilder string = new StringBuilder();
    for (int i = 0; i < len; ++i) {
      string.append(sigma.charAt(random.nextInt(sigma.length())));
    }
    return String.valueOf(string);
  }

  private List<ClientAccount> generateAccounts(int id) {
    int count = 1 + random.nextInt(5);

    List<ClientAccount> accounts = new ArrayList<>();

    for (int i = 0; i < count; ++i) {
      accounts.add(new ClientAccount(
        generateAccountId(),
        id,
        100 * nextFloat(),
        generateRandomString(10),
        pickDate().getTime()
      ));
    }

    return accounts;
  }

  private int generateAccountId() {
    return ++accountId;
  }

  private List<ClientPhone> generatePhones(int id) {
    int count = 1 + random.nextInt(10);
    List<ClientPhone> phones = new ArrayList<>();

    for (int i = 0; i < count; ++i) {
      phones.add(new ClientPhone(id, "+7" + pick(phoneCodes) + generateRandomString(7, "0123456789"), PhoneType.getRandomType(random)));
    }
    return phones;
  }

  private ClientAddress generateAddress(int id, AddressType type) {
    return new ClientAddress(
      id,
      type,
      pick(names) + " street",
      String.valueOf(1 + random.nextInt(1000)),
      String.valueOf(1 + random.nextInt(1000))
    );
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
    client.charm = charms.get(random.nextInt(charms.size())).id;
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

  private static List<String> load(String name) throws IOException {
    List<String> objs = new ArrayList<>();
    BufferedReader bf =
      new BufferedReader(new FileReader(
        "/home/aqali/work/greetgo/greetgo.sandbox/sandbox.db/test_src/kz/greetgo/sandbox/db/register_impl/NameDatabases/NamesDatabases/" + name));
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
