package com.Servlet;

import com.Dao.AppointmentDao;
import com.Model.Appointment;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;


import java.io.*;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ExportPDFServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("appointmentReportPDF".equals(action)) {
            int month = Integer.parseInt(request.getParameter("month"));
            int year = Integer.parseInt(request.getParameter("year"));
            String status = request.getParameter("status");

            try {
                AppointmentDao dao = new AppointmentDao();
                List<Appointment> appts = dao.getAppointmentsByMonthYearStatus(month, year, status);

                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=appointment_report.pdf");
                Document doc = new Document();
                PdfWriter.getInstance(doc, response.getOutputStream());
                doc.open();

                doc.add(new Paragraph("Appointment Report (" + month + "/" + year + ") - Status: " + status));
                doc.add(new Paragraph(" "));
                PdfPTable table = new PdfPTable(5);
                table.addCell("Date");
                table.addCell("Time");
                table.addCell("Client");
                table.addCell("Package");
                table.addCell("Status");

                for (Appointment a : appts) {
                    table.addCell(a.getAppointmentDate());
                    table.addCell(a.getAppointmentTime());
                    table.addCell(a.getClientName());
                    table.addCell(a.getPackageName());
                    table.addCell(a.getAppointmentStatus());
                }

                doc.add(table);
                doc.close();

            } catch (Exception e) {
                throw new ServletException(e);
            }
        }
    }
}
