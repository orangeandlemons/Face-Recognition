package com.zl.facerecognition.activity.teacher;

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
import com.zl.facerecognition.fragment.CourseListFragment;
import com.zl.facerecognition.fragment.Course_tea_attendance;
import com.zl.facerecognition.fragment.Course_tea_checkin;
import com.zl.facerecognition.fragment.Course_tea_infoFragment;
import com.zl.facerecognition.fragment.Course_tea_leave;
import com.zl.facerecognition.fragment.Course_tea_memberFragment;
import com.zl.facerecognition.fragment.MyInfoView;
import com.zl.facerecognition.viewModel.CourseViewModel;

import java.util.ArrayList;
import java.util.List;

public class TeacherCourseDetail extends BaseActivity {
    private CourseViewModel viewModel;
    private FragmentManager fragmentManager;
    private FrameLayout teacher_course_fragment;
    private LinearLayout teadetaBottomBar;
    private RelativeLayout btn_memember;
    private TextView tv_memember;
    private ImageView iv_memeber;
    private RelativeLayout btn_attend;
    private TextView tv_attend;
    private ImageView iv_attend;
    private RelativeLayout btn_check;
    private TextView tv_check;
    private ImageView iv_check;
    private RelativeLayout btn_leave;
    private TextView tv_leave;
    private ImageView iv_leave;
    private RelativeLayout btn_info;
    private TextView tv_info;
    private ImageView iv_info;
    private Course_tea_memberFragment course_tea_memberFragment;
    private Course_tea_checkin course_tea_checkin;
    private Course_tea_infoFragment course_tea_infoFragment;
    private Course_tea_leave course_tea_leave;
    private Course_tea_attendance course_tea_attendance;
    private FragmentTransaction mTransaction;

    private List<Fragment> mFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_teacher_course_detail);
        ImmersionBar.with(this).init();

        fragmentManager = getSupportFragmentManager();

        initBodyLayout();
        setTitle("课程详情");
        Intent intent = getIntent();
//        执行网络上传
        CourseList course = (CourseList) intent.getExtras().getSerializable("course");
        viewModel = new ViewModelProvider(this).get(CourseViewModel.class);

