package com.example.mynfcapp.AccountCreation;

public class NFCHelperClass {

    String id, fullName, phoneNo, location, date, time;


    //Constructors

    public NFCHelperClass() {
    }

    public NFCHelperClass(String id, String fullName, String phoneNo, String location, String date, String time) {
        this.id = id;
        this.fullName = fullName;
        this.phoneNo = phoneNo;
        this.location = location;
        this.date = date;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = id;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}
