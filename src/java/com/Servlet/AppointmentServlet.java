package com.Servlet;

import com.Dao.AppointmentDao;
import com.Dao.ClientDao;
import com.Dao.TherapistDao;
import com.Dao.TherapistScheduleDao;
import com.Dao.TherapyPackageDao;
import com.Model.Admin;
import com.Model.Appointment;
import com.Model.Client;
import com.Model.Therapist;
import com.Model.TherapistSchedule;
import com.Model.TherapyPackage;
import com.Utils.EmailUtil;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class AppointmentServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(AppointmentServlet.class.getName());
    private AppointmentDao appointmentDao;
    private TherapistScheduleDao scheduleDao;
    private TherapyPackageDao packageDao;
    private TherapistDao therapistDao;
    private ClientDao clientDao;
    @Override
    public void init() {
        appointmentDao = new AppointmentDao();
        scheduleDao = new TherapistScheduleDao();
        packageDao = new TherapyPackageDao();
        therapistDao = new TherapistDao();
        clientDao = new ClientDao();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if (action == null) {
                response.sendRedirect("client-view-packages.jsp");
                return;
            }

            switch (action) {
                case "bookNow":
                    showBookingPage(request, response);
                    break;
                    case "bookFromCart":  
            bookFromCart(request, response);
            break;
                case "viewSlots":
                    viewAvailableSlots(request, response);
                    break;
                case "clientAppointments":
                    viewClientAppointments(request, response);
                    break;
                case "therapistSchedule":
                    viewTherapistAppointments(request, response);
                    break;
                case "allAppointments":
                    viewAllAppointments(request, response);
                    break;

                case "viewBooking":
                    viewBookingConfirmation(request, response);
                    break;
                case "confirmPayment":
                    confirmPayment(request, response);
                    break;
                case "complete":
                    completeAppointment(request, response);
                    break;
               /* case "showRescheduleForm":
                    showRescheduleForm(request, response);
                    break;*/

                case "getRescheduleSlots":
                    getRescheduleSlots(request, response);
                    break;
                case "therapistRescheduleRequests":
                    viewTherapistRescheduleRequests(request, response);
                    break;
                 case "showRescheduleForm":
                    int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
                    showRescheduleForm(request, response, appointmentId);
                    break;

                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            }
        } catch (Exception e) {
            handleError(request, response, "Database error occurred", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if (action == null) {
                response.sendRedirect("client-view-packages.jsp");
                return;
            }

            switch (action) {
                case "checkSlots":
                    checkSlotsAjax(request, response);
                    break;
                case "book":
                    bookAppointment(request, response);
                    break;
 case "bookMultiple":
            bookMultiple(request, response);  // Tambah case baru ini
            break;
                case "cancel":
                    cancelAppointment(request, response);
                    break;
                    case "delete":
                    deleteAppointment(request, response);
                    break;
                case "complete":
                    completeAppointment(request, response);
                    break;
                case "reschedule":
                    handleReschedule(request, response);
                    break;
                case "therapistRequestReschedule":
                    therapistRequestReschedule(request, response);
                    break;
                     
                case "approveReschedule":
                    approveRescheduleRequest(request, response);
                    break;
                case "rejectReschedule":
                    rejectRescheduleRequest(request, response);
                    break;
                    case "markRefundCompleted":
    markRefundCompleted(request, response);
    break;

                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            }
        } catch (Exception e) {
            handleError(request, response, "Database error occurred", e);
        }
    }
    
    
   private void deleteAppointment(HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException {
    response.setContentType("application/json");
    PrintWriter out = response.getWriter();
    JSONObject jsonResponse = new JSONObject();

    try {
        // Verifikasi session admin
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Session expired. Please login again.");
            out.print(jsonResponse.toString());
            return;
        }

        // Validasi parameter
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Appointment ID is required");
            out.print(jsonResponse.toString());
            return;
        }

        int appointmentId = Integer.parseInt(idParam);
        
        // Lakukan penghapusan
        boolean success = appointmentDao.deleteAppointment(appointmentId);
        
        if (success) {
            response.setStatus(HttpServletResponse.SC_OK);
            jsonResponse.put("success", true);
            jsonResponse.put("message", "Appointment deleted successfully");
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Appointment not found or already deleted");
        }
    } catch (NumberFormatException e) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        jsonResponse.put("success", false);
        jsonResponse.put("message", "Invalid appointment ID format");
    } catch (SQLException e) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        jsonResponse.put("success", false);
        jsonResponse.put("message", "Database error: " + e.getMessage());
    } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        jsonResponse.put("success", false);
        jsonResponse.put("message", "Unexpected error: " + e.getMessage());
    } finally {
        out.print(jsonResponse.toString());
    }
}
    private void showBookingPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        int packageId = Integer.parseInt(request.getParameter("id"));
        TherapyPackage selectedPackage = packageDao.getPackageById(packageId);

        if (selectedPackage == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Package not found");
            return;
        }

        request.setAttribute("selectedPackage", selectedPackage);
        request.getRequestDispatcher("book-appointment.jsp").forward(request, response);
    }

    private void viewAvailableSlots(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        int packageId = Integer.parseInt(request.getParameter("packageId"));
        String gender = request.getParameter("gender");
        String dateStr = request.getParameter("date");

        TherapyPackage therapyPackage = packageDao.getPackageById(packageId);
        LocalDate date = LocalDate.parse(dateStr);
        String dayOfWeek = date.getDayOfWeek().toString();

        List<Therapist> therapists = therapistDao.getTherapistsByGender(gender);
        Map<Therapist, List<String>> availableSlotsMap = new LinkedHashMap<>();

        for (Therapist therapist : therapists) {
            List<String> slots = getAvailableSlotsForTherapist(
                    therapist.getTherapist_ID(),
                    dayOfWeek,
                    dateStr,
                    therapyPackage.getPackage_duration()
            );

            if (!slots.isEmpty()) {
                availableSlotsMap.put(therapist, slots);
            }
        }

        request.setAttribute("selectedPackage", therapyPackage);
        request.setAttribute("appointmentDate", dateStr);
        request.setAttribute("gender", gender);
        request.setAttribute("availableSlotsMap", availableSlotsMap);
        request.getRequestDispatcher("book-appointment.jsp").forward(request, response);
    }

    private void checkSlotsAjax(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        try {
            int packageId = Integer.parseInt(request.getParameter("packageId"));
            String gender = request.getParameter("gender");
            String dateStr = request.getParameter("date");

            TherapyPackage therapyPackage = packageDao.getPackageById(packageId);
            if (therapyPackage == null) {
                throw new IllegalArgumentException("Package not found");
            }

            List<Map<String, String>> availableSlots = appointmentDao.checkAvailableSlots(
                    therapyPackage.getPackage_duration(),
                    gender,
                    dateStr
            );

            JSONArray slotsArray = new JSONArray(availableSlots);
            jsonResponse.put("slots", slotsArray);
            out.print(jsonResponse.toString());

        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("error", "Failed to fetch slots: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(jsonResponse.toString());
        }
    }

    private void bookAppointment(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("clientId") == null) {
            response.sendRedirect("Login.jsp?error=session_expired");
            return;
        }

        int clientId = (int) session.getAttribute("clientId");
        int therapistId = Integer.parseInt(request.getParameter("therapistId"));
        int packageId = Integer.parseInt(request.getParameter("packageId"));
        String date = request.getParameter("appointmentDate");
        String time = request.getParameter("appointmentTime");
        String notes = request.getParameter("notes");

        Appointment appointment = new Appointment();
        appointment.setClientId(clientId);
        appointment.setTherapistId(therapistId);
        appointment.setPackageId(packageId);
        appointment.setAppointmentDate(date);
        appointment.setAppointmentTime(time);
        appointment.setAppointmentStatus("Pending");
        appointment.setNotes(notes);

        boolean success = appointmentDao.insertAppointment(appointment);
        

        if (success) {
            Client client = clientDao.getClientById(clientId);
            Appointment newAppt = appointmentDao.getLatestAppointment(clientId);
            TherapyPackage therapyPackage = packageDao.getPackageById(packageId);
            Therapist therapist = therapistDao.getTherapistById(therapistId);
            
            //  Hantar email pending
       EmailUtil.sendPendingEmail(
            client.getClient_email(),
            client.getClient_fullname(),
            date,
            time,
            therapyPackage.getPackage_name(),
            therapist.getTherapist_fullname()
        );
            request.setAttribute("appointment", newAppt);
            request.setAttribute("package", therapyPackage);
            request.setAttribute("therapist", therapist);
            request.getRequestDispatcher("booking-confirmation.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Failed to book appointment");
            request.getRequestDispatcher("book-appointment.jsp").forward(request, response);
        }
    }

    private List<String> getAvailableSlotsForTherapist(int therapistId, String dayOfWeek,
            String dateStr, int duration) throws SQLException {
        List<String> slots = new ArrayList<>();
        TherapistSchedule schedule = scheduleDao.getScheduleByTherapistAndDay(therapistId, dayOfWeek);

        if (schedule == null || !schedule.isIsActive()) {
            return slots;
        }

        LocalTime start = LocalTime.parse(schedule.getStartTime());
        LocalTime end = LocalTime.parse(schedule.getEndTime());
        List<String> bookedTimes = appointmentDao.getBookedTimes(therapistId, dateStr);

        while (start.plusMinutes(duration).isBefore(end.plusSeconds(1))) {
            String timeStr = start.toString();
            if (!bookedTimes.contains(timeStr)) {
                slots.add(timeStr);
            }
            start = start.plusMinutes(duration);
        }

        return slots;
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response,
            String message, Exception e) throws ServletException, IOException {
        e.printStackTrace();
        request.setAttribute("errorMessage", message + ": " + e.getMessage());
        request.getRequestDispatcher("error.jsp").forward(request, response);
    }

    // For client to view their appointments
    private void viewClientAppointments(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("clientId") == null) {
            response.sendRedirect("Login.jsp?error=session_expired");
            return;
        }

        int clientId = (int) session.getAttribute("clientId");
        List<Appointment> appointments = appointmentDao.getClientAppointments(clientId);

        request.setAttribute("appointments", appointments);
        request.getRequestDispatcher("client-appointments.jsp").forward(request, response);
    }

