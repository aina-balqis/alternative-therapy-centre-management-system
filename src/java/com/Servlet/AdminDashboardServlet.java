package com.Servlet;

import com.Dao.AdminDashboardDao;
import com.Model.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;


public class AdminDashboardServlet extends HttpServlet {
    private AdminDashboardDao dashboardDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        // Initialize the DAO with connection from DBConnection
        dashboardDAO = new AdminDashboardDao();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Get all dashboard data
            int todaysAppointments = dashboardDAO.getTodaysAppointments();
            int totalAppointments = dashboardDAO.getTotalAppointments();
            int totalClients = dashboardDAO.getTotalClients();
            int totalTherapists = dashboardDAO.getTotalTherapists();
            double monthlyRevenue = dashboardDAO.getMonthlyRevenue();
            int totalPackagesSold = dashboardDAO.getTotalPackagesSold();
            
            List<TherapistPerformance> topTherapists = dashboardDAO.getTopTherapists(5);
            List<PackagePerformance> popularPackages = dashboardDAO.getPopularPackages(5);
            Map<String, Integer> statusBreakdown = dashboardDAO.getAppointmentStatusBreakdown();
            Map<String, Double> revenueTrend = dashboardDAO.getMonthlyRevenueTrend();
            List<Appointment> refundRequests = dashboardDAO.getRefundRequests();

            // Set attributes for JSP
            request.setAttribute("todaysAppointments", todaysAppointments);
            request.setAttribute("totalAppointments", totalAppointments);
            request.setAttribute("totalClients", totalClients);
            request.setAttribute("totalTherapists", totalTherapists);
            request.setAttribute("monthlyRevenue", monthlyRevenue);
            request.setAttribute("totalPackagesSold", totalPackagesSold);
            request.setAttribute("topTherapists", topTherapists);
            request.setAttribute("popularPackages", popularPackages);
            request.setAttribute("statusBreakdown", statusBreakdown);
            request.setAttribute("revenueTrend", revenueTrend);
            request.setAttribute("refundRequests", refundRequests);

            // Forward to JSP
            RequestDispatcher dispatcher = request.getRequestDispatcher("/AdminDashboard.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Database error occurred", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}