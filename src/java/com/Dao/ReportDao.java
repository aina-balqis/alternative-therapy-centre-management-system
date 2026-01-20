package com.Dao;

import com.Model.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class ReportDao {
    private Connection conn;

    public ReportDao() {
        conn = DBConnection.getConnection();
    }

    // Get appointment statistics
    public Map<String, Object> getAppointmentStatistics(String period) throws SQLException {
        Map<String, Object> stats = new HashMap<>();
        String sql = "";
        
        switch (period) {
            case "daily":
                sql = "SELECT COUNT(*) as count, SUM(p.package_price) as revenue " +
                      "FROM appointment a " +
                      "JOIN therapy_package p ON a.package_id = p.package_ID " +
                      "WHERE DATE(a.appointment_date) = CURDATE() AND a.appointment_status = 'Completed'";
                break;
            case "weekly":
                sql = "SELECT COUNT(*) as count, SUM(p.package_price) as revenue " +
                      "FROM appointment a " +
                      "JOIN therapy_package p ON a.package_id = p.package_ID " +
                      "WHERE YEARWEEK(a.appointment_date) = YEARWEEK(CURDATE()) AND a.appointment_status = 'Completed'";
                break;
            case "monthly":
                sql = "SELECT COUNT(*) as count, SUM(p.package_price) as revenue " +
                      "FROM appointment a " +
                      "JOIN therapy_package p ON a.package_id = p.package_ID " +
                      "WHERE MONTH(a.appointment_date) = MONTH(CURDATE()) AND YEAR(a.appointment_date) = YEAR(CURDATE()) " +
                      "AND a.appointment_status = 'Completed'";
                break;
            case "yearly":
                sql = "SELECT COUNT(*) as count, SUM(p.package_price) as revenue " +
                      "FROM appointment a " +
                      "JOIN therapy_package p ON a.package_id = p.package_ID " +
                      "WHERE YEAR(a.appointment_date) = YEAR(CURDATE()) AND a.appointment_status = 'Completed'";
                break;
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                stats.put("appointmentCount", rs.getInt("count"));
                stats.put("totalRevenue", rs.getDouble("revenue"));
            }
        }
        return stats;
    }

    // Get revenue by package
    public List<Map<String, Object>> getRevenueByPackage() throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT p.package_name, COUNT(*) as appointment_count, SUM(p.package_price) as total_revenue " +
                     "FROM appointment a " +
                     "JOIN therapy_package p ON a.package_id = p.package_ID " +
                     "WHERE a.appointment_status = 'Completed' " +
                     "GROUP BY p.package_name " +
                     "ORDER BY total_revenue DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("packageName", rs.getString("package_name"));
                row.put("appointmentCount", rs.getInt("appointment_count"));
                row.put("totalRevenue", rs.getDouble("total_revenue"));
                result.add(row);
            }
        }
        return result;
    }

    // Get therapist performance
    public List<Map<String, Object>> getTherapistPerformance() throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT t.therapist_fullname, COUNT(*) as appointment_count, " +
                     "AVG(f.rating) as avg_rating, SUM(p.package_price) as total_revenue " +
                     "FROM appointment a " +
                     "JOIN therapist t ON a.therapist_id = t.therapist_ID " +
                     "JOIN therapy_package p ON a.package_id = p.package_ID " +
                     "LEFT JOIN feedback f ON a.appointment_id = f.appointment_id " +
                     "WHERE a.appointment_status = 'Completed' " +
                     "GROUP BY t.therapist_fullname " +
                     "ORDER BY total_revenue DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("therapistName", rs.getString("therapist_fullname"));
                row.put("appointmentCount", rs.getInt("appointment_count"));
                row.put("averageRating", rs.getDouble("avg_rating"));
                row.put("totalRevenue", rs.getDouble("total_revenue"));
                result.add(row);
            }
        }
        return result;
    }

    // Get appointment status distribution
    public Map<String, Integer> getAppointmentStatusDistribution() throws SQLException {
        Map<String, Integer> result = new HashMap<>();
        String sql = "SELECT appointment_status, COUNT(*) as count FROM appointment GROUP BY appointment_status";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.put(rs.getString("appointment_status"), rs.getInt("count"));
            }
        }
        return result;
    }

    // Get monthly revenue trend
    public List<Map<String, Object>> getMonthlyRevenueTrend(int year) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT MONTH(appointment_date) as month, SUM(p.package_price) as revenue " +
                     "FROM appointment a " +
                     "JOIN therapy_package p ON a.package_id = p.package_ID " +
                     "WHERE YEAR(appointment_date) = ? AND a.appointment_status = 'Completed' " +
                     "GROUP BY MONTH(appointment_date) " +
                     "ORDER BY MONTH(appointment_date)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("month", rs.getInt("month"));
                row.put("revenue", rs.getDouble("revenue"));
                result.add(row);
            }
        }
        return result;
    }

    // Get client demographics
    public Map<String, Object> getClientDemographics() throws SQLException {
        Map<String, Object> result = new HashMap<>();
        
        // Gender distribution
        String genderSql = "SELECT gender, COUNT(*) as count FROM client GROUP BY gender";
        Map<String, Integer> genderData = new HashMap<>();
        try (PreparedStatement stmt = conn.prepareStatement(genderSql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                genderData.put(rs.getString("gender"), rs.getInt("count"));
            }
        }
        result.put("genderDistribution", genderData);
        
        // Age distribution
        String ageSql = "SELECT " +
                        "SUM(CASE WHEN TIMESTAMPDIFF(YEAR, client_dob, CURDATE()) BETWEEN 18 AND 25 THEN 1 ELSE 0 END) as age18_25, " +
                        "SUM(CASE WHEN TIMESTAMPDIFF(YEAR, client_dob, CURDATE()) BETWEEN 26 AND 35 THEN 1 ELSE 0 END) as age26_35, " +
                        "SUM(CASE WHEN TIMESTAMPDIFF(YEAR, client_dob, CURDATE()) BETWEEN 36 AND 50 THEN 1 ELSE 0 END) as age36_50, " +
                        "SUM(CASE WHEN TIMESTAMPDIFF(YEAR, client_dob, CURDATE()) > 50 THEN 1 ELSE 0 END) as age50plus " +
                        "FROM client";
        try (PreparedStatement stmt = conn.prepareStatement(ageSql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Map<String, Integer> ageData = new HashMap<>();
                ageData.put("18-25", rs.getInt("age18_25"));
                ageData.put("26-35", rs.getInt("age26_35"));
                ageData.put("36-50", rs.getInt("age36_50"));
                ageData.put("50+", rs.getInt("age50plus"));
                result.put("ageDistribution", ageData);
            }
        }
        
        return result;
    }

    // Get detailed appointment report
    // Alternatif lebih efisien
