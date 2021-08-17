package com.example.shikshak;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Admin {
    public String adminName;
    public String adminEmail;
    public String adminPhone;

    public Admin() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public Admin(String name,String email,String phone) {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        this.adminName=name;
        this.adminEmail=email;
        this.adminPhone=phone;
    }

}