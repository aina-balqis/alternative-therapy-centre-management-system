package com.Dao;

import com.Model.TherapyPackage;
import com.Dao.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TherapyPackageDao {

    private Connection conn;

    public TherapyPackageDao() {
        conn = DBConnection.getConnection();
    }
    
    public TherapyPackageDao(Connection conn) {
    this.conn = conn;
}

    // Add a new package
    public void addPackage(TherapyPackage tp) throws SQLException {
        String sql = "INSERT INTO therapy_package (package_name, package_description, package_price, package_duration, is_active, image_url) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, tp.getPackage_name());
        ps.setString(2, tp.getPackage_description());
        ps.setDouble(3, tp.getPackage_price());
        ps.setInt(4, tp.getPackage_duration());
        ps.setBoolean(5, tp.isIs_active());
        ps.setString(6, tp.getImage_url());
        ps.executeUpdate();
        ps.close();
    }

    // Get all packages
    public List<TherapyPackage> getAllPackages() throws SQLException {
        List<TherapyPackage> list = new ArrayList<>();
        String sql = "SELECT * FROM therapy_package";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            TherapyPackage tp = new TherapyPackage();
            tp.setPackage_ID(rs.getInt("package_ID"));
            tp.setPackage_name(rs.getString("package_name"));
            tp.setPackage_description(rs.getString("package_description"));
            tp.setPackage_price(rs.getDouble("package_price"));
            tp.setPackage_duration(rs.getInt("package_duration"));
            tp.setIs_active(rs.getBoolean("is_active"));
            tp.setImage_url(rs.getString("image_url"));
            list.add(tp);
        }

        rs.close();
        stmt.close();
        return list;
    }

    // Get package by ID
    public TherapyPackage getPackageById(int id) throws SQLException {
        TherapyPackage tp = null;
        String sql = "SELECT * FROM therapy_package WHERE package_ID = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            tp = new TherapyPackage();
            tp.setPackage_ID(rs.getInt("package_ID"));
            tp.setPackage_name(rs.getString("package_name"));
            tp.setPackage_description(rs.getString("package_description"));
            tp.setPackage_price(rs.getDouble("package_price"));
            tp.setPackage_duration(rs.getInt("package_duration"));
            tp.setIs_active(rs.getBoolean("is_active"));
            tp.setImage_url(rs.getString("image_url"));
        }

        rs.close();
        ps.close();
        return tp;
    }

    // Update package
    public void updatePackage(TherapyPackage tp) throws SQLException {
        String sql = "UPDATE therapy_package SET package_name=?, package_description=?, package_price=?, package_duration=?, is_active=?, image_url=? WHERE package_ID=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, tp.getPackage_name());
        ps.setString(2, tp.getPackage_description());
        ps.setDouble(3, tp.getPackage_price());
        ps.setInt(4, tp.getPackage_duration());
        ps.setBoolean(5, tp.isIs_active());
        ps.setString(6, tp.getImage_url());
        ps.setInt(7, tp.getPackage_ID());
        ps.executeUpdate();
        ps.close();
    }

    // Delete package
    public void deletePackage(int id) throws SQLException {
        String sql = "DELETE FROM therapy_package WHERE package_ID=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        ps.close();
    }
    
    public int getPackageDurationById(int packageId) {
    int duration = 0;
    try (Connection con = DBConnection.getConnection()) {
        String sql = "SELECT duration FROM therapy_package WHERE package_id = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, packageId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            duration = rs.getInt("duration");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return duration;
}

    
    
    
}