// For therapist to view their confirmed appointments
    private void viewTherapistAppointments(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("therapistId") == null) {
            response.sendRedirect("TherapistLogin.jsp?error=session_expired");
            return;
        }

        int therapistId = (int) session.getAttribute("therapistId");
        List<Appointment> appointments = appointmentDao.getTherapistAppointments(therapistId);

        request.setAttribute("appointments", appointments);
        request.getRequestDispatcher("therapist-schedule.jsp").forward(request, response);
    }

    private void viewAllAppointments(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendRedirect("AdminLogin.jsp?error=session_expired");
            return;
        }

        try {
            AppointmentDao appointmentDao = new AppointmentDao();
            List<Appointment> appointments = appointmentDao.getAllAppointmentsWithDetails();

            if (appointments == null) {
                throw new SQLException("Failed to retrieve appointments");
            }

            request.setAttribute("appointments", appointments);
            request.getRequestDispatcher("admin-appointments.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error retrieving appointments: " + e.getMessage());
            request.getRequestDispatcher("AdminDashboard.jsp").forward(request, response);
        }
    }

//  payment confirmation endpoint
    private void confirmPayment(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        String paymentId = request.getParameter("paymentId");
        int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
        

        boolean paymentUpdated = appointmentDao.updatePaymentId(appointmentId, paymentId);
        boolean statusUpdated = appointmentDao.updateAppointmentStatus(appointmentId, "Confirmed");
       
        if (paymentUpdated && statusUpdated) {
            response.sendRedirect("booking-confirmation.jsp?success=payment_confirmed");
        } else {
            response.sendRedirect("payment-error.jsp");
        }
    }

    private void viewBookingConfirmation(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("clientId") == null) {
            response.sendRedirect("Login.jsp?error=session_expired");
            return;
        }

        int appointmentId = Integer.parseInt(request.getParameter("id"));
        Appointment appointment = appointmentDao.getAppointmentById(appointmentId);

        if (appointment == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Appointment not found");
            return;
        }

        int clientId = (int) session.getAttribute("clientId");
        if (appointment.getClientId() != clientId) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to view this appointment");
            return;
        }

        TherapyPackage therapyPackage = packageDao.getPackageById(appointment.getPackageId());
        Therapist therapist = therapistDao.getTherapistById(appointment.getTherapistId());

        request.setAttribute("appointment", appointment);
        request.setAttribute("package", therapyPackage);
        request.setAttribute("therapist", therapist);

        request.getRequestDispatcher("booking-confirmation.jsp").forward(request, response);
    }

    private void cancelAppointment(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("clientId") == null) {
            response.sendRedirect("Login.jsp?error=session_expired");
            return;
        }
