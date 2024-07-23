package com.zl.facerecognition.activity.student;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gyf.barlibrary.ImmersionBar;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.squareup.picasso.Picasso;
import com.zl.facerecognition.R;
import com.zl.facerecognition.activity.BaseActivity;
import com.zl.facerecognition.entity.CourseList;
import com.zl.facerecognition.utils.Constants;
import com.zl.facerecognition.utils.NetUtils;
import com.zl.facerecognition.utils.UiUtils;
import com.zl.facerecognition.utils.ViewUtils;
import com.zl.facerecognition.viewModel.AddCourseViewModel;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class AddCourseActivity extends BaseActivity implements View.OnClickListener {


    private ImageView iv_courseAvatar;
    private TextView tv_courseName;
    private TextView tv_code;
    private TextView tv_teacherName;
    private TextView tv_TeacherPhone;
    private TextView tv_TeacherBirthday;
    private TextView tv_Introduce;
    private Button btn_Add;
    private AddCourseViewModel viewModel;
    private CourseList course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_add_course);
        ImmersionBar.with(this).init();
        setTitle("课程信息");
        init();
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        viewModel = new ViewModelProvider(this).get(AddCourseViewModel.class);
        viewModel.getCourse().observe(this, courseList -> {
            course = courseList;
            initView();
        });
        assert data != null;
        viewModel.updateCourse(data);
    }

    private void initView() {
        Picasso.with(this)
                .load(Constants.SERVICE_PATH + course.getCourseAvatar())
                .fit()
                .error(R.drawable.ic_net_error)
                .into(iv_courseAvatar);
        tv_courseName.setText(course.getCourseName());
        tv_code.setText(course.getCourseCode());
        tv_Introduce.setText(course.getCourseIntroduce());
        tv_TeacherBirthday.setText(course.getUserBirthday().toString());
        tv_teacherName.setText(course.getUesrName());
        tv_TeacherPhone.setText(course.getUserPhone());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.course_confirm_add:
                Map<String, String> map = new HashMap<>();
                map.put("courseCode", course.getCourseCode());
                map.put("studentId", getSharedPreferences("localRecord", MODE_PRIVATE).getString("id", ""));
                BasePopupView loadingView = new XPopup.Builder(context).asLoading("加入课程中").show();
                NetUtils.request(v.getContext(), Constants.ADD_COURSE_STUDENT, map, result -> {
                    if (result.getMsg() != null) {
                        loadingView.dismiss();
                        String s = result.getData();
                        JSONObject data = JSON.parseObject(s);
                        Intent intent = new Intent(".activity.StudentCouDetail");
                        Bundle bundle = new Bundle();
                        course.setJoinTime(data.getString("joinTime"));
                        bundle.putSerializable("course", course);
                        intent.putExtras(bundle);
                        v.getContext().startActivity(intent);
                        finish();
                        UiUtils.showSuccess(v.getContext(), result.getMsg());
                    } else {
                        loadingView.dismiss();
                        UiUtils.showError(v.getContext(), result.getMsg());
                    }
                    return;
                });
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void init() {
        rl_title_bar.setVisibility(View.VISIBLE);
        iv_courseAvatar = findViewById(R.id.course_confirm_avatar);
        tv_courseName = findViewById(R.id.course_confirm_name);
        tv_code = findViewById(R.id.course_confirm_code);
        tv_teacherName = findViewById(R.id.course_confirm_teacher_name);
        tv_TeacherPhone = findViewById(R.id.course_confirm_teacher_phone);
        tv_TeacherBirthday = findViewById(R.id.course_confirm_teacher_birthday);
        tv_Introduce = findViewById(R.id.course_confirm_introduce);
        btn_Add = findViewById(R.id.course_confirm_add);
        btn_Add.setOnClickListener(this);
    }
}


