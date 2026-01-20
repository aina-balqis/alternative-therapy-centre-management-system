package com.Dao;

import com.Model.Appointment;
import com.Model.Therapist;
import com.Model.TherapistSchedule;
import java.sql.Date;
import java.sql.*;
import java.util.*;
import com.Dao.DBConnection;
import com.Model.TherapyPackage;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;


public class AppointmentDao {

    private Connection conn;

    public AppointmentDao() {
        conn = DBConnection.getConnection();
    }

    public AppointmentDao(Connection conn) {
        this.conn = conn;
    }

    // Method untuk create appointment
    public boolean insertAppointment(Appointment appt) {
        String sql = "INSERT INTO appointment (client_id, therapist_id, package_id, "
                + "appointment_date, appointment_time, appointment_status, notes) "
                + "VALUES (?, ?, ?, ?, ?, 'Pending', ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appt.getClientId());
            stmt.setInt(2, appt.getTherapistId());
            stmt.setInt(3, appt.getPackageId());
            stmt.setString(4, appt.getAppointmentDate());  // Format: YYYY-MM-DD
            stmt.setString(5, appt.getAppointmentTime());  // Format: HH:MM:SS
            stmt.setString(6, appt.getNotes());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAppointment(int appointmentId) throws SQLException {
        String sql = "DELETE FROM appointment WHERE appointment_id = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, appointmentId);
            int affectedRows = stmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting appointment: " + e.getMessage());
            throw e; // Re-throw exception untuk handling di servlet
        }
    }

    public List<String> getBookedTimes(int therapistId, String date) {
        List<String> bookedTimes = new ArrayList<>();
        String sql = "SELECT appointment_time FROM appointment WHERE therapist_id = ? AND appointment_date = ? AND appointment_status IN ('Pending', 'Confirmed')";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, therapistId);
            stmt.setString(2, date);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookedTimes.add(rs.getString("appointment_time"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookedTimes;
    }

    public List<Map<String, String>> checkAvailableSlots(int packageDuration, String gender, String date) {
        List<Map<String, String>> availableSlots = new ArrayList<>();
        TherapistDao therapistDao = new TherapistDao();
        TherapistScheduleDao scheduleDao = new TherapistScheduleDao();

        // 1. Dapatkan senarai therapist berdasarkan gender
        List<Therapist> therapistList = therapistDao.getTherapistsByGender(gender);

        // 2. Dapatkan hari dalam minggu dari tarikh yang dipilih
        LocalDate appointmentDate = LocalDate.parse(date);
        String dayOfWeek = appointmentDate.getDayOfWeek().toString();
        dayOfWeek = dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1).toLowerCase();

        for (Therapist therapist : therapistList) {
            // 3. Dapatkan jadual therapist untuk hari tersebut
            TherapistSchedule schedule = scheduleDao.getScheduleByTherapistAndDay(
                    therapist.getTherapist_ID(),
                    dayOfWeek
            );

            // 4. Skip jika therapist tidak bekerja pada hari tersebut
            if (schedule == null || !schedule.isIsActive()) {
                continue;
            }

            // 5. Generate semua slot 30 minit dalam jadual therapist
            List<String> allSlots = generateTimeSlots(
                    schedule.getStartTime(),
                    schedule.getEndTime(),
                    30 // Generate slot 30 menit terlepas dari package duration
            );

            // 6. Dapatkan tempahan yang sudah ada
            List<String> bookedTimes = getBookedTimes(therapist.getTherapist_ID(), date);

            // 7. Semak setiap slot untuk availability
            for (String slot : allSlots) {
                LocalTime slotStart = LocalTime.parse(slot);
                LocalTime slotEnd = slotStart.plusMinutes(packageDuration);

                boolean isAvailable = true;

                // 8. Semak overlap dengan tempahan lain
                for (String booked : bookedTimes) {
                    LocalTime bookedStart = LocalTime.parse(booked);
                    LocalTime bookedEnd = bookedStart.plusMinutes(packageDuration);

                    if (slotStart.isBefore(bookedEnd) && slotEnd.isAfter(bookedStart)) {
                        isAvailable = false;
                        break;
                    }
                }

                // 9. Jika slot available, tambahkan ke senarai
                if (isAvailable) {
                    Map<String, String> slotInfo = new HashMap<>();
                    slotInfo.put("therapistId", String.valueOf(therapist.getTherapist_ID()));
                    slotInfo.put("therapistName", therapist.getTherapist_fullname());
                    slotInfo.put("time", slot);
                    availableSlots.add(slotInfo);
                }
            }
        }
        return availableSlots;
    }

