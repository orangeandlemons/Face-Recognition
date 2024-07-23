package com.zl.facerecognition.activity.student;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ColorUtils;
import com.gyf.barlibrary.ImmersionBar;
import com.squareup.picasso.Picasso;
import com.zl.facerecognition.R;
import com.zl.facerecognition.activity.BaseActivity;
import com.zl.facerecognition.entity.CheckinList;
import com.zl.facerecognition.entity.Record;
import com.zl.facerecognition.utils.Constants;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class StudentRecord extends BaseActivity {
    private View view;
    private TextView studentRecordName;
    private TextView studentRecordAccount;
    private TextView textView3;
    private TextView studentRecordLocation;
    private TextView studentRecordStartTime;
    private TextView studentRecordEndTime;
    private TextView studentRecordResult;
    private LinearLayout studentRecordDetailLayout;
    private TextView studentRecordMyLocation;
    private TextView studentRecordMyTime;
    private LinearLayout faceLayout;
    private ImageView studentRecordFace;
    private CheckinList check;
    private Record record;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_student_record);
        ImmersionBar.with(this).init();

        Intent intent = getIntent();
        check = (CheckinList)intent.getExtras().getSerializable("attend");
        record = getRecord(intent.getExtras().getString("record"));
        init();

    }

    private void init() {
        view = (View) findViewById(R.id.view);
        studentRecordName = (TextView) findViewById(R.id.student_record_name);
        studentRecordAccount = (TextView) findViewById(R.id.student_record_account);
        textView3 = (TextView) findViewById(R.id.textView3);
        studentRecordLocation = (TextView) findViewById(R.id.student_record_location);
        studentRecordStartTime = (TextView) findViewById(R.id.student_record_start_time);
        studentRecordEndTime = (TextView) findViewById(R.id.student_record_end_time);
        studentRecordResult = (TextView) findViewById(R.id.student_record_result);
        studentRecordDetailLayout = (LinearLayout) findViewById(R.id.student_record_detail_layout);
        studentRecordMyLocation = (TextView) findViewById(R.id.student_record_my_location);
        studentRecordMyTime = (TextView) findViewById(R.id.student_record_my_time);
        faceLayout = (LinearLayout) findViewById(R.id.face_layout);
        studentRecordFace = (ImageView) findViewById(R.id.student_record_face);
        studentRecordName.setText(record.getRecordName());
        studentRecordAccount.setText(record.getRecordAccount());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        studentRecordLocation.setText(check.getLocation());
        studentRecordStartTime.setText(format.format(check.getStartTime()));
        studentRecordEndTime.setText(format.format(check.getEndTime()));

        if (record.getRecordResult().equals("2") || record.getRecordResult().equals("1")){
            if (record.getRecordResult().equals("2")) {
                studentRecordResult.setText("签到成功");
                studentRecordResult.setTextColor(ColorUtils.getColor(R.color.green));
            } else {
                studentRecordResult.setText("签到失败");
                studentRecordResult.setTextColor(ColorUtils.getColor(R.color.red));
            }

            studentRecordMyLocation.setText(record.getRecordLocation() == null ? "--" : record.getRecordLocation());
            studentRecordMyTime.setText(record.getRecordTime() == null ? "--" : format.format(record.getRecordTime()));
            if (record.getRecordPhoto() != null) {
                Log.d("NET-->photo:",Constants.SERVICE_PATH+ record.getRecordPhoto());
                faceLayout.setVisibility(View.VISIBLE);
                Picasso.with(this)
                        .load(Constants.SERVICE_PATH + record.getRecordPhoto())
                        .fit()
                        .error(R.drawable.ic_net_error)
                        .into(studentRecordFace);
            } else {
                faceLayout.setVisibility(View.GONE);
            }
        } else {
            if (record.getRecordResult().equals("3")){
                studentRecordResult.setText("已请假");
                studentRecordResult.setTextColor(ColorUtils.getColor(R.color.green));
            } else {
                studentRecordResult.setText("缺勤");
                studentRecordResult.setTextColor(ColorUtils.getColor(R.color.gray2));
            }
            studentRecordDetailLayout.setVisibility(View.GONE);
        }
    }

    private Record getRecord(String s){
        SharedPreferences preferences = getSharedPreferences("localRecord", MODE_PRIVATE);
        JSONObject object = JSONObject.parseObject(s);
        Timestamp timestamp = null;
        if (object.getTimestamp("recordTime") != null){
            timestamp = new Timestamp(object.getLongValue("recordTime")+ 8000 * 3600);
        }
        Record record = new Record(preferences.getString("avatar",""),timestamp,
                preferences.getString("name",""), preferences.getString("account",""),
                object.getString("recordResult"),object.getString("recordLocation"));
        record.setRecordPhoto(object.getString("recordPhoto"));
        return record;
    }
}
