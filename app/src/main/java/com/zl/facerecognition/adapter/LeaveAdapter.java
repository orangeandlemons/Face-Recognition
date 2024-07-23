package com.zl.facerecognition.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ColorUtils;
import com.squareup.picasso.Picasso;
import com.zl.facerecognition.R;
import com.zl.facerecognition.entity.Leave;
import com.zl.facerecognition.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LeaveAdapter extends RecyclerView.Adapter<LeaveAdapter.ViewHolder> {
    private List<Leave> leaves;
    private Context mContext;
    public LeaveAdapter(List<Leave> leaves) {
        this.leaves = leaves;
    }
    public Integer approvalResult=0;
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_teacher_member_item,parent,false);
        mContext =parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Leave leave = leaves.get(position);
        holder.name.setText(leave.getStudentName());
        holder.account.setText(leave.getStudentAccount());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String s = simpleDateFormat.format(leave.getLeaveTime()) + "至" + simpleDateFormat.format(leave.getBackTime());
        holder.time.setText(s);
        if (leave.getApprovalResult() > 0) {
            if (leave.getApprovalResult() == 1) {
                approvalResult = 1;
            } else {
                approvalResult = 2;
            }
        }
        switch (approvalResult) {
            case 0:
                holder.state.setText("未审批");
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

        holder.leaveLinear.setVisibility(View.VISIBLE);
        holder.state.setVisibility(View.VISIBLE);
        holder.arrow.setVisibility(View.GONE);

        Picasso.with(mContext)
                .load(Constants.SERVICE_PATH + leave.getStudentAvatar())
                .fit()
                .error(R.drawable.ic_net_error)
                .into(holder.avatar);

        holder.view.setOnClickListener(v -> {
            //TODO 请假界面
            Intent intent = new Intent(".activity.teacher.TeacherLeaveActivity");
            Bundle bundle = new Bundle();
            bundle.putSerializable("leave",leave);
            intent.putExtras(bundle);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return leaves.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView avatar;
        public ImageView arrow;
        public TextView name,account,time,state;
        public LinearLayout leaveLinear;
        public View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            avatar = itemView.findViewById(R.id.teacher_member_item_avatar);
            account = itemView.findViewById(R.id.teacher_member_item_account);
            name = itemView.findViewById(R.id.teacher_member_item_name);
            time = itemView.findViewById(R.id.member_leave_time);
            leaveLinear = itemView.findViewById(R.id.layout_leave_time);
            state = itemView.findViewById(R.id.member_leave_state);
            arrow = itemView.findViewById(R.id.member_arrow_right);
        }
    }
}