    private List<String> generateTimeSlots(String start, String end, int packageDuration) {
        List<String> slots = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        LocalTime startTime = LocalTime.parse(start, formatter);
        LocalTime endTime = LocalTime.parse(end, formatter);

        while (startTime.plusMinutes(packageDuration).compareTo(endTime) <= 0) {
            slots.add(startTime.format(DateTimeFormatter.ofPattern("HH:mm")));
            startTime = startTime.plusMinutes(30); // Tetap menggunakan interval 30 menit
        }
        return slots;
    }

    // Tambahkan method berikut ke dalam AppointmentDao anda:
// Get appointment by ID
    public Appointment getAppointmentById(int appointmentId) {
        String sql = "SELECT * FROM appointment WHERE appointment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointmentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Appointment appt = new Appointment();
                appt.setAppointmentId(rs.getInt("appointment_id"));
                appt.setClientId(rs.getInt("client_id"));
                appt.setTherapistId(rs.getInt("therapist_id"));
                appt.setPackageId(rs.getInt("package_id"));
                appt.setAppointmentDate(rs.getString("appointment_date"));
                appt.setAppointmentTime(rs.getString("appointment_time"));
                appt.setAppointmentStatus(rs.getString("appointment_status"));
                appt.setNotes(rs.getString("notes"));
                return appt;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

// Get appointments by client ID
    public List<Appointment> getClientAppointments(int clientId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.package_name, p.package_duration, p.package_price, "
                + "t.therapist_fullname FROM appointment a "
                + "JOIN therapy_package p ON a.package_id = p.package_id "
                + "JOIN therapist t ON a.therapist_id = t.therapist_id "
                + "WHERE a.client_id = ? ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Appointment appt = new Appointment();
                appt.setAppointmentId(rs.getInt("appointment_id"));
                appt.setClientId(rs.getInt("client_id"));
                appt.setTherapistId(rs.getInt("therapist_id"));
                appt.setPackageId(rs.getInt("package_id"));
                appt.setAppointmentDate(rs.getString("appointment_date"));
                appt.setAppointmentTime(rs.getString("appointment_time"));
                appt.setAppointmentStatus(rs.getString("appointment_status"));
                appt.setNotes(rs.getString("notes"));

                // Set additional fields for display
                appt.setPackageName(rs.getString("package_name"));
                appt.setPackageDuration(rs.getInt("package_duration"));
                appt.setPackagePrice(rs.getDouble("package_price"));
                appt.setTherapistName(rs.getString("therapist_fullname"));

                appointments.add(appt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

// Get appointments by therapist ID (all)
    public List<Appointment> getTherapistAppointments(int therapistId) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        Connection conn = DBConnection.getConnection();

        String sql = "SELECT a.*, c.client_fullname, c.client_email, c.client_phonenum, "
                + "p.package_name, p.package_duration, p.package_price "
                + "FROM appointment a "
                + "JOIN client c ON a.client_id = c.client_ID "
                + "JOIN therapy_package p ON a.package_id = p.package_ID "
                + "WHERE a.therapist_id = ? "
                + "ORDER BY a.appointment_date, a.appointment_time";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, therapistId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Appointment appt = new Appointment();
            appt.setAppointmentId(rs.getInt("appointment_id"));
            appt.setClientId(rs.getInt("client_id"));
            appt.setTherapistId(rs.getInt("therapist_id"));
            appt.setPackageId(rs.getInt("package_id"));
            appt.setAppointmentDate(rs.getString("appointment_date"));
            appt.setAppointmentTime(rs.getString("appointment_time"));
            appt.setAppointmentStatus(rs.getString("appointment_status"));
            appt.setNotes(rs.getString("notes"));

            // Set client details
            appt.setClientName(rs.getString("client_fullname"));
            appt.setClientEmail(rs.getString("client_email"));
            appt.setClientPhone(rs.getString("client_phonenum"));

            // Set package details
            appt.setPackageName(rs.getString("package_name"));
            appt.setPackageDuration(rs.getInt("package_duration"));
            appt.setPackagePrice(rs.getDouble("package_price"));
            //appt.setPackageDescription(rs.getString("package_description"));

            appointments.add(appt);
        }

        return appointments;
    }

    public Appointment getAppointmentWithClientDetails(int appointmentId) {
        String sql = "SELECT a.*,c.client_fullname, c.client_email, c.client_phonenum "
                + "FROM appointment a JOIN client c ON a.client_id = c.client_id "
                + "WHERE a.appointment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointmentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Appointment appt = new Appointment();

                appt.setAppointmentId(rs.getInt("appointment_id"));
                appt.setClientId(rs.getInt("client_id"));
                appt.setTherapistId(rs.getInt("therapist_id"));
                appt.setPackageId(rs.getInt("package_id"));
                appt.setAppointmentDate(rs.getString("appointment_date"));
                appt.setAppointmentTime(rs.getString("appointment_time"));
                appt.setAppointmentStatus(rs.getString("appointment_status"));
                appt.setNotes(rs.getString("notes"));

                // Set client details
                appt.setClientName(rs.getString("client_fullname"));
                appt.setClientEmail(rs.getString("client_email"));
                appt.setClientPhone(rs.getString("client_phonenum"));
                return appt;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

// Update payment ID
    public boolean updatePaymentId(int appointmentId, String paymentId) {
        String sql = "UPDATE appointment SET payment_id = ? WHERE appointment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, paymentId);
            stmt.setInt(2, appointmentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

// Cancel appointment
    public boolean cancelAppointment(int appointmentId) {
        String sql = "UPDATE appointment SET appointment_status = 'Cancelled' WHERE appointment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointmentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Tambahkan method getLatestAppointment() yang digunakan di bookAppointment()

    public Appointment getLatestAppointment(int clientId) {
        String sql = "SELECT * FROM appointment WHERE client_id = ? ORDER BY appointment_id DESC LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Appointment appt = new Appointment();
                // Set all basic fields
                appt.setAppointmentId(rs.getInt("appointment_id"));
                appt.setClientId(rs.getInt("client_id"));
                appt.setTherapistId(rs.getInt("therapist_id"));
                appt.setPackageId(rs.getInt("package_id"));
                appt.setAppointmentDate(rs.getString("appointment_date"));
                appt.setAppointmentTime(rs.getString("appointment_time"));
                appt.setAppointmentStatus(rs.getString("appointment_status"));
                appt.setNotes(rs.getString("notes"));

                // If you have these additional fields in your Appointment model
                /* if (hasColumn(rs, "created_at")) {
                appt.setCreatedAt(rs.getTimestamp("created_at"));
            }
            if (hasColumn(rs, "payment_id")) {
                appt.setPaymentId(rs.getString("payment_id"));
            }*/
                return appt;
            }
        } catch (SQLException e) {
            System.err.println("Error getting latest appointment for client " + clientId);
            e.printStackTrace();
        }
        return null;
    }

    public List<Appointment> getAllAppointmentsWithDetails() throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, c.client_fullname, t.therapist_fullname "
                + // Changed column names here
                "FROM appointment a "
                + "JOIN client c ON a.client_id = c.client_id "
                + "JOIN therapist t ON a.therapist_id = t.therapist_id "
                + "ORDER BY a.appointment_date DESC, a.appointment_time ASC";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Appointment appt = new Appointment();

                appt.setAppointmentId(rs.getInt("appointment_id"));
                appt.setClientId(rs.getInt("client_id"));
                appt.setTherapistId(rs.getInt("therapist_id"));
                appt.setAppointmentDate(rs.getString("appointment_date"));
                appt.setAppointmentTime(rs.getString("appointment_time"));
                appt.setAppointmentStatus(rs.getString("appointment_status"));
                appt.setNotes(rs.getString("notes"));

                // Custom fields for display
                appt.setClientName(rs.getString("client_fullname"));  // Matches SQL
                appt.setTherapistName(rs.getString("therapist_fullname"));  // Matches SQL

                appointments.add(appt);
            }
        }

        return appointments;
    }

// Dalam AppointmentDao.java
    // Confirm appointment
    public boolean confirmAppointment(int appointmentId) {
        String sql = "UPDATE appointment SET appointment_status = 'Confirmed' WHERE appointment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointmentId);
            int affectedRows = stmt.executeUpdate();
            System.out.println("Confirmed appointment ID: " + appointmentId + ", affected rows: " + affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error confirming appointment: " + e.getMessage());
            return false;
        }
    }

// Helper method to check if column exists in ResultSet
    private boolean hasColumn(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updateAppointmentStatus(int appointmentId, String status) throws SQLException {
        String sql = "UPDATE appointment SET appointment_status = ? WHERE appointment_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, appointmentId);
            return stmt.executeUpdate() > 0;
        }
    }
// Mark appointment as completed

