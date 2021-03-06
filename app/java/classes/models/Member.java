package com.example.admin1.gymtracker.models;

import java.util.Date;

/**
 * Created by admin1 on 24/06/2017.
 */

public class Member {
    private String  name;
    private String    dob;
    private String    sex;
    private double  height;
    private double  weight;
    private boolean isAdmin;
    private boolean isDeleted;

    public Member(){

    }

    public Member(String name, String dob, String sex,
                  double height, double weight, boolean isAdmin, boolean isDeleted){
        this.name      = name;
        this.dob       = dob;
        this.sex       = sex;
        this.height    = height;
        this.weight    = weight;
        this.isAdmin   = isAdmin;
        this.isDeleted = isDeleted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
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
