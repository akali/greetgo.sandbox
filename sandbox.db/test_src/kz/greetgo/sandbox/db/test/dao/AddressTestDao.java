package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.Address;
import kz.greetgo.sandbox.db.stand.model.AddressDot;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;

public interface AddressTestDao {

  @Insert("insert into Addresses ( clientId, type, street, house, flat ) " +
    "values ( #{clientId}, #{type}::AddressType, #{street}, #{house}, #{flat} )")
  void insertAddressDot(AddressDot addressDot);

  @Insert("insert into Addresses ( id, clientId, type, street, house, flat ) " +
    "values ( #{id,}, #{clientId}, #{type}::AddressType, #{street}, #{house}, #{flat} )")
  void insertAddress(Address address);

  @Delete("drop table if exists Addresses cascade;" +
    "create table Addresses (\n" +
    "        id serial primary key,\n" +
    "        clientId integer references Clients(id) on delete cascade,\n" +
    "        type AddressType not null,\n" +
    "        street varchar(100) not null,\n" +
    "        house varchar(100) not null,\n" +
    "        flat varchar(100),\n" +
    "        isActive boolean default true\n" +
    "      )")
  void recreateTable();

  @Delete("Truncate Addresses cascade")
  void truncateTable();

}