int clientId = (int) session.getAttribute("clientId");  
        int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
        boolean success = appointmentDao.cancelAppointment(appointmentId);

        if (success) {
             Client client = clientDao.getClientById(clientId);
              EmailUtil.sendCancelledEmail(client.getClient_email(), client.getClient_fullname());
            response.sendRedirect("AppointmentServlet?action=clientAppointments&success=cancelled");
           
        } else {
            response.sendRedirect("AppointmentServlet?action=clientAppointments&error=cancel_failed");
        }
    }

    private void completeAppointment(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("therapistId") == null) {
            response.sendRedirect("TherapistLogin.jsp?error=session_expired");
            return;
        }
       
      int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));

// Ambil appointment dulu
Appointment appt = appointmentDao.getAppointmentById(appointmentId);

// Pastikan appointment wujud
if (appt != null) {
    int clientId = appt.getClientId();
    Client client = clientDao.getClientById(clientId);

    boolean success = appointmentDao.completeAppointment(appointmentId);

    if (success) {
        EmailUtil.sendCompletedEmail(client.getClient_email(), client.getClient_fullname());
        response.sendRedirect("AppointmentServlet?action=therapistSchedule&success=appointment_completed");
    } else {
        response.sendRedirect("AppointmentServlet?action=therapistSchedule&error=complete_failed");
    }
} else {
    response.sendRedirect("AppointmentServlet?action=therapistSchedule&error=appointment_not_found");
}
    }

    //guna nanti
