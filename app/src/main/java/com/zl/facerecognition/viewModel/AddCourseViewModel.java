package com.zl.facerecognition.viewModel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zl.facerecognition.entity.CourseList;

import java.sql.Date;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddCourseViewModel extends ViewModel {
    private MutableLiveData<CourseList> course;

    public MutableLiveData<CourseList> getCourse() {
        if (course == null){
            course = new MutableLiveData<>();
        }
        return course;
    }

    public void updateCourse(String s){
        JSONObject o = JSON.parseObject(s);
        JSONObject teacher = JSONObject.parseObject(o.getString("teacher"));
        CourseList courseList = new CourseList(o.getInteger("courseId"),o.getString("teacherId"),
                teacher.getString("teacherName"),o.getString("courseName"),
                o.getString("courseIntroduce"),o.getString("courseCode"),
                o.getString("courseAvatar"));
        courseList.setUserBirthday(teacher.getString("teacherBirthday"));
        courseList.setUserPhone(teacher.getString("teacherPhone"));
        course.setValue(courseList);
    }
}
