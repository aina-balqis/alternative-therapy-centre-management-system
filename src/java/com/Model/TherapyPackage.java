package com.Model;

public class TherapyPackage {
    private int package_ID;
    private String package_name;
    private String package_description;
    private double package_price;
    private int package_duration; // in minutes
    private boolean is_active;
    private String image_url;

    // Getters and Setters
    public int getPackage_ID() {
        return package_ID;
    }
    public void setPackage_ID(int package_ID) {
        this.package_ID = package_ID;
    }

    public String getPackage_name() {
        return package_name;
    }
    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getPackage_description() {
        return package_description;
    }
    public void setPackage_description(String package_description) {
        this.package_description = package_description;
    }

    public double getPackage_price() {
        return package_price;
    }
    public void setPackage_price(double package_price) {
        this.package_price = package_price;
    }

    public int getPackage_duration() {
        return package_duration;
    }
    public void setPackage_duration(int package_duration) {
        this.package_duration = package_duration;
    }

    public boolean isIs_active() {
        return is_active;
    }
    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public String getImage_url() {
        return image_url;
    }
    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