    public boolean completeAppointment(int appointmentId) throws SQLException {
        String sql = "UPDATE appointment SET appointment_status = 'Completed' WHERE appointment_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointmentId);
            return stmt.executeUpdate() > 0;
        }
    }

//untuk report module
    public List<Appointment> getAppointmentsByMonthYearStatus(int month, int year, String status) throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, c.client_fullname, t.therapist_fullname, p.package_name FROM appointment a "
                + "JOIN client c ON a.client_id = c.client_id "
                + "JOIN therapist t ON a.therapist_id = t.therapist_id "
                + "JOIN therapy_package p ON a.package_id = p.package_id "
                + "WHERE MONTH(a.appointment_date) = ? AND YEAR(a.appointment_date) = ?"
                + (status.equals("All") ? "" : " AND a.appointment_status = ?")
                + " ORDER BY a.appointment_date, a.appointment_time";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, month);
            stmt.setInt(2, year);
            if (!status.equals("All")) {
                stmt.setString(3, status);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Appointment appt = new Appointment();
                appt.setAppointmentId(rs.getInt("appointment_id"));
                appt.setClientId(rs.getInt("client_id"));
                appt.setTherapistId(rs.getInt("therapist_id"));
                appt.setPackageId(rs.getInt("package_id"));
                appt.setAppointmentDate(rs.getString("appointment_date"));
                appt.setAppointmentTime(rs.getString("appointment_time"));
                appt.setAppointmentStatus(rs.getString("appointment_status"));
                appt.setClientName(rs.getString("client_fullname"));
                appt.setTherapistName(rs.getString("therapist_fullname"));
                appt.setPackageName(rs.getString("package_name"));
                list.add(appt);
            }
        }
        return list;
    }

    public List<Appointment> getAppointmentsByTherapistAndMonth(int therapistId, int month, int year) throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, c.client_fullname, p.package_name FROM appointment a "
                + "JOIN client c ON a.client_id = c.client_id "
                + "JOIN therapy_package p ON a.package_id = p.package_id "
                + "WHERE a.therapist_id = ? AND MONTH(a.appointment_date) = ? AND YEAR(a.appointment_date) = ? "
                + "ORDER BY a.appointment_date, a.appointment_time";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, therapistId);
            stmt.setInt(2, month);
            stmt.setInt(3, year);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Appointment appt = new Appointment();
                appt.setAppointmentId(rs.getInt("appointment_id"));
                appt.setClientName(rs.getString("client_fullname"));
                appt.setAppointmentDate(rs.getString("appointment_date"));
                appt.setAppointmentTime(rs.getString("appointment_time"));
                appt.setAppointmentStatus(rs.getString("appointment_status"));
                appt.setPackageName(rs.getString("package_name"));
                list.add(appt);
            }
        }
        return list;
    }

