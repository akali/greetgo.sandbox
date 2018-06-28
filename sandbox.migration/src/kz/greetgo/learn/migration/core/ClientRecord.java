package kz.greetgo.learn.migration.core;

import kz.greetgo.learn.migration.util.SaxHandler;
import org.fest.util.Lists;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
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
  protected void startingTag(Attributes attributes) throws ParseException {
    String path = path();
    switch (path) {
      case "/client": cia_id = attributes.getValue("id"); break;
      case "/client/surname": surname = attributes.getValue("value"); break;
      case "/client/name": name = attributes.getValue("value"); break;
      case "/client/patronymic": patronymic = attributes.getValue("value"); break;
      case "/client/charm": charm_name = attributes.getValue("value"); break;
      case "/client/birth": birthDate = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(attributes.getValue("value")).getTime()); break;
      case "/client/address/fact": factAddress = new ClientAddress(attributes.getValue("street"), attributes.getValue("house"), attributes.getValue("flat")); break;
      case "/client/address/register": regAddress = new ClientAddress(attributes.getValue("street"), attributes.getValue("house"), attributes.getValue("flat")); break;
    }
  }

  @Override
  protected void endedTag(String tagName) throws Exception {
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
}
