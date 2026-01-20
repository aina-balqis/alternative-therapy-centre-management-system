package com.Servlet;

import com.Dao.ClientDao;
import com.Model.Client;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;


public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("client_email");
        String password = request.getParameter("client_password");

        try {
            ClientDao dao = new ClientDao();
            Client client = dao.validateLogin(email, password);

            if (client != null) {
                // Cek jika belum verify email
                if (!client.isEmail_verified()) {
                    request.setAttribute("errorMessage", "Please verify your email before logging in.");
                    request.getRequestDispatcher("Login.jsp").forward(request, response);
                    return;
                }

                // Email verified → simpan dalam session dan teruskan
                HttpSession session = request.getSession();
                session.setAttribute("client", client);
                session.setAttribute("clientId", client.getClient_ID());
                response.sendRedirect("DashboardClientServlet");
            } else {
                // Login gagal
                request.setAttribute("errorMessage", "Invalid email or password.");
                request.getRequestDispatcher("Login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("Login.jsp").forward(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("Login.jsp");
    }
}
