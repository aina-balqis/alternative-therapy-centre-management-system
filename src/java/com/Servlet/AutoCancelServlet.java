package com.Servlet;

import com.Dao.AppointmentDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;


public class AutoCancelServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        AppointmentDao dao = new AppointmentDao();

        try {
            dao.autoCancelUnpaidAppointments();
            response.setContentType("text/html");
            response.getWriter().println("<h3>Auto-cancel completed.</h3>");
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("<h3>Error occurred during auto-cancel.</h3>");
        }
    }
}
