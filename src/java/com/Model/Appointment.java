package com.Model;

public class Appointment {
    private int appointmentId;
    private int clientId;
    private int therapistId;
    private int packageId;
    private String appointmentDate;
    private String appointmentTime;
    private String appointmentStatus;
    private String notes;
    
    // Package information
    private String packageName;
    private int packageDuration;
    private double packagePrice;
 
    
    // Client information
    private String clientName;
    private String clientEmail;
    private String clientPhone;
    
    // Therapist information
    private String therapistName;

    // Getters and Setters
    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getTherapistId() {
        return therapistId;
    }

    public void setTherapistId(int therapistId) {
        this.therapistId = therapistId;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getAppointmentStatus() {
        return appointmentStatus;
    }

    public void setAppointmentStatus(String appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getPackageDuration() {
        return packageDuration;
    }

    public void setPackageDuration(int packageDuration) {
        this.packageDuration = packageDuration;
    }

    public double getPackagePrice() {
        return packagePrice;
    }

    public void setPackagePrice(double packagePrice) {
        this.packagePrice = packagePrice;
    }

   

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getTherapistName() {
        return therapistName;
    }

    public void setTherapistName(String therapistName) {
        this.therapistName = therapistName;
    }
    
    public String getRescheduleReason() {
        if (this.notes != null && this.notes.startsWith("Reschedule Request by Therapist:")) {
            return this.notes.substring("Reschedule Request by Therapist:".length()).trim();
        }
        return null;
    }

    // Optional: toString() method for debugging
    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentId=" + appointmentId +
                ", clientId=" + clientId +
                ", therapistId=" + therapistId +
                ", packageId=" + packageId +
                ", appointmentDate='" + appointmentDate + '\'' +
                ", appointmentTime='" + appointmentTime + '\'' +
                ", appointmentStatus='" + appointmentStatus + '\'' +
                ", packageName='" + packageName + '\'' +
                ", packageDuration=" + packageDuration +
                ", packagePrice=" + packagePrice +
                ", clientName='" + clientName + '\'' +
                '}';
    }
}