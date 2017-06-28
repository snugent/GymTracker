package com.example.admin1.gymtracker.models;

import java.util.Date;

/**
 * Created by admin1 on 24/06/2017.
 */

public class Member {
    private String  memberId;
    private String  name;
    private Date    dob;
    private char    sex;
    private double  height;
    private double  weight;
    private boolean isAdmin;
    private boolean isDeleted;


    public Member(String memberId, String name, Date dob, char sex,
                  double height, double weight, boolean isAdmin, boolean isDeleted){
        this.memberId = memberId;
        this.name      = name;
        this.dob       = dob;
        this.sex       = sex;
        this.height    = height;
        this.weight    = weight;
        this.isAdmin   = isAdmin;
        this.isDeleted = isDeleted;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public char getSex() {
        return sex;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