//        先初始化course
        viewModel.getCourse();
        viewModel.setCourse(course);

        initBottom();
        initListener();
        setInitStatus();
    }

    private void initListener() {
        for (int i = 0; i < teadetaBottomBar.getChildCount(); i++) {
            teadetaBottomBar.getChildAt(i).setOnClickListener(this);
        }
    }
    /**
     * 获取帧布局
     */
    private void initBodyLayout() {
        teacher_course_fragment = findViewById(R.id.teacher_course_fragment);
    }

    private void initBottom() {

        teacher_course_fragment = (FrameLayout) findViewById(R.id.teacher_course_fragment);
        teadetaBottomBar = (LinearLayout) findViewById(R.id.teadeta_bottom_bar);
        btn_memember = (RelativeLayout) findViewById(R.id.menu_memember_btn);
        tv_memember = (TextView) findViewById(R.id.menu_memember_tv);
        iv_memeber = (ImageView) findViewById(R.id.menu_memember_iv);
        btn_leave = (RelativeLayout) findViewById(R.id.menu_leave_btn);
        tv_leave = (TextView) findViewById(R.id.menu_leave_tv);
        iv_leave = (ImageView) findViewById(R.id.menu_leave_iv);
        btn_check = (RelativeLayout) findViewById(R.id.menu_check_btn);
        tv_check = (TextView) findViewById(R.id.menu_check_tv);
        iv_check = (ImageView) findViewById(R.id.menu_check_iv);
        btn_attend = (RelativeLayout) findViewById(R.id.menu_tongji_btn);
        tv_attend = (TextView) findViewById(R.id.menu_tongji_tv);
        iv_attend = (ImageView) findViewById(R.id.menu_tongji_iv);
        btn_info = (RelativeLayout) findViewById(R.id.menu_info_btn);
        tv_info = (TextView) findViewById(R.id.menu_info_tv);
        iv_info = (ImageView) findViewById(R.id.menu_info_iv);
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
        tv_attend.setTextColor(Color.parseColor("#666666"));
        tv_leave.setTextColor(Color.parseColor("#666666"));
        tv_check.setTextColor(Color.parseColor("#666666"));
        tv_memember.setTextColor(Color.parseColor("#666666"));
        tv_info.setTextColor(Color.parseColor("#666666"));
        iv_check.setImageResource(R.drawable.in_check);
        iv_attend.setImageResource(R.drawable.tongji);
        iv_info.setImageResource(R.drawable.ic_info);
        iv_leave.setImageResource(R.drawable.ic_jingjia);
        iv_memeber.setImageResource(R.drawable.ic_memeber);
        for (int i = 0; i < teadetaBottomBar.getChildCount(); i++) {
            teadetaBottomBar.getChildAt(i).setSelected(false);
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
                btn_memember.setSelected(true);
                tv_memember.setTextColor(Color.parseColor("#0097f7"));
                rl_title_bar.setVisibility(View.VISIBLE);
                setTitle("成员");
                break;
            case 1:
                btn_leave.setSelected(true);
                tv_leave.setTextColor(Color.parseColor("#0097f7"));
                rl_title_bar.setVisibility(View.VISIBLE);
                setTitle("请假");
                break;
            case 2:
                btn_check.setSelected(true);
                tv_check.setTextColor(Color.parseColor("#0097f7"));
                rl_title_bar.setVisibility(View.VISIBLE);
                setTitle("签到");
                break;
            case 3:
                btn_attend.setSelected(true);
                tv_attend.setTextColor(Color.parseColor("#0097f7"));
                rl_title_bar.setVisibility(View.VISIBLE);
                setTitle("统计");
                break;
            case 4:
               btn_info.setSelected(true);
                tv_info.setTextColor(Color.parseColor("#0097f7"));
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
                if (course_tea_memberFragment == null) {
                    course_tea_memberFragment = new Course_tea_memberFragment();
                    mFragments.add(course_tea_memberFragment);
                    hideOthersFragment(course_tea_memberFragment, true);
                } else {
                    hideOthersFragment(course_tea_memberFragment, false);
                }
                break;
            case 1:
                if (course_tea_leave == null) {
                    course_tea_leave = new Course_tea_leave();
                    mFragments.add(course_tea_leave);
                    hideOthersFragment(course_tea_leave,true);

                } else {
                    hideOthersFragment(course_tea_leave,false);
                }
                break;
            case 2:
                //TODO 课程详情
                if (course_tea_checkin == null) {
                    course_tea_checkin = new Course_tea_checkin();
                    mFragments.add(course_tea_checkin);
                    hideOthersFragment(course_tea_checkin,true);

                } else {
                    hideOthersFragment(course_tea_checkin,false);
                }
                break;
            case 3:
                if (course_tea_attendance == null) {
                    course_tea_attendance = new Course_tea_attendance();
                    mFragments.add(course_tea_attendance);
                    hideOthersFragment(course_tea_attendance,true);

                } else {
                    hideOthersFragment(course_tea_attendance,false);
                }
                break;
            case 4:
                if (course_tea_infoFragment == null) {
                    course_tea_infoFragment = new Course_tea_infoFragment();
                    mFragments.add(course_tea_infoFragment);
                    hideOthersFragment(course_tea_infoFragment,true);

                } else {
                    hideOthersFragment(course_tea_infoFragment,false);
                }
                break;
        }
    }
    private void hideOthersFragment(Fragment showFragment, boolean add) {
        mTransaction = fragmentManager.beginTransaction();
        if (add) {
            mTransaction.add(R.id.teacher_course_fragment, showFragment);
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
            case R.id.menu_memember_btn:
                //清除底部按钮的的选中状态
                clearBottomImagesStatus();
                //显示对应的页面
                selectDisplayView(0);
                break;
            case R.id.menu_leave_btn:
                //清除底部按钮的的选中状态
                clearBottomImagesStatus();
                //显示对应的页面
                selectDisplayView(1);
                break;
            case R.id.menu_check_btn:
                //清除底部按钮的的选中状态
                clearBottomImagesStatus();
                //显示对应的页面
                selectDisplayView(2);
                break;
            case R.id.menu_tongji_btn:
                //清除底部按钮的的选中状态
                clearBottomImagesStatus();
                //显示对应的页面
                selectDisplayView(3);
                break;
            case R.id.menu_info_btn:
                //清除底部按钮的的选中状态
                clearBottomImagesStatus();
                //显示对应的页面
                selectDisplayView(4);
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
        //移除不需要的视图
//        removeAllview();
        //选择视图
        createView(index);
        //设置底部按钮的选中状态
        setSelectedStatus(index);
    }

    /**
     * 移除不需要的视图
     */
    private void removeAllview() {
        for (int i = 0; i < teacher_course_fragment.getChildCount(); i++) {
            teacher_course_fragment.getChildAt(i).setVisibility(View.GONE);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
