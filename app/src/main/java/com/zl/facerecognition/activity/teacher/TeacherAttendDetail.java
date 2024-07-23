package com.zl.facerecognition.activity.teacher;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ResourceUtils;
import com.gyf.barlibrary.ImmersionBar;
import com.zl.facerecognition.R;
import com.zl.facerecognition.activity.BaseActivity;
import com.zl.facerecognition.adapter.CheckDetailAdapter;
import com.zl.facerecognition.adapter.CheckinAdapter;
import com.zl.facerecognition.entity.CheckinList;
import com.zl.facerecognition.utils.NetUtils;
import com.zl.facerecognition.utils.UiUtils;
import com.zl.facerecognition.utils.ViewUtils;
import com.zl.facerecognition.viewModel.TeacherRecordDetailViewModel;

import java.util.HashMap;
import java.util.Map;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

public class TeacherAttendDetail extends BaseActivity {

    private RelativeLayout titleBar;
    private ImageView ivBack;
    private TextView tvMainTitle;
    private Button attendTypeAll;
    private Button attendTypeSuccess;
    private Button attendTypeFailure;
    private Button attendTypeAbsent;
    private Button attendTypeLeave;
    private RecyclerView recyclerAttendList;
    private LinearLayout contentNotFoundLayout;
    private String data = "[]";
    private CheckinList checkinList;

    private TeacherRecordDetailViewModel viewModel;
    private Integer currentType = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_attend_detail);
        ImmersionBar.with(this).init();
        setTitle("统计详情");
        init();
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        checkinList = (CheckinList) intent.getExtras().getSerializable("attend");
        Map<String, String> map = new HashMap<>();
        map.put("attendId",String.valueOf(checkinList.getAttendId()));

        viewModel = new ViewModelProvider(this).get(TeacherRecordDetailViewModel.class);
        viewModel.getCheckDetailList().observe(this,checkList -> {
            CheckDetailAdapter checkDetailAdapter = new CheckDetailAdapter(checkList,checkinList.getType());
            checkDetailAdapter.setResultChangedListener(() -> refreshView(currentType, map));
            ViewUtils.setRecycler(this, R.id.recycler_attend_list, checkDetailAdapter);
            if (checkList.size() == 0) {
                contentNotFoundLayout.setVisibility(View.VISIBLE);
            } else {
                contentNotFoundLayout.setVisibility(View.GONE);
            }
        });
        NetUtils.request(this,"/record/findAllRecord",map,result-> {
            if(result.getCode().equals("200")){
                data=result.getData();
                viewModel.setRecordList(data);
            } else {
                UiUtils.showError(this,result.getMsg());
            }
        });
    }

    private void init() {


        titleBar = (RelativeLayout) findViewById(R.id.title_bar);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvMainTitle = (TextView) findViewById(R.id.tv_main_title);
        attendTypeAll = (Button) findViewById(R.id.attend_type_all);
        attendTypeSuccess = (Button) findViewById(R.id.attend_type_success);
        attendTypeFailure = (Button) findViewById(R.id.attend_type_failure);
        attendTypeAbsent = (Button) findViewById(R.id.attend_type_absent);
        attendTypeLeave = (Button) findViewById(R.id.attend_type_leave);
        recyclerAttendList = (RecyclerView) findViewById(R.id.recycler_attend_list);
        contentNotFoundLayout = (LinearLayout) findViewById(R.id.content_not_found_layout);
        attendTypeAll.setOnClickListener(this);
        attendTypeSuccess.setOnClickListener(this);
        attendTypeAbsent.setOnClickListener(this);
        attendTypeLeave.setOnClickListener(this);
        attendTypeFailure.setOnClickListener(this);

    }

    @SuppressLint("ResourceAsColor")
    public void refreshButton(){
        findViewById(R.id.attend_type_all).setBackgroundColor(R.color.blue);
        findViewById(R.id.attend_type_success).setBackgroundColor(R.color.green);
        findViewById(R.id.attend_type_failure).setBackgroundColor(R.color.red);
        findViewById(R.id.attend_type_absent).setBackgroundColor(R.color.gray3);
        findViewById(R.id.attend_type_leave).setBackgroundColor(R.color.pink);
    }
    @Override
    public void onClick(View v) {
        refreshButton();
        switch (v.getId()){
            case R.id.attend_type_all:
                viewModel.setRecordList(data);
                currentType = -1;
                break;
            case R.id.attend_type_success:
                viewModel.updateRecordList(data,2);
                currentType = 2;
                break;
            case R.id.attend_type_failure:
                viewModel.updateRecordList(data,1);
                currentType = 1;
                break;
            case R.id.attend_type_absent:
                viewModel.updateRecordList(data,0);
                currentType = 0;
                break;
            case R.id.attend_type_leave:
                viewModel.updateRecordList(data,3);
                currentType = 3;
                break;
            case R.id.iv_back:
                finish();
                break;
            default:break;
        }
        v.setBackground(ResourceUtils.getDrawable(R.color.white));
    }
    public void refreshView(Integer type,Map<String, String> map){

        NetUtils.request(this,"/record/findAllRecord",map,result->{
            if(result.getCode().equals("200")){
                data=result.getData();
                viewModel.setRecordList(data);
                switch (type){
                    case -1:
                        findViewById(R.id.attend_type_all).performClick();
                        break;
                    case 0:
                        findViewById(R.id.attend_type_absent).performClick();
                        break;
                    case 1:
                        findViewById(R.id.attend_type_failure).performClick();
                        break;
                    case 2:
                        findViewById(R.id.attend_type_success).performClick();
                        break;
                    case 3:
                        findViewById(R.id.attend_type_leave).performClick();
                        break;
                }
            }else {
                UiUtils.showError(this,result.getMsg());
            }
        });

        return;
    }
}
