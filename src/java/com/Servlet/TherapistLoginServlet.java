package com.Servlet;

import com.Dao.TherapistDao;
import com.Dao.AppointmentDao;
import com.Model.Therapist;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class TherapistLoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
        
        // Dapatkan email dan password dari form login
        String therapist_email = request.getParameter("therapist_email");
        String therapist_password = request.getParameter("therapist_password");

        try {
            // Cipta objek TherapistDao
            TherapistDao therapistDao = new TherapistDao();

            // Semak kredensial login
            Therapist therapist = therapistDao.validateLogin(therapist_email, therapist_password);

            if (therapist != null) {
                // Jika login berjaya, simpan dalam session dan redirect ke dashboard
                HttpSession session = request.getSession();
                session.setAttribute("therapist", therapist);
                session.setAttribute("therapistId", therapist.getTherapist_ID()); 

                // ✅ Auto-cancel unpaid appointments selepas login
                try {
                    AppointmentDao appointmentDao = new AppointmentDao();
                    appointmentDao.autoCancelUnpaidAppointments();
                    System.out.println("DEBUG: Auto-cancel executed after therapist login.");
                } catch (Exception e) {
                    System.out.println("DEBUG: Auto-cancel failed (therapist): " + e.getMessage());
                    e.printStackTrace();
                }

                response.sendRedirect("TherapistDashboardServlet"); // Gantikan dengan halaman dashboard sebenar
            } else {
                // Jika gagal login, paparkan mesej ralat
                request.setAttribute("errorMessage", "Invalid email or password.");
                request.getRequestDispatcher("TherapistLogin.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while processing your login.");
            request.getRequestDispatcher("TherapistLogin.jsp").forward(request, response);
        }
    }
}
