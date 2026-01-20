package com.Servlet;

import com.Dao.AdminDao;
import com.Dao.ClientDao;
import com.Dao.TherapistDao;
import com.Model.Admin;
import com.Model.Client;
import com.Model.Therapist;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class AdminServlet extends HttpServlet {
    private ClientDao clientDao = new ClientDao();
    private TherapistDao therapistDao = new TherapistDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        String role = request.getParameter("role");
        
        try {
            switch (action != null ? action : "") {
                case "addClient":
                    handleAddClient(request, response);
                    break;
                    
                case "updateClient":
                    if ("client".equals(role)) {
                        handleUpdateClient(request, response);
                    }
                    break;
                    
                case "updateTherapist":
                case "updateProfile": // Untuk kompatibilitas
                    if ("therapist".equals(role)) {
                        handleUpdateTherapist(request, response);
                    }
                    break;
                    
                default:
                    // Handle login jika tidak ada action
                    if (action == null && role == null) {
                        handleLogin(request, response);
                    } else {
                        response.sendRedirect("AdminDashboard.jsp");
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        String role = request.getParameter("role");
        
        if (action == null || role == null) {
            response.sendRedirect("AdminLogin.jsp");
            return;
        }
        
        try {
            switch (action) {
                case "view":
                    handleView(request, response, role);
                    break;
                    
                case "delete":
                    handleDelete(request, response, role);
                    break;
                    
                case "edit":
                    handleEdit(request, response, role);
                    break;
                    
                default:
                    response.sendRedirect("AdminDashboard.jsp");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

   private void handleLogin(HttpServletRequest request, HttpServletResponse response)
        throws SQLException, ServletException, IOException {
    String email = request.getParameter("admin_email");
    String password = request.getParameter("admin_password");

    Admin admin = new AdminDao().validateLogin(email, password);
    if (admin != null) {
        HttpSession session = request.getSession();
        session.setAttribute("admin", admin);
        session.setMaxInactiveInterval(30 * 60);

        // 🔥 Tambah auto-cancel appointment di sini
        try {
            com.Dao.AppointmentDao dao = new com.Dao.AppointmentDao();
            dao.autoCancelUnpaidAppointments();
            System.out.println("DEBUG: Auto-cancel executed successfully (Admin login).");
        } catch (Exception e) {
            System.out.println("DEBUG: Auto-cancel FAILED: " + e.getMessage());
            e.printStackTrace();
        }

        response.sendRedirect("AdminDashboardServlet");
    } else {
        request.setAttribute("errorMessage", "Invalid email or password.");
        request.getRequestDispatcher("AdminLogin.jsp").forward(request, response);
    }
}


    private void handleAddClient(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        Client client = new Client();
        client.setClient_fullname(request.getParameter("client_fullname"));
        client.setClient_email(request.getParameter("client_email"));
        client.setClient_password(request.getParameter("client_password"));
        client.setClient_phonenum(request.getParameter("client_phonenum"));
        client.setGender(request.getParameter("gender"));
        client.setClient_dob(Date.valueOf(request.getParameter("client_dob")));
        client.setClient_address(request.getParameter("client_address"));
        client.setClient_state(request.getParameter("client_state"));
        client.setClient_district(request.getParameter("client_district"));
        client.setClient_postcode(request.getParameter("client_postcode"));

        clientDao.addClient(client);
        response.sendRedirect("AdminServlet?action=view&role=client");
    }

    private void handleUpdateClient(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        Client client = new Client();
        client.setClient_ID(Integer.parseInt(request.getParameter("client_ID")));
        client.setClient_fullname(request.getParameter("client_fullname"));
        client.setClient_email(request.getParameter("client_email"));
        client.setClient_password(request.getParameter("client_password"));
        client.setClient_phonenum(request.getParameter("client_phonenum"));
        client.setGender(request.getParameter("gender"));
        client.setClient_dob(Date.valueOf(request.getParameter("client_dob")));
        client.setClient_address(request.getParameter("client_address"));
        client.setClient_state(request.getParameter("client_state"));
        client.setClient_district(request.getParameter("client_district"));
        client.setClient_postcode(request.getParameter("client_postcode"));

        clientDao.updateClient(client);
        response.sendRedirect("AdminServlet?action=view&role=client");
    }

    private void handleUpdateTherapist(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        try {
            int therapistId = Integer.parseInt(request.getParameter("therapist_ID"));
            Therapist therapist = therapistDao.getTherapistById(therapistId);
            
            if (therapist == null) {
                response.sendRedirect("AdminServlet?action=view&role=therapist");
                return;
            }

            // Update fields
            therapist.setTherapist_fullname(request.getParameter("therapist_fullname"));
            therapist.setTherapist_email(request.getParameter("therapist_email"));
            therapist.setTherapist_phonenum(request.getParameter("therapist_phonenum"));
            therapist.setGender(request.getParameter("gender"));
            therapist.setTherapist_address(request.getParameter("therapist_address"));
            therapist.setTherapist_state(request.getParameter("therapist_state"));
            therapist.setTherapist_district(request.getParameter("therapist_district"));
            therapist.setTherapist_postcode(request.getParameter("therapist_postcode"));
            
            // Handle specializations
            String[] specializations = request.getParameterValues("therapist_specialization");
            if (specializations != null) {
                therapist.setTherapist_specialization(String.join(",", specializations));
            }

            therapistDao.updateTherapistProfile(therapist);
            
            response.sendRedirect("AdminServlet?action=view&role=therapist&success=Profile+updated+successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Update failed: " + e.getMessage());
            request.getRequestDispatcher("admin-edit-therapist.jsp").forward(request, response);
        }
    }

    private void handleView(HttpServletRequest request, HttpServletResponse response, String role)
            throws ServletException, IOException, SQLException {
        if ("client".equals(role)) {
            List<Client> clients = clientDao.getAllClients();
            request.setAttribute("clientList", clients);
            request.getRequestDispatcher("admin-view-client.jsp").forward(request, response);
        } else if ("therapist".equals(role)) {
            List<Therapist> therapists = therapistDao.getAllTherapists();
            request.setAttribute("therapistList", therapists);
            request.getRequestDispatcher("admin-view-therapist.jsp").forward(request, response);
        }
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response, String role)
            throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        if ("client".equals(role)) {
            clientDao.deleteClient(id);
        } else if ("therapist".equals(role)) {
            therapistDao.deleteTherapist(id);
        }
        response.sendRedirect("AdminServlet?action=view&role=" + role);
    }

    private void handleEdit(HttpServletRequest request, HttpServletResponse response, String role)
            throws ServletException, IOException, SQLException {
        int id = Integer.parseInt(request.getParameter("id"));
        if ("client".equals(role)) {
            Client client = clientDao.getClientById(id);
            request.setAttribute("client", client);
            request.getRequestDispatcher("admin-edit-client.jsp").forward(request, response);
        } else if ("therapist".equals(role)) {
            Therapist therapist = therapistDao.getTherapistById(id);
            request.setAttribute("therapist", therapist);
            request.getRequestDispatcher("admin-edit-therapist.jsp").forward(request, response);
        }
    }
}