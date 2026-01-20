package com.Servlet;

import com.Dao.AppointmentDao;
import com.Dao.FeedbackDao;
import com.Model.Appointment;
import com.Model.Client;
import com.Model.Feedback;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


public class FeedbackServlet extends HttpServlet {
    private FeedbackDao feedbackDao;
    private AppointmentDao appointmentDao;

    @Override
    public void init() {
        feedbackDao = new FeedbackDao();
        appointmentDao = new AppointmentDao();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        try {
            if (action == null) {
                response.sendRedirect("clientDashboard.jsp");
                return;
            }
            
            HttpSession session = request.getSession(false);
            if (session == null) {
                response.sendRedirect("Login.jsp");
                return;
            }
            
            
            if (session == null) {
                response.sendRedirect("AdminLogin.jsp");
                return;
            }
            switch (action) {
                case "viewFeedback":
                    viewFeedback(request, response);
                    break;
                case "viewClientFeedback":
                    viewClientFeedback(request, response);
                    break;
                case "viewTherapistFeedback":
                    viewTherapistFeedback(request, response);
                    break;
                case "viewAllFeedback":
                    viewAllFeedback(request, response);
                    break;
                case "showFeedbackForm":
                    showFeedbackForm(request, response);
                    break;
                case "showReplyForm":
                    showReplyForm(request, response);
                    break;
                    case "viewTherapistRating":
    viewTherapistRating(request, response);
    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            }
        } catch (Exception e) {
            handleError(request, response, "Database error occurred", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        try {
            if (action == null) {
                response.sendRedirect("clientDashboard.jsp");
                return;
            }
            
            HttpSession session = request.getSession(false);
            if (session == null) {
                response.sendRedirect("Login.jsp");
                return;
            }
            
            switch (action) {
                case "submitFeedback":
                    submitFeedback(request, response);
                    break;
                case "submitReply":
                    submitReply(request, response);
                    break;
                case "deleteFeedback":
                    deleteFeedback(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            }
        } catch (Exception e) {
            handleError(request, response, "Database error occurred", e);
        }
    }

   private void viewFeedback(HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException, SQLException {
    try {
        System.out.println("Attempting to view feedback with ID: " + request.getParameter("id"));
        int feedbackId = Integer.parseInt(request.getParameter("id"));
        Feedback feedback = feedbackDao.getFeedbackById(feedbackId);
        
        System.out.println("Retrieved feedback: " + feedback);
        
        if (feedback == null) {
            System.out.println("Feedback not found for ID: " + feedbackId);
            request.setAttribute("error", "Feedback not found");
            request.getRequestDispatcher("admin-feedback.jsp").forward(request, response);
            return;
        }
        
        request.setAttribute("feedback", feedback);
        System.out.println("Forwarding to admin-feedback-details.jsp");
        request.getRequestDispatcher("admin-feedback-details.jsp").forward(request, response);
    } catch (NumberFormatException e) {
        System.out.println("Invalid feedback ID format: " + e.getMessage());
        request.setAttribute("error", "Invalid feedback ID");
        request.getRequestDispatcher("admin-feedback.jsp").forward(request, response);
    }
}
    private void viewClientFeedback(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    HttpSession session = request.getSession();
    Client client = (Client) session.getAttribute("client");

    if (client == null) {
        response.sendRedirect("Login.jsp");
        return;
    }

    try {
        AppointmentDao appointmentDao = new AppointmentDao();
        FeedbackDao feedbackDao = new FeedbackDao();

        // Appointment yang sudah beri feedback
        List<Feedback> feedbackList = feedbackDao.getClientFeedback(client.getClient_ID());

        // Appointment Completed yang belum ada feedback
        List<Appointment> pendingFeedbackAppointments = appointmentDao.getCompletedAppointmentsWithoutFeedback(client.getClient_ID());

        request.setAttribute("feedbackList", feedbackList);
        request.setAttribute("pendingFeedbackAppointments", pendingFeedbackAppointments);

        RequestDispatcher dispatcher = request.getRequestDispatcher("client-feedback.jsp");
        dispatcher.forward(request, response);

    } catch (SQLException e) {
        e.printStackTrace();
        response.sendRedirect("error.jsp");
    }
}


   private void viewTherapistFeedback(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, SQLException {
    HttpSession session = request.getSession();
    int therapistId = (int) session.getAttribute("therapistId");
    
    List<Feedback> feedbackList = feedbackDao.getTherapistFeedback(therapistId);
    double averageRating = feedbackDao.getAverageRatingForTherapist(therapistId);
    int feedbackCount = feedbackDao.getFeedbackCountForTherapist(therapistId);
    

    
    request.setAttribute("feedbackList", feedbackList);
    request.setAttribute("averageRating", averageRating);
    request.setAttribute("feedbackCount", feedbackCount);
    
    request.getRequestDispatcher("therapist-feedback.jsp").forward(request, response);
}

    private void viewAllFeedback(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        List<Feedback> feedbackList = feedbackDao.getAllFeedback();
        request.setAttribute("feedbackList", feedbackList);
        request.getRequestDispatcher("admin-feedback.jsp").forward(request, response);
    }

    private void showFeedbackForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
        
        // Check if feedback already exists for this appointment
        if (feedbackDao.feedbackExistsForAppointment(appointmentId)) {
            response.sendRedirect("client-appointments.jsp?error=feedback_already_submitted");
            return;
        }
        
        // Get appointment details
        Appointment appointment = appointmentDao.getAppointmentById(appointmentId);
        if (appointment == null || !appointment.getAppointmentStatus().equals("Completed")) {
            response.sendRedirect("client-appointments.jsp?error=invalid_appointment_status");
            return;
        }
        
        request.setAttribute("appointment", appointment);
        request.getRequestDispatcher("submit-feedback.jsp").forward(request, response);
    }

    private void showReplyForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        int feedbackId = Integer.parseInt(request.getParameter("id"));
        Feedback feedback = feedbackDao.getFeedbackById(feedbackId);
        
        if (feedback == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Feedback not found");
            return;
        }
        
        HttpSession session = request.getSession();
        int therapistId = (int) session.getAttribute("therapistId");
        
        // Check if the therapist is authorized to reply to this feedback
        if (feedback.getTherapistId() != therapistId) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Not authorized to reply to this feedback");
            return;
        }
        
        request.setAttribute("feedback", feedback);
        request.getRequestDispatcher("therapist-reply.jsp").forward(request, response);
    }

    private void submitFeedback(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        HttpSession session = request.getSession();
        int clientId = (int) session.getAttribute("clientId");
        int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
        
        // Check if feedback already exists
        if (feedbackDao.feedbackExistsForAppointment(appointmentId)) {
            response.sendRedirect("client-appointments.jsp?error=feedback_already_submitted");
            return;
        }
        
        // Get appointment details
        Appointment appointment = appointmentDao.getAppointmentById(appointmentId);
        if (appointment == null || !appointment.getAppointmentStatus().equals("Completed") || 
            appointment.getClientId() != clientId) {
            response.sendRedirect("client-appointments.jsp?error=invalid_appointment");
            return;
        }
        
        // Create feedback object
        Feedback feedback = new Feedback();
        feedback.setAppointmentId(appointmentId);
        feedback.setClientId(clientId);
        feedback.setTherapistId(appointment.getTherapistId());
        feedback.setPackageId(appointment.getPackageId());
        feedback.setRating(Integer.parseInt(request.getParameter("rating")));
        feedback.setComment(request.getParameter("comment"));
        
        // Insert feedback
        boolean success = feedbackDao.insertFeedback(feedback);
        
        if (success) {
            response.sendRedirect("FeedbackServlet?action=viewClientFeedback&success=feedback_submitted");
        } else {
            response.sendRedirect("submit-feedback.jsp?appointmentId=" + appointmentId + "&error=submission_failed");
        }
    }

    private void submitReply(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        HttpSession session = request.getSession();
        int therapistId = (int) session.getAttribute("therapistId");
        int feedbackId = Integer.parseInt(request.getParameter("feedbackId"));
        String reply = request.getParameter("reply");
        
        // Get feedback to verify therapist ownership
        Feedback feedback = feedbackDao.getFeedbackById(feedbackId);
        if (feedback == null || feedback.getTherapistId() != therapistId) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Not authorized to reply to this feedback");
            return;
        }
        
        // Update feedback with reply
        boolean success = feedbackDao.addTherapistReply(feedbackId, reply);
        
        if (success) {
            response.sendRedirect("FeedbackServlet?action=viewTherapistFeedback&success=reply_submitted");
        } else {
            response.sendRedirect("therapist-reply.jsp?id=" + feedbackId + "&error=submission_failed");
        }
    }

    private void deleteFeedback(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        int feedbackId = Integer.parseInt(request.getParameter("id"));
        
        // Check if admin is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required");
            return;
        }
        
        boolean success = feedbackDao.deleteFeedback(feedbackId);
        
        if (success) {
            response.sendRedirect("FeedbackServlet?action=viewAllFeedback&success=feedback_deleted");
        } else {
            response.sendRedirect("FeedbackServlet?action=viewAllFeedback&error=delete_failed");
        }
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, 
            String message, Exception e) throws ServletException, IOException {
        e.printStackTrace();
        request.setAttribute("errorMessage", message + ": " + e.getMessage());
        request.getRequestDispatcher("error.jsp").forward(request, response);
    }
    
    private void viewTherapistRating(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, SQLException {
    int therapistId = Integer.parseInt(request.getParameter("therapistId"));
    
    double averageRating = feedbackDao.getAverageRatingForTherapist(therapistId);
    int feedbackCount = feedbackDao.getFeedbackCountForTherapist(therapistId);
    
    request.setAttribute("averageRating", averageRating);
    request.setAttribute("feedbackCount", feedbackCount);
    request.setAttribute("therapistId", therapistId);
    
    request.getRequestDispatcher("therapist-rating.jsp").forward(request, response);
}
}