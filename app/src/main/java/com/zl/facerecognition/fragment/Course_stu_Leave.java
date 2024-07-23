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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ColorUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zl.facerecognition.R;
import com.zl.facerecognition.entity.Leave;
import com.zl.facerecognition.popup.LeaveCreateDialog;
import com.zl.facerecognition.utils.NetUtils;
import com.zl.facerecognition.utils.ViewUtils;
import com.zl.facerecognition.viewModel.CourseTeaLeaveViewModel;
import com.zl.facerecognition.viewModel.CourseViewModel;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Course_stu_Leave extends Fragment implements View.OnClickListener {

    private CourseTeaLeaveViewModel mViewModel;
    private CourseViewModel viewModel;
    private Integer courseId;
    private Context mContext;
    private SwipeRefreshLayout refreshStudentLeave;
    private RecyclerView recyclerStudentLeaveList;
    private LinearLayout contentNotFoundLayout;
    private FloatingActionButton studentLeaveCreate;
    private Integer approvalResult = 0;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.course_stu__leave_fragment, container, false);
        mContext = view.getContext();
        refreshStudentLeave = (SwipeRefreshLayout) view.findViewById(R.id.refresh_student_leave);
        recyclerStudentLeaveList = (RecyclerView) view.findViewById(R.id.recycler_student_leave_list);
        contentNotFoundLayout = (LinearLayout) view.findViewById(R.id.content_not_found_layout);
        studentLeaveCreate = (FloatingActionButton) view.findViewById(R.id.student_leave_create);
        studentLeaveCreate.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CourseTeaLeaveViewModel.class);

        viewModel = new ViewModelProvider(getActivity()).get(CourseViewModel.class);
        mViewModel.getLeaveList().observe(getViewLifecycleOwner(), leaves -> {
            if (leaves.size() < 1) {
                contentNotFoundLayout.setVisibility(View.VISIBLE);
                return;
            } else {
                contentNotFoundLayout.setVisibility(View.GONE);
                ViewUtils.setRecycler(getActivity(), R.id.recycler_student_leave_list, new LeaveAdapter(leaves));
            }
        });
        courseId = viewModel.getCourse().getValue().getCourseId();

        Map<String, String> map = new HashMap<>();
        map.put("courseId", String.valueOf(courseId));
        map.put("studentId", getActivity().getSharedPreferences("localRecord", Context.MODE_PRIVATE).getString("id", ""));
        NetUtils.request(mContext, "/leave/findAllLeave", map, result -> {
            if (result.getCode().equals("200")) {
                mViewModel.updateLeaveList(result.getData());
            }
            Toast.makeText(getContext(), result.getMsg(), Toast.LENGTH_SHORT).show();
            return;
        });

        refreshStudentLeave.setColorSchemeColors(ViewUtils.getRefreshColor());
        refreshStudentLeave.setOnRefreshListener(() ->
                NetUtils.request(mContext, "/leave/findAllLeave", map, result -> {
                    if (result.getCode().equals("200")) {
                        mViewModel.updateLeaveList(result.getData());
                        refreshStudentLeave.setRefreshing(false);
                    }
                    Toast.makeText(getContext(), result.getMsg(), Toast.LENGTH_SHORT).show();
                    return;
                }));

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.student_leave_create:
                LeaveCreateDialog dialog = new LeaveCreateDialog(getContext(), courseId,getActivity());
                dialog.show();
                break;
        }
    }

    private class LeaveAdapter extends RecyclerView.Adapter<LeaveAdapter.ViewHolder> {
        private List<Leave> leaves;

        public LeaveAdapter(List<Leave> leaves) {
            this.leaves = leaves;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_student_leave_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Leave leave = leaves.get(position);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

            holder.start.setText(format.format(leave.getLeaveTime()));
            holder.end.setText(format.format(leave.getBackTime()));
            int i = (int) Math.ceil((double) (leave.getBackTime().getTime() - leave.getLeaveTime().getTime()) / (1000 * 3600 * 24));
            holder.duration.setText(i + "天");
            if (leave.getApprovalResult() > 0) {
                if (leave.getApprovalResult() == 1) {
                    approvalResult = 1;
                } else {
                    approvalResult = 2;
                }
            }
            switch (approvalResult) {
                case 0:
                    holder.state.setText("审批中");
                    holder.state.setTextColor(ColorUtils.getColor(R.color.yellow));
                    break;
                case 1:
                    holder.state.setText("不批准");
                    holder.state.setTextColor(ColorUtils.getColor(R.color.red));
                    break;
                case 2:
                    holder.state.setText("批准");
                    holder.state.setTextColor(ColorUtils.getColor(R.color.green));
                    break;
            }

            holder.view.setOnClickListener(v -> {
                Intent intent = new Intent(".activity.StudentLeaveDetail");
                Bundle bundle = new Bundle();
                bundle.putSerializable("leave", leave);
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return leaves.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView start, end, duration, state;
            public View view;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                view = itemView;
                start = itemView.findViewById(R.id.student_leave_item_start);
                end = itemView.findViewById(R.id.student_leave_item_end);
                duration = itemView.findViewById(R.id.student_leave_item_duration);
                state = itemView.findViewById(R.id.student_leave_item_leave_state);
            }
        }
    }

}