public List<Appointment> getDetailedAppointmentReport(Date startDate, Date endDate, String status) throws SQLException {
    List<Appointment> appointments = new ArrayList<>();
    
    String sql = "SELECT a.*, c.client_fullname, t.therapist_fullname, p.package_name " +
                 "FROM appointment a " +
                 "JOIN client c ON a.client_id = c.client_ID " +
                 "JOIN therapist t ON a.therapist_id = t.therapist_ID " +
                 "JOIN therapy_package p ON a.package_id = p.package_ID " +
                 "WHERE 1=1";
    
    if (startDate != null) {
        sql += " AND a.appointment_date >= ?";
    }
    if (endDate != null) {
        sql += " AND a.appointment_date <= ?";
    }
    if (status != null && !status.equals("All")) {
        sql += " AND a.appointment_status = ?";
    }
    sql += " ORDER BY a.appointment_date DESC, a.appointment_time ASC";
    
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        int paramIndex = 1;
        if (startDate != null) {
            stmt.setDate(paramIndex++, new java.sql.Date(startDate.getTime()));
        }
        if (endDate != null) {
            stmt.setDate(paramIndex++, new java.sql.Date(endDate.getTime()));
        }
        if (status != null && !status.equals("All")) {
            stmt.setString(paramIndex++, status);
        }
        
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Appointment appt = new Appointment();
            appt.setAppointmentId(rs.getInt("appointment_id"));
            appt.setClientId(rs.getInt("client_id"));
            appt.setTherapistId(rs.getInt("therapist_id"));
            appt.setPackageId(rs.getInt("package_id"));
            appt.setAppointmentDate(rs.getString("appointment_date"));
            appt.setAppointmentTime(rs.getString("appointment_time"));
            appt.setAppointmentStatus(rs.getString("appointment_status"));
            appt.setClientName(rs.getString("client_fullname"));
            appt.setTherapistName(rs.getString("therapist_fullname"));
            appt.setPackageName(rs.getString("package_name"));
            appointments.add(appt);
        }
    }
    
    return appointments;
}
    // Get payment report
    public List<Payment> getPaymentReport(Date startDate, Date endDate, String status) throws SQLException {
        PaymentDao paymentDao = new PaymentDao();
        String sql = "SELECT p.*, a.appointment_date, c.client_fullname " +
                     "FROM payment p " +
                     "JOIN appointment a ON p.appointment_id = a.appointment_id " +
                     "JOIN client c ON a.client_id = c.client_ID " +
                     "WHERE (p.payment_date BETWEEN ? AND ?) " +
                     (status.equals("All") ? "" : " AND p.payment_status = ?") +
                     " ORDER BY p.payment_date DESC";

        List<Payment> payments = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, new java.sql.Timestamp(startDate.getTime()));
            stmt.setTimestamp(2, new java.sql.Timestamp(endDate.getTime()));
            if (!status.equals("All")) {
                stmt.setString(3, status);
            }
            
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
                payment.setAdditionalInfo("appointmentDate", rs.getDate("appointment_date"));
                payment.setAdditionalInfo("clientName", rs.getString("client_fullname"));
                payments.add(payment);
            }
        }
        return payments;
    }

    // Get feedback analysis
    public Map<String, Object> getFeedbackAnalysis() throws SQLException {
        Map<String, Object> result = new HashMap<>();
        
        // Average rating by package
        String packageRatingSql = "SELECT p.package_name, AVG(f.rating) as avg_rating " +
                                 "FROM feedback f " +
                                 "JOIN therapy_package p ON f.package_id = p.package_ID " +
                                 "GROUP BY p.package_name";
        List<Map<String, Object>> packageRatings = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(packageRatingSql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("packageName", rs.getString("package_name"));
                row.put("averageRating", rs.getDouble("avg_rating"));
                packageRatings.add(row);
            }
        }
        result.put("packageRatings", packageRatings);
        
        // Feedback count by rating
        String ratingCountSql = "SELECT rating, COUNT(*) as count FROM feedback GROUP BY rating ORDER BY rating";
        Map<Integer, Integer> ratingCounts = new HashMap<>();
        try (PreparedStatement stmt = conn.prepareStatement(ratingCountSql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ratingCounts.put(rs.getInt("rating"), rs.getInt("count"));
            }
        }
        result.put("ratingDistribution", ratingCounts);
        
        return result;
    }
    
    // ReportDao.java
