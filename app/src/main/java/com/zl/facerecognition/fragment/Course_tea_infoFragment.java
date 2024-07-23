package com.zl.facerecognition.fragment;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.StringUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.squareup.picasso.Picasso;
import com.zl.facerecognition.R;
import com.zl.facerecognition.entity.CommonResult;
import com.zl.facerecognition.entity.CourseList;
import com.zl.facerecognition.popup.CourseMdifyPopup;
import com.zl.facerecognition.utils.Constants;
import com.zl.facerecognition.utils.NetUtils;
import com.zl.facerecognition.utils.UiUtils;
import com.zl.facerecognition.utils.ViewUtils;
import com.zl.facerecognition.viewModel.CourseViewModel;

import java.util.HashMap;
import java.util.Map;

public class Course_tea_infoFragment extends Fragment implements View.OnClickListener {

    private CourseViewModel mViewModel;
    private CourseList course;
    private Context mContext;

    private ImageView courseInfoAvatar;
    private TextView courseInfoName;
    private TextView courseInfoCode;
    private TextView courseInfoTeacherName;
    private TextView courseInfoTeacherPhone;
    private TextView courseInfoTeacherBirthday;
    private TextView courseInfoIntroduce;
    private Button courseInfoModify;
    private Button courseInfoDel;
    private SwipeRefreshLayout fragmentCourseinfoRefresh;




    public static Course_tea_infoFragment newInstance() {
        return new Course_tea_infoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.course_tea_info_fragment, container, false);
        mContext = view.getContext();
        courseInfoAvatar = (ImageView) view.findViewById(R.id.course_info_avatar);
        courseInfoName = (TextView) view.findViewById(R.id.course_info_name);
        courseInfoCode = (TextView) view.findViewById(R.id.course_info_code);
        courseInfoTeacherName = (TextView) view.findViewById(R.id.course_info_teacher_name);
        courseInfoTeacherPhone = (TextView) view.findViewById(R.id.course_info_teacher_phone);
        courseInfoTeacherBirthday = (TextView) view.findViewById(R.id.course_info_teacher_birthday);
        courseInfoIntroduce = (TextView) view.findViewById(R.id.course_info_introduce);
        courseInfoModify = (Button) view.findViewById(R.id.course_info_modify);
        courseInfoDel = (Button) view.findViewById(R.id.course_info_del);
//        fragmentCourseinfoRefresh = (SwipeRefreshLayout) view.findViewById(R.id.fragment_courseinfo_refresh);
        courseInfoDel.setOnClickListener(this);
        courseInfoModify.setOnClickListener(this);

        //下拉刷新的颜色
//        fragmentCourseinfoRefresh.setColorSchemeColors(ViewUtils.getRefreshColor());

        /*//刷新
        fragmentCourseinfoRefresh.setOnRefreshListener(() -> {
            fragmentCourseinfoRefresh.setRefreshing(false);
            NetUtils.request(getContext(), Constants.FIND_COURSE_BYSTUID, params, (CommonResult result) -> {
                if (result.getMsg() != null) {
                    viewModel.updateCourses(result.getData());
                }
                Toast.makeText(getActivity(), result.getMsg(), Toast.LENGTH_SHORT).show();
                System.out.println(result.getMsg());
                fragmentCourseinfoRefresh.setRefreshing(false);
            });
        });*/
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(CourseViewModel.class);
        // TODO: Use the ViewModel
        course = mViewModel.getCourse().getValue();
        initView();
    }

    private void initView() {
        Picasso.with(this.getContext())
                .load(Constants.SERVICE_PATH + course.getCourseAvatar())
                .fit()
                .error(R.drawable.ic_net_error)
                .into(courseInfoAvatar);

        courseInfoName.setText(course.getCourseName());
        courseInfoCode.setText(course.getCourseCode());
        courseInfoIntroduce.setText(course.getCourseIntroduce());
        courseInfoTeacherBirthday.setText(course.getUserBirthday());
        courseInfoTeacherName.setText(course.getUesrName());
        courseInfoTeacherPhone.setText(course.getUserPhone());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.course_info_modify:
//                new XPopup.Builder(getContext()).asCustom(new CourseMdifyPopup(mContext, course.getCourseName(), course.getCourseIntroduce(), course.getCourseId()));
                CourseMdifyPopup courseMdifyPopup = new CourseMdifyPopup(mContext, course.getCourseName(), course.getCourseIntroduce(), course.getCourseId());
                new XPopup.Builder(getContext()).asCustom(courseMdifyPopup);
                courseMdifyPopup.show();
//                String s=courseMdifyPopup.getIntroduce();
//                String d=courseMdifyPopup.getName();
//                System.out.println(s+d);
//                UiUtils.showConfirmDialog(mContext, "修改", "确认修改？", new OnConfirmListener() {
//                    @Override
//                    public void onConfirm() {
//                        courseInfoIntroduce.setText(courseMdifyPopup.getIntroduce());
//                        courseInfoName.setText(courseMdifyPopup.getName());
//                    }
//                });
                /*courseMdifyPopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()){
                            case R.id.yesmodify:
                            courseInfoIntroduce.setText(courseMdifyPopup.getIntroduce());
                            courseInfoName.setText(courseMdifyPopup.getName());
                            break;
                        }
                    }
                });*/
              /*  while (!courseMdifyPopup.isDismiss()) {
                    courseInfoIntroduce.setText(courseMdifyPopup.getIntroduce());
                    courseInfoName.setText(courseMdifyPopup.getName());
                }*/
                break;
            case R.id.course_info_del:
                UiUtils.showConfirmDialog(mContext, "最后一次", "确认删除？", new OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        Map<String, String> map = new HashMap<>();
                        map.put("id", String.valueOf(course.getCourseId()));
                        NetUtils.request(mContext, "/course/deleteCourse", map, result -> {
                            if (result.getCode().equals("200")) {
                                getActivity().finish();
                            }
                            UiUtils.showSuccess(mContext, result.getMsg());
                            return;
                        });
                    }
                });

        }
    }
}
