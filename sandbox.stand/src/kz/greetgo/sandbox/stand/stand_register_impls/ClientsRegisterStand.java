package kz.greetgo.sandbox.stand.stand_register_impls;

import com.itextpdf.text.DocumentException;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.interfaces.BinResponse;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.AuthRegister;
import kz.greetgo.sandbox.controller.register.ClientsRegister;
import kz.greetgo.sandbox.controller.reports.ReportClientRecordPdfGenerator;
import kz.greetgo.sandbox.controller.reports.ReportClientRecordXlsxGenerator;
import kz.greetgo.sandbox.controller.reports.ReportClientsRecord;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import org.apache.poi.util.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Bean
public class ClientsRegisterStand implements ClientsRegister {
  public BeanGetter<StandDb> standDb;
  public BeanGetter<AuthRegister> authRegister;

  private Client getClient(int clientId) {
    return standDb.get().clientStorage.get(String.valueOf(clientId));
  }

  @Override
  public List<Charm> getCharms() {
    List<Charm> list = new ArrayList<>();
    Map<String, Charm> map = standDb.get().charmStorage;
    for (String key: map.keySet())
      list.add(map.get(key));
    return list;
  }

  private List<ClientAccount> getClientAccounts(int id) {
    Map<String, ClientAccount> map = standDb.get().accountStorage;
    return map.values().stream()
      .filter(clientAccount -> clientAccount.id == id)
      .collect(Collectors.toList());
  }

  @Override
  public FilteredTable getClientRecords(QueryFilter queryFilter) {
    int start = queryFilter.start;
    int offset = queryFilter.limit;
    String direction = queryFilter.direction;
    String active = queryFilter.active;
    String filter = queryFilter.filter;
    System.out.println("start: " + start + "; " + "offset: " + offset);
    System.out.println("direction: " + direction + "; " + "active: " + active);
    List<ClientRecord> list = new ArrayList<>();

    Map<String, Client> map = standDb.get().clientStorage;

    for (String key: map.keySet()) {
      Client client = map.get(key);
      ClientRecord result = getClientRecord(client.id);
      list.add(result);
    }

    System.out.println(Arrays.toString(list.stream()
      .filter(clientRecord -> filter == null || clientRecord.getCombinedString().contains(filter)).toArray()));

    return new FilteredTable(list, start, offset, direction, active, filter);
  }

