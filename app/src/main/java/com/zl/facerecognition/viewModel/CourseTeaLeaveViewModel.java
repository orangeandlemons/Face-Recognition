package com.zl.facerecognition.viewModel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zl.facerecognition.entity.Leave;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CourseTeaLeaveViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<List<Leave>> leaveList;

    public MutableLiveData<List<Leave>> getLeaveList() {
        if (leaveList == null){
            leaveList = new MutableLiveData<>();
        }
        return leaveList;
    }

    public void updateLeaveList(String s){
        List<Leave> leaves = new ArrayList<>();
        JSONArray array = JSON.parseArray(s);
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.getJSONObject(i);
            Leave leave = new Leave();
            leave.setLeaveId(object.getInteger("leaveId"));
            leave.setLeaveTime((Timestamp) object.getTimestamp("leaveTime"));
            leave.setBackTime((Timestamp) object.getTimestamp("backTime"));
            leave.setLeaveReason(object.getString("leaveReason"));

            leave.setApprovalTime((Timestamp) object.getTimestamp("approvalTime"));
            leave.setApprovalResult(object.getInteger("approvalResult"));
            leave.setApprovalRemark(object.getString("approvalRemark"));

            JSONObject student = object.getJSONObject("student");
            leave.setStudentId(student.getInteger("studentId"));
            leave.setStudentAccount(student.getString("studentAccount"));
            leave.setStudentAvatar(student.getString("studentAvatar"));
            leave.setStudentName(student.getString("studentName"));
            leave.setStudentPhone(student.getString("studentPhone"));
            leaves.add(leave);
        }
        leaveList.setValue(leaves);
    }
}
