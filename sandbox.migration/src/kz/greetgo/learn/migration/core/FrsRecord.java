package kz.greetgo.learn.migration.core;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class FrsRecord {
  public long number;
  public Type type;
  public Date finished_at;
  public String account_number;
  public String transaction_type;
  public float money;
  public Date registered_at;
  public String client_id;

  public FrsRecord(String type, Date finished_at, String account_number, String transaction_type, float money, Date registered_at, String client_id) {
    this.type = Type.get(type);
    this.finished_at = finished_at;
    this.account_number = account_number;
    this.transaction_type = transaction_type;
    this.money = money;
    this.registered_at = registered_at;
    this.client_id = client_id;
  }

  public static FrsRecord parse(String data) throws IOException {
    return new ObjectMapper()
      .registerModule(
        new SimpleModule()
          .addDeserializer(
            FrsRecord.class,
            new FrsRecordDeserializer()
          )
      )
      .readValue
        (data,
          FrsRecord.class
        );
  }

  public static void main(String[] args) {
    Type type = Type.get("TRANSACTIOn");
    System.out.println(type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(type).append(" {");
    if ("transaction".equalsIgnoreCase(type.toString())) {
      sb.append("\n\ttransaction_type: ").append(transaction_type);
      sb.append("\n\taccount_number: ").append(account_number);
      sb.append("\n\tmoney: ").append(money);
      sb.append("\n\tfinished_at: ").append(finished_at);
      sb.append("\n}");
    } else {
      sb.append("\n\tregistered_at: ").append(registered_at);
      sb.append("\n\tclient_id: ").append(client_id);
      sb.append("\n\taccount_number: ").append(account_number);
      sb.append("\n}");
    }
    return sb.toString();
  }

  public enum Type {
    NEW_ACCOUNT,
    TRANSACTION;

    public static Type get(String s) throws IllegalArgumentException {
      for (Type value : values()) {
        if (value.toString().equalsIgnoreCase(s)) return value;
      }
      throw new IllegalArgumentException();
    }
  }

  public static class FrsRecordDeserializer extends JsonDeserializer<FrsRecord> {

    @Override
    public FrsRecord deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      ObjectCodec oc = p.getCodec();
      JsonNode node = oc.readTree(p);

      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

      String type = node.get("type").asText();
      String transaction_type = null;
      Date finished_at, registered_at;
      String account_number = null;
      float money = 0;
      String client_id = null;
      Date registered_at1 = null, finished_at1 = null;
      try {
        if ("transaction".equalsIgnoreCase(type)) {
          transaction_type = node.get("transaction_type").asText();
          money = Float.parseFloat(node.get("money").asText().replaceAll("[ _]", ""));
          finished_at1 = new java.sql.Date(sdf.parse(node.get("finished_at").asText().replace("T", " ")).getTime());
        } else {
          registered_at1 = new java.sql.Date(sdf.parse(node.get("registered_at").asText().replace("T", " ")).getTime());
          client_id = node.get("client_id").asText();
        }
        account_number = node.get("account_number").asText();
      } catch (ParseException e) {
        finished_at1 = null;
        registered_at1 = null;
      }

      registered_at = registered_at1;
      finished_at = finished_at1;

      return new FrsRecord(type, finished_at, account_number, transaction_type, money, registered_at, client_id);
    }
  }
}
