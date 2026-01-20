/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Model;

import java.sql.Timestamp;
import java.util.Date;

import java.time.LocalDate;

/**
 *
 * @author ASUS
 */
public class Client {

    private int client_ID;
    private String client_fullname;
    private String client_email;
    private String client_password;
    private Date client_dob;
    private String client_phonenum;
    private String client_address;
    private String client_state;
    private String client_district;
    private String client_postcode;
    private String gender;

    // Tambah field baru untuk email verification
    private boolean email_verified;
    
     private String reset_token;          // Untuk simpan token reset password
    private Timestamp reset_token_expiry; // Untuk simpan expiry time token

    public String getReset_token() {
        return reset_token;
    }

    public void setReset_token(String reset_token) {
        this.reset_token = reset_token;
    }

    public Timestamp getReset_token_expiry() {
        return reset_token_expiry;
    }

    public void setReset_token_expiry(Timestamp reset_token_expiry) {
        this.reset_token_expiry = reset_token_expiry;
    }

    public boolean isEmail_verified() {
        return email_verified;
    }

    public void setEmail_verified(boolean email_verified) {
        this.email_verified = email_verified;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getClient_dob() {
        return client_dob;
    }

    public void setClient_dob(Date client_dob) {
        this.client_dob = client_dob;
    }

    public int getClient_ID() {
        return client_ID;
    }

    public void setClient_ID(int client_ID) {
        this.client_ID = client_ID;
    }

    public String getClient_fullname() {
        return client_fullname;
    }

    public void setClient_fullname(String client_fullname) {
        this.client_fullname = client_fullname;
    }

    public String getClient_email() {
        return client_email;
    }

    public void setClient_email(String client_email) {
        this.client_email = client_email;
    }

    public String getClient_password() {
        return client_password;
    }

    public void setClient_password(String client_password) {
        this.client_password = client_password;
    }

    public String getClient_phonenum() {
        return client_phonenum;
    }

    public void setClient_phonenum(String client_phonenum) {
        this.client_phonenum = client_phonenum;
    }

    public String getClient_address() {
        return client_address;
    }

    public void setClient_address(String client_address) {
        this.client_address = client_address;
    }

    public String getClient_state() {
        return client_state;
    }

    public void setClient_state(String client_state) {
        this.client_state = client_state;
    }

    public String getClient_district() {
        return client_district;
    }

    public void setClient_district(String client_district) {
        this.client_district = client_district;
    }

    public String getClient_postcode() {
        return client_postcode;
    }

    public void setClient_postcode(String client_postcode) {
        this.client_postcode = client_postcode;
    }

}
