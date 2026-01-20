package com.Dao;

import com.Model.Therapist;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TherapistDao {

    // Get database connection
    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/atcms", "root", "");
        } catch (Exception e) {
            System.out.println("Connection Error: " + e.getMessage());
        }
        return con;
    }
    // Tambah import Connection
private Connection conn;

public TherapistDao() {
    conn = getConnection();
}



public List<Therapist> getTherapistsByGender(String gender) {
    List<Therapist> therapists = new ArrayList<>();
    try {
        String sql = "SELECT * FROM therapist WHERE gender = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, gender);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Therapist therapist = new Therapist();
            therapist.setTherapist_ID(rs.getInt("therapist_ID"));
            therapist.setTherapist_fullname(rs.getString("therapist_fullname"));
            therapist.setGender(rs.getString("gender"));
            therapist.setTherapist_specialization(rs.getString("therapist_specialization"));
            therapists.add(therapist);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return therapists;
}


   

    // Validate therapist login
    public Therapist validateLogin(String email, String password) throws SQLException {
        String query = "SELECT * FROM therapist WHERE therapist_email = ? AND therapist_password = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Therapist therapist = new Therapist();
                therapist.setTherapist_ID(rs.getInt("therapist_ID"));
                therapist.setTherapist_email(rs.getString("therapist_email"));
                therapist.setTherapist_password(rs.getString("therapist_password"));
                therapist.setTherapist_dob(rs.getDate("therapist_dob"));
                therapist.setTherapist_IC(rs.getString("therapist_IC"));
                therapist.setTherapist_fullname(rs.getString("therapist_fullname"));
                therapist.setTherapist_phonenum(rs.getString("therapist_phonenum"));
                therapist.setTherapist_address(rs.getString("therapist_address"));
                therapist.setTherapist_state(rs.getString("therapist_state"));
                therapist.setTherapist_district(rs.getString("therapist_district"));
                therapist.setTherapist_postcode(rs.getString("therapist_postcode"));
                therapist.setTherapist_specialization(rs.getString("therapist_specialization"));
                therapist.setGender(rs.getString("gender"));
                return therapist;
            }
        } catch (SQLException e) {
            System.err.println("Therapist login error: " + e.getMessage());
            throw e;
        }
        return null;
    }

    // Add new therapist
   // Add new therapist with better error handling
