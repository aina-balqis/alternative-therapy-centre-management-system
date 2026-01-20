package com.Servlet;

import com.Model.TherapyPackage;
import com.Dao.TherapyPackageDao;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class CartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        List<TherapyPackage> cart = (List<TherapyPackage>) session.getAttribute("cart");

        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }

        try {
            if ("add".equals(action)) {
                int package_ID = Integer.parseInt(request.getParameter("package_ID"));
                TherapyPackageDao dao = new TherapyPackageDao();
                TherapyPackage therapyPackage = dao.getPackageById(package_ID);

                if (therapyPackage != null) {
                    boolean alreadyInCart = false;
                    for (TherapyPackage p : cart) {
                        if (p.getPackage_ID() == package_ID) {
                            alreadyInCart = true;
                            break;
                        }
                    }

                    if (!alreadyInCart) {
                        cart.add(therapyPackage);
                        session.setAttribute("successMessage", "Package added to cart successfully!");
                    } else {
                        session.setAttribute("warningMessage", "This package is already in your cart");
                    }
                }
                response.sendRedirect("TherapyPackageServlet?action=clientView");

            } else if ("remove".equals(action)) {
                int package_ID = Integer.parseInt(request.getParameter("package_ID"));
                Iterator<TherapyPackage> iterator = cart.iterator();
                while (iterator.hasNext()) {
                    TherapyPackage p = iterator.next();
                    if (p.getPackage_ID() == package_ID) {
                        iterator.remove();
                        session.setAttribute("successMessage", "Package removed from cart");
                        break;
                    }
                }
                response.sendRedirect("view-cart.jsp");

            } else if ("view".equals(action)) {
                request.getRequestDispatcher("view-cart.jsp").forward(request, response);

            } else if ("clear".equals(action)) {
                cart.clear();
                session.setAttribute("successMessage", "Cart cleared successfully");
                response.sendRedirect("view-cart.jsp");

            } else {
                response.sendRedirect("view-cart.jsp");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Error processing your request");
            response.sendRedirect("view-cart.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
