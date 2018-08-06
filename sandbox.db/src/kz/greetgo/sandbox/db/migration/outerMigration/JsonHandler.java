package kz.greetgo.sandbox.db.migration.outerMigration;

import kz.greetgo.sandbox.db.interfaces.RowParser;
import kz.greetgo.sandbox.db.interfaces.StreamHandler;
import kz.greetgo.sandbox.db.util.ConfigFiles;
import kz.greetgo.sandbox.db.util.ConnectionUtils;
import kz.greetgo.sandbox.db.util.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class JsonHandler implements StreamHandler {
  private static final int MAX_CHUNK_SIZE = 1000000;
  private StreamHandler parentStreamHandler;
  private RowParser parser;
  private TransitionDbWriter frsWriter =
    new FrsTransitionWriter(ConnectionUtils.fileToConnectionConfig(ConfigFiles.migrationDb()));

  public JsonHandler() throws IOException { }

  public RowParser getParser() {
    return parser;
  }

  public void setParser(RowParser parser) {
    this.parser = parser;
    parser.setOnParse(parsed -> {
      try {
        frsWriter.writeRow(parsed);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  private void parse(String json) {
    parser.parseRow(json);
  }

  StreamHandler.OnChunkLoaded onChunkLoaded;

  @Override
  public void setParentHandler(StreamHandler handler) {
    parentStreamHandler = handler;
  }

  @Override
  public void handle(InputStreamReader streamReader, String filename) {
    Logger.d(getClass().getSimpleName(), "Handling file " + filename);

    if (!filename.endsWith(".json") && !filename.endsWith(".txt")) {
      if (parentStreamHandler != null)
        parentStreamHandler.handle(streamReader, filename);
      return;
    }

    try {
      frsWriter.start();
      try (BufferedReader isr = new BufferedReader(streamReader)) {
        String line;
        int chunks = 0;
        while ((line = isr.readLine()) != null) {
          if (!line.isEmpty()) {
            parse(line);
            ++chunks;
            if (chunks == MAX_CHUNK_SIZE) {
              if (onChunkLoaded != null)
                onChunkLoaded.loaded();
              chunks = 0;
            }
          }
        }
        if (chunks > 0) {
          if (onChunkLoaded != null)
            onChunkLoaded.loaded();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      frsWriter.finish();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void setOnChunkLoaded(OnChunkLoaded onChunkLoaded) {
    this.onChunkLoaded = onChunkLoaded;
  }

}
