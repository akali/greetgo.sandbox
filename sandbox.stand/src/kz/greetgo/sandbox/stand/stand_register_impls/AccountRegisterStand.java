package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.account.AccountRegister;
import kz.greetgo.sandbox.controller.register.charm.CharmRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.AccountDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Calendar.*;

@Bean
public class AccountRegisterStand implements AccountRegister {

  public BeanGetter<StandDb> db;
  public BeanGetter<CharmRegister> charmRegister;

  @Override
  public ClientAccountRecordPage getClientAccountRecordPage(TableRequestDetails requestDetails) {
    List<ClientAccountRecord> clientAccountInfoList = new ArrayList<>();

    for (ClientDot clientDot : db.get().clientStorage.values()) {
      if (clientDot.isActive) {
        ClientAccountRecord clientAccountInfo = getClientAccountRecord(clientDot.id);
        clientAccountInfoList.add(clientAccountInfo);
      }
    }

    clientAccountInfoList = filter(clientAccountInfoList, requestDetails.filter);
    clientAccountInfoList = sort(clientAccountInfoList, requestDetails.sortBy, requestDetails.sortDirection);

    int totalAccountInfoCount = clientAccountInfoList.size();

    clientAccountInfoList = paginate(clientAccountInfoList, requestDetails.pageIndex, requestDetails.pageSize);


    return new ClientAccountRecordPage(clientAccountInfoList, totalAccountInfoCount);
  }

  @Override
  public List<ClientAccountRecord> filter(List<ClientAccountRecord> list, String filterValue) {
    return list.stream()
      .filter(a -> a.clientFullName.replaceAll("\\s+", "").toLowerCase()
        .contains(filterValue.replaceAll("\\s+", "").toLowerCase())
      ).collect(Collectors.toCollection(ArrayList::new));
  }

  @Override
  public List<ClientAccountRecord> sort(List<ClientAccountRecord> list, SortColumn column, SortDirection direction) {

    switch (column) {
      case FIO:
        list.sort(Comparator.comparing(a -> a.clientFullName));
        break;
      case AGE:
        list.sort(Comparator.comparingInt(a -> a.clientAge));
        break;
      case TOTAL:
        list.sort((ClientAccountRecord a1, ClientAccountRecord a2) -> (int) (a1.totalAccBalance - a2.totalAccBalance));
        break;
      case MAX:
        list.sort((ClientAccountRecord a1, ClientAccountRecord a2) -> (int) (a1.maxAccBalance - a2.maxAccBalance));
        break;
      case MIN:
        list.sort((ClientAccountRecord a1, ClientAccountRecord a2) -> (int) (a1.minAccBalance - a2.minAccBalance));
        break;
    }

    if (column != SortColumn.NONE && direction == SortDirection.DESC) Collections.reverse(list);

    return list;
  }

  @Override
  public List<ClientAccountRecord> paginate(List<ClientAccountRecord> list, int pageIndex, int pageSize) {
    int fromIndex = pageIndex * pageSize;
    int toIndex = fromIndex + pageSize;
    if (fromIndex > list.size()) fromIndex = 0;
    if (toIndex > list.size()) toIndex = list.size();

    return new ArrayList<>(list.subList(fromIndex, toIndex));
  }

  @Override
  public ClientAccountRecord getClientAccountRecord(int clientId) {
    ClientDot clientDot = db.get().clientStorage.get(clientId);

    ClientAccountRecord clientAccountInfo = new ClientAccountRecord();
    clientAccountInfo.clientId = clientDot.id;
    clientAccountInfo.clientFullName = String.format("%s %s %s", clientDot.name, clientDot.surname, clientDot.patronymic);
    clientAccountInfo.clientCharmName = charmRegister.get().getCharm(clientDot.charmId).name;
    clientAccountInfo.clientAge = calculateYearDiff(clientDot.birthDate);

    List<Account> accounts = getClientAccounts(clientAccountInfo.clientId);
    if (accounts.size() == 0) return null;

    clientAccountInfo.totalAccBalance = getTotalAccBalance(clientId);
    clientAccountInfo.minAccBalance = getMinAccBalance(clientId);
    clientAccountInfo.maxAccBalance = getMaxAccBalance(clientId);

    return clientAccountInfo;
  }

  @Override
  public float getMinAccBalance(int clientId) {
    List<Account> accounts = getClientAccounts(clientId);

    float result = accounts.get(0).money;
    for (int i = 1; i < accounts.size(); i++) {
      Account curAcc = accounts.get(i);
      if (curAcc.money < result) result = curAcc.money;
    }
    return result;
  }

  @Override
  public float getMaxAccBalance(int clientId) {
    List<Account> accounts = getClientAccounts(clientId);

    float result = accounts.get(0).money;
    for (int i = 1; i < accounts.size(); i++) {
      Account curAcc = accounts.get(i);
      if (curAcc.money > result) result = curAcc.money;
    }
    return result;
  }

  @Override
  public float getTotalAccBalance(int clientId) {
    List<Account> accounts = getClientAccounts(clientId);

    float result = 0f;
    for (Account acc : accounts) {
      result += acc.money;
    }
    return result;
  }

  private List<Account> getClientAccounts(int clientId) {
    ArrayList<Account> accounts = new ArrayList<>();

    for (AccountDot accountDot : db.get().accountStorage.values()) {
      if (accountDot.clientId == clientId && accountDot.isActive) accounts.add(accountDot.toAccount());
    }

    return accounts;
  }

  private int calculateYearDiff(Date date) {
    Calendar dateNow = Calendar.getInstance();
    dateNow.setTime(Date.from(Instant.now()));

    Calendar dateBirth = Calendar.getInstance();
    dateBirth.setTime(date);

    int diff = dateNow.get(YEAR) - dateBirth.get(YEAR);
    if (dateNow.get(MONTH) > dateBirth.get(MONTH) ||
      (dateNow.get(MONTH) == dateBirth.get(MONTH) && dateNow.get(DATE) > dateBirth.get(DATE))) {
      diff--;
    }
    return diff;
  }

}