public List<Appointment> getAppointmentsWithFilters(
    java.sql.Date startDate, 
    java.sql.Date endDate,
    String status,
    String packageName,
    String therapistName) throws SQLException {
    
    String sql = "SELECT a.*, c.client_fullname, t.therapist_fullname, p.package_name " +
                 "FROM appointment a " +
                 "JOIN client c ON a.client_id = c.client_ID " +
                 "JOIN therapist t ON a.therapist_id = t.therapist_ID " +
                 "JOIN therapy_package p ON a.package_id = p.package_ID " +
                 "WHERE 1=1";
    
    // Dynamic filters
    if (startDate != null) {
        sql += " AND a.appointment_date >= ?";
    }
    if (endDate != null) {
        sql += " AND a.appointment_date <= ?";
    }
    if (!"All".equals(status)) {
        sql += " AND a.appointment_status = ?";
    }
    if (!"All".equals(packageName)) {
        sql += " AND p.package_name = ?";
    }
    if (!"All".equals(therapistName)) {
        sql += " AND t.therapist_fullname = ?";
    }
    
    // Execute query dengan PreparedStatement
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        int paramIndex = 1;
        
        if (startDate != null) {
            stmt.setDate(paramIndex++, startDate);
        }
        if (endDate != null) {
            stmt.setDate(paramIndex++, endDate);
        }
        if (!"All".equals(status)) {
            stmt.setString(paramIndex++, status);
        }
        if (!"All".equals(packageName)) {
            stmt.setString(paramIndex++, packageName);
        }
        if (!"All".equals(therapistName)) {
            stmt.setString(paramIndex++, therapistName);
        }
        
        ResultSet rs = stmt.executeQuery();
        // ... process result set
    }
        return null;
}

