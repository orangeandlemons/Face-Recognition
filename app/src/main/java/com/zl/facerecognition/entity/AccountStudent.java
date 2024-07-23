package com.zl.facerecognition.entity;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class AccountStudent {
    private String name;
    private String account;
    private String password;
    private int sex;
    private String major;
    private String phone;
    private String birthday;

    public AccountStudent(String name, String account, String password, int sex, String major, String phone, String birthday) {
        this.name = name;
        this.account = account;
        this.password = password;
        this.sex = sex;
        this.major = major;
        this.phone = phone;
        this.birthday = birthday;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

//    public Date getBirthday() {
//        return birthday;
//    }
//
//    public void setBirthday(Date birthday) {
//        this.birthday = birthday;

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday =birthday;

    }
}

