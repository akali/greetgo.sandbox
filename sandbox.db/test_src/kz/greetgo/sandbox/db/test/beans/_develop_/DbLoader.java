package kz.greetgo.sandbox.db.test.beans._develop_;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.register_impl.TokenRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.*;
import kz.greetgo.sandbox.db.test.dao.*;
import org.apache.log4j.Logger;

import java.util.function.Function;

@Bean
public class DbLoader {
  final Logger logger = Logger.getLogger(getClass());

  public BeanGetter<StandDb> standDb;
  public BeanGetter<TokenRegister> tokenManager;
  public BeanGetter<AuthTestDao> authTestDao;
  public BeanGetter<CharmTestDao> charmTestDao;
  public BeanGetter<ClientTestDao> clientTestDao;
  public BeanGetter<AccountTestDao> accountTestDao;
  public BeanGetter<AddressTestDao> addressTestDao;
  public BeanGetter<PhoneTestDao> phoneTestDao;

  public void loadTestData() {
    logger.info("Start loading test data...");

    logger.info("Loading persons...");
    Function<String, String> passwordEncryption = tokenManager.get()::encryptPassword;
    standDb.get().personStorage.values().stream()
      .peek(p -> p.encryptedPassword = passwordEncryption.apply(p.password))
      .peek(PersonDot::showInfo)
      .forEach(authTestDao.get()::insertPersonDot);

    logger.info("Loading charms...");
    standDb.get().charmStorage.values().stream()
      .peek(CharmDot::showInfo)
      .forEach(charmTestDao.get()::insertCharmDot);

    logger.info("Loading clients...");
    standDb.get().clientStorage.values().stream()
      .peek(ClientDot::showInfo)
      .forEach(clientTestDao.get()::insertClientDot);

    logger.info("Loading accounts...");
    standDb.get().accountStorage.values().stream()
      .peek(AccountDot::showInfo)
      .forEach(accountTestDao.get()::insertAccountDot);

    logger.info("Loading addresses...");
    standDb.get().addressStorage.values().stream()
      .peek(AddressDot::showInfo)
      .forEach(addressTestDao.get()::insertAddressDot);

    logger.info("Loading phones...");
    standDb.get().phoneStorage.values().stream()
      .peek(PhoneDot::showInfo)
      .forEach(phoneTestDao.get()::insertPhoneDot);


    logger.info("Finish loading test data");
  }
}