//auto cancel appointment kalau tak bayar
    public void autoCancelUnpaidAppointments() throws SQLException {
        String sql = "UPDATE appointment "
                + "SET appointment_status = 'Cancelled (Unpaid)' "
                + "WHERE appointment_status = 'Pending' "
                + "AND created_at IS NOT NULL "
                + "AND created_at < NOW() - INTERVAL 30 MINUTE"; //edit minit untuk set bila akan auto cancel

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            int affected = stmt.executeUpdate();
            System.out.println("Auto-cancelled " + affected + " unpaid appointments.");
        }
    }

    public boolean isSlotAvailable(int therapistId, String date, String time) {
        String sql = "SELECT COUNT(*) FROM appointment WHERE therapist_id = ? "
                + "AND appointment_date = ? AND appointment_time = ? "
                + "AND appointment_status = 'Confirmed'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, therapistId);
            stmt.setString(2, date);
            stmt.setString(3, time);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

// Get available slots for rescheduling (excluding current appointment)
    public List<Map<String, String>> getAvailableSlotsForReschedule(int appointmentId, String newDate) throws SQLException {
        Appointment currentAppt = getAppointmentById(appointmentId);
        if (currentAppt == null) {
            return new ArrayList<>();
        }

        TherapyPackageDao packageDao = new TherapyPackageDao();
        TherapyPackage therapyPackage = packageDao.getPackageById(currentAppt.getPackageId());
        if (therapyPackage == null) {
            return new ArrayList<>();
        }

        TherapistDao therapistDao = new TherapistDao();
        Therapist therapist = therapistDao.getTherapistById(currentAppt.getTherapistId());
        if (therapist == null) {
            return new ArrayList<>();
        }

        // Get all available slots for this therapist on the new date
        List<Map<String, String>> allSlots = checkAvailableSlots(
                therapyPackage.getPackage_duration(),
                therapist.getGender(),
                newDate
        );

        // Filter to only slots for this therapist
        List<Map<String, String>> therapistSlots = new ArrayList<>();
        for (Map<String, String> slot : allSlots) {
            if (slot.get("therapistId").equals(String.valueOf(currentAppt.getTherapistId()))) {
                therapistSlots.add(slot);
            }
        }

        return therapistSlots;
    }

// Verify reschedule request
    public boolean verifyRescheduleRequest(int appointmentId) {
        String sql = "SELECT appointment_status FROM appointment WHERE appointment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointmentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String status = rs.getString("appointment_status");
                return "Confirmed".equals(status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

// Update your existing rescheduleAppointment method to include validation
    public boolean rescheduleAppointment(int appointmentId, String newDate, String newTime) throws SQLException {
        String sql = "UPDATE appointment SET appointment_date = ?, appointment_time = ?, created_at = NOW()  "
                + "WHERE appointment_id = ? AND (appointment_status = 'Confirmed' OR appointment_status = 'Reschedule Approved')";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newDate);
            stmt.setString(2, newTime);
            stmt.setInt(3, appointmentId);

            int rowsAffected = stmt.executeUpdate();
            System.out.println("DEBUG: Reschedule affected " + rowsAffected + " rows");
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("SQL Error rescheduling appointment: " + e.getMessage());
            throw e;
        }
    }

    public List<String> getAvailableSlotsForReschedule(int therapistId, String date, int packageDuration) throws SQLException {
        List<String> availableSlots = new ArrayList<>();

        // 1. Get therapist's schedule for that day
        LocalDate appointmentDate = LocalDate.parse(date);
        String dayOfWeek = appointmentDate.getDayOfWeek().toString();
        TherapistSchedule schedule = new TherapistScheduleDao().getScheduleByTherapistAndDay(therapistId, dayOfWeek);

        if (schedule == null || !schedule.isIsActive()) {
            return availableSlots;
        }

        // 2. Get all booked slots for that therapist on that date
        List<String> bookedTimes = getBookedTimes(therapistId, date);

        // 3. Generate all possible slots
        LocalTime start = LocalTime.parse(schedule.getStartTime());
        LocalTime end = LocalTime.parse(schedule.getEndTime());

        while (start.plusMinutes(packageDuration).isBefore(end.plusSeconds(1))) {
            String timeStr = start.toString();

            // Check if slot is available
            boolean isAvailable = true;
            for (String bookedTime : bookedTimes) {
                LocalTime bookedStart = LocalTime.parse(bookedTime);
                LocalTime bookedEnd = bookedStart.plusMinutes(packageDuration);

                if (start.isBefore(bookedEnd) && start.plusMinutes(packageDuration).isAfter(bookedStart)) {
                    isAvailable = false;
                    break;
                }
            }

            if (isAvailable) {
                availableSlots.add(timeStr.substring(0, 5)); // Return HH:mm format
            }

            start = start.plusMinutes(30); // Check every 30 minutes
        }

        return availableSlots;
    }

// Therapist request reschedule
    public boolean therapistRequestReschedule(int appointmentId, String reason) throws SQLException {
        String sql = "UPDATE appointment SET appointment_status = 'Reschedule Request', notes = CONCAT(IFNULL(notes, ''), ?) WHERE appointment_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            String note = "\n\nTherapist Reschedule Request Reason: " + reason;
            stmt.setString(1, note);
            stmt.setInt(2, appointmentId);
            return stmt.executeUpdate() > 0;
        }
    }

