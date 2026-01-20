/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author ASUS
 */
public class AppointmentExpiryChecker {
    
     private Connection conn;

    public AppointmentExpiryChecker() {
        conn = DBConnection.getConnection();
    }
    public static void checkExpiredAppointments() {
        // Cancel appointment jika status 'Pending' dan melebihi 1 jam dari booking time
        String sql = "UPDATE appointment SET appointment_status = 'Cancelled' " +
                     "WHERE appointment_status = 'Pending' AND " +
                     "CONCAT(appointment_date, ' ', appointment_time) < NOW() - INTERVAL 1 HOUR";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            int affectedRows = stmt.executeUpdate(sql);
            System.out.println("Expired appointments cancelled: " + affectedRows);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}