// Add these new methods to ReportDao
    public List<String> getDistinctPackages() throws SQLException {
        List<String> packages = new ArrayList<>();
        String sql = "SELECT DISTINCT package_name FROM therapy_package ORDER BY package_name";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                packages.add(rs.getString("package_name"));
            }
        }
        return packages;
    }

    public List<String> getDistinctTherapists() throws SQLException {
        List<String> therapists = new ArrayList<>();
        String sql = "SELECT DISTINCT therapist_fullname FROM therapist ORDER BY therapist_fullname";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                therapists.add(rs.getString("therapist_fullname"));
            }
        }
        return therapists;
    }

    public Map<String, Object> getClientStatistics() throws SQLException {
    Map<String, Object> stats = new HashMap<>();
    
    // Total clients
    String totalSql = "SELECT COUNT(*) as total FROM client";
    try (PreparedStatement stmt = conn.prepareStatement(totalSql)) {
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            stats.put("totalClients", rs.getInt("total"));
        }
    }
    
    // Active clients (have appointments)
    String activeSql = "SELECT COUNT(DISTINCT client_id) as active FROM appointment";
    try (PreparedStatement stmt = conn.prepareStatement(activeSql)) {
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            stats.put("activeClients", rs.getInt("active"));
        }
    }
    
    // Set new clients to 0 since we don't have registration date
    stats.put("newClients", 0);
    
    return stats;
}
    public List<Map<String, Object>> getTherapistStatistics() throws SQLException {
        List<Map<String, Object>> stats = new ArrayList<>();
        String sql = "SELECT t.therapist_ID, t.therapist_fullname, " +
                     "COUNT(a.appointment_id) as total_appointments, " +
                     "AVG(f.rating) as avg_rating, " +
                     "SUM(CASE WHEN a.appointment_status = 'Completed' THEN 1 ELSE 0 END) as completed " +
                     "FROM therapist t " +
                     "LEFT JOIN appointment a ON t.therapist_ID = a.therapist_id " +
                     "LEFT JOIN feedback f ON a.appointment_id = f.appointment_id " +
                     "GROUP BY t.therapist_ID, t.therapist_fullname " +
                     "ORDER BY total_appointments DESC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("therapistId", rs.getInt("therapist_ID"));
                row.put("therapistName", rs.getString("therapist_fullname"));
                row.put("totalAppointments", rs.getInt("total_appointments"));
                row.put("avgRating", rs.getDouble("avg_rating"));
                row.put("completedAppointments", rs.getInt("completed"));
                stats.add(row);
            }
        }
        return stats;
    }

    public List<Integer> getAvailableReportYears() throws SQLException {
        List<Integer> years = new ArrayList<>();
        String sql = "SELECT DISTINCT YEAR(appointment_date) as year FROM appointment ORDER BY year DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                years.add(rs.getInt("year"));
            }
        }
        return years;
    }

   
}

