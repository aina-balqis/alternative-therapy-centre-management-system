package com.Servlet;

import com.Dao.TherapistDashboardDao;
import com.Dao.DBConnection;
import com.Model.Therapist;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class TherapistDashboardServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Therapist therapist = (Therapist) request.getSession().getAttribute("therapist");
        if (therapist == null) {
            response.sendRedirect("TherapistLogin.jsp");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            TherapistDashboardDao dao = new TherapistDashboardDao(conn);
            int therapistId = therapist.getTherapist_ID();

            request.setAttribute("todayAppointments", dao.getAppointmentsToday(therapistId));
            request.setAttribute("weekAppointments", dao.getAppointmentsThisWeek(therapistId));
            request.setAttribute("monthAppointments", dao.getAppointmentsThisMonth(therapistId));
            request.setAttribute("completedAppointments", dao.getCompletedAppointments(therapistId));
            request.setAttribute("upcomingAppointments", dao.getUpcomingAppointments(therapistId));
            request.setAttribute("totalRevenue", dao.getTotalRevenue(therapistId));
            request.setAttribute("packageStats", dao.getPackageStats(therapistId));

            RequestDispatcher dispatcher = request.getRequestDispatcher("TherapistDashboard.jsp");
            dispatcher.forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
