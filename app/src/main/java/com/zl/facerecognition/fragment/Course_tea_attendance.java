package com.zl.facerecognition.fragment;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zl.facerecognition.R;
import com.zl.facerecognition.adapter.AttendAdapter;
import com.zl.facerecognition.utils.NetUtils;
import com.zl.facerecognition.utils.UiUtils;
import com.zl.facerecognition.utils.ViewUtils;
import com.zl.facerecognition.viewModel.CourseTeaAttendanceViewModel;
import com.zl.facerecognition.viewModel.CourseViewModel;

import java.util.HashMap;
import java.util.Map;

public class Course_tea_attendance extends Fragment {

    private SwipeRefreshLayout refreshTeacherAttendance;
    private LinearLayout attendanceLayout;
    private TextView courseAttendanceName;
    private TextView courseAttendanceAccount;
    private TextView courseAttendance;
    private TextView courseAttendanceFailed;
    private TextView courseAttendanceLeave;
    private TextView courseAttendanceSuccess;
    private RecyclerView recyclerCourseAttendanceList;
    private LinearLayout contentNotFoundLayout;
    private Context mContext;

    private CourseTeaAttendanceViewModel mViewModel;
    private CourseViewModel viewModel;

    public static Course_tea_attendance newInstance() {
        return new Course_tea_attendance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.course_tea_attendance_fragment, container, false);
        mContext=view.getContext();
        refreshTeacherAttendance = (SwipeRefreshLayout) view.findViewById(R.id.refresh_teacher_attendance);
        attendanceLayout = (LinearLayout) view.findViewById(R.id.attendance_layout);
        courseAttendanceName = (TextView) view.findViewById(R.id.course_attendance_name);
        courseAttendanceAccount = (TextView)view. findViewById(R.id.course_attendance_account);
        courseAttendance = (TextView) view.findViewById(R.id.course_attendance_);
        courseAttendanceFailed = (TextView)view. findViewById(R.id.course_attendance_failed);
        courseAttendanceLeave = (TextView)view. findViewById(R.id.course_attendance_leave);
        courseAttendanceSuccess = (TextView)view. findViewById(R.id.course_attendance_success);
        recyclerCourseAttendanceList = (RecyclerView) view.findViewById(R.id.recycler_course_attendance_list);
        contentNotFoundLayout = (LinearLayout) view.findViewById(R.id.content_not_found_layout);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(CourseTeaAttendanceViewModel.class);
        viewModel = new ViewModelProvider(getActivity()).get(CourseViewModel.class);
        // TODO: Use the ViewModel
        mViewModel.getAttendance().observe(getViewLifecycleOwner(),attendances -> {
            if (attendances.size() < 1){
                contentNotFoundLayout.setVisibility(View.VISIBLE);
                attendanceLayout.setVisibility(View.GONE);
            } else {
                contentNotFoundLayout.setVisibility(View.GONE);
                attendanceLayout.setVisibility(View.VISIBLE);
                ViewUtils.setRecycler(getActivity(),R.id.recycler_course_attendance_list,new AttendAdapter(attendances));
            }
        });
        Integer courseId = viewModel.getCourse().getValue().getCourseId();
        Map<String, String> map = new HashMap<>();
        map.put("courseId",String.valueOf(courseId));
        NetUtils.request(mContext,"/record/findAllStudentRecord",map,result->{
            if(result.getCode().equals("200")){
                mViewModel.updateAttendance(result.getData());
            }
            UiUtils.showInfo(mContext,result.getMsg());
            return;
        });

        refreshTeacherAttendance.setColorSchemeColors(ViewUtils.getRefreshColor());
        refreshTeacherAttendance.setOnRefreshListener(() ->
                NetUtils.request(mContext,"/record/findAllStudentRecord",map,result->{
            if(result.getCode().equals("200")){
                mViewModel.updateAttendance(result.getData());
            }
                    UiUtils.showInfo(mContext,result.getMsg());
            refreshTeacherAttendance.setRefreshing(false);
            return;
        }));
    }

}
