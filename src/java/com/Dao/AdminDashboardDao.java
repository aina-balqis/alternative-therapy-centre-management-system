package com.Dao;

import com.Model.*;
import java.sql.*;
import java.util.*;
import java.sql.Date;

public class AdminDashboardDao {
    private Connection conn;

    public AdminDashboardDao() {
        conn = DBConnection.getConnection();
    }
    
    public AdminDashboardDao(Connection conn) {
        this.conn = conn;
    }

    // Get today's appointments count
    public int getTodaysAppointments() throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                 "SELECT COUNT(*) FROM appointment WHERE DATE(appointment_date) = CURDATE()");
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // Get total appointments count
    public int getTotalAppointments() throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                 "SELECT COUNT(*) FROM appointment");
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // Get total clients count
    public int getTotalClients() throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                 "SELECT COUNT(*) FROM client");
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // Get total therapists count
    public int getTotalTherapists() throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                 "SELECT COUNT(*) FROM therapist");
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // Get monthly revenue
    public double getMonthlyRevenue() throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                 "SELECT COALESCE(SUM(amount), 0) FROM payment " +
                 "WHERE MONTH(payment_date) = MONTH(CURRENT_DATE()) " +
                 "AND YEAR(payment_date) = YEAR(CURRENT_DATE())");
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0;
        }
    }

    // Get total packages sold
    public int getTotalPackagesSold() throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                 "SELECT COUNT(*) FROM appointment WHERE package_id IS NOT NULL");
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // Get top therapists
    public List<TherapistPerformance> getTopTherapists(int limit) throws SQLException {
        List<TherapistPerformance> therapists = new ArrayList<>();
        String query = "SELECT t.therapist_ID, t.therapist_fullname, " +
                       "COUNT(a.appointment_id) as completed_count, " +
                       "COALESCE(AVG(f.rating), 0) as avg_rating " +
                       "FROM therapist t " +
                       "LEFT JOIN appointment a ON t.therapist_ID = a.therapist_id AND a.appointment_status = 'Completed' " +
                       "LEFT JOIN feedback f ON a.appointment_id = f.appointment_id " +
                       "GROUP BY t.therapist_ID, t.therapist_fullname " +
                       "ORDER BY completed_count DESC, avg_rating DESC " +
                       "LIMIT ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TherapistPerformance tp = new TherapistPerformance();
                    tp.setTherapistId(rs.getInt("therapist_ID"));
                    tp.setTherapistName(rs.getString("therapist_fullname"));
                    tp.setCompletedAppointments(rs.getInt("completed_count"));
                    tp.setAverageRating(rs.getDouble("avg_rating"));
                    therapists.add(tp);
                }
            }
        }
        return therapists;
    }

    // Get popular packages
    public List<PackagePerformance> getPopularPackages(int limit) throws SQLException {
        List<PackagePerformance> packages = new ArrayList<>();
        String query = "SELECT p.package_ID, p.package_name, " +
                       "COUNT(a.appointment_id) as times_booked, " +
                       "COALESCE(SUM(py.amount), 0) as revenue " +
                       "FROM therapy_package p " +
                       "LEFT JOIN appointment a ON p.package_ID = a.package_id " +
                       "LEFT JOIN payment py ON a.appointment_id = py.appointment_id " +
                       "WHERE p.is_active = 1 " +
                       "GROUP BY p.package_ID, p.package_name " +
                       "ORDER BY times_booked DESC " +
                       "LIMIT ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PackagePerformance pp = new PackagePerformance();
                    pp.setPackageId(rs.getInt("package_ID"));
                    pp.setPackageName(rs.getString("package_name"));
                    pp.setTimesBooked(rs.getInt("times_booked"));
                    pp.setRevenue(rs.getDouble("revenue"));
                    packages.add(pp);
                }
            }
        }
        return packages;
    }

    // Get appointment status breakdown
    public Map<String, Integer> getAppointmentStatusBreakdown() throws SQLException {
        Map<String, Integer> statusMap = new LinkedHashMap<>();
        String query = "SELECT appointment_status, COUNT(*) as count FROM appointment GROUP BY appointment_status";
        
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                statusMap.put(rs.getString("appointment_status"), rs.getInt("count"));
            }
        }
        return statusMap;
    }

    // Get monthly revenue trend
    public Map<String, Double> getMonthlyRevenueTrend() throws SQLException {
        Map<String, Double> revenueTrend = new LinkedHashMap<>();
        String query = "SELECT DATE_FORMAT(payment_date, '%Y-%m') as month, " +
                       "COALESCE(SUM(amount), 0) as revenue " +
                       "FROM payment " +
                       "WHERE payment_date >= DATE_SUB(CURRENT_DATE(), INTERVAL 6 MONTH) " +
                       "GROUP BY DATE_FORMAT(payment_date, '%Y-%m') " +
                       "ORDER BY month";
        
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                revenueTrend.put(rs.getString("month"), rs.getDouble("revenue"));
            }
        }
        return revenueTrend;
    }

    // Get refund requests
    public List<Appointment> getRefundRequests() throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String query = "SELECT a.*, c.client_fullname, t.therapist_fullname, p.package_name " +
                       "FROM appointment a " +
                       "JOIN client c ON a.client_id = c.client_ID " +
                       "LEFT JOIN therapist t ON a.therapist_id = t.therapist_ID " +
                       "LEFT JOIN therapy_package p ON a.package_id = p.package_ID " +
                       "WHERE a.appointment_status = 'Cancelled (Refund Pending)'";
        
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setAppointmentId(rs.getInt("appointment_id"));
                appointment.setClientId(rs.getInt("client_id"));
                appointment.setTherapistId(rs.getInt("therapist_id"));
                appointment.setPackageId(rs.getInt("package_id"));
                
                Date appointmentDate = rs.getDate("appointment_date");
                appointment.setAppointmentDate(appointmentDate != null ? appointmentDate.toString() : "");
                
                Time appointmentTime = rs.getTime("appointment_time");
                appointment.setAppointmentTime(appointmentTime != null ? appointmentTime.toString() : "");
                
                appointment.setAppointmentStatus(rs.getString("appointment_status"));
                appointment.setNotes(rs.getString("notes"));
                appointment.setClientName(rs.getString("client_fullname"));
                appointment.setTherapistName(rs.getString("therapist_fullname"));
                appointment.setPackageName(rs.getString("package_name"));
                
                appointments.add(appointment);
            }
        }
        return appointments;
    }
    
  

    public int countUpcomingAppointments(int clientId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointment WHERE client_id = ? AND appointment_status = 'Confirmed'";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, clientId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt(1);
        return 0;
    }

    public int countCompletedAppointments(int clientId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointment WHERE client_id = ? AND appointment_status = 'Completed'";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, clientId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt(1);
        return 0;
    }

    public int countPendingPayment(int clientId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointment a LEFT JOIN payment p ON a.appointment_id = p.appointment_id "
                   + "WHERE a.client_id = ? AND a.appointment_status = 'Pending' "
                   + "AND (p.payment_id IS NULL OR p.payment_status != 'Completed')";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, clientId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt(1);
        return 0;
    }

    public int countPendingFeedback(int clientId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointment a LEFT JOIN feedback f ON a.appointment_id = f.appointment_id "
                   + "WHERE a.client_id = ? AND a.appointment_status = 'Completed' AND f.feedback_id IS NULL";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, clientId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt(1);
        return 0;
    }

    public List<Appointment> getUpcomingAppointments(int clientId) throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointment WHERE client_id = ? AND appointment_status = 'Confirmed'";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, clientId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Appointment a = new Appointment();
            a.setAppointmentId(rs.getInt("appointment_id"));
            a.setAppointmentDate(rs.getString("appointment_date"));
            a.setAppointmentTime(rs.getString("appointment_time"));
            a.setAppointmentStatus(rs.getString("appointment_status"));
            a.setNotes(rs.getString("notes"));
            // Tambah field lain jika perlu
            list.add(a);
        }
        return list;
    }

    public List<Appointment> getAppointmentHistory(int clientId) throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointment WHERE client_id = ? AND appointment_status = 'Completed'";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, clientId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Appointment a = new Appointment();
            a.setAppointmentId(rs.getInt("appointment_id"));
            a.setAppointmentDate(rs.getString("appointment_date"));
            a.setAppointmentTime(rs.getString("appointment_time"));
            a.setAppointmentStatus(rs.getString("appointment_status"));
            a.setNotes(rs.getString("notes"));
            list.add(a);
        }
        return list;
    }

    public List<Payment> getPaymentHistory(int clientId) throws SQLException {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.*, a.appointment_date, a.appointment_time FROM payment p "
                   + "INNER JOIN appointment a ON p.appointment_id = a.appointment_id "
                   + "WHERE a.client_id = ? AND a.appointment_status = 'Completed'";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, clientId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Payment p = new Payment();
            p.setPaymentId(rs.getInt("payment_id"));
            p.setAppointmentId(rs.getInt("appointment_id"));
            p.setAmount(rs.getDouble("amount"));
            p.setPaymentDate(rs.getTimestamp("payment_date"));
            p.setTransactionId(rs.getString("transaction_id"));
            p.setPaymentMethod(rs.getString("payment_method"));
            p.setPaymentStatus(rs.getString("payment_status"));
            // Add appointment info if needed
            list.add(p);
        }
        return list;
    }
}

