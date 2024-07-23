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
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zl.facerecognition.R;
import com.zl.facerecognition.entity.CheckinList;
import com.zl.facerecognition.entity.CourseList;
import com.zl.facerecognition.utils.CommonUtil;
import com.zl.facerecognition.utils.NetUtils;
import com.zl.facerecognition.utils.UiUtils;
import com.zl.facerecognition.utils.ViewUtils;
import com.zl.facerecognition.viewModel.CourseStuCheckinViewModel;
import com.zl.facerecognition.viewModel.CourseViewModel;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Course_stu_checkin extends Fragment {
    private SwipeRefreshLayout refreshStudentCheckin;
    private EditText stuSearch;
    private RecyclerView recyclerStuCheckinList;
    private LinearLayout contentNotFoundLayout;
    private FloatingActionButton courseStuCheckinButton;
    private CourseStuCheckinViewModel mViewModel;
    private Context mContext;

    public static Course_stu_checkin newInstance() {
        return new Course_stu_checkin();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.course_stu_checkin_fragment, container, false);
        mContext = view.getContext();
        refreshStudentCheckin = (SwipeRefreshLayout) view.findViewById(R.id.refresh_student_checkin);
        stuSearch = (EditText) view.findViewById(R.id.stu_search);
        recyclerStuCheckinList = (RecyclerView) view.findViewById(R.id.recycler_stu_checkin_list);
        contentNotFoundLayout = (LinearLayout) view.findViewById(R.id.content_not_found_layout);
        courseStuCheckinButton = (FloatingActionButton) view.findViewById(R.id.course_stu_checkin_button);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CourseStuCheckinViewModel.class);
        CourseViewModel courseViewModel = new ViewModelProvider(getActivity()).get(CourseViewModel.class);
        CourseList course = courseViewModel.getCourse().getValue();
        mViewModel.getcheckLists().observe(getViewLifecycleOwner(), checkinLists -> {
            if (checkinLists.size() < 1) {
                contentNotFoundLayout.setVisibility(View.VISIBLE);
            } else {
                contentNotFoundLayout.setVisibility(View.GONE);
                ViewUtils.setRecycler(getActivity(), R.id.recycler_stu_checkin_list, new MyAdapter(checkinLists));
            }
        });

        String id = String.valueOf(course.getCourseId());
        String joinTime = course.getJoinTime();
        Map<String, String> map = new HashMap<>();
        map.put("courseId", id);
        map.put("joinTime", joinTime);

        NetUtils.request(getContext(), "/attend/findStudentAttend", map, result -> {
            if (result.getCode().equals("200")) {
                mViewModel.updateCheckList(result.getData());
            } else {
                Toast.makeText(getContext(), result.getMsg(), Toast.LENGTH_SHORT).show();
            }
            return;
        });


        refreshStudentCheckin.setColorSchemeColors(ViewUtils.getRefreshColor());
        refreshStudentCheckin.setOnRefreshListener(() -> {
            NetUtils.request(getContext(), "/attend/findStudentAttend", map, result -> {
                if (result.getCode().equals("200")) {
                    mViewModel.updateCheckList(result.getData());
                } else {
                    Toast.makeText(getContext(), result.getMsg(), Toast.LENGTH_SHORT).show();
                }
                refreshStudentCheckin.setRefreshing(false);
                return;
            });
        });

        stuSearch.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                String time = stuSearch.getText().toString();
                Map<String, String> map1 = new HashMap<>();
                map1.put("courseId", id);
                map1.put("time", time);
                map1.put("joinTime", joinTime);
                NetUtils.request(getContext(), "/attend/findStudentAttendByTime", map, result -> {
                    if (result.getCode().equals("200")) {
                        mViewModel.updateCheckList(result.getData());
                    } else {
                        Toast.makeText(getContext(), result.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                    return;
                });
            }
            return false;
        });
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<CheckinList> checkinLists;

        public MyAdapter(List<CheckinList> checkinLists) {
            this.checkinLists = checkinLists;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_checkin, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CheckinList checkinList = checkinLists.get(position);
            String method = checkinList.getType() == 0 ? "位置定位" : checkinList.getType() == 1 ? "人脸识别" : "手势签到";
            holder.method.setText(method);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
            holder.startText.setText(format.format(checkinList.getStartTime()));
            long time = checkinList.getEndTime().getTime() - checkinList.getStartTime().getTime();
            holder.duration.setText(CommonUtil.longString(time));

            holder.state.setText(checkinList.getState());

            holder.view.setOnClickListener(v -> {

                if (checkinList.getState().equals("未开始")) {
                    UiUtils.showText(getContext(),"还没开始呢");
                } else {

                    Map<String, String> map = new HashMap<>();
                    map.put("student_id", v.getContext().getSharedPreferences("localRecord", Context.MODE_PRIVATE).getString("id", ""));
                    map.put("attend_id", String.valueOf(checkinList.getAttendId()));
                    System.out.println(map);
                    NetUtils.request(getContext(), "/record/findRecordByMap", map, result -> {
                        if (result.getCode().equals("200")&&result.getData()!=null) {
                            String data = result.getData();
                            System.out.println(data);
                            JSONArray array = JSON.parseArray(data);
                            System.out.println(array);
                            JSONObject jsonObject = array.getJSONObject(0);
                            String recordResult = jsonObject.getString("recordResult");

                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            if (checkinList.getState().equals("进行中")) {
                                intent.setAction(recordResult.equals("2") || recordResult.equals("3") ? ".activity.StudentRecord" : ".activity.StudentDocheck");
                            } else {
                                intent.setAction(".activity.StudentRecord");
                            }
                            bundle.putSerializable("attend", checkinList);
                            bundle.putString("record", jsonObject.toJSONString());
                            intent.putExtras(bundle);
                            v.getContext().startActivity(intent);
                        } else {
                            UiUtils.showError(getContext(), result.getMsg());
                        }
                        return;
                    });

                }
            });
        }

        @Override
        public int getItemCount() {
            return checkinLists.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            View view;
            TextView method, startText, duration, state;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                view = itemView;
                method = itemView.findViewById(R.id.attend_item_method);
                startText = itemView.findViewById(R.id.attend_item_start);
                duration = itemView.findViewById(R.id.attend_item_duration);
                state = itemView.findViewById(R.id.attend_item_current_state);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