// Client approve therapist reschedule request
    public boolean approveTherapistReschedule(int appointmentId) throws SQLException {
        String sql = "UPDATE appointment SET appointment_status = 'Reschedule Approved' WHERE appointment_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointmentId);
            return stmt.executeUpdate() > 0;
        }
    }

// Client reject therapist reschedule request
    public boolean rejectTherapistReschedule(int appointmentId, boolean cancelAppointment, String refundNotes) throws SQLException {
        String newStatus = cancelAppointment ? "Cancelled (Refund Pending)" : "Confirmed";
        String sql = "UPDATE appointment SET appointment_status = ?, notes = CONCAT(IFNULL(notes,''), ?) WHERE appointment_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            String note = "\n\nClient rejected reschedule request"
                    + (cancelAppointment ? " and requested refund. " + refundNotes : "");

            stmt.setString(1, newStatus);
            stmt.setString(2, note);
            stmt.setInt(3, appointmentId);
            return stmt.executeUpdate() > 0;
        }
    }

// Get appointments with therapist reschedule requests for a client
    public List<Appointment> getTherapistRescheduleRequests(int clientId) throws SQLException {
        List<Appointment> requests = new ArrayList<>();
        String sql = "SELECT a.*, t.therapist_fullname, p.package_name FROM appointment a "
                + "JOIN therapist t ON a.therapist_id = t.therapist_id "
                + "JOIN therapy_package p ON a.package_id = p.package_id "
                + "WHERE a.client_id = ? AND a.appointment_status = 'Reschedule Request'";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Appointment appt = new Appointment();
                appt.setAppointmentId(rs.getInt("appointment_id"));
                appt.setClientId(rs.getInt("client_id"));
                appt.setTherapistId(rs.getInt("therapist_id"));
                appt.setPackageId(rs.getInt("package_id"));
                appt.setAppointmentDate(rs.getString("appointment_date"));
                appt.setAppointmentTime(rs.getString("appointment_time"));
                appt.setAppointmentStatus(rs.getString("appointment_status"));
                appt.setNotes(rs.getString("notes"));
                appt.setTherapistName(rs.getString("therapist_fullname"));
                appt.setPackageName(rs.getString("package_name"));
                requests.add(appt);
            }
        }
        return requests;
    }

    public boolean markRefundCompleted(int appointmentId, String refundMethod, String refundNotes)
            throws SQLException {
        String sql = "UPDATE appointment SET "
                + "appointment_status = 'Refund Completed', "
                + "notes = CONCAT(IFNULL(notes,''), ?) "
                + "WHERE appointment_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            String note = "\n\n[Refund Completed " + new java.util.Date() + "]"
                    + "\nMethod: " + refundMethod
                    + "\nNotes: " + refundNotes;

            stmt.setString(1, note);
            stmt.setInt(2, appointmentId);

            int rowsAffected = stmt.executeUpdate();

            // Additional logging for audit trail
            if (rowsAffected > 0) {
                System.out.println("Marked refund as completed for appointment: " + appointmentId);
                logRefundAction(appointmentId, refundMethod);
            }

            return rowsAffected > 0;
        }
    }

    private void logRefundAction(int appointmentId, String refundMethod) {
        String sql = "INSERT INTO refund_logs (appointment_id, refund_method, processed_by, processed_at) "
                + "VALUES (?, ?, 'system', NOW())";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointmentId);
            stmt.setString(2, refundMethod);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error logging refund action: " + e.getMessage());
            // Continue even if logging fails
        }
    }

