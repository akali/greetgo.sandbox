package kz.greetgo.learn.migration.core;

import kz.greetgo.learn.migration.interfaces.RowParser;
import kz.greetgo.learn.migration.interfaces.StreamHandler;
import kz.greetgo.learn.migration.util.ConfigFiles;
import kz.greetgo.learn.migration.util.ConnectionUtils;

import java.io.*;

public class XmlHandler implements StreamHandler {
  private StreamHandler parentHandler;
  private RowParser rowParser;
  private TransitionDbWriter ciaWriter = new CiaTransitionWriter(ConnectionUtils.fileToConnectionConfig(ConfigFiles.migrationDb()));

  public XmlHandler() throws IOException {}

  public void setRowParser(RowParser rowParser) {
    this.rowParser = rowParser;
    this.rowParser.setOnParse(parsed -> {
      try {
        ciaWriter.writeRow(parsed);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  @Override
  public void handle(InputStreamReader streamReader, String filename) {
    if (!filename.endsWith(".xml")) {
      if (parentHandler != null)
        parentHandler.handle(streamReader, filename);
      return;
    }
    try {
      ciaWriter.start();
      try (BufferedReader isr = new BufferedReader(streamReader)) {
        String line;
        while ((line = isr.readLine()) != null)
          rowParser.parseRow(line);
      } catch (Exception e) {
        e.printStackTrace();
      }
      ciaWriter.finish();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void setParentHandler(StreamHandler handler) {
    this.parentHandler = handler;
  }
}
