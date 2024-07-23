package com.zl.facerecognition.fragment;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.StringUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.squareup.picasso.Picasso;
import com.zl.facerecognition.R;
import com.zl.facerecognition.entity.CourseList;
import com.zl.facerecognition.utils.Constants;
import com.zl.facerecognition.utils.NetUtils;
import com.zl.facerecognition.utils.UiUtils;
import com.zl.facerecognition.viewModel.CourseViewModel;

import java.util.HashMap;
import java.util.Map;

public class Course_stu_info extends Fragment implements View.OnClickListener {

    private ImageView courseStuAvatar;
    private TextView courseStuName;
    private TextView courseStuCode;
    private TextView courseStuTeacherName;
    private TextView courseStuTeacherPhone;
    private TextView courseStuTeacherBirthday;
    private TextView courseStuIntroduce;
    private Button courseStuAdd;
    private CourseViewModel viewModel;
    private CourseList course;

    public static Course_stu_info newInstance() {
        return new Course_stu_info();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.course_stu_info_fragment, container, false);


        courseStuAvatar = (ImageView) view.findViewById(R.id.course_stu_avatar);
        courseStuName = (TextView) view.findViewById(R.id.course_stu_name);
        courseStuCode = (TextView) view.findViewById(R.id.course_stu_code);
        courseStuTeacherName = (TextView) view.findViewById(R.id.course_stu_teacher_name);
        courseStuTeacherPhone = (TextView) view.findViewById(R.id.course_stu_teacher_phone);
        courseStuTeacherBirthday = (TextView) view.findViewById(R.id.course_stu_teacher_birthday);
        courseStuIntroduce = (TextView) view.findViewById(R.id.course_stu_introduce);
        courseStuAdd = (Button) view.findViewById(R.id.course_stu_add);
        courseStuAdd.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(CourseViewModel.class);
        course = viewModel.getCourse().getValue();
        initView();
    }

    private void initView() {
        Picasso.with(this.getContext())
                .load(Constants.SERVICE_PATH + course.getCourseAvatar())
                .fit()
                .error(R.drawable.ic_net_error)
                .into(courseStuAvatar);

        courseStuName.setText(course.getCourseName());
        courseStuCode.setText(course.getCourseCode());
        courseStuIntroduce.setText(course.getCourseIntroduce());
        courseStuTeacherBirthday.setText(course.getUserBirthday());
        courseStuTeacherName.setText(course.getUesrName());
        courseStuTeacherPhone.setText(course.getUserPhone());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.course_stu_add:

                BasePopupView loadingView = new XPopup.Builder(v.getContext()).asLoading("退出课程中").show();
                Map<String, String> map = new HashMap<>();
                map.put("courseId", String.valueOf(course.getCourseId()));
                map.put("studentId", v.getContext().getSharedPreferences("localRecord", Context.MODE_PRIVATE).getString("id", ""));
                NetUtils.request(v.getContext(), "/courseStudent/deleteCourseStudent", map, result -> {
                    if (result.getMsg() != null) {
                        loadingView.dismiss();
                        getActivity().finish();
                        UiUtils.showSuccess(v.getContext(), result.getMsg());
                    } else {
                        loadingView.dismiss();
                        UiUtils.showError(v.getContext(), result.getMsg());
                    }
                });

                break;
        }
    }
}
