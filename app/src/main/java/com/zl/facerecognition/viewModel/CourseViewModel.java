package com.zl.facerecognition.viewModel;

import com.zl.facerecognition.entity.CourseList;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CourseViewModel extends ViewModel {
    private MutableLiveData<CourseList> course;

    public MutableLiveData<CourseList> getCourse() {
        if (course == null){
            course = new MutableLiveData<>();
        }
        return course;
    }

    public void setCourse(CourseList courseList){
        course.setValue(courseList);
    }
}
