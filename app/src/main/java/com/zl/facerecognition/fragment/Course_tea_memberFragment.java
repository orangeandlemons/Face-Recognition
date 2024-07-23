package com.zl.facerecognition.fragment;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

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
import com.zl.facerecognition.adapter.MemberAdapter;
import com.zl.facerecognition.utils.NetUtils;
import com.zl.facerecognition.utils.UiUtils;
import com.zl.facerecognition.utils.ViewUtils;
import com.zl.facerecognition.viewModel.CourseTeaMemberViewModel;
import com.zl.facerecognition.viewModel.CourseViewModel;

import java.util.HashMap;
import java.util.Map;

public class Course_tea_memberFragment extends Fragment {

    private CourseTeaMemberViewModel mViewModel;
    private CourseViewModel viewModel;
    private SwipeRefreshLayout refresh_member;
    private RecyclerView recyclerTeacherMemberList;
    private LinearLayout contentNotFoundLayout;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.course_tea_member_fragment, container, false);


        refresh_member = (SwipeRefreshLayout) view.findViewById(R.id.refresh_teacher_member);
        recyclerTeacherMemberList = (RecyclerView)view.findViewById(R.id.recycler_teacher_member_list);
        contentNotFoundLayout = (LinearLayout) view.findViewById(R.id.content_not_found_layout);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CourseTeaMemberViewModel.class);
        viewModel = new ViewModelProvider(getActivity()).get(CourseViewModel.class);
        // TODO: Use the ViewModel
        mViewModel.getStudentList().observe(getViewLifecycleOwner(),students -> {
            if (students.size() < 1){
                contentNotFoundLayout.setVisibility(View.VISIBLE);
            } else {
                contentNotFoundLayout.setVisibility(View.GONE);
                ViewUtils.setRecycler(getActivity(), R.id.recycler_teacher_member_list, new MemberAdapter(students, viewModel.getCourse().getValue().getCourseId()));
            }
        });
        Map<String,String> map = new HashMap<>();
        map.put("courseId",String.valueOf(viewModel.getCourse().getValue().getCourseId()));
        NetUtils.request(getContext(),"/courseStudent/findAllByCourseId",map,result->{
            if(result.getCode().equals("200")){
                mViewModel.updateStudentList(result.getData());
            }else {
                UiUtils.showError(getContext(), "请求失败");
            }
            refresh_member.setRefreshing(false);
        });

        refresh_member.setColorSchemeColors(ViewUtils.getRefreshColor());
        refresh_member.setOnRefreshListener(() -> {
            NetUtils.request(getContext(),"/courseStudent/findAllByCourseId",map,result->{
                if(result.getMsg() != null){
                    mViewModel.updateStudentList(result.getData());
                }else {
                    UiUtils.showError(getContext(), "请求失败");
                }
                refresh_member.setRefreshing(false);
            });
        });
    }

}
