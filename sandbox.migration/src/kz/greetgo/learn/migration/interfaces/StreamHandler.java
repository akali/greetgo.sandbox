package kz.greetgo.learn.migration.interfaces;

import java.io.InputStreamReader;

public interface StreamHandler {
  long CHUNK_SIZE = 250_000;
  void handle(InputStreamReader streamReader, String filename);
  void setParentHandler(StreamHandler handler);
}
