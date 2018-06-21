package kz.greetgo.sandbox.controller.reports;

import kz.greetgo.sandbox.controller.model.ClientRecordRow;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.Date;

public class ReportClientRecordXlsxGenerator implements ReportClientsRecord {

  private String client;
  private Date date;

  private OutputStream out;
  private Workbook workbook;
  private CreationHelper creationHelper;
  private Sheet sheet;

  int currentRow = 0;

  public ReportClientRecordXlsxGenerator(OutputStream out) {
    this.out = out;
  }

  @Override
  public void start(String client, Date date) {
    this.client = client;
    this.date = date;
    workbook = new SXSSFWorkbook();
    creationHelper = workbook.getCreationHelper();
    sheet = workbook.createSheet("Client Record");
    Row row = sheet.createRow(currentRow++);
    row.createCell(0).setCellValue("от " + date);
  }

  @Override
  public void append(ClientRecordRow clientRecordRow) {
    Row row = sheet.createRow(currentRow++);
    row.createCell(0).setCellValue(clientRecordRow.no);
    row.createCell(1).setCellValue(clientRecordRow.name);
    row.createCell(2).setCellValue(clientRecordRow.age);
    row.createCell(3).setCellValue(clientRecordRow.total);
    row.createCell(4).setCellValue(clientRecordRow.min);
    row.createCell(5).setCellValue(clientRecordRow.max);
  }

  @Override
  public void finish() throws IOException {
    Row row = sheet.createRow(++currentRow);
    row.createCell(0).setCellValue("Created by " + client);

    workbook.write(out);
  }
}

