package com.Dao;

import com.Model.TherapistSchedule;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TherapistScheduleDao {
    private Connection connection;

    public TherapistScheduleDao() {
        connection = getConnection();
    }

    // Get database connection
    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atcms", "root", "");
        } catch (Exception e) {
            System.out.println("Connection Error: " + e.getMessage());
        }
        return connection;
    }

    // Create new schedule
    public boolean addSchedule(TherapistSchedule schedule) throws SQLException {
        String sql = "INSERT INTO therapist_schedules (therapistId, dayOfWeek, startTime, endTime, isActive) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, schedule.getTherapistId());
            statement.setString(2, schedule.getDayOfWeek());
            statement.setString(3, schedule.getStartTime());
            statement.setString(4, schedule.getEndTime());
            statement.setBoolean(5, schedule.isIsActive());
            return statement.executeUpdate() > 0;
        }
    }

    // Get all schedules
    public List<TherapistSchedule> getAllSchedules() throws SQLException {
        List<TherapistSchedule> schedules = new ArrayList<>();
        String sql = "SELECT * FROM therapist_schedules ORDER BY therapistId, dayOfWeek";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                TherapistSchedule schedule = new TherapistSchedule();
                schedule.setScheduleId(resultSet.getInt("scheduleId"));
                schedule.setTherapistId(resultSet.getInt("therapistId"));
                schedule.setDayOfWeek(resultSet.getString("dayOfWeek"));
                schedule.setStartTime(resultSet.getString("startTime"));
                schedule.setEndTime(resultSet.getString("endTime"));
                schedule.setIsActive(resultSet.getBoolean("isActive"));
                schedules.add(schedule);
            }
        }
        return schedules;
    }

    // Get schedule by ID
    public TherapistSchedule getScheduleById(int scheduleId) throws SQLException {
        String sql = "SELECT * FROM therapist_schedules WHERE scheduleId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, scheduleId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    TherapistSchedule schedule = new TherapistSchedule();
                    schedule.setScheduleId(resultSet.getInt("scheduleId"));
                    schedule.setTherapistId(resultSet.getInt("therapistId"));
                    schedule.setDayOfWeek(resultSet.getString("dayOfWeek"));
                    schedule.setStartTime(resultSet.getString("startTime"));
                    schedule.setEndTime(resultSet.getString("endTime"));
                    schedule.setIsActive(resultSet.getBoolean("isActive"));
                    return schedule;
                }
            }
        }
        return null;
    }

    // Get all active schedules by therapist ID
    public List<TherapistSchedule> getActiveSchedulesByTherapist(int therapistId) throws SQLException {
        List<TherapistSchedule> schedules = new ArrayList<>();
        String sql = "SELECT * FROM therapist_schedules WHERE therapistId = ? AND isActive = TRUE ORDER BY dayOfWeek, startTime";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, therapistId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    TherapistSchedule schedule = new TherapistSchedule();
                    schedule.setScheduleId(resultSet.getInt("scheduleId"));
                    schedule.setTherapistId(resultSet.getInt("therapistId"));
                    schedule.setDayOfWeek(resultSet.getString("dayOfWeek"));
                    schedule.setStartTime(resultSet.getString("startTime"));
                    schedule.setEndTime(resultSet.getString("endTime"));
                    schedule.setIsActive(resultSet.getBoolean("isActive"));
                    schedules.add(schedule);
                }
            }
        }
        return schedules;
    }

    // Update schedule
    public boolean updateSchedule(TherapistSchedule schedule) throws SQLException {
        String sql = "UPDATE therapist_schedules SET therapistId = ?, dayOfWeek = ?, startTime = ?, endTime = ?, isActive = ? WHERE scheduleId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, schedule.getTherapistId());
            statement.setString(2, schedule.getDayOfWeek());
            statement.setString(3, schedule.getStartTime());
            statement.setString(4, schedule.getEndTime());
            statement.setBoolean(5, schedule.isIsActive());
            statement.setInt(6, schedule.getScheduleId());
            return statement.executeUpdate() > 0;
        }
    }

    // Delete schedule
    public boolean deleteSchedule(int scheduleId) throws SQLException {
        String sql = "DELETE FROM therapist_schedules WHERE scheduleId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, scheduleId);
            return statement.executeUpdate() > 0;
        }
    }

    // Get all schedules by therapist ID
    public List<TherapistSchedule> getScheduleByTherapistId(int therapistId) {
        List<TherapistSchedule> list = new ArrayList<>();
        String sql = "SELECT * FROM therapist_schedules WHERE therapistId = ? ORDER BY FIELD(dayOfWeek, 'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday')";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, therapistId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                TherapistSchedule schedule = new TherapistSchedule();
                schedule.setScheduleId(rs.getInt("scheduleId"));
                schedule.setTherapistId(rs.getInt("therapistId"));
                schedule.setDayOfWeek(rs.getString("dayOfWeek"));
                schedule.setStartTime(rs.getString("startTime"));
                schedule.setEndTime(rs.getString("endTime"));
                schedule.setIsActive(rs.getBoolean("isActive"));
                list.add(schedule);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

  public List<Integer> getTherapistIdsByGenderAndDay(String gender, String dayOfWeek) {
    List<Integer> therapistIds = new ArrayList<>();
    String sql = "SELECT DISTINCT ts.therapistId FROM therapist_schedules ts " +
                 "JOIN therapist t ON ts.therapistId = t.therapistId " +
                 "WHERE t.gender = ? AND ts.dayOfWeek = ? AND ts.isActive = TRUE";

    try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setString(1, gender);
        ps.setString(2, dayOfWeek);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            therapistIds.add(rs.getInt("therapistId"));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return therapistIds;
}


    // Tambahan: Dapatkan semua therapistId yang aktif pada hari dan jantina tertentu
    public List<Integer> getActiveTherapistsByDayAndGender(String gender, String dayOfWeek) {
        List<Integer> therapistIds = new ArrayList<>();
        String sql = "SELECT DISTINCT t.therapistId FROM therapist_schedules ts " +
                     "JOIN therapist t ON ts.therapistId = t.therapistId " +
                     "WHERE t.gender = ? AND ts.dayOfWeek = ? AND ts.isActive = true";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, gender);
            ps.setString(2, dayOfWeek);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                therapistIds.add(rs.getInt("therapistId"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return therapistIds;
    }
    
    // Dalam TherapistScheduleDao.java

public List<TherapistSchedule> getScheduleByTherapistAndDate(int therapistId, String dayOfWeek) {
    List<TherapistSchedule> scheduleList = new ArrayList<>();
    
    try (Connection con = DBConnection.getConnection()) {
        String query = "SELECT * FROM therapist_schedules WHERE therapistId = ? AND dayOfWeek = ? AND isActive = TRUE";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, therapistId);
        ps.setString(2, dayOfWeek);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            TherapistSchedule schedule = new TherapistSchedule();
            schedule.setScheduleId(rs.getInt("scheduleId"));
            schedule.setTherapistId(rs.getInt("therapistId"));
            schedule.setDayOfWeek(rs.getString("dayOfWeek"));
            schedule.setStartTime(rs.getString("startTime"));
            schedule.setEndTime(rs.getString("endTime"));
            schedule.setIsActive(rs.getBoolean("isActive"));
            scheduleList.add(schedule);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    return scheduleList;
}

    
    public TherapistSchedule getScheduleByTherapistAndDay(int therapistId, String dayOfWeek) {
    System.out.println("DEBUG: Searching schedule for therapistId=" + therapistId + ", day=" + dayOfWeek);
    
    TherapistSchedule schedule = null;
    
    // Gunakan LIKE untuk case insensitive comparison
    String sql = "SELECT * FROM therapist_schedules WHERE therapistId = ? AND LOWER(dayOfWeek) = LOWER(?) AND isActive = 1";

    try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setInt(1, therapistId);
        ps.setString(2, dayOfWeek);
        System.out.println("DEBUG: Executing query: " + ps.toString());
        
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            schedule = new TherapistSchedule();
            schedule.setScheduleId(rs.getInt("scheduleId"));
            schedule.setTherapistId(rs.getInt("therapistId"));
            schedule.setDayOfWeek(rs.getString("dayOfWeek"));
            schedule.setStartTime(rs.getString("startTime"));
            schedule.setEndTime(rs.getString("endTime"));
            schedule.setIsActive(rs.getBoolean("isActive"));
            
            System.out.println("DEBUG: Found schedule - " + schedule);
        } else {
            System.out.println("DEBUG: No schedule found");
        }
    } catch (SQLException e) {
        System.out.println("ERROR in getScheduleByTherapistAndDay: " + e.getMessage());
        e.printStackTrace();
    }
    return schedule;
}

}
