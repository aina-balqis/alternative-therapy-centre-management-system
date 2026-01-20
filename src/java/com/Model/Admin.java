/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Model;
import java.util.Date;

/**
 *
 * @author ASUS
 */
public class Admin {
    private int admin_id;
    private String admin_fullname;
    private String admin_email;
    private String admin_password;
    private Date admin_dob;
    private int admin_phonenum;
    private String register_passcode;

    
    public Date getAdmin_dob() {
        return admin_dob;
    }

    /**
     * @return the admin_id
     */
    public void setAdmin_dob(Date admin_dob) {    
        this.admin_dob = admin_dob;
    }

    public int getAdmin_id() {
        return admin_id;
    }

    /**
     * @param admin_id the admin_id to set
     */
    public void setAdmin_id(int admin_id) {
        this.admin_id = admin_id;
    }

    /**
     * @return the admin_fullname
     */
    public String getAdmin_fullname() {
        return admin_fullname;
    }

    /**
     * @param admin_fullname the admin_fullname to set
     */
    public void setAdmin_fullname(String admin_fullname) {
        this.admin_fullname = admin_fullname;
    }

    /**
     * @return the admin_email
     */
    public String getAdmin_email() {
        return admin_email;
    }

    /**
     * @param admin_email the admin_email to set
     */
    public void setAdmin_email(String admin_email) {
        this.admin_email = admin_email;
    }

    /**
     * @return the admin_password
     */
    public String getAdmin_password() {
        return admin_password;
    }

    /**
     * @param admin_password the admin_password to set
     */
    public void setAdmin_password(String admin_password) {
        this.admin_password = admin_password;
    }

    /**
     * @return the user_phonenum
     */
    public int getAdmin_phonenum() {
        return admin_phonenum;
    }

    /**
     * @param user_phonenum the user_phonenum to set
     */
    public void setAdmin_phonenum(int admin_phonenum) {
        this.admin_phonenum = admin_phonenum;
    }

    /**
     * @return the register_passcode
     */
    public String getRegister_passcode() {
        return register_passcode;
    }

    /**
     * @param register_passcode the register_passcode to set
     */
    public void setRegister_passcode(String register_passcode) {
        this.register_passcode = register_passcode;
    }
}
