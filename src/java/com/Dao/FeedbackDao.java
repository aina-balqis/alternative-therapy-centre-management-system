package com.Dao;

import com.Model.Appointment;
import com.Model.Feedback;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDao {
    private Connection conn;

    public FeedbackDao() {
        conn = DBConnection.getConnection();
    }

    // Insert new feedback
    public boolean insertFeedback(Feedback feedback) throws SQLException {
        String sql = "INSERT INTO feedback (appointment_id, client_id, therapist_id, package_id, rating, comment, feedback_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, feedback.getAppointmentId());
            stmt.setInt(2, feedback.getClientId());
            stmt.setInt(3, feedback.getTherapistId());
            stmt.setInt(4, feedback.getPackageId());
            stmt.setInt(5, feedback.getRating());
            stmt.setString(6, feedback.getComment());
            
            return stmt.executeUpdate() > 0;
        }
    }

    // Add therapist reply to feedback
    public boolean addTherapistReply(int feedbackId, String reply) throws SQLException {
        String sql = "UPDATE feedback SET therapist_reply = ?, reply_date = CURRENT_TIMESTAMP WHERE feedback_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reply);
            stmt.setInt(2, feedbackId);
            
            return stmt.executeUpdate() > 0;
        }
    }

    // Get feedback by ID
    public Feedback getFeedbackById(int feedbackId) throws SQLException {
        String sql = "SELECT f.*, c.client_fullname, t.therapist_fullname, p.package_name " +
                     "FROM feedback f " +
                     "JOIN client c ON f.client_id = c.client_ID " +
                     "JOIN therapist t ON f.therapist_id = t.therapist_ID " +
                     "JOIN therapy_package p ON f.package_id = p.package_ID " +
                     "WHERE f.feedback_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, feedbackId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Feedback feedback = new Feedback();
                feedback.setFeedbackId(rs.getInt("feedback_id"));
                feedback.setAppointmentId(rs.getInt("appointment_id"));
                feedback.setClientId(rs.getInt("client_id"));
                feedback.setTherapistId(rs.getInt("therapist_id"));
                feedback.setPackageId(rs.getInt("package_id"));
                feedback.setRating(rs.getInt("rating"));
                feedback.setComment(rs.getString("comment"));
                feedback.setFeedbackDate(rs.getTimestamp("feedback_date"));
                feedback.setTherapistReply(rs.getString("therapist_reply"));
                feedback.setReplyDate(rs.getTimestamp("reply_date"));
                feedback.setClientName(rs.getString("client_fullname"));
                feedback.setTherapistName(rs.getString("therapist_fullname"));
                feedback.setPackageName(rs.getString("package_name"));
                
                return feedback;
            }
        }
        return null;
    }

    // Get all feedback for a client
    public List<Feedback> getClientFeedback(int clientId) throws SQLException {
        List<Feedback> feedbackList = new ArrayList<>();
        String sql = "SELECT f.*, t.therapist_fullname, p.package_name " +
                     "FROM feedback f " +
                     "JOIN therapist t ON f.therapist_id = t.therapist_ID " +
                     "JOIN therapy_package p ON f.package_id = p.package_ID " +
                     "WHERE f.client_id = ? ORDER BY f.feedback_date DESC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Feedback feedback = new Feedback();
                feedback.setFeedbackId(rs.getInt("feedback_id"));
                feedback.setAppointmentId(rs.getInt("appointment_id"));
                feedback.setTherapistId(rs.getInt("therapist_id"));
                feedback.setPackageId(rs.getInt("package_id"));
                feedback.setRating(rs.getInt("rating"));
                feedback.setComment(rs.getString("comment"));
                feedback.setFeedbackDate(rs.getTimestamp("feedback_date"));
                feedback.setTherapistReply(rs.getString("therapist_reply"));
                feedback.setReplyDate(rs.getTimestamp("reply_date"));
                feedback.setTherapistName(rs.getString("therapist_fullname"));
                feedback.setPackageName(rs.getString("package_name"));
                
                feedbackList.add(feedback);
            }
        }
        return feedbackList;
    }

    // Get all feedback for a therapist
    public List<Feedback> getTherapistFeedback(int therapistId) throws SQLException {
        List<Feedback> feedbackList = new ArrayList<>();
        String sql = "SELECT f.*, c.client_fullname, p.package_name " +
                     "FROM feedback f " +
                     "JOIN client c ON f.client_id = c.client_ID " +
                     "JOIN therapy_package p ON f.package_id = p.package_ID " +
                     "WHERE f.therapist_id = ? ORDER BY f.feedback_date DESC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, therapistId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Feedback feedback = new Feedback();
                feedback.setFeedbackId(rs.getInt("feedback_id"));
                feedback.setAppointmentId(rs.getInt("appointment_id"));
                feedback.setClientId(rs.getInt("client_id"));
                feedback.setPackageId(rs.getInt("package_id"));
                feedback.setRating(rs.getInt("rating"));
                feedback.setComment(rs.getString("comment"));
                feedback.setFeedbackDate(rs.getTimestamp("feedback_date"));
                feedback.setTherapistReply(rs.getString("therapist_reply"));
                feedback.setReplyDate(rs.getTimestamp("reply_date"));
                feedback.setClientName(rs.getString("client_fullname"));
                feedback.setPackageName(rs.getString("package_name"));
                
                feedbackList.add(feedback);
            }
        }
        return feedbackList;
    }

    // Get all feedback (admin view)
    public List<Feedback> getAllFeedback() throws SQLException {
        List<Feedback> feedbackList = new ArrayList<>();
        String sql = "SELECT f.*, c.client_fullname, t.therapist_fullname, p.package_name " +
                     "FROM feedback f " +
                     "JOIN client c ON f.client_id = c.client_ID " +
                     "JOIN therapist t ON f.therapist_id = t.therapist_ID " +
                     "JOIN therapy_package p ON f.package_id = p.package_ID " +
                     "ORDER BY f.feedback_date DESC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Feedback feedback = new Feedback();
                feedback.setFeedbackId(rs.getInt("feedback_id"));
                feedback.setAppointmentId(rs.getInt("appointment_id"));
                feedback.setClientId(rs.getInt("client_id"));
                feedback.setTherapistId(rs.getInt("therapist_id"));
                feedback.setPackageId(rs.getInt("package_id"));
                feedback.setRating(rs.getInt("rating"));
                feedback.setComment(rs.getString("comment"));
                feedback.setFeedbackDate(rs.getTimestamp("feedback_date"));
                feedback.setTherapistReply(rs.getString("therapist_reply"));
                feedback.setReplyDate(rs.getTimestamp("reply_date"));
                feedback.setClientName(rs.getString("client_fullname"));
                feedback.setTherapistName(rs.getString("therapist_fullname"));
                feedback.setPackageName(rs.getString("package_name"));
                
                feedbackList.add(feedback);
            }
        }
        return feedbackList;
    }

    // Delete feedback (admin only)
    public boolean deleteFeedback(int feedbackId) throws SQLException {
        String sql = "DELETE FROM feedback WHERE feedback_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, feedbackId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Check if feedback exists for an appointment
    public boolean feedbackExistsForAppointment(int appointmentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM feedback WHERE appointment_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointmentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    
    // Dapatkan average rating untuk therapist
public double getAverageRatingForTherapist(int therapistId) throws SQLException {
    String sql = "SELECT AVG(rating) FROM feedback WHERE therapist_id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, therapistId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getDouble(1);
        }
    }
    return 0;
}