//direct ke form untuk reschedule
    private void showRescheduleForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("clientId") == null) {
                logger.warning("Unauthorized access attempt - no valid session");
                response.sendRedirect("Login.jsp?error=session_expired");
                return;
            }

            int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
            int clientId = (int) session.getAttribute("clientId");

            // Log permintaan reschedule
            logger.info("Attempting to reschedule appointment ID: " + appointmentId + " for client ID: " + clientId);

            Appointment appointment = appointmentDao.getAppointmentById(appointmentId);
            if (appointment == null) {
                logger.warning("Appointment not found - ID: " + appointmentId);
                request.setAttribute("error", "Appointment not found");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }

            if (appointment.getClientId() != clientId) {
                logger.warning("Authorization failed - Client ID mismatch: " + clientId + " vs " + appointment.getClientId());
                request.setAttribute("error", "You are not authorized to reschedule this appointment");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }

            if (!"Confirmed".equals(appointment.getAppointmentStatus())) {
                logger.warning("Invalid status for reschedule - Status: " + appointment.getAppointmentStatus());
                request.setAttribute("error", "Only confirmed appointments can be rescheduled");
                request.getRequestDispatcher("client-appointments.jsp").forward(request, response);
                return;
            }

            // Log sebelum forward ke halaman reschedule
            logger.info("Loading reschedule form for appointment ID: " + appointmentId);

            TherapyPackage therapyPackage = packageDao.getPackageById(appointment.getPackageId());
            Therapist therapist = therapistDao.getTherapistById(appointment.getTherapistId());

            request.setAttribute("appointment", appointment);
            request.setAttribute("package", therapyPackage);
            request.setAttribute("therapist", therapist);
            request.getRequestDispatcher("reschedule-appointment.jsp").forward(request, response);

        } catch (Exception e) {
            logger.severe("Error in showRescheduleForm: " + e.getMessage());
            request.setAttribute("error", "System error: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    // In AppointmentServlet.java
    private void handleReschedule(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    HttpSession session = request.getSession(false);
    if (session == null || session.getAttribute("clientId") == null) {
        response.sendRedirect("Login.jsp?error=session_expired");
        return;
    }

    try {
        int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
        String newDate = request.getParameter("appointmentDate");
        String newTime = request.getParameter("appointmentTime");
        int clientId = (int) session.getAttribute("clientId");
        
        System.out.println("DEBUG: Reschedule attempt - ID: " + appointmentId + 
                         ", Date: " + newDate + ", Time: " + newTime);

        // Validate input
        if (newDate == null || newTime == null || newDate.isEmpty() || newTime.isEmpty()) {
            throw new Exception("Date and time are required");
        }

        // Get appointment
        Appointment appointment = appointmentDao.getAppointmentById(appointmentId);
        if (appointment == null) {
            throw new Exception("Appointment not found");
        }

        // Validate user
        if (appointment.getClientId() != clientId) {
            throw new Exception("You are not authorized to reschedule this appointment");
        }

        // Validate status
        if (!"Confirmed".equals(appointment.getAppointmentStatus()) && 
            !"Reschedule Approved".equals(appointment.getAppointmentStatus())) {
            throw new Exception("Only confirmed appointments can be rescheduled");
        }

        // Reschedule
        boolean success = appointmentDao.rescheduleAppointment(appointmentId, newDate, newTime);
        
        if (success) {
            // Update status
            appointmentDao.updateAppointmentStatus(appointmentId, "Confirmed");

            // Get client details
            Client client = clientDao.getClientById(clientId);
            if (client != null) {
                EmailUtil.sendRescheduleEmail(
                    client.getClient_email(),
                    client.getClient_fullname(),
                    newDate,
                    newTime
                );
            }

            response.sendRedirect("client-appointments.jsp?success=rescheduled");
        } else {
            throw new Exception("Failed to update appointment in database");
        }
    } catch (Exception e) {
        e.printStackTrace();
        request.setAttribute("error", "Reschedule failed: " + e.getMessage());
        request.getRequestDispatcher("reschedule-appointment.jsp").forward(request, response);
    }
}

    private void showRescheduleForm(HttpServletRequest request, HttpServletResponse response, int appointmentId)
            throws ServletException, IOException {
       try {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("clientId") == null) {
            logger.warning("Unauthorized access attempt - no valid session");
            response.sendRedirect("Login.jsp?error=session_expired");
            return;
        }

        //int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
        int clientId = (int) session.getAttribute("clientId");
        

       Appointment appointment = appointmentDao.getAppointmentById(appointmentId);
        if (appointment == null) {
            throw new Exception("Appointment not found");
        }

        TherapyPackage therapyPackage = packageDao.getPackageById(appointment.getPackageId());
        if (therapyPackage == null) {
            throw new Exception("Therapy package not found for this appointment");
        }

        Therapist therapist = therapistDao.getTherapistById(appointment.getTherapistId());
        if (therapist == null) {
            throw new Exception("Therapist not found for this appointment");
        }

             if (appointment.getClientId() != clientId || 
            (!"Confirmed".equals(appointment.getAppointmentStatus()) && 
             !"Reschedule Approved".equals(appointment.getAppointmentStatus()))) {
            logger.warning("Authorization failed - Client ID mismatch or invalid status");
            request.setAttribute("error", "You are not authorized to reschedule this appointment");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

          
            request.setAttribute("appointment", appointment);
            request.setAttribute("package", therapyPackage);
            request.setAttribute("therapist", therapist);

            request.getRequestDispatcher("reschedule-appointment.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading reschedule form: " + e.getMessage());
        response.sendRedirect("client-appointments.jsp?error=load_failed");
        }
    }
//later

    private void sendRescheduleNotification(Appointment appointment, String newDate, String newTime) {
        // Implement notification logic (email, SMS, etc.) to therapist
        // This is just a placeholder - implement according to your notification system
        System.out.println("Notification: Appointment " + appointment.getAppointmentId()
                + " rescheduled to " + newDate + " at " + newTime);
    }

    // In AppointmentServlet.java
    private void getRescheduleSlots(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

       try {
        int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
        String dateStr = request.getParameter("date");

        // Validasi appointment
        Appointment appointment = appointmentDao.getAppointmentById(appointmentId);
        if (appointment == null || 
            (!"Confirmed".equals(appointment.getAppointmentStatus()) && 
             !"Reschedule Approved".equals(appointment.getAppointmentStatus()))) {
            jsonResponse.put("error", "Invalid appointment status for rescheduling");
            out.print(jsonResponse.toString());
            return;
        }


            TherapyPackageDao packageDao = new TherapyPackageDao();
            TherapyPackage therapyPackage = packageDao.getPackageById(appointment.getPackageId());

            TherapistDao therapistDao = new TherapistDao();
            Therapist therapist = therapistDao.getTherapistById(appointment.getTherapistId());

            List<String> availableSlots = appointmentDao.getAvailableSlotsForReschedule(
                    appointment.getTherapistId(),
                    dateStr,
                    therapyPackage.getPackage_duration()
            );

            jsonResponse.put("therapistName", therapist.getTherapist_fullname());
            jsonResponse.put("slots", availableSlots);
            out.print(jsonResponse.toString());

        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("error", "System error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(jsonResponse.toString());
        }
    }

    //>>>>>>>>> FOR REQUEST RESCHEDULE BY THERAPIST <<<<<<<<<<<<
    // Therapist request reschedule
   private void therapistRequestReschedule(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException, SQLException {
    HttpSession session = request.getSession(false);
    if (session == null || session.getAttribute("therapistId") == null) {
        response.sendRedirect("TherapistLogin.jsp?error=session_expired");
        return;
    }

    int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
    String reason = request.getParameter("reason");

    // Dapatkan appointment
    Appointment appt = appointmentDao.getAppointmentById(appointmentId);
    if (appt != null) {
        int clientId = appt.getClientId();
        Client client = clientDao.getClientById(clientId);

        boolean success = appointmentDao.therapistRequestReschedule(appointmentId, reason);

        if (success) {
            EmailUtil.sendTherapistRequestRescheduleEmail(
                client.getClient_email(),
                client.getClient_fullname(),
                reason
            );
            response.sendRedirect("AppointmentServlet?action=therapistSchedule&success=reschedule_requested");
        } else {
            response.sendRedirect("AppointmentServlet?action=therapistSchedule&error=reschedule_failed");
        }
    } else {
        response.sendRedirect("AppointmentServlet?action=therapistSchedule&error=appointment_not_found");
    }
}

    
   
// Client approve reschedule request
    private void approveRescheduleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("clientId") == null) {
            response.sendRedirect("Login.jsp?error=session_expired");
            return;
        }

        int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));

        try {
            boolean success = appointmentDao.approveTherapistReschedule(appointmentId);

            if (success) {
                response.sendRedirect("AppointmentServlet?action=clientAppointments&success=reschedule_approved");
            } else {
                response.sendRedirect("AppointmentServlet?action=clientAppointments&error=approve_failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("AppointmentServlet?action=clientAppointments&error=system_error");
        }
    }

// Client reject reschedule request
    private void rejectRescheduleRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    HttpSession session = request.getSession(false);
    if (session == null || session.getAttribute("clientId") == null) {
        response.sendRedirect("Login.jsp?error=session_expired");
        return;
    }

    int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
    boolean cancelAppointment = "true".equals(request.getParameter("cancel"));
    String refundNotes = "You will be contacted within 3 working days for refund process.";

    try {
        boolean success = appointmentDao.rejectTherapistReschedule(
            appointmentId, 
            cancelAppointment,
            cancelAppointment ? refundNotes : null
        );
        
        if (success) {
            String message = cancelAppointment ? 
                "refund_pending" : "reschedule_rejected";
            response.sendRedirect("client-appointments.jsp?success=" + message);
        } else {
            response.sendRedirect("client-appointments.jsp?error=reject_failed");
        }
    } catch (Exception e) {
        e.printStackTrace();
        response.sendRedirect("client-appointments.jsp?error=system_error");
    }
}
    private void viewTherapistRescheduleRequests(HttpServletRequest request, HttpServletResponse response) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
   private void markRefundCompleted(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException, SQLException {
    HttpSession session = request.getSession(false);
    if (session == null || session.getAttribute("admin") == null) {
        response.sendRedirect("AdminLogin.jsp?error=session_expired");
        return;
    }

    int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
    String refundMethod = request.getParameter("refundMethod");
    String refundNotes = request.getParameter("refundNotes");

    // Dapatkan appointment
    Appointment appt = appointmentDao.getAppointmentById(appointmentId);
    if (appt != null) {
        int clientId = appt.getClientId();
        Client client = clientDao.getClientById(clientId);

        boolean success = appointmentDao.markRefundCompleted(appointmentId, refundMethod, refundNotes);

        if (success) {
            EmailUtil.sendRefundCompletedEmail(
                client.getClient_email(),
                client.getClient_fullname(),
                refundMethod,
                refundNotes
            );
            response.sendRedirect("AppointmentServlet?action=allAppointments&success=refund_completed");
        } else {
            response.sendRedirect("AppointmentServlet?action=allAppointments&error=refund_failed");
        }
    } else {
        response.sendRedirect("AppointmentServlet?action=allAppointments&error=appointment_not_found");
    }
}
protected void bookFromCart(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    
    HttpSession session = request.getSession();
    List<TherapyPackage> cart = (List<TherapyPackage>) session.getAttribute("cart");
    
    if (cart == null || cart.isEmpty()) {
        session.setAttribute("error", "Your cart is empty");
        response.sendRedirect("view-cart.jsp");
        return;
    }
    
    // Forward to a new booking page for multiple packages
    request.setAttribute("cartPackages", cart);
    request.getRequestDispatcher("book-multiple.jsp").forward(request, response);
}

protected void bookMultiple(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    
    HttpSession session = request.getSession();
    Client client = (Client) session.getAttribute("client");
    
    if (client == null) {
        response.sendRedirect("Login.jsp");
        return;
    }
    
    List<TherapyPackage> cart = (List<TherapyPackage>) session.getAttribute("cart");
    if (cart == null || cart.isEmpty()) {
        session.setAttribute("error", "Your cart is empty");
        response.sendRedirect("view-cart.jsp");
        return;
    }
    
    String gender = request.getParameter("gender");
    String appointmentDate = request.getParameter("appointmentDate");
    String notes = request.getParameter("notes");
    
    try {
        // Validate the date format first
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        
        // This will throw ParseException if date is invalid
        Date parsedDate = sdf.parse(appointmentDate);
        
        // Verify date is in the future
        if (parsedDate.before(new Date())) {
            session.setAttribute("error", "Appointment date must be in the future");
            response.sendRedirect("view-cart.jsp");
            return;
        }
        
        AppointmentDao appointmentDao = new AppointmentDao();
        List<Appointment> createdAppointments = new ArrayList<>();
        boolean allSuccessful = true;
        
        for (TherapyPackage p : cart) {
            String therapistIdParam = "therapistId-" + p.getPackage_ID();
            String timeParam = "appointmentTime-" + p.getPackage_ID();
            
            int therapistId = Integer.parseInt(request.getParameter(therapistIdParam));
            String appointmentTime = request.getParameter(timeParam);
            
            Appointment appointment = new Appointment();
            appointment.setClientId(client.getClient_ID());
            appointment.setTherapistId(therapistId);
            appointment.setPackageId(p.getPackage_ID());
            appointment.setAppointmentDate(appointmentDate); // Using String directly
            appointment.setAppointmentTime(appointmentTime);
            appointment.setAppointmentStatus("Pending");
            appointment.setNotes(notes);
            appointment.setPackagePrice(p.getPackage_price());
            
            boolean success = appointmentDao.insertAppointment(appointment);
            if (success) {
                createdAppointments.add(appointment);
            } else {
                allSuccessful = false;
                session.setAttribute("error", "Failed to book appointment for package: " + p.getPackage_name());
                break;
            }
        }
        
        if (allSuccessful && !createdAppointments.isEmpty()) {
            cart.clear();
            session.setAttribute("successMessage", "Successfully booked " + createdAppointments.size() + " appointments");
            response.sendRedirect("AppointmentServlet?action=clientAppointments");
        } else {
            if (createdAppointments.isEmpty()) {
                session.setAttribute("error", "Failed to create any appointments");
            }
            response.sendRedirect("view-cart.jsp");
        }
        
    } catch (ParseException e) {
        session.setAttribute("error", "Invalid date format. Please use YYYY-MM-DD format");
        response.sendRedirect("view-cart.jsp");
    } catch (NumberFormatException e) {
        session.setAttribute("error", "Invalid therapist selection");
        response.sendRedirect("view-cart.jsp");
    } catch (Exception e) {
        e.printStackTrace();
        session.setAttribute("error", "System error: " + e.getMessage());
        response.sendRedirect("view-cart.jsp");
    }
}
}
