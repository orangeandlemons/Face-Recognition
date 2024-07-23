package com.zl.facerecognition.activity.student;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ColorUtils;
import com.gyf.barlibrary.ImmersionBar;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.zl.facerecognition.R;
import com.zl.facerecognition.activity.BaseActivity;
import com.zl.facerecognition.entity.CheckinList;
import com.zl.facerecognition.entity.Leave;
import com.zl.facerecognition.utils.NetUtils;
import com.zl.facerecognition.utils.UiUtils;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StudentLeaveDetail extends BaseActivity {

    private View view;
    private TextView leaveDetailName;
    private TextView leaveDetailAccount;
    private TextView leaveDetailPhone;
    private TextView leaveDetailDateStart;
    private TextView leaveDetailDateEnd;
    private TextView leaveDetailReason;
    private TextView leaveDetailResult;
    private LinearLayout leaveDetailRemarkLayout;
    private EditText leaveDetailRemark;
    private Button leaveDetailDelete;
    private Leave leave;
    private Integer approvalResult=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_student_leave_detail);
        ImmersionBar.with(this).init();
        setTitle("请假详情");
        Intent intent = getIntent();
        leave = (Leave) intent.getExtras().getSerializable("leave");
        init();
        initView();

    }

    private void init() {
        view = (View) findViewById(R.id.view);
        leaveDetailName = (TextView) findViewById(R.id.leave_detail_name);
        leaveDetailAccount = (TextView) findViewById(R.id.leave_detail_account);
        leaveDetailPhone = (TextView) findViewById(R.id.leave_detail_phone);
        leaveDetailDateStart = (TextView) findViewById(R.id.leave_detail_date_start);
        leaveDetailDateEnd = (TextView) findViewById(R.id.leave_detail_date_end);
        leaveDetailReason = (TextView) findViewById(R.id.leave_detail_reason);
        leaveDetailResult = (TextView) findViewById(R.id.leave_detail_result);
        leaveDetailRemarkLayout = (LinearLayout) findViewById(R.id.leave_detail_remark_layout);
        leaveDetailRemark = (EditText) findViewById(R.id.leave_detail_remark);
        leaveDetailDelete = (Button) findViewById(R.id.leave_detail_delete);
    }

    private void initView() {
        leaveDetailName.setText(leave.getStudentName());
        leaveDetailAccount.setText(leave.getStudentAccount());
        leaveDetailPhone.setText(leave.getStudentPhone());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        leaveDetailDateStart.setText(format.format(leave.getLeaveTime()));
        leaveDetailDateEnd.setText(format.format(leave.getBackTime()));
        leaveDetailReason.setText(leave.getLeaveReason());

        String remark = leave.getApprovalRemark() == null ? "无备注信息" : leave.getApprovalRemark();
        if (leave.getApprovalResult() > 0) {
            if (leave.getApprovalResult() == 1) {
                approvalResult = 1;
            } else {
                approvalResult = 2;
            }
        }
        switch (approvalResult) {
            case 0:
                leaveDetailResult.setText("审批中");
                leaveDetailResult.setTextColor(ColorUtils.getColor(R.color.blue));
                leaveDetailRemarkLayout.setVisibility(View.GONE);
                leaveDetailDelete.setVisibility(View.VISIBLE);
                leaveDetailDelete.setOnClickListener(v -> {
                    //撤销申请

                    BasePopupView loadingView = new XPopup.Builder(context).asLoading("撤销中").show();
                    Map<String, String> map = new HashMap<>();
                    map.put("leaveId",String.valueOf(leave.getLeaveId()));
                    NetUtils.request(context,"/leave/deleteLeave",map,result->{
                        loadingView.dismiss();
                        UiUtils.showSuccess(context,result.getMsg());
                        return;
                    });

                });
                break;
            case 1:
                leaveDetailResult.setText("不批准");
                leaveDetailResult.setTextColor(ColorUtils.getColor(R.color.red));
                leaveDetailRemark.setText(remark);
                break;
            case 2:
                leaveDetailResult.setText("批准");
                leaveDetailResult.setTextColor(ColorUtils.getColor(R.color.green));
                leaveDetailRemark.setText(remark);
                break;
        }

    }
}