  public static int calculateAge(long birthDateTs) {
    LocalDate date = Instant.ofEpochMilli(new Date().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    LocalDate date1 = new Timestamp(birthDateTs).toLocalDateTime().toLocalDate();

    return date.minusYears(date1.getYear()).getYear();
  }


  @Override
  public ClientDetail getClientDetailsById(int clientId) {
    Client client = getClient(clientId);

    ClientDetail clientDetail = new ClientDetail();
    clientDetail.id = client.id;
    clientDetail.name = client.name;
    clientDetail.surname = client.surname;
    clientDetail.patronymic = client.patronymic;
    clientDetail.gender = client.gender;
    clientDetail.birthDate = client.birth_date.getTime();
    clientDetail.charm = standDb.get().charmStorage.get(String.valueOf(client.charm));

    standDb.get().addressStorage.values().stream()
      .filter(clientAddress -> clientAddress.client == client.id)
      .forEach(clientAddress -> {
        if (clientAddress.type == AddressType.REG)
          clientDetail.regAddress = clientAddress;
        else
          clientDetail.factAddress = clientAddress;
      });

    clientDetail.phones = standDb.get().phoneStorage.values().stream()
      .filter(clientPhone -> clientPhone.client == clientId)
      .collect(Collectors.toList());

    clientDetail.charms = new ArrayList<>(standDb.get().charmStorage.values());

    return clientDetail;
  }

  private ClientRecord editClient(int clientId, ClientToSave clientToSave) {
    Client client = getClient(clientId);
    client.surname = clientToSave.surname;
    client.name = clientToSave.name;
    client.patronymic = clientToSave.patronymic;
    client.birth_date = new java.sql.Date(clientToSave.birthDate);
    client.gender = clientToSave.gender;
    client.charm = clientToSave.charm;

    for (ClientPhone phone: clientToSave.phones) {
      standDb.get().phoneStorage.put(phone.getId(), phone);
    }

    standDb.get().addressStorage.put(clientToSave.regAddress.getId(), clientToSave.regAddress);
    standDb.get().addressStorage.put(clientToSave.factAddress.getId(), clientToSave.factAddress);

    return getClientRecord(client.id);
  }

  @Override
  public ClientRecord addClientToSave(ClientToSave clientToSave) {
    System.err.println(clientToSave);
    if (!verify(clientToSave))
      throw new RuntimeException("Incorrect data");

    Client client = new Client();
    client.id = ++standDb.get().clientId;

    clientToSave.set(client.id);

    standDb.get().clientStorage.put(String.valueOf(client.id), client);

    return editClient(client.id, clientToSave);
  }

  @Override
  public ClientRecord editClientToSave(ClientToSave clientToSave) {
    System.err.println(clientToSave);
    if (!verify(clientToSave))
      throw new RuntimeException("Incorrect data");
    return editClient(clientToSave.id, clientToSave);
  }

  private ClientRecord getClientRecord(int id) {
    Client client = getClient(id);
    ClientRecord result = new ClientRecord();
    result.id = client.id;
    result.name = client.name;
    result.surname = client.surname;
    result.patronymic = client.patronymic;
    Charm charm = (Charm) getCharms().stream().filter(ch -> ch.id == client.charm).toArray()[0];
    result.charm = charm.name;
    result.total = 0;
    List<ClientAccount> accounts = getClientAccounts(client.id);
    if (accounts != null && !accounts.isEmpty()) {
      accounts.forEach(clientAccount -> result.total += clientAccount.money);
      result.min = accounts.stream().min((a, b) -> Float.compare(a.money, b.money)).get().money;
      result.max = accounts.stream().max((a, b) -> Float.compare(a.money, b.money)).get().money;
    }
    result.name = result.name + " " + result.surname;
    if (result.patronymic != null)
      result.name += " " + result.patronymic;
    result.age = calculateAge(client.birth_date.getTime());
    return result;
  }

  private boolean verify(ClientToSave clientToSave) {
    if (clientToSave == null) return false;
    if (clientToSave.name == null || clientToSave.name.isEmpty()) return false;
    if (clientToSave.surname == null || clientToSave.surname.isEmpty()) return false;
    if (getCharms().stream().noneMatch(charm -> charm.id == clientToSave.charm)) return false;
    if (clientToSave.regAddress == null) return false;
    return clientToSave.phones != null && !clientToSave.phones.isEmpty();
  }

  @Override
  public void removeClientById(int clientId) {
    standDb.get().clientStorage.remove(String.valueOf(clientId));
  }

  @Override
  public String generateReport(ReportType reportType, QueryFilter filter, String token) throws IOException, DocumentException {
    filter.start = 0;
    filter.limit = 1000000000;

    FilteredTable response = getClientRecords(filter);
    ReportClientsRecord reportClientsRecord  = null;

    String root = "/home/aqali/tmp/" + "Report_" + new Random().nextInt(100000);

    FileOutputStream fos = null; // = new FileOutputStream(root);

    switch (reportType) {
      case XLSX:
        root += ".xlsx";
        fos = new FileOutputStream(root);
        reportClientsRecord = new ReportClientRecordXlsxGenerator(fos);
        break;
      case PDF:
        root += ".pdf";
        fos = new FileOutputStream(root);
        reportClientsRecord = new ReportClientRecordPdfGenerator(fos);
        break;
    }

    reportClientsRecord.start(authRegister.get().getUserInfo(token).accountName, new Date());

    int count = 1;

    for (ClientRecord record: response.list) {
      reportClientsRecord.append(new ClientRecordRow(count++, record));
    }

    reportClientsRecord.finish();
    return standDb.get().putUrl(root);
  }

  @Override
  public void downloadReport(String id, BinResponse binResponse) throws IOException {
    System.out.println(id);
    standDb.get().downloadUrl.forEach((s, s2) -> System.out.println(s + ": " + s2));

    String filename = standDb.get().getUrl(id);

    binResponse.setFilename(filename);
    binResponse.setContentType("application/octet-stream");

    System.out.println(filename);

    try (FileInputStream fin = new FileInputStream(new File(filename))) {
      IOUtils.copy(fin, binResponse.out());
    }

    binResponse.out().flush();
  }
}
