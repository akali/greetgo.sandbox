package kz.greetgo.learn.migration.core.outerMigration;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Observable;
import java.util.Observer;

public class ArchiveParser {
  private InputStream inputStream;

  public ArchiveParser(InputStream file) {
    this.inputStream = file;
  }

  public static class MyObservable extends Observable {
    public void set(StreamBundle streamReader) {
      setChanged();
      notifyObservers(streamReader);
    }
    public MyObservable withObserver(Observer observer) {
      this.addObserver(observer);
      return this;
    }
  }

  public void run(MyObservable observable) {
    try (CompressorInputStream cim = new BZip2CompressorInputStream(inputStream)) {
      try (TarArchiveInputStream i = new TarArchiveInputStream(cim)) {
        ArchiveEntry entry;
        while ((entry = i.getNextEntry()) != null) {
          if (!i.canReadEntryData(entry)) {
            continue;
          }
          if (!entry.isDirectory()) {
            OpenInputStreamReader inputStreamReader = new OpenInputStreamReader(i);
            inputStreamReader.setAutoClose(false);
            observable.set(new StreamBundle(inputStreamReader, entry.getName()));
            inputStreamReader.setAutoClose(true);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static class StreamBundle {
    public InputStreamReader streamReader;
    public String filename;

    public StreamBundle(InputStreamReader streamReader, String filename) {
      this.streamReader = streamReader;
      this.filename = filename;
    }
  }
}