public void addTherapist(Therapist therapist) throws SQLException {
    String query = "INSERT INTO therapist (therapist_email, therapist_password, therapist_dob, therapist_IC, "
            + "therapist_fullname, therapist_phonenum, therapist_address, therapist_state, "
            + "therapist_district, therapist_postcode, therapist_specialization, gender) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    try (Connection con = getConnection(); 
         PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
        
        ps.setString(1, therapist.getTherapist_email());
        ps.setString(2, therapist.getTherapist_password());
        
        if (therapist.getTherapist_dob() != null) {
            ps.setDate(3, new java.sql.Date(therapist.getTherapist_dob().getTime()));
        } else {
            ps.setNull(3, Types.DATE);
        }
        
        ps.setString(4, therapist.getTherapist_IC());
        ps.setString(5, therapist.getTherapist_fullname());
        ps.setString(6, therapist.getTherapist_phonenum());
        ps.setString(7, therapist.getTherapist_address());
        ps.setString(8, therapist.getTherapist_state());
        ps.setString(9, therapist.getTherapist_district());
        ps.setString(10, therapist.getTherapist_postcode());
        ps.setString(11, therapist.getTherapist_specialization());
        ps.setString(12, therapist.getGender());
        
        int affectedRows = ps.executeUpdate();
        
        if (affectedRows == 0) {
            throw new SQLException("Creating therapist failed, no rows affected.");
        }
        
        try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                therapist.setTherapist_ID(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Creating therapist failed, no ID obtained.");
            }
        }
    } catch (SQLException e) {
        System.err.println("Error adding therapist: " + e.getMessage());
        throw e;
    }
}
    // Get all therapists
    public static List<Therapist> getAllTherapists() throws SQLException {
        List<Therapist> therapists = new ArrayList<>();
        String query = "SELECT * FROM therapist";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Therapist therapist = new Therapist();
                therapist.setTherapist_ID(rs.getInt("therapist_ID"));
                therapist.setTherapist_email(rs.getString("therapist_email"));
                therapist.setTherapist_password(rs.getString("therapist_password"));
                therapist.setTherapist_dob(rs.getDate("therapist_dob"));
                therapist.setTherapist_IC(rs.getString("therapist_IC"));
                therapist.setTherapist_fullname(rs.getString("therapist_fullname"));
                therapist.setTherapist_phonenum(rs.getString("therapist_phonenum"));
                therapist.setTherapist_address(rs.getString("therapist_address"));
                therapist.setTherapist_state(rs.getString("therapist_state"));
                therapist.setTherapist_district(rs.getString("therapist_district"));
                therapist.setTherapist_postcode(rs.getString("therapist_postcode"));
                therapist.setTherapist_specialization(rs.getString("therapist_specialization"));
                 therapist.setGender(rs.getString("gender"));
                therapists.add(therapist);
            }
        }
        return therapists;
    }

    // Get therapist by ID
    public static Therapist getTherapistById(int id) throws SQLException {
        Therapist therapist = null;
        String query = "SELECT * FROM therapist WHERE therapist_ID = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    therapist = new Therapist();
                    therapist.setTherapist_ID(rs.getInt("therapist_ID"));
                    therapist.setTherapist_email(rs.getString("therapist_email"));
                    therapist.setTherapist_password(rs.getString("therapist_password"));
                    therapist.setTherapist_dob(rs.getDate("therapist_dob"));
                    therapist.setTherapist_IC(rs.getString("therapist_IC"));
                    therapist.setTherapist_fullname(rs.getString("therapist_fullname"));
                    therapist.setTherapist_phonenum(rs.getString("therapist_phonenum"));
                    therapist.setTherapist_address(rs.getString("therapist_address"));
                    therapist.setTherapist_state(rs.getString("therapist_state"));
                    therapist.setTherapist_district(rs.getString("therapist_district"));
                    therapist.setTherapist_postcode(rs.getString("therapist_postcode"));
                    therapist.setTherapist_specialization(rs.getString("therapist_specialization"));
                     therapist.setGender(rs.getString("gender"));
                }
            }
        }
        return therapist;
    }

    // Update therapist profile
    public void updateTherapistProfile(Therapist therapist) throws SQLException {
        String query = "UPDATE therapist SET therapist_email = ?, therapist_password = ?, therapist_dob = ?, therapist_IC = ?, therapist_fullname = ?, therapist_phonenum = ?, therapist_address = ?, therapist_state = ?, therapist_district = ?, therapist_postcode = ?, therapist_specialization = ?, gender=? WHERE therapist_ID = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, therapist.getTherapist_email());
            ps.setString(2, therapist.getTherapist_password());
            ps.setDate(3, new java.sql.Date(therapist.getTherapist_dob().getTime()));
            ps.setString(4, therapist.getTherapist_IC());
            ps.setString(5, therapist.getTherapist_fullname());
            ps.setString(6, therapist.getTherapist_phonenum());
            ps.setString(7, therapist.getTherapist_address());
            ps.setString(8, therapist.getTherapist_state());
            ps.setString(9, therapist.getTherapist_district());
            ps.setString(10, therapist.getTherapist_postcode());
            ps.setString(11, therapist.getTherapist_specialization());
            ps.setString(12, therapist.getGender());
            ps.setInt(13, therapist.getTherapist_ID());
            ps.executeUpdate();
        }
    }

    // Delete therapist by ID
    public void deleteTherapist(int id) throws SQLException {
        String query = "DELETE FROM therapist WHERE therapist_ID = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    
   
}
