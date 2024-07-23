package com.zl.facerecognition.activity.teacher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.StringUtils;
import com.gyf.barlibrary.ImmersionBar;
import com.zl.facerecognition.R;
import com.zl.facerecognition.activity.BaseActivity;
import com.zl.facerecognition.entity.Leave;
import com.zl.facerecognition.utils.NetUtils;
import com.zl.facerecognition.utils.UiUtils;
import com.zl.facerecognition.utils.ViewUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TeacherLeaveActicity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private TextView leaveDetailName;
    private TextView leaveDetailAccount;
    private TextView leaveDetailPhone;
    private TextView leaveDetailDateStart;
    private TextView leaveDetailDateEnd;
    private TextView leaveDetailReason;
    private RadioButton leaveDetailRefuse;
    private RadioButton leaveDetailAgree;
    private EditText leaveDetailRemark;
    private Button leaveDetailSubmit;
    private Leave leave;
    private Integer approvalResult = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_teacher_leave_acticity);
        ImmersionBar.with(this).init();
        setTitle("请假审批");

        Intent intent = getIntent();
        leave = (Leave) intent.getExtras().getSerializable("leave");
        init();
        initView();

    }

    private void init() {

        leaveDetailName = (TextView) findViewById(R.id.leave_detail_name);
        leaveDetailAccount = (TextView) findViewById(R.id.leave_detail_account);
        leaveDetailPhone = (TextView) findViewById(R.id.leave_detail_phone);
        leaveDetailDateStart = (TextView) findViewById(R.id.leave_detail_date_start);
        leaveDetailDateEnd = (TextView) findViewById(R.id.leave_detail_date_end);
        leaveDetailReason = (TextView) findViewById(R.id.leave_detail_reason);
        leaveDetailRefuse = (RadioButton) findViewById(R.id.leave_detail_refuse);
        leaveDetailAgree = (RadioButton) findViewById(R.id.leave_detail_agree);
        leaveDetailRemark = (EditText) findViewById(R.id.leave_detail_remark);
        leaveDetailSubmit = (Button) findViewById(R.id.leave_detail_submit);
        leaveDetailRefuse.setOnCheckedChangeListener(this);
        leaveDetailAgree.setOnCheckedChangeListener(this);
        leaveDetailSubmit.setOnClickListener(this);

    }

    private void initView() {
        leaveDetailName.setText(leave.getStudentName());
        leaveDetailAccount.setText(leave.getStudentAccount());
        leaveDetailPhone.setText(leave.getStudentPhone());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        leaveDetailDateStart.setText(format.format(leave.getLeaveTime()));
        leaveDetailDateEnd.setText(format.format(leave.getBackTime()));
        leaveDetailReason.setText(leave.getLeaveReason());

        if (leave.getApprovalResult() > 0) {
            if (leave.getApprovalResult() == 1) {
                leaveDetailRefuse.performClick();
                approvalResult = 1;
            } else {
                leaveDetailAgree.performClick();
                approvalResult = 2;
            }
            leaveDetailRemark.setText(leave.getApprovalRemark());
        } else {
            leaveDetailRemark.setHint("请输入备注");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leave_detail_submit:
                if (approvalResult < 1) {
                    Toast.makeText(this, "未填写审批结果", Toast.LENGTH_SHORT).show();
                    return;
                }
                Timestamp submitTime = new Timestamp(System.currentTimeMillis());
                Map<String, String> map = new HashMap<>();
                map.put("leaveId", String.valueOf(leave.getLeaveId()));
                map.put("time", submitTime.toString());
                map.put("result", String.valueOf(approvalResult));
                map.put("remark", leaveDetailRemark.getText() == null ? "" : leaveDetailRemark.getText().toString());
                NetUtils.request(context, "/leave/modifyLeave", map, result -> {
                    UiUtils.showText(context, result.getMsg());
                    if (result.getCode().equals("200")) {
                        finish();
                    }
                });
                break;
            case R.id.iv_back:
                finish();
                break;
        }

    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {

            case R.id.leave_detail_agree:
                if (isChecked) {
                    approvalResult = 2;
                }
                break;
            case R.id.leave_detail_refuse:
                if (isChecked) {
                    approvalResult = 1;
                }
                break;
        }
    }
}
