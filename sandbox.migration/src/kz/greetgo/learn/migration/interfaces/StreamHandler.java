package kz.greetgo.learn.migration.interfaces;

import java.io.InputStreamReader;

public interface StreamHandler {
//  HashMap<String, Class> klases = new HashMap<String, Class>() {
//    {""},
//    {}
//  };
  void handle(InputStreamReader streamReader, String filename);
  void setParentHandler(StreamHandler handler);
}
