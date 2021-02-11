//Class that supports user registration storage

package com.example.mynfcapp.AccountCreation;

public class UserHelperClass {

    String fullName, email, phoneNo, password, date, gender;

    //Constructors

    public UserHelperClass() {
    }

    public UserHelperClass(String fullName, String email, String phoneNo, String password, String date, String gender) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNo = phoneNo;
        this.password = password;
        this.date = date;
        this.gender = gender;
    }


    //Getters

    public String getFullName() {
        return fullName;
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

    public String getDate() {
        return date;
    }

    public String getGender() {
        return gender;
    }


    //Setters

    public void setFullName(String name) {
        this.fullName = name;
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

    public void setDate(String password) {
        this.date = date;
    }

    public void setGender(String password) {
        this.gender = gender;
    }


}
