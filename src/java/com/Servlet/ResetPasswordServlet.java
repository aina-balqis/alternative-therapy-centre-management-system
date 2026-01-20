/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.Servlet;

import com.Dao.ClientDao;
import com.Model.Client;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ASUS
 */

public class ResetPasswordServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String token = request.getParameter("token");
        String newPassword = request.getParameter("password");
        
        try {
            ClientDao clientDao = new ClientDao();
            Client client = clientDao.getClientByResetToken(token);
            
            if (client == null || client.getReset_token_expiry().before(new Timestamp(System.currentTimeMillis()))) {
                request.setAttribute("errorMessage", "Invalid or expired token.");
                request.getRequestDispatcher("reset-password.jsp?token=" + token).forward(request, response);
                return;
            }
            
            // Update password & clear token
            clientDao.updatePassword(client.getClient_ID(), newPassword);
            clientDao.clearResetToken(client.getClient_ID());
            
            request.setAttribute("successMessage", "Password updated successfully! You can now login.");
            response.sendRedirect("Login.jsp");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
            request.getRequestDispatcher("reset-password.jsp?token=" + token).forward(request, response);
        }
    }
}