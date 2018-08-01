package kz.greetgo.sandbox.db.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.db.stand.model.PersonDot;
import kz.greetgo.util.RND;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Bean
public class StandDb implements HasAfterInject {
  public final Map<String, PersonDot> personStorage = new HashMap<>();
  public final Map<String, Client> clientStorage = new HashMap<>();
  public final Map<String, ClientAddress> addressStorage = new HashMap<>();
  public final Map<String, ClientPhone> phoneStorage = new HashMap<>();
  public final Map<String, ClientAccount> accountStorage = new HashMap<>();
  public final Map<String, ClientAccountTransaction> transactionStorage = new HashMap<>();
  public final Map<String, TransactionType> transactionTypeStorage = new HashMap<>();
  public final Map<String, Charm> charmStorage = new HashMap<>();
  public final Map<String, String> downloadUrl = new HashMap<>();
  private final static Random random = new Random();

  public int clientId;
  public int addressId;

  public final static String CLIENTS = "StandDbClients.txt";
  public final static String ADDRESSES = "StandDbAddresses.txt";
  public final static String PHONES = "StandDbPhones.txt";
  public final static String ACCOUNTS = "StandDbAccounts.txt";
  public final static String TRANSACTIONS = "StandDbTransactions.txt";
  public final static String TRANSACTION_TYPES = "StandDbTransactionTypes.txt";
  public final static String CHARMS = "StandDbCharms.txt";

  public String getUrl(String id) {
    return downloadUrl.get(id);
  }

  private int myId = 100000000;

  public String putUrl(String url) {
    String id = "Report_" + new Date() + RND.intStr(10);
    downloadUrl.put(id, url);
    return id;
  }

  private void parseCharms(String[] line) {
    Charm charm = Charm.parse(line);
    charmStorage.put(String.valueOf(charm.id), charm);
  }

  private void parseTransactionTypes(String[] splitLine) {
    TransactionType type = TransactionType.parse(splitLine);
    transactionTypeStorage.put(String.valueOf(type.id), type);
  }

  private void parseTransactions(String[] line) {
    ClientAccountTransaction transaction = ClientAccountTransaction.parse(line);
    transactionStorage.put(String.valueOf(transaction.id), transaction);
  }

  private void parseAccount(String[] line) {
    ClientAccount account = ClientAccount.parse(line);
    accountStorage.put(String.valueOf(account.id), account);
  }

  private void parsePhone(String[] splitLine) {
    ClientPhone phone = ClientPhone.parse(splitLine);
    phoneStorage.put(phone.getId(), phone);
  }

  private void parseAddress(String[] splitLine) {
    ClientAddress address = ClientAddress.parse(splitLine);
    addressStorage.put(String.valueOf(address.getId()), address);
    ++addressId;
  }

  @Override
  public void afterInject() throws Exception {
    String[] files = new String[]{
            CLIENTS,
            ADDRESSES,
            PHONES,
            ACCOUNTS,
            TRANSACTIONS,
            TRANSACTION_TYPES,
            CHARMS,
            "StandDbInitData.txt"
    };

    for (String filename : files) {
      try (BufferedReader br = new BufferedReader(
        new InputStreamReader(getClass().getResourceAsStream(filename), StandardCharsets.UTF_8))) {

        int lineNo = 0;

        while (true) {
          String line = br.readLine();
          if (line == null) break;
          lineNo++;
          String trimmedLine = line.trim();
          if (trimmedLine.length() == 0) continue;
          if (trimmedLine.startsWith("#")) continue;

          String[] splitLine = line.split(";");

          String command = splitLine[0].trim();
          if (filename.equals(files[files.length - 1]))
            switch (command) {
              case "PERSON":
                appendPerson(splitLine, line, lineNo);
                break;

              default:
                throw new RuntimeException("Unknown command " + command);
            }
          else {
            switch(filename) {
              case CLIENTS:
                parseClient(splitLine);
                break;
              case ADDRESSES:
                parseAddress(splitLine);
                break;
              case PHONES:
                parsePhone(splitLine);
                break;
              case ACCOUNTS:
                parseAccount(splitLine);
                break;
              case TRANSACTIONS:
                parseTransactions(splitLine);
                break;
              case TRANSACTION_TYPES:
                parseTransactionTypes(splitLine);
                break;
              case CHARMS:
                parseCharms(splitLine);
                break;
              default:
                throw new RuntimeException("Unknown filename: " + filename);
            }
          }
        }
      }
    }
    System.out.println("personStorage: ");
    for (String key : personStorage.keySet()) {
      System.out.println(key + " " + personStorage.get(key));
    }
    System.out.println("clientStorage: ");
    for (String key : clientStorage.keySet().stream().limit(500).collect(Collectors.toList())) {
      System.out.println(key + " " + clientStorage.get(key));
    }
    System.out.println("addressStorage: ");
    for (String key : addressStorage.keySet()) {
      System.out.println(key + " " + addressStorage.get(key));
    }
    System.out.println("phoneStorage: ");
    for (String key : phoneStorage.keySet()) {
      System.out.println(key + " " + phoneStorage.get(key));
    }
    System.out.println("accountStorage: ");
    for (String key : accountStorage.keySet()) {
      System.out.println(key + " " + accountStorage.get(key));
    }
    System.out.println("transactionStorage: ");
    for (String key : transactionStorage.keySet()) {
      System.out.println(key + " " + transactionStorage.get(key));
    }
    System.out.println("transactionTypeStorage: ");
    for (String key : transactionTypeStorage.keySet()) {
      System.out.println(key + " " + transactionTypeStorage.get(key));
    }
    System.out.println("charmStorage: ");
    for (String key : charmStorage.keySet()) {
      System.out.println(key + " " + charmStorage.get(key));
    }
  }

  private void parseClient(String[] splitLine) {
    Client client = Client.parse(splitLine);
    clientStorage.put(String.valueOf(client.id), client);
    ++clientId;
    for (int i = 0; i < 1000; ++i) {
      Client fakeClient = client.clone();
      fakeClient.id = ++myId;
      clientStorage.put(String.valueOf(fakeClient.id), fakeClient);
      ++clientId;
    }
  }

  @SuppressWarnings("unused")
  private void appendPerson(String[] splitLine, String line, int lineNo) {
    PersonDot p = new PersonDot();
    p.id = splitLine[1].trim();
    String[] ap = splitLine[2].trim().split("\\s+");
    String[] fio = splitLine[3].trim().split("\\s+");
    p.accountName = ap[0];
    p.password = ap[1];
    p.surname = fio[0];
    p.name = fio[1];
    if (fio.length > 2) p.patronymic = fio[2];
    personStorage.put(p.id, p);
  }
}
