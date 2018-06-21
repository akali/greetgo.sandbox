package kz.greetgo.sandbox.controller.reports;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import kz.greetgo.sandbox.controller.model.ClientRecordRow;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

public class ReportClientRecordPdfGenerator implements ReportClientsRecord {

  private OutputStream out;
  private Document document;
  private String client;
  private Date date;

  public ReportClientRecordPdfGenerator(OutputStream out) {
    this.out = out;
  }

  @Override
  public void start(String client, Date date) throws DocumentException, IOException {
    this.client = client;
    this.date = date;

    document = new Document();
    PdfWriter.getInstance(document, out);

    document.add(new Paragraph("Client Records Report from: " + date));

    PdfPTable table = new PdfPTable(7);
    table.addCell("#");
    table.addCell("Name Surname Patronymic");
    table.addCell("age");
    table.addCell("charm");
    table.addCell("total");
    table.addCell("max");
    table.addCell("min");
    document.add(table);
  }

  int rowCount = 0;

  @Override
  public void append(ClientRecordRow row) throws DocumentException {
    PdfPTable table = new PdfPTable(7);
    table.addCell(new PdfPCell(new Phrase("" + ++rowCount)));
    table.addCell(row.name);
    table.addCell("" + row.age);
    table.addCell(row.charm);
    table.addCell("" + row.total);
    table.addCell("" + row.max);
    table.addCell("" + row.min);
  }

  @Override
  public void finish() throws IOException, DocumentException {
    document.add(new Phrase("Generated by " + client));
    document.close();
  }
}
