package com.Dao;

import com.Model.Client;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDao {

    // Mendapatkan sambungan ke database
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/atcms", "root", "");
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC Driver not found.", e);
        }
    }

    // Tambah client baru ke dalam database
    public void addClient(Client client) throws SQLException {
        String query = "INSERT INTO client (client_fullname, client_email, client_password, client_dob, client_phonenum, client_address, client_state, client_district, client_postcode, gender, email_verified) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, client.getClient_fullname());
            ps.setString(2, client.getClient_email());
            ps.setString(3, client.getClient_password());

            if (client.getClient_dob() != null) {
                ps.setDate(4, new java.sql.Date(client.getClient_dob().getTime()));
            } else {
                ps.setNull(4, java.sql.Types.DATE);
            }

            ps.setString(5, client.getClient_phonenum());
            ps.setString(6, client.getClient_address());
            ps.setString(7, client.getClient_state());
            ps.setString(8, client.getClient_district());
            ps.setString(9, client.getClient_postcode());
            ps.setString(10, client.getGender());

            ps.executeUpdate();
        }
    }

    // Validasi login client berdasarkan email dan password
    public Client validateLogin(String email, String password) throws SQLException {
        String query = "SELECT * FROM client WHERE client_email = ? AND client_password = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToClient(rs);
                }
            }
        }
        return null;
    }

    // Dapatkan semua client dalam list
    public List<Client> getAllClients() throws SQLException {
        List<Client> clients = new ArrayList<>();
        String query = "SELECT * FROM client";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                clients.add(mapResultSetToClient(rs));
            }
        }
        return clients;
    }

    // Dapatkan client berdasarkan ID
    // Dalam ClientDao
    public Client getClientById(int id) throws SQLException {
        String query = "SELECT * FROM client WHERE client_ID = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Client client = new Client();

                client.setClient_ID(rs.getInt("client_ID"));
                client.setClient_fullname(rs.getString("client_fullname"));
                client.setClient_email(rs.getString("client_email"));
                client.setClient_password(rs.getString("client_password"));
                client.setClient_dob(rs.getDate("client_dob"));
                client.setClient_phonenum(rs.getString("client_phonenum"));

                client.setClient_address(rs.getString("client_address"));
                client.setClient_state(rs.getString("client_state"));
                client.setClient_district(rs.getString("client_district"));
                client.setClient_postcode(rs.getString("client_postcode"));
                client.setGender(rs.getString("gender"));
                // Set semua field lain...
                return client;
            }
            return null;
        }
    }

    // Kemaskini maklumat client
    public void updateClient(Client client) throws SQLException {

        String query = "UPDATE client SET client_fullname = ?, client_email = ?, client_password = ?, client_dob = ?, client_phonenum = ?, client_address = ?, client_state = ?, client_district = ?, client_postcode = ?, gender = ? WHERE client_ID = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, client.getClient_fullname());
            ps.setString(2, client.getClient_email());
            ps.setString(3, client.getClient_password());

            if (client.getClient_dob() != null) {
                ps.setDate(4, new java.sql.Date(client.getClient_dob().getTime()));
            } else {
                ps.setNull(4, java.sql.Types.DATE);
            }

            ps.setString(5, client.getClient_phonenum());
            ps.setString(6, client.getClient_address());
            ps.setString(7, client.getClient_state());
            ps.setString(8, client.getClient_district());
            ps.setString(9, client.getClient_postcode());
            ps.setString(10, client.getGender());
            ps.setInt(11, client.getClient_ID());

            ps.executeUpdate();
        }
    }

    // Padam client berdasarkan ID
    public void deleteClient(int id) throws SQLException {
        String query = "DELETE FROM client WHERE client_ID = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Fungsi pembantu untuk mapping ResultSet ke objek Client
    private Client mapResultSetToClient(ResultSet rs) throws SQLException {
        Client client = new Client();
        client.setClient_ID(rs.getInt("client_ID"));
        client.setClient_fullname(rs.getString("client_fullname"));
        client.setClient_email(rs.getString("client_email"));
        client.setClient_password(rs.getString("client_password"));
        client.setClient_dob(rs.getDate("client_dob"));
        client.setClient_phonenum(rs.getString("client_phonenum"));
        client.setGender(rs.getString("gender"));
        client.setClient_address(rs.getString("client_address"));
        client.setClient_state(rs.getString("client_state"));
        client.setClient_district(rs.getString("client_district"));
        client.setClient_postcode(rs.getString("client_postcode"));
        client.setEmail_verified(rs.getBoolean("email_verified"));
         //  mapping untuk reset_token & reset_token_expiry
    client.setReset_token(rs.getString("reset_token"));
    client.setReset_token_expiry(rs.getTimestamp("reset_token_expiry"));

        return client;
    }
    // Dapatkan client berdasarkan email

    public Client getClientByEmail(String email) throws SQLException {
        String query = "SELECT * FROM client WHERE client_email = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapResultSetToClient(rs) : null;
            }
        }
    }

// Dapatkan client berdasarkan reset token
    public Client getClientByResetToken(String token) throws SQLException {
        String query = "SELECT * FROM client WHERE reset_token = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapResultSetToClient(rs) : null;
            }
        }
    }

// Update reset token & expiry
    public void updateResetToken(int clientId, String token, Timestamp expiry) throws SQLException {
        String query = "UPDATE client SET reset_token = ?, reset_token_expiry = ? WHERE client_ID = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, token);
            ps.setTimestamp(2, expiry);
            ps.setInt(3, clientId);
            ps.executeUpdate();
        }
    }

// Clear reset token selepas password diupdate
    public void clearResetToken(int clientId) throws SQLException {
        String query = "UPDATE client SET reset_token = NULL, reset_token_expiry = NULL WHERE client_ID = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, clientId);
            ps.executeUpdate();
        }
    }

// Update password baru
    public void updatePassword(int clientId, String newPassword) throws SQLException {
        String query = "UPDATE client SET client_password = ? WHERE client_ID = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, newPassword); // (⚠️ Hash password dulu dalam production!)
            ps.setInt(2, clientId);
            ps.executeUpdate();
        }
    }
}
