package com.Servlet;

import com.Dao.AppointmentDao;
import com.Dao.ClientDao;
import com.Model.Appointment;
import com.Model.Client;
import com.Utils.EmailUtil;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppointmentReminderServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            AppointmentDao appointmentDao = new AppointmentDao();
            ClientDao clientDao = new ClientDao();
            List<Appointment> appointments = appointmentDao.getTomorrowConfirmedAppointments();

            for (Appointment appt : appointments) {
                Client c = clientDao.getClientById(appt.getClientId());
                if (c != null) {
                    EmailUtil.sendAppointmentReminderEmail(
                            c.getClient_email(),
                            c.getClient_fullname(),
                            appt.getAppointmentDate(),
                            appt.getAppointmentTime(),
                            appt.getPackageName(),
                            appt.getTherapistName()
                    );
                
            
           
                }
            }

            response.getWriter().println("Reminder emails sent successfully!");

        } catch (SQLException ex) {
            Logger.getLogger(AppointmentReminderServlet.class.getName()).log(Level.SEVERE, null, ex);
            response.getWriter().println("Database error: " + ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error sending reminders: " + e.getMessage());
        }
    }
}
