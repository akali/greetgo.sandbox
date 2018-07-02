package kz.greetgo.learn.migration.core;

import kz.greetgo.learn.migration.interfaces.RowParser;
import kz.greetgo.learn.migration.interfaces.StreamHandler;
import kz.greetgo.learn.migration.util.ConfigFiles;
import kz.greetgo.learn.migration.util.ConnectionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class JsonHandler implements StreamHandler {

  private StreamHandler parentStreamHandler;
  private RowParser parser;
  private TransitionDbWriter frsWriter = new FrsTransitionWriter(ConnectionUtils.fileToConnectionConfig(ConfigFiles.migrationDb()));

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

  @Override
  public void handle(InputStreamReader streamReader, String filename) {
    if (!filename.endsWith(".json") && !filename.endsWith(".txt")) {
      if (parentStreamHandler != null)
        parentStreamHandler.handle(streamReader, filename);
      return;
    }
    try {
      frsWriter.start();
      try (BufferedReader isr = new BufferedReader(streamReader)) {
        String line;
        while ((line = isr.readLine()) != null) {
          if (!line.isEmpty()) {
            parse(line);
          }
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
  public void setParentHandler(StreamHandler handler) {
    parentStreamHandler = handler;
  }
}
