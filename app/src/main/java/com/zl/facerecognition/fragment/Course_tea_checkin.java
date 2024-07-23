package com.zl.facerecognition.fragment;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lxj.xpopup.XPopup;
import com.zl.facerecognition.R;
import com.zl.facerecognition.adapter.CheckinAdapter;
import com.zl.facerecognition.popup.CreateCheckinPopup;
import com.zl.facerecognition.utils.NetUtils;
import com.zl.facerecognition.utils.UiUtils;
import com.zl.facerecognition.utils.ViewUtils;
import com.zl.facerecognition.viewModel.CourseTeaCheckinViewModel;
import com.zl.facerecognition.viewModel.CourseViewModel;

import java.util.HashMap;
import java.util.Map;

public class Course_tea_checkin extends Fragment implements View.OnClickListener {

    private CourseTeaCheckinViewModel mViewModel;
    private CourseViewModel viewModel;
    private Context mContext;
    private SwipeRefreshLayout refreshTeacherCheckin;
    private EditText search;
    private RecyclerView recyclerCheckinList;
    private LinearLayout contentNotFoundLayout;
    private FloatingActionButton courseCheckinButton;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.course_tea_checkin_fragment, container, false);
        mContext=view.getContext();
        refreshTeacherCheckin = (SwipeRefreshLayout) view.findViewById(R.id.refresh_teacher_checkin);
        search = (EditText) view.findViewById(R.id.search);
        recyclerCheckinList = (RecyclerView) view.findViewById(R.id.recycler_checkin_list);
        contentNotFoundLayout = (LinearLayout) view.findViewById(R.id.content_not_found_layout);
        courseCheckinButton = (FloatingActionButton) view.findViewById(R.id.course_checkin_button);
        courseCheckinButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CourseTeaCheckinViewModel.class);
        viewModel = new ViewModelProvider(getActivity()).get(CourseViewModel.class);
        // TODO: Use the ViewModel
        mViewModel.getcheckinLists().observe(getViewLifecycleOwner(), checkLists -> {
            if (checkLists.size() < 1){
                contentNotFoundLayout.setVisibility(View.VISIBLE);
            } else {
                contentNotFoundLayout.setVisibility(View.GONE);
                ViewUtils.setRecycler(getActivity(), R.id.recycler_checkin_list, new CheckinAdapter(checkLists));
            }
        });
        String id = String.valueOf(viewModel.getCourse().getValue().getCourseId());
        Map<String, String> map = new HashMap<>();
        map.put("courseId",id);
        NetUtils.request(mContext,"/attend/findAttendByCourseId",map,result->{
            if (result.getCode().equals("200")){
                if (result.getData().equals("[]")){
                    Toast.makeText(getContext(), "查询结果为空", Toast.LENGTH_SHORT).show();
                }
                mViewModel.updatecheckinList(result.getData());
            } else {
                UiUtils.showInfo(getContext(), result.getMsg());
            }
            refreshTeacherCheckin.setRefreshing(false);
            return;
        });
        refreshTeacherCheckin.setColorSchemeColors(ViewUtils.getRefreshColor());
        refreshTeacherCheckin.setOnRefreshListener(() -> {
            NetUtils.request(mContext,"/attend/findAttendByCourseId",map,result->{
                if (result.getCode().equals("200")){
                    if (result.getData().equals("[]")){
                        Toast.makeText(getContext(), "查询结果为空", Toast.LENGTH_SHORT).show();
                    }
                    mViewModel.updatecheckinList(result.getData());
                } else {
                    UiUtils.showInfo(getContext(), result.getMsg());
                }
                refreshTeacherCheckin.setRefreshing(false);
                return;
            });
        });

        search.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER){
                String time = search.getText().toString();
                Map<String, String> map1 = new HashMap<>();
                map1.put("courseId",id);
                map1.put("time",time);
                NetUtils.request(mContext,"/attend/findAttendByTime",map1,result->{
                    if (result.getCode().equals("200")){
                        if (result.getData().equals("[]")){
                            Toast.makeText(getContext(), "查询结果为空", Toast.LENGTH_SHORT).show();
                        }
                        mViewModel.updatecheckinList(result.getData());
                    } else {
                        UiUtils.showInfo(getContext(), result.getMsg());
                    }
                    refreshTeacherCheckin.setRefreshing(false);
                    return;
                });
            }
            return false;
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if (resultCode == -1) {
                    if (data.getStringExtra("location") != null) {
                        CreateCheckinPopup.updateLocation(data.getStringExtra("location"));
                        CreateCheckinPopup.setLatitude(data.getDoubleExtra("latitude",0));
                        CreateCheckinPopup.setLongitude(data.getDoubleExtra("longitude",0));
                    } else {
                        CreateCheckinPopup.updateLocation("未选择坐标");
                    }
                }
                break;
            default:break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.course_checkin_button:
                new XPopup.Builder(mContext).asCustom(new CreateCheckinPopup(mContext,viewModel.getCourse().getValue().getCourseId(),getActivity())).show();
//                CreateCheckinPopup createCheckinPopup=new CreateCheckinPopup(mContext,viewModel.getCourse().getValue().getCourseId());
                CreateCheckinPopup.setChoose(()->{

                        Intent intent = new Intent("map");
                        startActivityForResult(intent,1);
                });
                break;
        }
    }
}