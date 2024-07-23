package com.zl.facerecognition.viewModel;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.zl.facerecognition.entity.Attendance;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CourseTeaAttendanceViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<List<Attendance>> attend;

    public MutableLiveData<List<Attendance>> getAttendance() {
        if (attend == null){
            attend = new MutableLiveData<>();
        }
        return attend;
    }

    public void updateAttendance(String s){
        List<Attendance> list = new ArrayList<>();
        JSONArray array = JSONArray.parseArray(s);
        Attendance attendance;
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.getJSONObject(i);
            attendance = new Attendance(object.getString("studentName"),object.getString("studentAccount"),
                    object.getInteger("absentCount"),object.getInteger("failedCount"),
                    object.getInteger("successCount"),object.getInteger("leaveCount"));
            list.add(attendance);
        }
        attend.setValue(list);
    }
}
