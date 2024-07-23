package com.zl.facerecognition.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zl.facerecognition.R;
import com.zl.facerecognition.entity.Attendance;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AttendAdapter extends RecyclerView.Adapter<AttendAdapter.ViewHolder> {
    private List<Attendance> attendances;
    public AttendAdapter(List<Attendance> attendances) {
        this.attendances=attendances;
    }

    @NonNull
    @Override
    public AttendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_course_attend_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Attendance attendance = attendances.get(position);
        holder.name.setText(attendance.getStudentName());
        holder.account.setText(attendance.getStudentAccount());
        holder.absent.setText(String.valueOf(attendance.getAbsentCount()));
        holder.leave.setText(String.valueOf(attendance.getLeaveCount()));
        holder.failed.setText(String.valueOf(attendance.getFailedCount()));
        holder.success.setText(String.valueOf(attendance.getSuccessCount()));

        if (position % 2 == 0){
            holder.view.setBackgroundColor(Color.WHITE);
        } else {
            holder.view.setBackgroundColor(Color.parseColor("#d8e3e7"));
        }

    }


    @Override
    public int getItemCount() {
        return attendances.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name,account,absent,leave,success,failed;
        public View view;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            name = itemView.findViewById(R.id.attend_item_name);
            account = itemView.findViewById(R.id.attend_item_account);
            absent = itemView.findViewById(R.id.attend_item_absent);
            leave = itemView.findViewById(R.id.attend_item_leave);
            success = itemView.findViewById(R.id.attend_item_success);
            failed = itemView.findViewById(R.id.attend_item_failed);
        }
    }
}

