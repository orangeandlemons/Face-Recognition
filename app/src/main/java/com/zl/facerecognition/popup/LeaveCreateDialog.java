package com.zl.facerecognition.popup;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codbking.widget.DatePickDialog;
import com.codbking.widget.bean.DateType;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.zl.facerecognition.R;
import com.zl.facerecognition.utils.NetUtils;
import com.zl.facerecognition.utils.TimeUtil;
import com.zl.facerecognition.utils.UiUtils;
import com.zl.facerecognition.utils.ViewUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;

public class LeaveCreateDialog extends Dialog implements View.OnClickListener {

    private TextView yes;
    private TextView no;
    private TextView leaveCreateDateStart;
    private EditText leaveCreateNum;
    private EditText leaveCreateReason;
    private final Activity activity;
    private Integer courseId;

    private Timestamp startTime;
    private Timestamp endTime;

    public LeaveCreateDialog(@NonNull Context context, Integer courseId,Activity activity) {
        super(context,R.style.dialo);
        this.courseId = courseId;
        this.activity=activity;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_leave_create, null);

        setContentView(view);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
//        initEvent();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        no = (TextView) findViewById(R.id.no);
        yes = (TextView) findViewById(R.id.yes);
        leaveCreateDateStart = (TextView) findViewById(R.id.leave_create_date_start);
        leaveCreateNum = (EditText) findViewById(R.id.leave_create_num);
        leaveCreateReason = (EditText) findViewById(R.id.leave_create_reason);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        leaveCreateDateStart.setOnClickListener(this);
    }

    private void initEvent() {
        yes.setOnClickListener(v -> {
            String reason = leaveCreateReason.getText().toString();
            if (reason.length() < 1 || startTime == null || leaveCreateNum.getText().toString().length() < 1) {
                Toast.makeText(v.getContext(), "请将信息填写完整", Toast.LENGTH_SHORT).show();
                return;
            }
            Integer integer = Integer.valueOf(leaveCreateNum.getText().toString());
            if (integer < 1) {
                Toast.makeText(v.getContext(), "请假时长最少为1", Toast.LENGTH_SHORT).show();
                return;
            }

            String s = leaveCreateDateStart.getText().toString();
            s = s + " 00:00:00";
            startTime = Timestamp.valueOf(s);

            endTime = new Timestamp(startTime.getTime() + 1000 * 3600 * 24 * integer);
            Log.d("NET-->", startTime + "," + endTime);

            BasePopupView loadingView = new XPopup.Builder(v.getContext()).asLoading("请假申请").show();
            Map<String, String> map = new HashMap<>();
            map.put("leaveReason", reason);
            map.put("leaveTime", startTime.toString());
            map.put("backTime", endTime.toString());

            map.put("courseId", String.valueOf(courseId));
            map.put("studentId", v.getContext().getSharedPreferences("localRecord", Context.MODE_PRIVATE).getString("id", ""));

            NetUtils.request(getContext(), "/leave/addLeave", map, result -> {

                if (result.getCode().equals("200")) {
                    loadingView.dismiss();
                    UiUtils.showText(v.getContext(), result.getMsg());
                } else {

                    loadingView.dismiss();
                    UiUtils.showText(v.getContext(), result.getMsg());
                }
                return;
            });

        });

        no.setOnClickListener(v -> dismiss());
    }


    private DatePickDialog createDateTimeDialog() {
        DatePickDialog dialog = new DatePickDialog(getContext());
        dialog.setYearLimt(5);
        dialog.setTitle("选择时间");
        dialog.setType(DateType.TYPE_YMD);
        dialog.setMessageFormat("yyyy-MM-dd");
        return dialog;
    }

    @Override
    public void show() {
        super.show();
        ViewUtils.show(getWindow());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leave_create_date_start:
                TimeUtil.selectDateTime(activity, "请选择请假时间", true, true, true, (d, view) -> {
                    startTime = new Timestamp(d.getTime());
                    leaveCreateDateStart.setText(TimeUtil.getDateTimeText(d, "yyyy-MM-dd"));
                });

                break;
            case R.id.yes:
                String reason = leaveCreateReason.getText().toString();
                if (reason.length() < 1 || startTime == null || leaveCreateNum.getText().toString().length() < 1) {
                    Toast.makeText(v.getContext(), "请将信息填写完整", Toast.LENGTH_SHORT).show();
                    return;
                }
                Integer integer = Integer.valueOf(leaveCreateNum.getText().toString());
                if (integer < 1) {
                    Toast.makeText(v.getContext(), "请假时长最少为1", Toast.LENGTH_SHORT).show();
                    return;
                }

                String s = leaveCreateDateStart.getText().toString();
                s = s + " 00:00:00";
                startTime = Timestamp.valueOf(s);

                endTime = new Timestamp(startTime.getTime() + 1000 * 3600 * 24 * integer);
                Log.d("NET-->", startTime + "," + endTime);

                BasePopupView loadingView = new XPopup.Builder(v.getContext()).asLoading("请假申请").show();
                Map<String, String> map = new HashMap<>();
                map.put("leaveReason", reason);
                map.put("leaveTime", startTime.toString());
                map.put("backTime", endTime.toString());
                map.put("courseId", String.valueOf(courseId));
                map.put("studentId", v.getContext().getSharedPreferences("localRecord", Context.MODE_PRIVATE).getString("id", ""));

                NetUtils.request(v.getContext(), "/leave/addLeave", map, result -> {

                    if (result.getCode().equals("200")) {
                        loadingView.dismiss();
                        UiUtils.showText(v.getContext(), result.getMsg());
                        dismiss();
                    } else {
                        loadingView.dismiss();
                        UiUtils.showText(v.getContext(), result.getMsg());
                        dismiss();
                    }
                    return;
                });
                break;
            case R.id.no:
                dismiss();
                break;
        }

    }
}
