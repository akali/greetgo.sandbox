package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.InvalidCharmError;
import kz.greetgo.sandbox.controller.model.Charm;
import kz.greetgo.sandbox.controller.register.charm.CharmRegister;
import kz.greetgo.sandbox.db.dao.AuthDao;
import kz.greetgo.sandbox.db.dao.CharmDao;

import java.util.List;

@Bean
public class CharmRegisterImpl implements CharmRegister {

  public BeanGetter<CharmDao> charmDao;

  @Override
  public List<Charm> getCharmDictionary() {
    return charmDao.get().getAllCharms();
  }

  @Override
  public Charm getCharm(int charmId) {
    Charm charm = charmDao.get().getCharm(charmId);

    if(charm == null) throw new InvalidCharmError(404, "Invalid charm id:" + charmId);

    return charm;
  }
}
