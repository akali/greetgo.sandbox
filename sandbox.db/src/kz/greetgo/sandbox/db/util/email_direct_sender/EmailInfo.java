package kz.greetgo.sandbox.db.util.email_direct_sender;

import kz.greetgo.conf.ConfData;

public class EmailInfo {
  private EmailInfo() {}

  final ConfData c = new ConfData();

  public static EmailInfo load() {
    return new EmailInfo().loadData();
  }

  private EmailInfo loadData() {
    c.readFromStream(getClass().getResourceAsStream("EmailInfo.txt"));
    return this;
  }

  public String googleAccountName() {return c.str("googleAccountName");}

  public String googleAccountPassword() {return c.str("googleAccountPassword");}

  public String toEmail1() {return c.str("toEmail1");}

  public String toEmail2() {return c.str("toEmail2");}

  public String toEmail3() {return c.str("toEmail3");}

}
