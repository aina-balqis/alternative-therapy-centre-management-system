package com.Model;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalTime;

public class TimeSlot {
    private LocalTime startTime;
    private int durationMinutes;
    
    public TimeSlot(LocalTime startTime, int durationMinutes) {
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
    }
    
    public boolean overlapsWith(LocalTime otherStart, int otherDuration) {
        LocalTime thisEnd = startTime.plusMinutes(durationMinutes);
        LocalTime otherEnd = otherStart.plusMinutes(otherDuration);
        
        return !(thisEnd.isBefore(otherStart) || startTime.isAfter(otherEnd));
    }
    
    // Getters, toString(), etc.

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    
}