// Dapatkan jumlah feedback untuk therapist
public int getFeedbackCountForTherapist(int therapistId) throws SQLException {
    String sql = "SELECT COUNT(*) FROM feedback WHERE therapist_id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, therapistId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
    }
    return 0;
}
public List<Feedback> getFeedbackByTherapistAndMonth(int therapistId, int month, int year) throws SQLException {
    List<Feedback> list = new ArrayList<>();
    String sql = "SELECT f.*, c.client_fullname, p.package_name FROM feedback f " +
                 "JOIN client c ON f.client_id = c.client_id " +
                 "JOIN therapy_package p ON f.package_id = p.package_id " +
                 "WHERE f.therapist_id = ? AND MONTH(f.feedback_date) = ? AND YEAR(f.feedback_date) = ? " +
                 "ORDER BY f.feedback_date DESC";
    
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, therapistId);
        stmt.setInt(2, month);
        stmt.setInt(3, year);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Feedback f = new Feedback();
            f.setFeedbackId(rs.getInt("feedback_id"));
            f.setClientName(rs.getString("client_fullname"));
            f.setRating(rs.getInt("rating"));
            f.setComment(rs.getString("comment"));
            f.setPackageName(rs.getString("package_name"));
            f.setFeedbackDate(rs.getTimestamp("feedback_date"));
            list.add(f);
        }
    }
    return list;
}


    public List<Feedback> getApprovedFeedbacks(int limit) {
        List<Feedback> feedbacks = new ArrayList<>();
        String sql = "SELECT f.*, c.client_fullname FROM feedback f " +
                     "JOIN clients c ON f.client_id = c.client_id " +
                     "WHERE f.status = 'approved' ORDER BY f.feedback_date DESC LIMIT ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Feedback feedback = new Feedback();
                feedback.setFeedbackId(rs.getInt("feedback_id"));
                feedback.setClientId(rs.getInt("client_id"));
                feedback.setClientName(rs.getString("client_fullname"));
                feedback.setTherapistId(rs.getInt("therapist_id"));
                feedback.setAppointmentId(rs.getInt("appointment_id"));
                feedback.setRating(rs.getInt("rating"));
                feedback.setComment(rs.getString("comment"));
                //feedback.setStatus(rs.getString("status"));
                feedback.setFeedbackDate(rs.getTimestamp("feedback_date"));
                
                feedbacks.add(feedback);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return feedbacks;
    }

