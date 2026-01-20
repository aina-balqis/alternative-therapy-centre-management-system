package com.Dao;

import com.Model.Admin;
import java.sql.*;

public class AdminDao {

    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/atcms", "root", "");
        } catch (Exception e) {
            System.out.println(e);
        }
        return con;
    }

    public static Admin validateLogin(String email, String password) throws SQLException {
        String query = "SELECT * FROM admin WHERE admin_email = ? AND admin_password = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Admin admin = new Admin();
                admin.setAdmin_id(rs.getInt("admin_id"));
                admin.setAdmin_fullname(rs.getString("admin_fullname"));
                admin.setAdmin_email(rs.getString("admin_email"));
                admin.setAdmin_password(rs.getString("admin_password"));
                admin.setAdmin_dob(rs.getDate("admin_dob"));
                admin.setAdmin_phonenum(rs.getInt("admin_phonenum"));
                admin.setRegister_passcode(rs.getString("register_passcode"));
                return admin;
            }
        } catch (SQLException e) {
            System.err.println("Admin login error: " + e.getMessage());
            throw e;
        }
        return null;
    }


    public static int registerAdmin(Admin admin) throws Exception {
        int status = 0;
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO admin(admin_fullname, admin_email, admin_password, admin_dob, admin_phonenum, register_passcode) " +
                "VALUES (?, ?, ?, ?, ?, ?)"
            );
            ps.setString(1, admin.getAdmin_fullname());
            ps.setString(2, admin.getAdmin_email());
            ps.setString(3, admin.getAdmin_password());
            ps.setDate(4, new java.sql.Date(admin.getAdmin_dob().getTime()));
            ps.setInt(5, admin.getAdmin_phonenum());
            ps.setString(6, admin.getRegister_passcode());
            status = ps.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return status;
    }

    public Admin validateRegister(String register_passcode) throws SQLException {
        String sql = "SELECT * FROM admin WHERE register_passcode = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, register_passcode);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Admin admin = new Admin();
                admin.setAdmin_id(rs.getInt("admin_id"));
                admin.setAdmin_fullname(rs.getString("admin_fullname"));
                admin.setAdmin_email(rs.getString("admin_email"));
                admin.setAdmin_password(rs.getString("admin_password"));
                admin.setAdmin_dob(rs.getDate("admin_dob"));
                admin.setAdmin_phonenum(rs.getInt("admin_phonenum"));
                admin.setRegister_passcode(rs.getString("register_passcode"));
                return admin;
            }
        } catch (SQLException ex) {
            System.err.println("SQL Error: " + ex.getMessage());
            throw ex;
        }
        return null;
    }
}
