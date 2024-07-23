package com.zl.facerecognition.viewModel;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zl.facerecognition.entity.CheckinList;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CourseTeaCheckinViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<List<CheckinList>> checkinLists;

    public MutableLiveData<List<CheckinList>> getcheckinLists() {
        if (checkinLists == null){
            checkinLists = new MutableLiveData<>();
        }
        return checkinLists;
    }

    public void updatecheckinList(String s){
        List<CheckinList> lists = new ArrayList<>();
        CheckinList checkinList;
        JSONArray objects = JSONObject.parseArray(s);
        for (int i = 0; i < objects.size(); i++) {
            JSONObject object = (JSONObject) objects.get(i);
            Timestamp start = (Timestamp) object.getTimestamp("attendStart");
            Timestamp end = (Timestamp) object.getTimestamp("attendEnd");

            Timestamp current = new Timestamp(System.currentTimeMillis());
            String state = "进行中";
            if (current.before(start)){
                state = "未开始";
            } else if (current.after(end)){
                state = "已结束";
            }
            Integer type = object.getInteger("attendType");
            checkinList = new CheckinList(object.getInteger("attendId"),object.getString("courseId"),start,end,
                    object.getDouble("attendLongitude"),object.getDouble("attendLatitude"),
                    object.getString("attendLocation"),state,type);
            checkinList.setGesture(type == 2 ? object.getString("attendGesture") : null);
            lists.add(checkinList);
        }
        checkinLists.setValue(lists);
    }
}