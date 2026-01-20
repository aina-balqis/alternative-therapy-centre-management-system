package com.Servlet;

import com.Dao.AdminDashboardDao;
import com.Dao.DBConnection;
import com.Dao.AdminDashboardDao;
import com.Model.Appointment;
import com.Model.Payment;
import com.Dao.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;


public class DashboardClientServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer clientId = (Integer) session.getAttribute("clientId");
        if (clientId == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            AdminDashboardDao dao = new AdminDashboardDao(con);

            int upcomingCount = dao.countUpcomingAppointments(clientId);
            int completedCount = dao.countCompletedAppointments(clientId);
            int pendingPaymentCount = dao.countPendingPayment(clientId);
            int pendingFeedbackCount = dao.countPendingFeedback(clientId);

            List<Appointment> upcomingList = dao.getUpcomingAppointments(clientId);
            List<Appointment> historyList = dao.getAppointmentHistory(clientId);
            List<Payment> paymentList = dao.getPaymentHistory(clientId);

            request.setAttribute("upcomingCount", upcomingCount);
            request.setAttribute("completedCount", completedCount);
            request.setAttribute("pendingPaymentCount", pendingPaymentCount);
            request.setAttribute("pendingFeedbackCount", pendingFeedbackCount);

            request.setAttribute("upcomingList", upcomingList);
            request.setAttribute("historyList", historyList);
            request.setAttribute("paymentList", paymentList);

            request.getRequestDispatcher("ClientDashboard.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}
