package com.Model;

public class TherapistPerformance {
    private int therapistId;
    private String therapistName;
    private int completedAppointments;
    private double averageRating;

    // Getters and setters
    public int getTherapistId() { return therapistId; }
    public void setTherapistId(int therapistId) { this.therapistId = therapistId; }
    public String getTherapistName() { return therapistName; }
    public void setTherapistName(String therapistName) { this.therapistName = therapistName; }
    public int getCompletedAppointments() { return completedAppointments; }
    public void setCompletedAppointments(int completedAppointments) { this.completedAppointments = completedAppointments; }
    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
}