public List<Feedback> getLandingPageTestimonials(int limit) throws SQLException {
    List<Feedback> testimonials = new ArrayList<>();
    String sql = "SELECT f.feedback_id, f.rating, f.comment, f.feedback_date, " +
                 "c.client_fullname, " +  // Langsung ambil fullname
                 "p.package_name " +
                 "FROM feedback f " +
                 "JOIN client c ON f.client_id = c.client_ID " +
                 "JOIN therapy_package p ON f.package_id = p.package_ID " +
                 "WHERE f.rating >= 4 " +
                 "ORDER BY f.feedback_date DESC " +
                 "LIMIT ?";

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, limit);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            Feedback feedback = new Feedback();
            feedback.setFeedbackId(rs.getInt("feedback_id"));
            feedback.setRating(rs.getInt("rating"));
            feedback.setComment(rs.getString("comment"));
            feedback.setClientName(rs.getString("client_fullname")); // Full name langsung
            feedback.setPackageName(rs.getString("package_name"));
            feedback.setFeedbackDate(rs.getTimestamp("feedback_date"));
            
            testimonials.add(feedback);
        }
    }
    return testimonials;
}
// Add this to your FeedbackDao class
public List<Feedback> getFeedbackByPackage(int packageId) throws SQLException {
    List<Feedback> feedbackList = new ArrayList<>();
    String sql = "SELECT f.*, c.client_fullname, t.therapist_fullname " +
                 "FROM feedback f " +
                 "JOIN client c ON f.client_id = c.client_ID " +
                 "JOIN therapist t ON f.therapist_id = t.therapist_ID " +
                 "WHERE f.package_id = ? ORDER BY f.feedback_date DESC";
    
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, packageId);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            Feedback feedback = new Feedback();
            feedback.setFeedbackId(rs.getInt("feedback_id"));
            feedback.setAppointmentId(rs.getInt("appointment_id"));
            feedback.setClientId(rs.getInt("client_id"));
            feedback.setTherapistId(rs.getInt("therapist_id"));
            feedback.setRating(rs.getInt("rating"));
            feedback.setComment(rs.getString("comment"));
            feedback.setFeedbackDate(rs.getTimestamp("feedback_date"));
            feedback.setTherapistReply(rs.getString("therapist_reply"));
            feedback.setReplyDate(rs.getTimestamp("reply_date"));
            feedback.setClientName(rs.getString("client_fullname"));
            feedback.setTherapistName(rs.getString("therapist_fullname"));
            
            feedbackList.add(feedback);
        }
    }
    return feedbackList;
}
}

