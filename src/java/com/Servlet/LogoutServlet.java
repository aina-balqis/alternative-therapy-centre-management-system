package com.Servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            // Invalidate the session (clears all session attributes)
            session.invalidate();
        }
        
        // Redirect to appropriate page after logout
        String role = request.getParameter("role");
        String redirectPage = "index.jsp"; // Default redirect
        
        if ("client".equals(role)) {
            redirectPage = "Login.jsp";
        } else if ("therapist".equals(role)) {
            redirectPage = "TherapistLogin.jsp";
        } else if ("admin".equals(role)) {
            redirectPage = "AdminLogin.jsp";
        }
        
        response.sendRedirect(redirectPage);
    }
}