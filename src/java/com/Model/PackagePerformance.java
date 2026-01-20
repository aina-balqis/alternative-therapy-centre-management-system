package com.Model;

public class PackagePerformance {
    private int packageId;
    private String packageName;
    private int timesBooked;
    private double revenue;

    // Getters and setters
    public int getPackageId() { return packageId; }
    public void setPackageId(int packageId) { this.packageId = packageId; }
    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public int getTimesBooked() { return timesBooked; }
    public void setTimesBooked(int timesBooked) { this.timesBooked = timesBooked; }
    public double getRevenue() { return revenue; }
    public void setRevenue(double revenue) { this.revenue = revenue; }
}