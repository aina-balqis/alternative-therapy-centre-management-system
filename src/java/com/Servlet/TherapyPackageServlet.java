package com.Servlet;

import com.Dao.FeedbackDao;
import com.Dao.TherapyPackageDao;
import com.Model.Feedback;
import com.Model.TherapyPackage;
import java.io.File;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.annotation.MultipartConfig;

//@WebServlet("/TherapyPackageServlet")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50)   // 50MB

public class TherapyPackageServlet extends HttpServlet {

    private TherapyPackageDao therapyPackageDao = new TherapyPackageDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            switch (action != null ? action : "") {
                case "view":
                    viewAllPackages(request, response);
                    break;
                case "clientView":
                    viewClientPackages(request, response);
                    break;
                case "clientDashboard":
                    clientDashboard(request, response);
                    break;
                case "edit":
                    getSinglePackage(request, response);
                    break;
                case "delete":
                    deletePackage(request, response);
                    break;

                case "details":
                    viewPackageDetails(request, response);
                    break;
                default:
                    viewAllPackages(request, response);
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
            switch (action) {
                case "add":
                    addPackage(request, response);
                    break;
                case "update":
                    updatePackage(request, response);
                    break;
                default:
                    response.sendRedirect("TherapyPackageServlet?action=view");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error processing form: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    //client view package
    private void viewClientPackages(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<TherapyPackage> packages = therapyPackageDao.getAllPackages();
        request.setAttribute("packages", packages);
        request.getRequestDispatcher("client-view-packages.jsp").forward(request, response);
    }

    private void clientDashboard(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<TherapyPackage> packages = therapyPackageDao.getAllPackages();
        request.setAttribute("packages", packages);
        request.getRequestDispatcher("ClientDashboard.jsp").forward(request, response);  // Ensure this path is correct
    }

    // View all packages
    private void viewAllPackages(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<TherapyPackage> packages = therapyPackageDao.getAllPackages();
        request.setAttribute("packages", packages);
        request.getRequestDispatcher("admin-view-packages.jsp").forward(request, response);
    }

    // Get single package for editing
    private void getSinglePackage(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        TherapyPackage p = therapyPackageDao.getPackageById(id);
        request.setAttribute("package", p);
        request.getRequestDispatcher("admin-edit-package.jsp").forward(request, response);
    }

    // Add new package
    private void addPackage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        String name = request.getParameter("package_name");
        String description = request.getParameter("package_description");
        double price = Double.parseDouble(request.getParameter("package_price"));
        int duration = Integer.parseInt(request.getParameter("package_duration"));
        boolean isActive = Boolean.parseBoolean(request.getParameter("is_active"));

        // Handle file upload
        Part filePart = request.getPart("image_url");
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // Get file name
        String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";

// Create upload directory if not exists
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

// Validate and upload file
        if (filePart != null && fileName != null && !fileName.isEmpty()) {
            filePart.write(uploadPath + File.separator + fileName);
            System.out.println("File uploaded: " + fileName); // Debug
        } else {
            throw new ServletException("File upload failed: Missing file part or filename");
        }

// Save to DB
        TherapyPackage p = new TherapyPackage();
        p.setPackage_name(name);
        p.setPackage_description(description);
        p.setPackage_price(price);
        p.setPackage_duration(duration);
        p.setIs_active(isActive);
        p.setImage_url("uploads/" + fileName); // Store relative path

        therapyPackageDao.addPackage(p);
        response.sendRedirect("TherapyPackageServlet?action=view");
    }

    // Update package
    private void updatePackage(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {

        TherapyPackage p = new TherapyPackage();
        p.setPackage_ID(Integer.parseInt(request.getParameter("package_ID")));
        p.setPackage_name(request.getParameter("package_name"));
        p.setPackage_description(request.getParameter("package_description"));
        p.setPackage_price(Double.parseDouble(request.getParameter("package_price")));
        p.setPackage_duration(Integer.parseInt(request.getParameter("package_duration")));
        p.setIs_active(Boolean.parseBoolean(request.getParameter("is_active")));

        // Handle image upload
        Part filePart = request.getPart("package_image");
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

        String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        if (fileName != null && !fileName.isEmpty()) {
            // Save uploaded file
            String filePath = uploadPath + File.separator + fileName;
            filePart.write(filePath);
            p.setImage_url("uploads/" + fileName); // relative path to be used in <img src=...>
        } else {
            // If no new file uploaded, use existing image
            String existingImageUrl = request.getParameter("existing_image_url");
            p.setImage_url(existingImageUrl);
        }

        therapyPackageDao.updatePackage(p);
        response.sendRedirect("TherapyPackageServlet?action=view");
    }

    // Delete package
    private void deletePackage(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        therapyPackageDao.deletePackage(id);
        response.sendRedirect("TherapyPackageServlet?action=view");
    }

    private void viewPackageDetails(HttpServletRequest request, HttpServletResponse response)
        throws SQLException, ServletException, IOException {
    int packageId = Integer.parseInt(request.getParameter("id"));
    TherapyPackage therapyPackage = therapyPackageDao.getPackageById(packageId);
    
    // Get feedback for this package
    FeedbackDao feedbackDao = new FeedbackDao();
    List<Feedback> feedbacks = feedbackDao.getFeedbackByPackage(packageId);
    
    // Calculate average rating - using traditional loop instead of streams
    double averageRating = 0.0;
    if (!feedbacks.isEmpty()) {
        int total = 0;
        for (Feedback feedback : feedbacks) {
            total += feedback.getRating();
        }
        averageRating = (double) total / feedbacks.size();
    }
    
    request.setAttribute("package", therapyPackage);
    request.setAttribute("feedbacks", feedbacks);
    request.setAttribute("averageRating", averageRating);
    request.setAttribute("feedbackCount", feedbacks.size());
    
    request.getRequestDispatcher("package-details.jsp").forward(request, response);
}
}
