package kz.greetgo.sandbox.db.interfaces;

import java.io.InputStreamReader;

public interface StreamHandler {
  long CHUNK_SIZE = 250_000;
  void handle(InputStreamReader streamReader, String filename);
  void setParentHandler(StreamHandler handler);

  void setOnChunkLoaded(OnChunkLoaded onChunkLoaded);

  interface OnChunkLoaded {
    void loaded();
  }
}
