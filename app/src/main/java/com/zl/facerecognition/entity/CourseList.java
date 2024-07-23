package com.zl.facerecognition.entity;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class CourseList implements Serializable {
    private Integer courseId;
    private String userId;
    private String uesrName;
    private String userPhone;
    private String userBirthday;
    private String courseName;
    private String courseIntroduce;
    private String courseCode;
    private String courseAvatar;

    private String joinTime;

    public CourseList(Integer courseId, String userId, String uesrName, String courseName, String courseIntroduce, String courseCode, String courseAvatar) {
        this.courseId = courseId;
        this.userId = userId;
        this.uesrName = uesrName;
        this.courseName = courseName;
        this.courseIntroduce = courseIntroduce;
        this.courseCode = courseCode;
        this.courseAvatar = courseAvatar;
    }


    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUesrName(String uesrName) {
        this.uesrName = uesrName;
    }

    public void setUserBirthday(String userBirthday) {
        this.userBirthday=userBirthday;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCourseIntroduce(String courseIntroduce) {
        this.courseIntroduce = courseIntroduce;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public void setCourseAvatar(String courseAvatar) {
        this.courseAvatar = courseAvatar;
    }

    public String getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(String joinTime) {
        this.joinTime = joinTime;
    }

    public String getUserBirthday() {
        return userBirthday;
    }


    public Integer getCourseId() {
        return courseId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUesrName() {
        return uesrName;
    }

    public String getUserPhone() {
        return userPhone;
    }


    public String getCourseName() {
        return courseName;
    }

    public String getCourseIntroduce() {
        return courseIntroduce;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseAvatar() {
        return courseAvatar;
    }
}

