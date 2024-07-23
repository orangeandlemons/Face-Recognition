package com.zl.facerecognition.activity.student;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.zl.facerecognition.R;
import com.zl.facerecognition.activity.BaseActivity;
import com.zl.facerecognition.entity.CourseList;
import com.zl.facerecognition.fragment.Course_stu_Leave;
import com.zl.facerecognition.fragment.Course_stu_checkin;
import com.zl.facerecognition.fragment.Course_stu_info;
import com.zl.facerecognition.fragment.Course_tea_attendance;
import com.zl.facerecognition.fragment.Course_tea_checkin;
import com.zl.facerecognition.fragment.Course_tea_infoFragment;
import com.zl.facerecognition.fragment.Course_tea_leave;
import com.zl.facerecognition.fragment.Course_tea_memberFragment;
import com.zl.facerecognition.viewModel.CourseViewModel;

import java.util.ArrayList;
import java.util.List;

public class StudentCouDetail extends BaseActivity {

    private FrameLayout studentCourseFragment;
    private LinearLayout studenBottomBar;
    private RelativeLayout menu1LeaveBtn;
    private TextView menu1LeaveTv;
    private ImageView menu1LeaveIv;
    private RelativeLayout menu1CheckBtn;
    private TextView menu1CheckTv;
    private ImageView menu1CheckIv;
    private RelativeLayout menu1InfoBtn;
    private TextView menu1InfoTv;
    private ImageView menu1InfoIv;
    private FragmentTransaction mTransaction;
    private FragmentManager fragmentManager;
    private List<Fragment> mFragments = new ArrayList<>();
    private Course_stu_checkin course_stu_checkin;
    private Course_stu_info course_stu_info;
    private Course_stu_Leave course_stu_leave;

    private CourseViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_student_cou_detail);
        ImmersionBar.with(this).init();
        fragmentManager = getSupportFragmentManager();

        Intent intent = getIntent();
        CourseList course = (CourseList) intent.getExtras().getSerializable("course");
        viewModel = new ViewModelProvider(this).get(CourseViewModel.class);
//        先初始化course
        viewModel.getCourse();
        viewModel.setCourse(course);
        initBodyLayout();
        initBottom();
        initListener();
        setInitStatus();

    }
    private void initListener() {
        for (int i = 0; i < studenBottomBar.getChildCount(); i++) {
            studenBottomBar.getChildAt(i).setOnClickListener(this);
        }
    }
    /**
     * 获取帧布局
     */
    private void initBodyLayout() {
        studentCourseFragment = findViewById(R.id.student_course_fragment);
    }
    private void initBottom() {


        studentCourseFragment = (FrameLayout) findViewById(R.id.student_course_fragment);
        studenBottomBar = (LinearLayout) findViewById(R.id.studen_bottom_bar);
        menu1LeaveBtn = (RelativeLayout) findViewById(R.id.menu1_leave_btn);
        menu1LeaveTv = (TextView) findViewById(R.id.menu1_leave_tv);
        menu1LeaveIv = (ImageView) findViewById(R.id.menu1_leave_iv);
        menu1CheckBtn = (RelativeLayout) findViewById(R.id.menu1_check_btn);
        menu1CheckTv = (TextView) findViewById(R.id.menu1_check_tv);
        menu1CheckIv = (ImageView) findViewById(R.id.menu1_check_iv);
        menu1InfoBtn = (RelativeLayout) findViewById(R.id.menu1_info_btn);
        menu1InfoTv = (TextView) findViewById(R.id.menu1_info_tv);
        menu1InfoIv = (ImageView) findViewById(R.id.menu1_info_iv);
        mTransaction = fragmentManager.beginTransaction();

    }
    /**
     * 设置界面View初始化的状态
     */
    private void setInitStatus() {
        clearBottomImagesStatus();
        setSelectedStatus(0);
        createView(0);
    }
    /**
     * 清除底部按钮的选中状态
     */
    private void clearBottomImagesStatus() {
        menu1InfoTv.setTextColor(Color.parseColor("#666666"));
        menu1CheckTv.setTextColor(Color.parseColor("#666666"));
        menu1LeaveTv.setTextColor(Color.parseColor("#666666"));
        menu1CheckIv.setImageResource(R.drawable.in_check);
        menu1InfoIv.setImageResource(R.drawable.ic_info);
        menu1LeaveIv.setImageResource(R.drawable.ic_jingjia);
        for (int i = 0; i < studenBottomBar.getChildCount(); i++) {
            studenBottomBar.getChildAt(i).setSelected(false);
        }
    }
    /**
     * 设置底部按钮的选中状态
     *
     * @param index
     */
    private void setSelectedStatus(int index) {
        switch (index) {

            case 0:
                menu1LeaveBtn.setSelected(true);
                menu1LeaveTv.setTextColor(Color.parseColor("#0097f7"));
                rl_title_bar.setVisibility(View.VISIBLE);
                setTitle("请假");
                break;
            case 1:
                menu1CheckBtn.setSelected(true);
               menu1CheckTv.setTextColor(Color.parseColor("#0097f7"));
                rl_title_bar.setVisibility(View.VISIBLE);
                setTitle("签到");
                break;
            case 2:
                menu1InfoBtn.setSelected(true);
                menu1InfoTv.setTextColor(Color.parseColor("#0097f7"));
                rl_title_bar.setVisibility(View.VISIBLE);
                setTitle("信息");
                break;
        }
    }

    /**
     * 选择视图
     *
     * @param index
     */
    private void createView(int index) {

        switch (index) {
            case 0:
                if (course_stu_leave == null) {
                    course_stu_leave = new Course_stu_Leave();
                    mFragments.add(course_stu_leave);
                    hideOthersFragment(course_stu_leave,true);

                } else {
                    hideOthersFragment(course_stu_leave,false);
                }
                break;
            case 1:
                if (course_stu_checkin == null) {
                    course_stu_checkin = new Course_stu_checkin();
                    mFragments.add(course_stu_checkin);
                    hideOthersFragment(course_stu_checkin, true);
                } else {
                    hideOthersFragment(course_stu_checkin, false);
                }
                break;
            case 2:
                //TODO 课程详情
                if (course_stu_info == null) {
                    course_stu_info = new Course_stu_info();
                    mFragments.add(course_stu_info);
                    hideOthersFragment(course_stu_info,true);

                } else {
                    hideOthersFragment(course_stu_info,false);
                }
                break;

        }
    }
    private void hideOthersFragment(Fragment showFragment, boolean add) {
        mTransaction = fragmentManager.beginTransaction();
        if (add) {
            mTransaction.add(R.id.student_course_fragment, showFragment);
        }

        for (Fragment fragment : mFragments) {
            if (showFragment.equals(fragment)) {
                mTransaction.show(fragment);
            } else {
                mTransaction.hide(fragment);
            }
        }
        mTransaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.menu1_leave_btn:
                //清除底部按钮的的选中状态
                clearBottomImagesStatus();
                //显示对应的页面
                selectDisplayView(0);
                break;
            case R.id.menu1_check_btn:
                //清除底部按钮的的选中状态
                clearBottomImagesStatus();
                //显示对应的页面
                selectDisplayView(1);
                break;
            case R.id.menu1_info_btn:
                //清除底部按钮的的选中状态
                clearBottomImagesStatus();
                //显示对应的页面
                selectDisplayView(2);
                break;

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }
    /**
     * 显示对应的页面
     *
     * @param index
     */
    private void selectDisplayView(int index) {
        //选择视图
        createView(index);
        //设置底部按钮的选中状态
        setSelectedStatus(index);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}