package com.Dao;

import com.Model.Payment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDao {
    private Connection conn;

    public PaymentDao() {
        this.conn = DBConnection.getConnection();
    }
    
    public PaymentDao(Connection conn) {
        this.conn = conn;
    }
    
    public boolean insertPayment(Payment payment) throws SQLException {
        String sql = "INSERT INTO payment (appointment_id, amount, payment_date, transaction_id, "
                   + "payment_method, payment_status) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, payment.getAppointmentId());
            stmt.setDouble(2, payment.getAmount());
            stmt.setTimestamp(3, payment.getPaymentDate());
            stmt.setString(4, payment.getTransactionId());
            stmt.setString(5, payment.getPaymentMethod());
            stmt.setString(6, payment.getPaymentStatus());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating payment failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    payment.setPaymentId(generatedKeys.getInt(1));
                    return true;
                } else {
                    throw new SQLException("Creating payment failed, no ID obtained.");
                }
            }
        }
    }

    public Payment getPaymentByAppointmentId(int appointmentId) throws SQLException {
        String sql = "SELECT * FROM payment WHERE appointment_id = ? ORDER BY payment_date DESC LIMIT 1";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointmentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapPaymentFromResultSet(rs);
                }
            }
        }
        return null;
    }
    
    public Payment getPaymentByTransactionId(String transactionId) throws SQLException {
        String sql = "SELECT * FROM payment WHERE transaction_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, transactionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapPaymentFromResultSet(rs);
                }
            }
        }
        return null;
    }
    
    // New method to check if appointment has been paid for
    public boolean isAppointmentPaid(int appointmentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM payment WHERE appointment_id = ? AND payment_status = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointmentId);
            stmt.setString(2, Payment.STATUS_PAID);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    // New method to get all payments for an appointment (history)
    public List<Payment> getPaymentHistoryForAppointment(int appointmentId) throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payment WHERE appointment_id = ? ORDER BY payment_date DESC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointmentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapPaymentFromResultSet(rs));
                }
            }
        }
        return payments;
    }
    
    private Payment mapPaymentFromResultSet(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("payment_id"));
        payment.setAppointmentId(rs.getInt("appointment_id"));
        payment.setAmount(rs.getDouble("amount"));
        payment.setPaymentDate(rs.getTimestamp("payment_date"));
        payment.setTransactionId(rs.getString("transaction_id"));
        payment.setPaymentMethod(rs.getString("payment_method"));
        payment.setPaymentStatus(rs.getString("payment_status"));
        return payment;
    }
    
    public List<Payment> getPaymentHistoryForClient(int clientId) throws SQLException {
    List<Payment> payments = new ArrayList<>();
    String sql = "SELECT p.* FROM payment p " +
                 "JOIN appointment a ON p.appointment_id = a.appointment_id " +
                 "WHERE a.client_id = ? ORDER BY p.payment_date DESC";
    
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, clientId);
        
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                payments.add(mapPaymentFromResultSet(rs));
            }
        }
    }
    return payments;
}
   

    // Get payment history for a client
    public List<Payment> getPaymentHistory(int clientId) throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.*, a.appointment_date, tp.package_name " +
                     "FROM payment p " +
                     "JOIN appointment a ON p.appointment_id = a.appointment_id " +
                     "JOIN therapy_package tp ON a.package_id = tp.package_ID " +
                     "WHERE a.client_id = ? " +
                     "ORDER BY p.payment_date DESC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Payment payment = new Payment();
                payment.setPaymentId(rs.getInt("payment_id"));
                payment.setAppointmentId(rs.getInt("appointment_id"));
                 payment.setAmount(rs.getDouble("amount"));
                payment.setPaymentDate(rs.getTimestamp("payment_date"));
                payment.setTransactionId(rs.getString("transaction_id"));
                payment.setPaymentMethod(rs.getString("payment_method"));
                payment.setPaymentStatus(rs.getString("payment_status"));
               
                payments.add(payment);
            }
        }
        return payments;
    }
    
    
}
