package com.Model;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class Payment {
    
    public static final String STATUS_PAID = "Paid";
public static final String STATUS_PENDING = "Pending";
public static final String STATUS_FAILED = "Failed";

    private int paymentId;
    private int appointmentId;
    private double amount;
    private Timestamp paymentDate;
    private String transactionId;
    private String paymentMethod;
    private String paymentStatus;
    
    // Getters and Setters
    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Timestamp getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Timestamp paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    private Map<String, Object> additionalInfo = new HashMap<>();

public void setAdditionalInfo(String key, Object value) {
    this.additionalInfo.put(key, value);
}

public Object getAdditionalInfo(String key) {
    return this.additionalInfo.get(key);
}

public Map<String, Object> getAdditionalInfo() {
    return additionalInfo;
}
}