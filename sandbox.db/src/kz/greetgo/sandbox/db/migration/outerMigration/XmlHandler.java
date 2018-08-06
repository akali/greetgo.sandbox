package kz.greetgo.sandbox.db.migration.outerMigration;

import kz.greetgo.sandbox.db.interfaces.RowParser;
import kz.greetgo.sandbox.db.interfaces.StreamHandler;
import kz.greetgo.sandbox.db.util.ConfigFiles;
import kz.greetgo.sandbox.db.util.ConnectionUtils;
import kz.greetgo.sandbox.db.util.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class XmlHandler implements StreamHandler {
  private static final int MAX_CHUNK_SIZE = 1000000;
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

  OnChunkLoaded onChunkLoaded;

  @Override
  public void setParentHandler(StreamHandler handler) {
    this.parentHandler = handler;
  }

  @Override
  public void handle(InputStreamReader streamReader, String filename) {
    Logger.d(getClass().getSimpleName(), "handling file " + filename);

    if (!filename.endsWith(".xml")) {
      if (parentHandler != null)
        parentHandler.handle(streamReader, filename);
      return;
    }
    try {
      ciaWriter.start();
      try (BufferedReader isr = new BufferedReader(streamReader)) {
        String line;
        int chunks = 0;
        while ((line = isr.readLine()) != null) {
          rowParser.parseRow(line);
          ++chunks;
          if (chunks == MAX_CHUNK_SIZE) {
            Logger.d(getClass().getSimpleName(), "uploaded chunk " + chunks);
            chunks = 0;
            if (onChunkLoaded != null)
              onChunkLoaded.loaded();
          }
        }
        if (chunks > 0) {
          Logger.d(getClass().getSimpleName(), "uploaded chunk " + chunks);
          if (onChunkLoaded != null)
            onChunkLoaded.loaded();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      ciaWriter.finish();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void setOnChunkLoaded(OnChunkLoaded onChunkLoaded) {
    this.onChunkLoaded = onChunkLoaded;
  }
}
