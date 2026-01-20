package com.Dao;

import com.Model.Appointment;
import com.Model.TherapyPackage;
import java.sql.*;
import java.util.*;

public class TherapistDashboardDao {
    private Connection conn;

    public TherapistDashboardDao(Connection conn) {
        this.conn = conn;
    }

    public int getAppointmentsToday(int therapistId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointment WHERE therapist_id = ? AND appointment_date = CURDATE()";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, therapistId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int getAppointmentsThisWeek(int therapistId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointment WHERE therapist_id = ? AND YEARWEEK(appointment_date, 1) = YEARWEEK(CURDATE(), 1)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, therapistId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int getAppointmentsThisMonth(int therapistId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointment WHERE therapist_id = ? AND MONTH(appointment_date) = MONTH(CURDATE()) AND YEAR(appointment_date) = YEAR(CURDATE())";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, therapistId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int getCompletedAppointments(int therapistId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointment WHERE therapist_id = ? AND appointment_status = 'Completed'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, therapistId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int getUpcomingAppointments(int therapistId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointment WHERE therapist_id = ? AND appointment_status = 'Confirmed'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, therapistId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public double getTotalRevenue(int therapistId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(p.amount),0) FROM payment p JOIN appointment a ON p.appointment_id = a.appointment_id WHERE a.therapist_id = ? AND p.payment_status = 'Paid'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, therapistId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        }
        return 0;
    }

    public List<Map<String, Object>> getPackageStats(int therapistId) throws SQLException {
        List<Map<String, Object>> stats = new ArrayList<>();
        String sql = "SELECT tp.package_name, COUNT(a.appointment_id) AS total_sessions, COALESCE(SUM(p.amount),0) AS revenue " +
                     "FROM appointment a " +
                     "JOIN therapy_package tp ON a.package_id = tp.package_ID " +
                     "LEFT JOIN payment p ON a.appointment_id = p.appointment_id AND p.payment_status = 'Paid' " +
                     "WHERE a.therapist_id = ? " +
                     "GROUP BY tp.package_name";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, therapistId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("packageName", rs.getString("package_name"));
                map.put("totalSessions", rs.getInt("total_sessions"));
                map.put("revenue", rs.getDouble("revenue"));
                stats.add(map);
            }
        }
        return stats;
    }
}
