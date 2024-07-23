package com.zl.facerecognition.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.zl.facerecognition.R;
import com.zl.facerecognition.fragment.CourseListFragment;
import com.zl.facerecognition.fragment.MyInfoView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    private FrameLayout main_body;
    private LinearLayout mainBottomBar;
    private RelativeLayout bCourseBtn;
    private TextView bTextCourse;
    private ImageView iv_Course;
    private RelativeLayout bDataBtn;
    private TextView tv_Data;
    private ImageView iv_Data;
    private RelativeLayout bMyinfoBtn;
    private TextView tv_Myinfo;
    private ImageView iv_Myinfo;
    private CourseListFragment courseListFragment;
    private FragmentManager fragmentManager;
    private MyInfoView mMyInfoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_main);
        ImmersionBar.with(this).init();
        init();
        if (savedInstanceState == null) {
            fragmentManager = getSupportFragmentManager();
        }
        initBottomBar();
        initListener();
        setInitStatus();
    }

    /**
     * 获取帧布局
     */
    private void initBodyLayout() {
        main_body = findViewById(R.id.main_body);
    }

    /**
     * 初始化控件
     */
    private void init() {

        ivBack.setVisibility(View.GONE);
        setTitle("课程列表");
        initBodyLayout();
    }


    /**
     * 获取底部导航栏上的控件
     */
    private void initBottomBar() {
        mainBottomBar = findViewById(R.id.main_bottom_bar);
        bCourseBtn = findViewById(R.id.bottom_bar_course_btn);
        bTextCourse = findViewById(R.id.bottom_bar_text_course);
        iv_Course = findViewById(R.id.bottom_bar_image_course);
//        bDataBtn = findViewById(R.id.bottom_bar_data_btn);
//        tv_Data = findViewById(R.id.bottom_bar_text_data);
//        iv_Data = findViewById(R.id.bottom_bar_image_data);
        bMyinfoBtn = findViewById(R.id.bottom_bar_myinfo_btn);
        tv_Myinfo = findViewById(R.id.bottom_bar_text_myinfo);
        iv_Myinfo = findViewById(R.id.bottom_bar_image_myinfo);
    }

    /**
     * 设置底部导航栏按钮的监听事件
     */
    private void initListener() {
        for (int i = 0; i < mainBottomBar.getChildCount(); i++) {
            mainBottomBar.getChildAt(i).setOnClickListener(this);
        }
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
        bTextCourse.setTextColor(Color.parseColor("#666666"));
        tv_Myinfo.setTextColor(Color.parseColor("#666666"));
        iv_Course.setImageResource(R.drawable.course_icon);
//        iv_Data.setImageResource(R.drawable.data_icon);
        iv_Myinfo.setImageResource(R.drawable.my_icon);
        for (int i = 0; i < mainBottomBar.getChildCount(); i++) {
            mainBottomBar.getChildAt(i).setSelected(false);
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
                bCourseBtn.setSelected(true);
//                iv_course.setImageResource(R.drawable.main_course_icon_selected);
                bTextCourse.setTextColor(Color.parseColor("#0097f7"));
                rl_title_bar.setVisibility(View.VISIBLE);
                setTitle("课程列表");
                break;
            case 1:
                bMyinfoBtn.setSelected(true);
//                iv_myInfo.setImageResource(R.drawable.main_my_icon_selected);
                tv_Myinfo.setTextColor(Color.parseColor("#0097f7"));
                rl_title_bar.setVisibility(View.VISIBLE);
                setTitle("我的信息");
                break;
        }
    }

    /**
     * 选择视图
     *
     * @param index
     */
    private void createView(int index) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (index) {
            case 0:
                if (courseListFragment == null) {
                    courseListFragment = new CourseListFragment();
                    transaction.add(R.id.main_body, courseListFragment);
                } else {
                    main_body.getChildAt(0).setVisibility(View.VISIBLE);
                }
                transaction.commit();
                break;

            case 1:
                //我的界面
                if(mMyInfoView==null){
                    mMyInfoView=new MyInfoView(this);
                    main_body.addView(mMyInfoView.getView());
                }else {
                    mMyInfoView.getView();
                }
                mMyInfoView.showView();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_bar_course_btn:
                //课程的点击事件
                //清除底部按钮的的选中状态
                clearBottomImagesStatus();
                //显示对应的页面
                selectDisplayView(0);
                break;
            case R.id.bottom_bar_myinfo_btn:
                //我的的点击事件
                //清除底部按钮的的选中状态
                clearBottomImagesStatus();
                //显示对应的页面
                selectDisplayView(1);
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
        removeAllview();
        //选择视图
        createView(index);
        //设置底部按钮的选中状态
        setSelectedStatus(index);
    }

    /**
     * 移除不需要的视图
     */
    private void removeAllview() {
        for (int i = 0; i < main_body.getChildCount(); i++) {
            main_body.getChildAt(i).setVisibility(View.GONE);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 按下按键时触发
     * @param keyCode
     * @param event
     * @return
     */
    protected long exitTime;
    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime)>2000){
                Toast.makeText(MainActivity.this,"再按一次退出APP",Toast.LENGTH_SHORT).show();
                exitTime=System.currentTimeMillis();
            }else{
                MainActivity.this.finish();
                System.exit(0);
            }
            return  true;
        }
        return super.onKeyLongPress(keyCode, event);
    }
}
