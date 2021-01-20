//Class that supports user registration storage

package com.example.mynfcapp;

public class UserHelperClass {

    String name, email, phoneNo, password;

    //Constructors

    public UserHelperClass() {
    }

    public UserHelperClass(String name, String email, String phoneNo, String password) {
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.password = password;
    }


    //Getters

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getPassword() {
        return password;
    }


    //Setters

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
