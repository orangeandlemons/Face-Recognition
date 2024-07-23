package com.zl.facerecognition.viewModel;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zl.facerecognition.entity.CourseList;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CourseListViewModel extends ViewModel {
    private MutableLiveData<List<CourseList>> courseLists;

    public MutableLiveData<List<CourseList>> getCourseLists() {
        if (courseLists == null) {
            courseLists = new MutableLiveData<>();
        }
        return courseLists;
    }

    //    更新recyclerview列表
    public void updateCourses(String s) {
        List<CourseList> lists = new ArrayList<>();
        CourseList courseList;
        JSONArray objects = JSONObject.parseArray(s);
//        JSONArray objects = new JSONArray();
//        objects.add(s);
        for (int i = 0; i < objects.size(); i++) {
            JSONObject o = (JSONObject) objects.get(i);
            JSONObject teacher = JSONObject.parseObject(o.getString("teacher"));
            courseList = new CourseList(o.getInteger("courseId"), o.getString("teacherId"),
                    teacher.getString("teacherName"), o.getString("courseName"),
                    o.getString("courseIntroduce"), o.getString("courseCode"),
                    o.getString("courseAvatar"));
            courseList.setUserBirthday(teacher.getString("teacherBirthday"));
            courseList.setUserPhone(teacher.getString("teacherPhone"));
            courseList.setJoinTime(o.getString("joinTime"));
            lists.add(courseList);
        }
        courseLists.setValue(lists);
    }
}
