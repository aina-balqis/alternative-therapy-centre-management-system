package com.Model;

public class TherapistSchedule {
    private int scheduleId;
    private int therapistId;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private boolean isActive;
    
    public TherapistSchedule() {
    // constructor kosong diperlukan untuk buat objek kosong
}


    public TherapistSchedule(int therapistId, String dayOfWeek, String startTime, String endTime, boolean isActive) {
    this.therapistId = therapistId;
    this.dayOfWeek = dayOfWeek;
    this.startTime = startTime;
    this.endTime = endTime;
    this.isActive = isActive;
}

    // Getter & Setter
    public int getScheduleId() { return scheduleId; }
    public void setScheduleId(int scheduleId) { this.scheduleId = scheduleId; }

    public int getTherapistId() { return therapistId; }
    public void setTherapistId(int therapistId) { this.therapistId = therapistId; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

   
}
