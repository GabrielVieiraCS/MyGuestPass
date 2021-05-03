package com.example.mynfcapp.AccountCreation;

import android.net.Uri;

public class NFCHelperClass {

    String id, fullName, phoneNo, location, date, endDate, time, endTime, fileName;
    boolean activated, voidTag, tagLock;


    //Constructors

    public NFCHelperClass() {
    }



    public NFCHelperClass(String id, String fullName, String phoneNo, String location, String date, String endDate, String time, String endTime, boolean activated, boolean voidTag, boolean tagLock) {
        this.id = id;
        this.fullName = fullName;
        this.phoneNo = phoneNo;
        this.location = location;
        this.date = date;
        this.endDate = endDate;
        this.time = time;
        this.endTime = endTime;
        this.activated = activated;
        this.voidTag = voidTag;
    }


    //With image
    public NFCHelperClass(String id, String fullName, String phoneNo, String location, String date, String endDate, String time, String endTime, boolean activated, boolean voidTag, String fileName, boolean tagLock) {
        this.id = id;
        this.fullName = fullName;
        this.phoneNo = phoneNo;
        this.location = location;
        this.date = date;
        this.endDate = endDate;
        this.time = time;
        this.endTime = endTime;
        this.activated = activated;
        this.voidTag = voidTag;
        this.fileName = fileName;
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

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public boolean getActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public boolean getVoidTag() {
        return voidTag;
    }

    public void setVoidTag(boolean voidTag) {
        this.voidTag = voidTag;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String imageUri) {
        this.fileName = fileName;
    }

    public boolean isTagLock() {
        return tagLock;
    }

    public void setTagLock(boolean tagLock) {
        this.tagLock = tagLock;
    }

}
