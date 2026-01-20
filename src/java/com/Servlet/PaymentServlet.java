package com.Servlet;

import com.Dao.*;
import com.Model.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class PaymentServlet extends HttpServlet {

    private PaymentDao paymentDao;
    private AppointmentDao appointmentDao;

    @Override
    public void init() throws ServletException {
        paymentDao = new PaymentDao();
        appointmentDao = new AppointmentDao();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        switch (action) {
            case "initiatePayment":
                initiatePayment(request, response);
                break;
            case "viewHistory":
                viewPaymentHistory(request, response);
                break;
            case " getPendingAppointments" :
            {
                try {
                    getPendingAppointments(request, response);
                } catch (SQLException ex) {
                    Logger.getLogger(PaymentServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
                break;

            case "checkStatus":
                checkPaymentStatus(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    // 1. Initiate Payment - Redirect ke payment-initiate.jsp
    private void initiatePayment(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
            System.out.println("Initiating payment for appointment ID: " + appointmentId); // Debug log

            // Fetch data from database
            Appointment appointment = appointmentDao.getAppointmentById(appointmentId);
            if (appointment == null) {
                throw new Exception("Appointment not found");
            }

            TherapyPackage therapyPackage = new TherapyPackageDao().getPackageById(appointment.getPackageId());
            if (therapyPackage == null) {
                throw new Exception("Therapy package not found");
            }

            Therapist therapist = new TherapistDao().getTherapistById(appointment.getTherapistId());
            if (therapist == null) {
                throw new Exception("Therapist not found");
            }

            Client client = new ClientDao().getClientById(appointment.getClientId());
            if (client == null) {
                throw new Exception("Client not found");
            }

            // Debug logs
            System.out.println("Fetched data - Appointment: " + appointment.getAppointmentId());
            System.out.println("Package: " + therapyPackage.getPackage_name());
            System.out.println("Therapist: " + therapist.getTherapist_fullname());
            System.out.println("Client: " + client.getClient_fullname());

            // Set attributes for JSP
            request.setAttribute("appointment", appointment);
            request.setAttribute("package", therapyPackage);
            request.setAttribute("therapist", therapist);
            request.setAttribute("client", client);

            // Forward to payment-initiate.jsp
            System.out.println("Forwarding to payment-initiate.jsp"); // Debug log
            request.getRequestDispatcher("payment-initiate.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid appointment ID");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to initiate payment: " + e.getMessage());
        }
    }

    // 3. Check Payment Status (AJAX/API call)
    private void checkPaymentStatus(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
            Payment payment = paymentDao.getPaymentByAppointmentId(appointmentId);

            if (payment != null) {
                response.setContentType("application/json");
                response.getWriter().print(
                        String.format("{\"status\":\"%s\",\"transactionId\":\"%s\"}",
                                payment.getPaymentStatus(),
                                payment.getTransactionId())
                );
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Payment not found");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error checking payment status");
        }
    }

    private void viewPaymentHistory(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        HttpSession session = request.getSession();
    Client client = (Client) session.getAttribute("client");

    if (client == null) {
        response.sendRedirect("Login.jsp");
        return;
    }

    try {
        AppointmentDao appointmentDao = new AppointmentDao();
        PaymentDao paymentDao = new PaymentDao();

        // Dapatkan appointment yang pending
        List<Appointment> pendingAppointments = appointmentDao.getPendingAppointmentsForClient(client.getClient_ID());

        // Dapatkan payment history
        List<Payment> payments = paymentDao.getPaymentHistoryForClient(client.getClient_ID());

        request.setAttribute("pendingAppointments", pendingAppointments);
        request.setAttribute("payments", payments);

        RequestDispatcher dispatcher = request.getRequestDispatcher("paymentHistory.jsp");
        dispatcher.forward(request, response);

    } catch (Exception e) {
        e.printStackTrace();
        request.setAttribute("errorMessage", "Error retrieving payment history.");
        RequestDispatcher dispatcher = request.getRequestDispatcher("paymentHistory.jsp");
        dispatcher.forward(request, response);
    }
}

// In PaymentServlet.java
protected void getPendingAppointments(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, SQLException {

    HttpSession session = request.getSession();
    Client client = (Client) session.getAttribute("client");

    if (client == null) {
        response.sendRedirect("Login.jsp");
        return;
    }

    AppointmentDao appointmentDao = new AppointmentDao();
    List<Appointment> pendingAppointments = appointmentDao.getPendingAppointmentsForClient(client.getClient_ID());

    // Debug log
    System.out.println("DEBUG: Pending appointments size = " + pendingAppointments.size());

    request.setAttribute("pendingAppointments", pendingAppointments);
    RequestDispatcher dispatcher = request.getRequestDispatcher("PaymentSection.jsp");
    dispatcher.forward(request, response);
}

}