//appointment reminder
    public List<Appointment> getTomorrowConfirmedAppointments() throws SQLException {
        List<Appointment> list = new ArrayList<>();

        String sql = "SELECT a.*, "
                + "c.client_fullname, c.client_email, "
                + "p.package_name, "
                + "t.therapist_fullname "
                + "FROM appointment a "
                + "JOIN client c ON a.client_id = c.client_ID "
                + "JOIN therapy_package p ON a.package_id = p.package_ID "
                + "JOIN therapist t ON a.therapist_id = t.therapist_ID "
                + "WHERE a.appointment_status = 'Confirmed' "
                + "AND a.appointment_date = CURDATE() + INTERVAL 1 DAY";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Appointment appt = new Appointment();
                appt.setAppointmentId(rs.getInt("appointment_id"));
                appt.setClientId(rs.getInt("client_id"));
                appt.setClientName(rs.getString("client_fullname"));
                appt.setClientEmail(rs.getString("client_email"));
                appt.setPackageName(rs.getString("package_name"));
                appt.setAppointmentDate(rs.getString("appointment_date"));
                appt.setAppointmentTime(rs.getString("appointment_time"));
                appt.setAppointmentStatus(rs.getString("appointment_status"));

                // Set therapist name
                appt.setTherapistName(rs.getString("therapist_fullname"));

                list.add(appt);
            }
        }
        return list;
    }
