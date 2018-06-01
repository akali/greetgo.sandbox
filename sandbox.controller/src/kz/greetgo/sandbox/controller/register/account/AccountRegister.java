package kz.greetgo.sandbox.controller.register.account;

import kz.greetgo.sandbox.controller.model.*;

import java.util.List;

public interface AccountRegister {
  ClientAccountRecordPage getClientAccountRecordPage(TableRequestDetails requestDetails);

  ClientAccountRecord getClientAccountRecord(int clientId);

  float getMinAccBalance(int clientId);

  float getMaxAccBalance(int clientId);

  float getTotalAccBalance(int clientId);
}
