package com.Servlet;

import com.Dao.AdminDao;
import com.Dao.AppointmentDao;
import com.Model.Admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

//@WebServlet("/AdminLoginServlet")
public class AdminLoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("admin_email");
        String password = request.getParameter("admin_password");

        try {
            AdminDao adminDao = new AdminDao();
            Admin admin = adminDao.validateLogin(email, password);

            if (admin != null) {
                HttpSession session = request.getSession();
                session.setAttribute("admin", admin);

                // ✅ Auto-cancel unpaid appointments
                try {
                    AppointmentDao appointmentDao = new AppointmentDao();
                    appointmentDao.autoCancelUnpaidAppointments();
                    System.out.println("DEBUG: Auto-cancel executed after admin login.");
                } catch (Exception e) {
                    System.out.println("DEBUG: Auto-cancel failed: " + e.getMessage());
                    e.printStackTrace();
                }

                response.sendRedirect("AdminDashboard.jsp");
            } else {
                request.setAttribute("errorMessage", "Invalid email or password.");
                request.getRequestDispatcher("AdminLogin.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("AdminLogin.jsp").forward(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("AdminLogin.jsp");
    }
}
