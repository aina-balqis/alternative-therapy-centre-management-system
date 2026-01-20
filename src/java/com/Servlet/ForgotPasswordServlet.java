/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.Servlet;

import com.Dao.ClientDao;
import com.Model.Client;
import com.Utils.EmailUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ASUS
 */

public class ForgotPasswordServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        
        try {
            ClientDao clientDao = new ClientDao();
            Client client = clientDao.getClientByEmail(email);
            
            if (client == null) {
                request.setAttribute("errorMessage", "Email not found.");
                request.getRequestDispatcher("forgot-password.jsp").forward(request, response);
                return;
            }
            
            // Generate token (contoh: UUID random)
            String token = UUID.randomUUID().toString();
            
            // Set expiry (1 jam dari sekarang)
            Timestamp expiry = new Timestamp(System.currentTimeMillis() + 3600000); // 1 jam
            
            // Simpan token & expiry ke database
            clientDao.updateResetToken(client.getClient_ID(), token, expiry);
            
            // Hantar email reset password
            String resetLink = "http://localhost:8080/ATCMS3/reset-password.jsp?token=" + token;
            EmailUtil.sendResetPasswordEmail(client.getClient_email(), client.getClient_fullname(), resetLink);
            
            request.setAttribute("successMessage", "Password reset link sent to your email!");
            request.getRequestDispatcher("forgot-password.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
            request.getRequestDispatcher("forgot-password.jsp").forward(request, response);
        }
    }
}