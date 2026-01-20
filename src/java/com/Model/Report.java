package com.Model;

import java.util.List;
import java.util.Map;

public class Report {
    private String reportType;
    private String title;
    private String description;
    private String timePeriod;
    private List<Map<String, Object>> data;
    private Map<String, Double> summary;
    
    // Constructors
    public Report() {}
    
    public Report(String reportType, String title, String description) {
        this.reportType = reportType;
        this.title = title;
        this.description = description;
    }
    
    // Getters and Setters
    public String getReportType() {
        return reportType;
    }
    
    public void setReportType(String reportType) {
        this.reportType = reportType;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getTimePeriod() {
        return timePeriod;
    }
    
    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }
    
    public List<Map<String, Object>> getData() {
        return data;
    }
    
    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }
    
    public Map<String, Double> getSummary() {
        return summary;
    }
    
    public void setSummary(Map<String, Double> summary) {
        this.summary = summary;
    }
}