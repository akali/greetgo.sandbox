package kz.greetgo.learn.migration.core;

import kz.greetgo.learn.migration.interfaces.StreamHandler;
import kz.greetgo.learn.migration.util.ConfigFiles;
import kz.greetgo.learn.migration.util.ConnectionUtils;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.*;
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

  public static void main(String[] args) throws Exception {
//    File file = new File("migration_data/from_cia_2018-02-21-154955-5-1000000.xml.tar.bz2");
    File file = new File("migration_data/from_frs_2018-02-21-155112-1-30002.json_row.txt.tar.bz2");
    StreamHandler handler = new JsonHandler(), xmlHandler = new XmlHandler();
    ((JsonHandler) handler).setParser(new JsonParser());
    handler.setParentHandler(xmlHandler);
    ((XmlHandler) xmlHandler).setRowParser(new XmlParser());

    ArchiveParser parser = new ArchiveParser(file);
    parser.run(new MyObservable().withObserver((observable1, o) -> {
      if (o == null) return;
      StreamBundle sb = (StreamBundle) o;
      handler.handle(sb.streamReader, sb.filename);
    }));
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
