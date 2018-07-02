package kz.greetgo.learn.migration.core;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Observable;
import java.util.Observer;

public class ArchiveParser {
  private File file;

  public ArchiveParser(File file) {
    this.file = file;
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

  public class StreamBundle {
    public InputStreamReader streamReader;
    public String filename;
    public StreamBundle(InputStreamReader streamReader, String filename) {
      this.streamReader = streamReader;
      this.filename = filename;
    }
  }

  public void run(MyObservable observable) {
    try (CompressorInputStream cim = new BZip2CompressorInputStream(new FileInputStream(file))) {
      try (TarArchiveInputStream i = new TarArchiveInputStream(cim)) {
        ArchiveEntry entry = null;
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
}
