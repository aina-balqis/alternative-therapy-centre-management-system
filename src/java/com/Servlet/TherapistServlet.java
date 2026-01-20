package com.Servlet;

import com.Dao.TherapistDao;
import com.Model.Therapist;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

//@WebServlet("/TherapistServlet")
public class TherapistServlet extends HttpServlet {

    private TherapistDao therapistDao;
     @Override
    public void init() throws ServletException {
        therapistDao = new TherapistDao(); // Initialize DAO
    }

    

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if (action == null) {
                response.sendRedirect("error.jsp");
            } else {
                switch (action) {
                    case "add":
                        addTherapist(request, response);
                        break;
                    //case "login":
                        //loginTherapist(request, response);
                       // break;
                    case "updateProfile":
                        updateTherapistProfile(request, response);
                        break;
                    default:
                        response.sendRedirect("error.jsp");
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if (action == null) {
                response.sendRedirect("error.jsp");
            } else {
                switch (action) {
                    case "list":
                        listTherapists(request, response);
                        break;
                    case "edit":
                        showEditForm(request, response);
                        break;
                    case "delete":
                        deleteTherapist(request, response);
                        break;
                    default:
                        response.sendRedirect("error.jsp");
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

   private void addTherapist(HttpServletRequest request, HttpServletResponse response)
        throws SQLException, IOException, ServletException {
    try {
        Therapist therapist = extractTherapistFromRequest(request);
        
        // Validate required fields
        if (therapist.getTherapist_dob() == null || 
            therapist.getTherapist_IC() == null || 
            therapist.getTherapist_IC().isEmpty()) {
            request.setAttribute("errorMessage", "All required fields must be filled");
            request.getRequestDispatcher("admin-add-therapist.jsp").forward(request, response);
            return;
        }

        therapistDao.addTherapist(therapist);
        response.sendRedirect("AdminServlet?action=view&role=therapist");
    } catch (SQLException e) {
        e.printStackTrace();
        request.setAttribute("errorMessage", "Error adding therapist: " + e.getMessage());
        request.getRequestDispatcher("admin-add-therapist.jsp").forward(request, response);
    } catch (Exception e) {
        e.printStackTrace();
        request.setAttribute("errorMessage", "System error: " + e.getMessage());
        request.getRequestDispatcher("admin-add-therapist.jsp").forward(request, response);
    }
}

private Therapist extractTherapistFromRequest(HttpServletRequest request) {
    Therapist t = new Therapist();

    try {
        t.setTherapist_email(request.getParameter("therapist_email"));
        t.setTherapist_password(request.getParameter("therapist_password"));
        
        // Handle date - only parse if not empty
        String dobStr = request.getParameter("therapist_dob");
        if (dobStr != null && !dobStr.isEmpty()) {
            t.setTherapist_dob(java.sql.Date.valueOf(dobStr));
        }
        
        t.setTherapist_IC(request.getParameter("therapist_IC"));
        t.setTherapist_fullname(request.getParameter("therapist_fullname"));
        t.setTherapist_phonenum(request.getParameter("therapist_phonenum"));
        t.setTherapist_address(request.getParameter("therapist_address"));
        t.setTherapist_state(request.getParameter("therapist_state"));
        t.setTherapist_district(request.getParameter("therapist_district"));
        t.setTherapist_postcode(request.getParameter("therapist_postcode"));
        t.setTherapist_specialization(request.getParameter("therapist_specialization"));
        t.setGender(request.getParameter("gender"));

    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Error parsing therapist data", e);
    }

    return t;
}

    private void updateTherapistProfile(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        try {
            // Get current therapist from session
            HttpSession session = request.getSession();
            Therapist currentTherapist = (Therapist) session.getAttribute("therapist");
            
            if (currentTherapist == null) {
                response.sendRedirect("TherapistLogin.jsp");
                return;
            }

            // Create updated therapist object
            Therapist updatedTherapist = new Therapist();
            updatedTherapist.setTherapist_ID(currentTherapist.getTherapist_ID());
            updatedTherapist.setTherapist_email(request.getParameter("therapist_email"));
            updatedTherapist.setTherapist_fullname(request.getParameter("therapist_fullname"));
            
            // Handle password - keep current if not changed
            String newPassword = request.getParameter("new_password");
            if(newPassword != null && !newPassword.isEmpty()) {
                updatedTherapist.setTherapist_password(newPassword);
            } else {
                updatedTherapist.setTherapist_password(currentTherapist.getTherapist_password());
            }

            // Set other fields with proper validation
            try {
                updatedTherapist.setTherapist_phonenum(request.getParameter("therapist_phonenum"));
                updatedTherapist.setTherapist_postcode(request.getParameter("therapist_postcode"));
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid phone number or postcode format");
                request.getRequestDispatcher("TherapistEditProfile.jsp").forward(request, response);
                return;
            }

            updatedTherapist.setTherapist_dob(java.sql.Date.valueOf(request.getParameter("therapist_dob")));
            updatedTherapist.setTherapist_IC(request.getParameter("therapist_IC"));
            updatedTherapist.setTherapist_address(request.getParameter("therapist_address"));
            updatedTherapist.setTherapist_state(request.getParameter("therapist_state"));
            updatedTherapist.setTherapist_district(request.getParameter("therapist_district"));
            updatedTherapist.setTherapist_specialization(request.getParameter("therapist_specialization"));
            updatedTherapist.setGender(request.getParameter("gender"));

            // Update in database
            therapistDao.updateTherapistProfile(updatedTherapist);
            
            // Update session with FRESH data from DB
            Therapist freshTherapist = therapistDao.getTherapistById(updatedTherapist.getTherapist_ID());
            session.setAttribute("therapist", freshTherapist); // Use same attribute name
            
            // Redirect to profile page with success message
            response.sendRedirect("TherapistProfile.jsp?success=Profile updated successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Update failed: " + e.getMessage());
            request.getRequestDispatcher("TherapistEditProfile.jsp").forward(request, response);
        }
    }

    private void deleteTherapist(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        therapistDao.deleteTherapist(id);
        response.sendRedirect("TherapistServlet?action=list");
    }

    private void listTherapists(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<Therapist> therapistList = therapistDao.getAllTherapists();
        request.setAttribute("therapists", therapistList);
        RequestDispatcher dispatcher = request.getRequestDispatcher("admin-view-therapist.jsp");
        dispatcher.forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Therapist existing = therapistDao.getTherapistById(id);
        request.setAttribute("therapist", existing);
        RequestDispatcher dispatcher = request.getRequestDispatcher("therapistForm.jsp");
        dispatcher.forward(request, response);
    }

   /* private void loginTherapist(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        String email = request.getParameter("therapist_email");
        String password = request.getParameter("therapist_password");

        Therapist t = therapistDao.validateLogin(email, password);

        if (t != null) {
            HttpSession session = request.getSession();
            session.setAttribute("therapist", t);
            response.sendRedirect("TherapistDashboard.jsp");
        } else {
            request.setAttribute("errorMsg", "Invalid email or password");
            RequestDispatcher dispatcher = request.getRequestDispatcher("TherapistLogin.jsp");
            dispatcher.forward(request, response);
        }
    }*/

   /* private Therapist extractTherapistFromRequest(HttpServletRequest request) {
        Therapist t = new Therapist();

        t.setTherapist_email(request.getParameter("therapist_email"));
        t.setTherapist_password(request.getParameter("therapist_password"));
        t.setTherapist_dob(java.sql.Date.valueOf(request.getParameter("therapist_dob")));
        t.setTherapist_IC(request.getParameter("therapist_IC"));
        t.setTherapist_fullname(request.getParameter("therapist_fullname"));
        t.setTherapist_phonenum(request.getParameter("therapist_phonenum"));
        t.setTherapist_address(request.getParameter("therapist_address"));
        t.setTherapist_state(request.getParameter("therapist_state"));
        t.setTherapist_district(request.getParameter("therapist_district"));
        t.setTherapist_postcode(request.getParameter("therapist_postcode"));
        t.setTherapist_specialization(request.getParameter("therapist_specialization"));
        t.setGender(request.getParameter("gender"));

        return t;
    }*/
}
