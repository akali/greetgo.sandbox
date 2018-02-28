package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.report.ClientReportView;
import kz.greetgo.sandbox.controller.report.ClientReportViewPDF;
import kz.greetgo.sandbox.controller.report.ClientReportViewXLSX;
import kz.greetgo.sandbox.controller.util.Controller;

import java.io.OutputStream;
import java.util.Date;

@Bean
@Mapping("/report")
public class ReportController implements Controller {

  public BeanGetter<ClientRegister> clientRegister;

  @Mapping("/clientRecordList")
  public void clientRecordList(@Par("type") String type, @Par("orderBy") String orderBy, @Par("desc") int desc,
                               @Par("filter") String filter, RequestTunnel requestTunnel) throws Exception {

    OutputStream out = requestTunnel.getResponseOutputStream();
    ClientReportView view;

    switch (type) {
      case "pdf":
        view = new ClientReportViewPDF(out);
        break;
      case "xlsx":
        view = new ClientReportViewXLSX(out);
        break;
      default:
        throw new Exception("Unsupported File Format");
    }

    String fileName = "client_report_" + new Date() + "." + type;
    requestTunnel.setResponseHeader("Content-disposition", "attachment; filename=" + fileName);
    requestTunnel.setResponseHeader("Access-Control-Expose-Headers", "Content-disposition");

    this.clientRegister.get().genClientRecordListReport(filter, orderBy, desc, view);
    out.flush();
    requestTunnel.flushBuffer();
  }
}
