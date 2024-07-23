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

import com.zl.facerecognition.R;
import com.zl.facerecognition.adapter.LeaveAdapter;
import com.zl.facerecognition.utils.NetUtils;
import com.zl.facerecognition.utils.UiUtils;
import com.zl.facerecognition.utils.ViewUtils;
import com.zl.facerecognition.viewModel.CourseTeaLeaveViewModel;
import com.zl.facerecognition.viewModel.CourseViewModel;

import java.util.HashMap;
import java.util.Map;

public class Course_tea_leave extends Fragment {

    private SwipeRefreshLayout refreshTeacherLeave;
    private RecyclerView recyclerTeacherLeaveList;
    private LinearLayout contentNotFoundLayout;

    private CourseTeaLeaveViewModel mViewModel;
    private CourseViewModel viewModel;
    private Context mContext;

    public static Course_tea_leave newInstance() {
        return new Course_tea_leave();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.course_tea_leave_fragment, container, false);
        mContext = view.getContext();


        refreshTeacherLeave = (SwipeRefreshLayout) view.findViewById(R.id.refresh_teacher_leave);
        recyclerTeacherLeaveList = (RecyclerView) view.findViewById(R.id.recycler_teacher_leave_list);
        contentNotFoundLayout = (LinearLayout) view.findViewById(R.id.content_not_found_layout);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CourseTeaLeaveViewModel.class);
        // TODO: Use the ViewModel
        viewModel = new ViewModelProvider(getActivity()).get(CourseViewModel.class);
        mViewModel.getLeaveList().observe(getViewLifecycleOwner(), leaves -> {
            if (leaves.size() < 1) {
                contentNotFoundLayout.setVisibility(View.VISIBLE);
            } else {
                contentNotFoundLayout.setVisibility(View.GONE);
                ViewUtils.setRecycler(getActivity(), R.id.recycler_teacher_leave_list, new LeaveAdapter(leaves));
            }
        });
        Integer courseId = viewModel.getCourse().getValue().getCourseId();

        Map<String, String> map = new HashMap<>();
        map.put("courseId", String.valueOf(courseId));
        NetUtils.request(mContext, "/leave/findAllLeave", map, result -> {
            if (result.getCode().equals("200")) {
                String data = result.getData();
                mViewModel.updateLeaveList(data);
            }
            UiUtils.showInfo(mContext, result.getMsg());
            return;
        });

        refreshTeacherLeave.setColorSchemeColors(ViewUtils.getRefreshColor());
        refreshTeacherLeave.setOnRefreshListener(() ->
                NetUtils.request(mContext, "/leave/findAllLeave", map, result -> {
                    if (result.getCode().equals("200")) {
                        mViewModel.updateLeaveList(result.getData());
                    }
                    UiUtils.showInfo(mContext, result.getMsg());
                    refreshTeacherLeave.setRefreshing(false);
                    return;
                }));
    }

}
