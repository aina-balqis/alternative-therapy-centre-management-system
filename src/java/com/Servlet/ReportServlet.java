package com.Servlet;

import com.Dao.ReportDao;
import com.Model.*;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ReportServlet extends HttpServlet {
    private ReportDao reportDao;

    @Override
    public void init() {
        reportDao = new ReportDao();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        try {
            switch (action) {
                case "dashboard":
                    showDashboard(request, response);
                    break;
                case "appointment":
                    showAppointmentReport(request, response);
                    break;
                case "payment":
                    showPaymentReport(request, response);
                    break;
                case "feedback":
                    showFeedbackReport(request, response);
                    break;
                case "client":
                    showClientReport(request, response);
                    break;
                case "therapist":
                    showTherapistReport(request, response);
                    break;
                case "revenue":
                    showRevenueReport(request, response);
                    break;
                case "export":
                    exportReport(request, response);
                    break;
                default:
                    showDashboard(request, response);
            }
        } catch (SQLException | ServletException | IOException ex) {
            throw new ServletException(ex);
        }
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        // Get statistics for dashboard
        Map<String, Object> dailyStats = reportDao.getAppointmentStatistics("daily");
        Map<String, Object> weeklyStats = reportDao.getAppointmentStatistics("weekly");
        Map<String, Object> monthlyStats = reportDao.getAppointmentStatistics("monthly");
        
        // Get top packages
        List<Map<String, Object>> revenueByPackage = reportDao.getRevenueByPackage();
        
        // Get therapist performance
        List<Map<String, Object>> therapistPerformance = reportDao.getTherapistPerformance();
        
        // Get appointment status distribution
        Map<String, Integer> statusDistribution = reportDao.getAppointmentStatusDistribution();
        
        // Get client demographics
        Map<String, Object> clientDemographics = reportDao.getClientDemographics();
        
        request.setAttribute("dailyStats", dailyStats);
        request.setAttribute("weeklyStats", weeklyStats);
        request.setAttribute("monthlyStats", monthlyStats);
        request.setAttribute("revenueByPackage", revenueByPackage);
        request.setAttribute("therapistPerformance", therapistPerformance);
        request.setAttribute("statusDistribution", statusDistribution);
        request.setAttribute("clientDemographics", clientDemographics);
        
        request.getRequestDispatcher("reportDashboard.jsp").forward(request, response);
    }

    private void showAppointmentReport(HttpServletRequest request, HttpServletResponse response) 
        throws SQLException, ServletException, IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.sql.Date startDate = null;
        java.sql.Date endDate = null;
        String status = request.getParameter("status") != null ? request.getParameter("status") : "All";
        String packageName = request.getParameter("package") != null ? request.getParameter("package") : "All";
        String therapistName = request.getParameter("therapist") != null ? request.getParameter("therapist") : "All";
        
        try {
            if (request.getParameter("startDate") != null && !request.getParameter("startDate").isEmpty()) {
                Date utilStartDate = sdf.parse(request.getParameter("startDate"));
                startDate = new java.sql.Date(utilStartDate.getTime());
            }
            if (request.getParameter("endDate") != null && !request.getParameter("endDate").isEmpty()) {
                Date utilEndDate = sdf.parse(request.getParameter("endDate"));
                endDate = new java.sql.Date(utilEndDate.getTime());
            }
        } catch (Exception e) {
            throw new ServletException("Error parsing date", e);
        }
        
        List<Appointment> appointments = reportDao.getDetailedAppointmentReport(startDate, endDate, status);
        
        // Get distinct packages and therapists for filters
        List<String> packages = reportDao.getDistinctPackages();
        List<String> therapists = reportDao.getDistinctTherapists();
        
        request.setAttribute("appointments", appointments);
        request.setAttribute("startDate", startDate != null ? sdf.format(startDate) : "");
        request.setAttribute("endDate", endDate != null ? sdf.format(endDate) : "");
        request.setAttribute("selectedStatus", status);
        request.setAttribute("selectedPackage", packageName);
        request.setAttribute("selectedTherapist", therapistName);
        request.setAttribute("packages", packages);
        request.setAttribute("therapists", therapists);
        
        request.getRequestDispatcher("appointmentReport.jsp").forward(request, response);
    }

    private void showPaymentReport(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.sql.Date startDate = null;
        java.sql.Date endDate = null;
        String status = request.getParameter("status") != null ? request.getParameter("status") : "All";
        
        try {
            if (request.getParameter("startDate") != null && !request.getParameter("startDate").isEmpty()) {
                Date utilStartDate = sdf.parse(request.getParameter("startDate"));
                startDate = new java.sql.Date(utilStartDate.getTime());
            }
            if (request.getParameter("endDate") != null && !request.getParameter("endDate").isEmpty()) {
                Date utilEndDate = sdf.parse(request.getParameter("endDate"));
                endDate = new java.sql.Date(utilEndDate.getTime());
            }
        } catch (Exception e) {
            throw new ServletException("Error parsing date", e);
        }
        
        // Default to last 30 days if no dates provided
        if (startDate == null || endDate == null) {
            Calendar cal = Calendar.getInstance();
            endDate = new java.sql.Date(cal.getTime().getTime());
            cal.add(Calendar.DAY_OF_MONTH, -30);
            startDate = new java.sql.Date(cal.getTime().getTime());
        }
        
        List<Payment> payments = reportDao.getPaymentReport(startDate, endDate, status);
        
        double totalAmount = 0;
        for (Payment payment : payments) {
            totalAmount += payment.getAmount();
        }
        
        request.setAttribute("payments", payments);
        request.setAttribute("totalAmount", totalAmount);
        request.setAttribute("startDate", sdf.format(startDate));
        request.setAttribute("endDate", sdf.format(endDate));
        request.setAttribute("selectedStatus", status);
        
        request.getRequestDispatcher("paymentReport.jsp").forward(request, response);
    }

    private void showFeedbackReport(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        Map<String, Object> feedbackAnalysis = reportDao.getFeedbackAnalysis();
        request.setAttribute("feedbackAnalysis", feedbackAnalysis);
        request.getRequestDispatcher("feedbackReport.jsp").forward(request, response);
    }

   private void showClientReport(HttpServletRequest request, HttpServletResponse response) 
        throws SQLException, ServletException, IOException {
    Map<String, Object> clientStats = reportDao.getClientStatistics();
    Map<String, Object> clientDemographics = reportDao.getClientDemographics();
    
    request.setAttribute("clientStats", clientStats);
    request.setAttribute("clientDemographics", clientDemographics);
    request.getRequestDispatcher("clientReport.jsp").forward(request, response);
}

    private void showTherapistReport(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        List<Map<String, Object>> therapistStats = reportDao.getTherapistStatistics();
        request.setAttribute("therapistStats", therapistStats);
        request.getRequestDispatcher("therapistReport.jsp").forward(request, response);
    }

    private void showRevenueReport(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        if (request.getParameter("year") != null) {
            year = Integer.parseInt(request.getParameter("year"));
        }
        
        List<Map<String, Object>> monthlyRevenue = reportDao.getMonthlyRevenueTrend(year);
        List<Integer> availableYears = reportDao.getAvailableReportYears();
        
        request.setAttribute("monthlyRevenue", monthlyRevenue);
        request.setAttribute("selectedYear", year);
        request.setAttribute("availableYears", availableYears);
        request.getRequestDispatcher("revenueReport.jsp").forward(request, response);
    }

    private void exportReport(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        String reportType = request.getParameter("reportType");
        String format = request.getParameter("format");
        
        // Here you would implement the export logic using libraries like:
        // - Apache POI for Excel
        // - iText for PDF
        // - JasperReports for both
        
        // For now, just redirect back to the report
        response.sendRedirect(request.getContextPath() + "/ReportServlet?action=" + reportType);
    }
    
}