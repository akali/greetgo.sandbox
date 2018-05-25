package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.InvalidCharmError;
import kz.greetgo.sandbox.controller.errors.NotFound;
import kz.greetgo.sandbox.controller.model.Charm;
import kz.greetgo.sandbox.controller.register.charm.CharmRegister;
import kz.greetgo.sandbox.db.dao.CharmDao;
import kz.greetgo.sandbox.db.stand.model.CharmDot;
import kz.greetgo.sandbox.db.test.dao.CharmTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class CharmRegisterImplTest extends ParentTestNg {

  public BeanGetter<CharmRegister> charmRegister;
  public BeanGetter<CharmTestDao> charmTestDao;

  @Test(expectedExceptions = InvalidCharmError.class)
  public void get_invalidId() {
    charmTestDao.get().clearTable();

    int negativeId = -1 * RND.plusInt(10);
    int notExistingId = RND.plusInt(111);

    //
    //
    charmRegister.get().getCharm(negativeId);
    //
    //

    //
    //
    charmRegister.get().getCharm(notExistingId);
    //
    //
  }

  @Test
  public void get_notActive() {
    charmTestDao.get().clearTable();

    Charm charm = new Charm();
    charm.name = RND.str(10);
    charm.isActive = false;

    charmTestDao.get().insertCharm(charm);

    //
    //
    List<Charm> list = charmRegister.get().getCharmDictionary();
    //
    //

    assertThat(list).isNotNull();
    assertThat(list).hasSize(0);
  }

  @Test
  public void get_dictionary() {
    charmTestDao.get().clearTable();

    String expectedName = RND.str(10);
    Charm activeCharm = new Charm();
    activeCharm.name = expectedName;
    activeCharm.isActive = true;

    Charm inActiveCharm = new Charm();
    inActiveCharm.name = RND.str(10);
    inActiveCharm.isActive = false;

    charmTestDao.get().insertCharm(activeCharm);
    charmTestDao.get().insertCharm(inActiveCharm);

    //
    //
    List<Charm> list = charmRegister.get().getCharmDictionary();
    //
    //

    assertThat(list).isNotNull();
    assertThat(list).hasSize(1);
    assertThat(list.get(0).isActive).isTrue();
    assertThat(list.get(0).name).isEqualToIgnoringCase(expectedName);
  }

}