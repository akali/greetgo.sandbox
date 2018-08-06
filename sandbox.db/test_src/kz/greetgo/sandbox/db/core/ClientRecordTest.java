package kz.greetgo.sandbox.db.core;

import kz.greetgo.sandbox.db.__prepare__.core.ClientInRecord;
import kz.greetgo.sandbox.db.migration.innerMigration.ClientRecord;
import kz.greetgo.sandbox.db.util.RND;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;

import static org.fest.assertions.api.Assertions.assertThat;

public class ClientRecordTest {

  @Test
  public void parseRecordData() throws Exception {
    ClientInRecord in = new ClientInRecord();

    in.id = RND.str(10);
    in.surname = RND.str(10);
    in.name = RND.str(10);
    in.patronymic = RND.str(10);
    in.birthDate = RND.date(-10000, -10);

    ClientRecord record = new ClientRecord();
    record.parseRecordData(in.toXml());

    assertThat(record.id).isEqualTo(in.id);
    assertThat(record.surname).isEqualTo(in.surname);
    assertThat(record.name).isEqualTo(in.name);
    assertThat(record.patronymic).isEqualTo(in.patronymic);
    assertThat(record.birthDate).isNotNull();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    assertThat(sdf.format(record.birthDate)).isEqualTo(sdf.format(in.birthDate));
  }
}
