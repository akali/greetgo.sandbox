package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.db.stand.model.AccountDot;
import kz.greetgo.sandbox.db.stand.model.AdressDot;
import org.apache.ibatis.annotations.Insert;

public interface AccountTestDao {

    @Insert("insert into accounts (client_id, id, money, number, registered_at) " +
            "values (#{clientID}, #{id}, #{money}, #{number}, #{registered_at})")
    void insertAccount(AccountDot accountDot);
}