//untuk cleient dashboard 
    // Get upcoming appointments for a client

    public List<Appointment> getUpcomingAppointments(int clientId) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.package_name, t.therapist_fullname "
                + "FROM appointment a "
                + "JOIN therapy_package p ON a.package_id = p.package_ID "
                + "JOIN therapist t ON a.therapist_id = t.therapist_ID "
                + "WHERE a.client_id = ? AND a.appointment_date >= CURDATE() "
                + "AND a.appointment_status = 'Confirmed' "
                + "ORDER BY a.appointment_date, a.appointment_time";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setAppointmentId(rs.getInt("appointment_id"));
                appointment.setClientId(rs.getInt("client_id"));
                appointment.setTherapistId(rs.getInt("therapist_id"));
                appointment.setPackageId(rs.getInt("package_id"));
                appointment.setAppointmentDate(rs.getString("appointment_date"));
                appointment.setAppointmentTime(rs.getString("appointment_time"));
                appointment.setAppointmentStatus(rs.getString("appointment_status"));
                appointment.setNotes(rs.getString("notes"));
                //appointment.setCreated_at(rs.getTimestamp("created_at"));
                appointment.setPackageName(rs.getString("package_name"));
                appointment.setTherapistName(rs.getString("therapist_fullname"));
                appointments.add(appointment);
            }
        }
        return appointments;
    }

    // Get completed appointments for a client
    public List<Appointment> getCompletedAppointments(int clientId) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.package_name, t.therapist_fullname "
                + "FROM appointment a "
                + "JOIN therapy_package p ON a.package_id = p.package_ID "
                + "JOIN therapist t ON a.therapist_id = t.therapist_ID "
                + "WHERE a.client_id = ? AND a.appointment_status = 'Completed' "
                + "ORDER BY a.appointment_date DESC, a.appointment_time DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setAppointmentId(rs.getInt("appointment_id"));
                appointment.setClientId(rs.getInt("client_id"));
                appointment.setTherapistId(rs.getInt("therapist_id"));
                appointment.setPackageId(rs.getInt("package_id"));
                appointment.setAppointmentDate(rs.getString("appointment_date"));
                appointment.setAppointmentTime(rs.getString("appointment_time"));
             
                appointment.setAppointmentStatus(rs.getString("appointment_status"));
                appointment.setNotes(rs.getString("notes"));
                //appointment.setCreated_at(rs.getTimestamp("created_at"));
                appointment.setPackageName(rs.getString("package_name"));
                appointment.setTherapistName(rs.getString("therapist_fullname"));
                appointments.add(appointment);
            }
        }
        return appointments;
    }

    // Get appointment statistics for dashboard cards
    public int[] getAppointmentStats(int clientId) throws SQLException {
        int[] stats = new int[4]; // upcoming, completed, pending payment, pending feedback

        // Upcoming appointments
        String sql1 = "SELECT COUNT(*) FROM appointment WHERE client_id = ? "
                + "AND appointment_date >= CURDATE() AND appointment_status = 'Confirmed'";
        try (PreparedStatement stmt = conn.prepareStatement(sql1)) {
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                stats[0] = rs.getInt(1);
            }
        }

        // Completed appointments
        String sql2 = "SELECT COUNT(*) FROM appointment WHERE client_id = ? "
                + "AND appointment_status = 'Completed'";
        try (PreparedStatement stmt = conn.prepareStatement(sql2)) {
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                stats[1] = rs.getInt(1);
            }
        }

        // Pending payment
        String sql3 = "SELECT COUNT(*)\n"
                + "FROM appointment\n"
                + "WHERE client_id = ?\n"
                + "  AND appointment_status = 'Pending'";
        try (PreparedStatement stmt = conn.prepareStatement(sql3)) {
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                stats[2] = rs.getInt(1);
            }
        }

        // Pending feedback
        String sql4 = "SELECT COUNT(DISTINCT a.appointment_id) FROM appointment a "
                + "LEFT JOIN feedback f ON a.appointment_id = f.appointment_id "
                + "WHERE a.client_id = ? AND a.appointment_status = 'Completed' "
                + "AND f.feedback_id IS NULL";
        try (PreparedStatement stmt = conn.prepareStatement(sql4)) {
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                stats[3] = rs.getInt(1);
            }
        }

        return stats;
    }
    
   public List<Appointment> getPendingAppointmentsForClient(int clientId) throws SQLException {
    List<Appointment> list = new ArrayList<>();
    String sql = "SELECT a.*, p.package_name, p.package_duration, p.package_price, "
               + "t.therapist_fullname "
               + "FROM appointment a "
               + "JOIN therapy_package p ON a.package_id = p.package_id "
               + "JOIN therapist t ON a.therapist_id = t.therapist_id "
               + "WHERE a.client_id = ? AND a.appointment_status = 'Pending' "
               + "ORDER BY a.appointment_date DESC, a.appointment_time DESC";

    Connection con = DBConnection.getConnection();
    PreparedStatement ps = con.prepareStatement(sql);
    ps.setInt(1, clientId);
    ResultSet rs = ps.executeQuery();
    while (rs.next()) {
        Appointment a = new Appointment();
        a.setAppointmentId(rs.getInt("appointment_id"));
        a.setClientId(rs.getInt("client_id"));
        a.setTherapistId(rs.getInt("therapist_id"));
        a.setPackageId(rs.getInt("package_id"));
        a.setAppointmentDate(rs.getString("appointment_date"));
        a.setAppointmentTime(rs.getString("appointment_time"));
        a.setAppointmentStatus(rs.getString("appointment_status"));
        a.setNotes(rs.getString("notes"));
        a.setPackageName(rs.getString("package_name"));
        a.setPackageDuration(rs.getInt("package_duration"));
        a.setPackagePrice(rs.getDouble("package_price"));
        a.setTherapistName(rs.getString("therapist_fullname"));
        list.add(a);
    }
    return list;
}
public List<Appointment> getCompletedAppointmentsWithoutFeedback(int clientId) throws SQLException {
    List<Appointment> list = new ArrayList<>();
    String sql = "SELECT a.*, p.package_name, t.therapist_fullname, p.package_price " +
                 "FROM appointment a " +
                 "JOIN therapy_package p ON a.package_id = p.package_id " +
                 "JOIN therapist t ON a.therapist_id = t.therapist_id " +
                 "LEFT JOIN feedback f ON a.appointment_id = f.appointment_id " +
                 "WHERE a.client_id = ? AND a.appointment_status = 'Completed' AND f.feedback_id IS NULL";
    Connection con = DBConnection.getConnection();
    PreparedStatement ps = con.prepareStatement(sql);
    ps.setInt(1, clientId);
    ResultSet rs = ps.executeQuery();
    while (rs.next()) {
        Appointment a = new Appointment();
        a.setAppointmentId(rs.getInt("appointment_id"));
        a.setAppointmentDate(rs.getString("appointment_date"));
        a.setAppointmentTime(rs.getString("appointment_time"));
        a.setAppointmentStatus(rs.getString("appointment_status"));
        a.setPackageName(rs.getString("package_name"));
        a.setPackagePrice(rs.getDouble("package_price"));
        a.setTherapistName(rs.getString("therapist_fullname"));
        list.add(a);
    }
    return list;
}



}
