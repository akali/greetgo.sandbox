package kz.greetgo.sandbox.controller.reports;

import com.itextpdf.text.DocumentException;
import kz.greetgo.sandbox.controller.model.ClientRecordRow;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

public interface ReportClientsRecord {
  void start(String client, Date date) throws DocumentException, IOException;

  void append(ClientRecordRow row) throws DocumentException;

  void finish() throws IOException, DocumentException;
}
