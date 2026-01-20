package com.Servlet;

import com.Dao.AppointmentDao;
import com.Dao.DBConnection;
import com.Dao.PaymentDao;
import com.Model.Payment;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;


public class PaymentCallbackServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();
        Connection conn = null;
        
        try {
            // Get all callback parameters from ToyyibPay
            String status = request.getParameter("status_id");
            String billCode = request.getParameter("billcode");
            String orderId = request.getParameter("order_id");
            String transactionId = request.getParameter("transaction_id");
            String amount = request.getParameter("amount");
            String msg = request.getParameter("msg");
            
            // Debug logging
            System.out.println("PaymentCallbackServlet received: " + 
                "status_id=" + status + 
                ", billcode=" + billCode + 
                ", order_id=" + orderId + 
                ", transaction_id=" + transactionId + 
                ", amount=" + amount);

            // Validate required parameters
            if (status == null || orderId == null || !orderId.startsWith("APPT-")) {
                throw new ServletException("Invalid callback parameters");
            }

            int appointmentId = Integer.parseInt(orderId.substring(5));
            
            try {
                conn = DBConnection.getConnection();
                conn.setAutoCommit(false); // Start transaction
                
                PaymentDao paymentDao = new PaymentDao(conn);
                AppointmentDao appointmentDao = new AppointmentDao(conn);

                // Check if payment already exists for this appointment
                Payment existingPayment = paymentDao.getPaymentByAppointmentId(appointmentId);
                
                if (existingPayment == null || 
                    Payment.STATUS_PENDING.equals(existingPayment.getPaymentStatus())) {
                    
                    // Create new payment record
                    Payment payment = new Payment();
                    payment.setAppointmentId(appointmentId);
                    payment.setAmount(Double.parseDouble(amount));
                    payment.setPaymentDate(new Timestamp(new Date().getTime()));
                    payment.setTransactionId(transactionId != null ? transactionId : billCode);
                    payment.setPaymentMethod("ToyyibPay");
                    
                    // Set payment status based on callback
                    if ("1".equals(status)) {
                        payment.setPaymentStatus(Payment.STATUS_PAID);
                    } else {
                        payment.setPaymentStatus(Payment.STATUS_FAILED);
                    }

                    // Save payment to database
                    boolean paymentSaved = paymentDao.insertPayment(payment);
                    
                    if (!paymentSaved) {
                        conn.rollback();
                        throw new ServletException("Failed to save payment record");
                    }
                    
                    // Update appointment status if payment was successful
                    if (Payment.STATUS_PAID.equals(payment.getPaymentStatus())) {
                        boolean statusUpdated = appointmentDao.updateAppointmentStatus(
                            appointmentId, "Confirmed");
                        
                        if (!statusUpdated) {
                            conn.rollback();
                            throw new ServletException("Failed to update appointment status");
                        }
                    }
                    
                    conn.commit(); // Commit transaction
                    
                    // Prepare success response
                    jsonResponse.put("status", "success");
                    jsonResponse.put("paymentStatus", payment.getPaymentStatus());
                    jsonResponse.put("appointmentId", appointmentId);
                    jsonResponse.put("transactionId", payment.getTransactionId());
                    
                    System.out.println("Successfully processed payment for appointment: " + appointmentId);
                } else {
                    // Payment already processed
                    jsonResponse.put("status", "success");
                    jsonResponse.put("message", "Payment already processed");
                    jsonResponse.put("existingStatus", existingPayment.getPaymentStatus());
                }
                
            } catch (Exception e) {
                if (conn != null) {
                    conn.rollback();
                }
                throw e;
            } finally {
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true);
                        conn.close();
                    } catch (Exception e) {
                        System.err.println("Error closing connection: " + e.getMessage());
                    }
                }
            }
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Invalid appointment ID format");
            System.err.println("NumberFormatException: " + e.getMessage());
        } catch (ServletException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("status", "error");
            jsonResponse.put("message", e.getMessage());
            System.err.println("ServletException: " + e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Internal server error");
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
        
        out.print(jsonResponse.toString());
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}