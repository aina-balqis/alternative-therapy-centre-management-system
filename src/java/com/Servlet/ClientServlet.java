package com.Servlet;

import com.Dao.ClientDao;
import com.Dao.DBConnection;
import com.Model.Admin;
import com.Model.Client;
import com.Utils.EmailUtil;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

//@WebServlet("/ClientServlet")
public class ClientServlet extends HttpServlet {

    private ClientDao clientDao = new ClientDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            switch (action == null ? "view" : action) {
                case "list":
                    listClients(request, response);
                    break;
                case "view":    // Untuk view detail client tertentu (optional)
                viewClient(request, response);
                break;
                case "profile":  // Untuk view profile client sendiri
                viewOwnProfile(request, response);
                break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "delete":
                    deleteClient(request, response);
                    break;
                default:
                    viewOwnProfile(request, response); // Default ke profile sendiri
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Something went wrong: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            switch (action == null ? "" : action) {
                case "add":
                    addClient(request, response);
                    break;
                case "update":
                    updateClient(request, response);
                    break;
                 case "deleteAccount":
                    deleteOwnAccount(request, response);
                    break;    
                default:
                    listClients(request, response);
            }
        } catch (Exception e) {
            Logger.getLogger(ClientServlet.class.getName()).log(Level.SEVERE, null, e);
            throw new ServletException(e);
        }
    }
    
  private void viewOwnProfile(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    HttpSession session = request.getSession();
    Client sessionClient = (Client) session.getAttribute("client");
    
    if (sessionClient == null) {
        response.sendRedirect("Login.jsp");
        return;
    }

    try {
        // REFRESH data dari DB
        Client freshClient = clientDao.getClientById(sessionClient.getClient_ID());
        session.setAttribute("client", freshClient); // kemas kini session

        request.setAttribute("client", freshClient); // hantar ke JSP
        request.getRequestDispatcher("ClientProfile.jsp").forward(request, response);
    } catch (Exception e) {
        e.printStackTrace();
        request.setAttribute("error", "Error loading profile");
        request.getRequestDispatcher("error.jsp").forward(request, response);
    }
}

    
    private void viewClient(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    try {
        // 1. Dapatkan client_ID dari parameter
        String clientIdParam = request.getParameter("client_ID");
        if (clientIdParam == null || clientIdParam.isEmpty()) {
            request.setAttribute("error", "Client ID is required");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        // 2. Parse ID dan dapatkan data dari DAO
        int clientId = Integer.parseInt(clientIdParam);
        Client client = clientDao.getClientById(clientId);

        // 3. Validasi jika client wujud
        if (client == null) {
            request.setAttribute("error", "Client not found");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        // 4. Semak permission (contoh: admin atau client sendiri)
        HttpSession session = request.getSession();
        Client loggedInClient = (Client) session.getAttribute("client");
        
       
        
        // Jika bukan admin dan bukan client sendiri, block access
        boolean isAdmin = false ;
        if (!isAdmin && (loggedInClient == null || loggedInClient.getClient_ID() != clientId)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        // 5. Set attribute dan forward ke view page
        request.setAttribute("viewedClient", client);
        request.getRequestDispatcher("admin-client-profile.jsp").forward(request, response);

    } catch (NumberFormatException e) {
        request.setAttribute("error", "Invalid Client ID format");
        request.getRequestDispatcher("error.jsp").forward(request, response);
    } catch (SQLException e) {
        e.printStackTrace();
        request.setAttribute("error", "Database error: " + e.getMessage());
        request.getRequestDispatcher("error.jsp").forward(request, response);
    }
}
    
    private void deleteOwnAccount(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
    HttpSession session = request.getSession(false);
    if (session != null && session.getAttribute("client") != null) {
        Client client = (Client) session.getAttribute("client");
        clientDao.deleteClient(client.getClient_ID());
        session.invalidate(); // logout terus
        response.sendRedirect("Login.jsp?message=Account deleted successfully");
    } else {
        response.sendRedirect("Login.jsp?error=Unauthorized action");
    }
}

    

    private void listClients(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        List<Client> clients = clientDao.getAllClients();
        request.setAttribute("clients", clients);
        request.getRequestDispatcher("client-list.jsp").forward(request, response);
    }

    //register client
  private void addClient(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    
    try {
        Client client = new Client();
        client.setClient_fullname(request.getParameter("client_fullname"));
        client.setClient_email(request.getParameter("client_email"));
        client.setClient_password(request.getParameter("client_password"));
        client.setClient_dob(java.sql.Date.valueOf(request.getParameter("client_dob")));
        client.setClient_phonenum(request.getParameter("client_phonenum"));
        client.setClient_address(request.getParameter("client_address"));
        client.setClient_state(request.getParameter("client_state"));
        client.setClient_district(request.getParameter("client_district"));
        client.setClient_postcode(request.getParameter("client_postcode"));
        client.setGender(request.getParameter("gender"));
        client.setEmail_verified(false); // Set to false initially

        // Save client to DB
        clientDao.addClient(client);

        // Generate verification token
        String token = java.util.UUID.randomUUID().toString();
        java.sql.Timestamp expiry = new java.sql.Timestamp(
            System.currentTimeMillis() + 24 * 60 * 60 * 1000); // 24 hours expiry

        // Save token to database
        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO email_verification (client_email, token, expiry) VALUES (?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, client.getClient_email());
                ps.setString(2, token);
                ps.setTimestamp(3, expiry);
                ps.executeUpdate();
            }
        }

        // Send verification email (async)
        EmailUtil.sendVerificationEmail(
            client.getClient_email(), 
            client.getClient_fullname(), 
            token
        );

        // Set success message in session
        HttpSession session = request.getSession();
        session.setAttribute("message", 
            "Registration successful! Please check your email to verify your account.");

        // Redirect to login page
        response.sendRedirect("Login.jsp");

    } catch (Exception e) {
        e.printStackTrace();
        request.setAttribute("errorMessage", 
            "Registration failed: " + e.getMessage());
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }
}

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Client client = clientDao.getClientById(id);
        request.setAttribute("client", client);
        request.getRequestDispatcher("clientProfile.jsp").forward(request, response);
    }

   private void updateClient(HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException {
    try {
        // Get current client from session
        HttpSession session = request.getSession();
        Client currentClient = (Client) session.getAttribute("client");
        
        if (currentClient == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        // Create updated client object
        Client updatedClient = new Client();
        updatedClient.setClient_ID(currentClient.getClient_ID());
        updatedClient.setClient_fullname(request.getParameter("client_fullname"));
        updatedClient.setClient_email(request.getParameter("client_email"));
        
        // Handle password - keep current if not changed
        String newPassword = request.getParameter("client_password");
        if(newPassword != null && !newPassword.isEmpty()) {
            updatedClient.setClient_password(newPassword);
        } else {
            updatedClient.setClient_password(currentClient.getClient_password());
        }

        // Handle date of birth
        String dobStr = request.getParameter("client_dob");
        if (dobStr != null && !dobStr.trim().isEmpty()) {
            try {
                updatedClient.setClient_dob(java.sql.Date.valueOf(dobStr));
            } catch (IllegalArgumentException e) {
                throw new ServletException("Invalid date format: " + dobStr, e);
            }
        } else {
            updatedClient.setClient_dob(null);
        }

        // Set other fields
        updatedClient.setClient_phonenum(request.getParameter("client_phonenum"));
        updatedClient.setClient_address(request.getParameter("client_address"));
        updatedClient.setClient_state(request.getParameter("client_state"));
        updatedClient.setClient_district(request.getParameter("client_district"));
        updatedClient.setClient_postcode(request.getParameter("client_postcode"));
        updatedClient.setGender(request.getParameter("gender"));

        // Update in database
        clientDao.updateClient(updatedClient);
        
        // Update session with FRESH data from DB
        Client freshClient = clientDao.getClientById(updatedClient.getClient_ID());
        session.setAttribute("client", freshClient);
        
        // Redirect to profile page with success message
        response.sendRedirect("ClientProfile.jsp?success=Profile updated successfully");
        
    } catch (Exception e) {
        e.printStackTrace();
        request.setAttribute("error", "Update failed: " + e.getMessage());
        request.getRequestDispatcher("ClientEditProfile.jsp").forward(request, response);
    }
}
    private void deleteClient(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        clientDao.deleteClient(id);
        response.sendRedirect("ClientServlet?action=view");
    }
}
