package com.Servlet;

import com.Dao.FeedbackDao;
import com.Dao.TherapyPackageDao;
import com.Model.Feedback;
import com.Model.TherapyPackage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginChoiceServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    try {
        TherapyPackageDao packageDao = new TherapyPackageDao();
        FeedbackDao feedbackDao = new FeedbackDao();
        
        List<TherapyPackage> packages = packageDao.getAllPackages();
        List<Feedback> testimonials = feedbackDao.getLandingPageTestimonials(3);
        
        // Debug
        System.out.println("Testimonials found: " + testimonials.size());
        
        request.setAttribute("packages", packages);
        request.setAttribute("testimonials", testimonials); // Pastikan nama attribute ini
        request.getRequestDispatcher("LoginChoice.jsp").forward(request, response);
    } catch (SQLException ex) {
        Logger.getLogger(LoginChoiceServlet.class.getName()).log(Level.SEVERE, null, ex);
        request.setAttribute("error", "Failed to load testimonials");
        request.getRequestDispatcher("LoginChoice.jsp").forward(request, response);
    }
}
}