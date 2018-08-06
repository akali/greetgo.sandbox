package kz.greetgo.sandbox.db.migration.innerMigration;

import kz.greetgo.sandbox.db.util.SaxHandler;
import org.fest.util.Lists;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.List;

public class ClientRecord extends SaxHandler {
  public long number;
  public String id;
  public String surname, name, patronymic;
  public java.sql.Date birthDate;
  public String cia_id;
  public String charm_name;
  public ClientAddress factAddress, regAddress;
  public List<String>
    workPhones = Lists.newArrayList(),
    homePhones = Lists.newArrayList(),
    mobilePhones = Lists.newArrayList();

  public void parseRecordData(String recordData) throws SAXException, IOException {
    if (recordData == null) return;
    XMLReader reader = XMLReaderFactory.createXMLReader();
    reader.setContentHandler(this);
    reader.parse(new InputSource(new StringReader(recordData)));
  }

  @Override
  protected void startingTag(Attributes attributes) {
    String path = path();
    switch (path) {
      case "/client": cia_id = attributes.getValue("id"); break;
      case "/client/surname": surname = attributes.getValue("value"); break;
      case "/client/name": name = attributes.getValue("value"); break;
      case "/client/patronymic": patronymic = attributes.getValue("value"); break;
      case "/client/charm": charm_name = attributes.getValue("value"); break;
      case "/client/birth": {
        try {
          birthDate =
            new java.sql.Date(
              new SimpleDateFormat("yyyy-MM-dd")
                .parse(
                  attributes.getValue("value")
                ).getTime()
            );
        } catch (Exception ignored) {
          birthDate = null;
        }
      } break;
      case "/client/address/fact": factAddress = new ClientAddress(attributes.getValue("street"), attributes.getValue("house"), attributes.getValue("flat")); break;
      case "/client/address/register": regAddress = new ClientAddress(attributes.getValue("street"), attributes.getValue("house"), attributes.getValue("flat")); break;
    }
  }

  @Override
  protected void endedTag(String tagName) {
    String path = path() + "/" + tagName;

    switch (path) {
      case "/client/homePhone": addHomePhone(text()); break;
      case "/client/mobilePhone": addMobilePhone(text()); break;
      case "/client/workPhone": addWorkPhone(text()); break;
    }
  }

  private void addWorkPhone(String text) {
    workPhones.add(text);
  }

  private void addMobilePhone(String text) {
    mobilePhones.add(text);
  }

  private void addHomePhone(String text) {
    homePhones.add(text);
  }

  @Override
  public String toString() {
    return "ClientRecord{" +
      "number=" + number +
      ", id='" + id + '\'' +
      ", surname='" + surname + '\'' +
      ", name='" + name + '\'' +
      ", patronymic='" + patronymic + '\'' +
      ", birthDate=" + birthDate +
      ", cia_id='" + cia_id + '\'' +
      ", charm_name='" + charm_name + '\'' +
      ", factAddress=" + factAddress +
      ", regAddress=" + regAddress +
      ", workPhones=" + workPhones +
      ", homePhones=" + homePhones +
      ", mobilePhones=" + mobilePhones +
      '}';
  }

  public static void main(String[] args) {
    String xml = "  <client id=\"4-DU8-32-H7\"> <!-- Идентификаторы строковые, не длиннее 50 символов -->\n" +
      "    <surname value=\"Иванов\" />\n" +
      "    <name value=\"Иван\" />\n" +
      "    <patronymic value=\"Иваныч\" />\n" +
      "    <gender value=\"MALE\" />\n" +
      "    <charm value=\"Уситчивый\" />\n" +
      "    <birth value=\"1980-11-12\" />\n" +
      "    <address>\n" +
      "      <fact street=\"Панфилова\" house=\"23A\" flat=\"22\" />\n" +
      "      <register street=\"Панфилова\" house=\"23A\" flat=\"22\" />\n" +
      "    </address>\n" +
      "    \n" +
      "      <homePhone>+7-123-111-22-33</homePhone>\n" +
      "    <mobilePhone>+7-123-111-33-33</mobilePhone>\n" +
      "    <mobilePhone>+7-123-111-44-33</mobilePhone>\n" +
      "    <mobilePhone>+7-123-111-55-33</mobilePhone>\n" +
      "      <workPhone>+7-123-111-00-33 вн. 3344</workPhone>\n" +
      "      <workPhone>+7-123-111-00-33 вн. 3343</workPhone>\n" +
      "  </client>";
    ClientRecord c = new ClientRecord();
    try {
      c.parseRecordData(xml);
      System.out.println(c);
    } catch (SAXException | IOException e) {
      e.printStackTrace();
    }
  }
}
