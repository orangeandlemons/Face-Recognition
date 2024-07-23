package com.zl.facerecognition.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zl.facerecognition.R;
import com.zl.facerecognition.entity.CheckinList;
import com.zl.facerecognition.utils.CommonUtil;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CheckinAdapter extends RecyclerView.Adapter<CheckinAdapter.ViewHolder> {
    private Context mContext;
    private List<CheckinList> checkLists;

    public CheckinAdapter(List<CheckinList> checkinLists) {
        this.checkLists = checkinLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_checkin,parent,false);
        mContext=parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CheckinList checkinList = checkLists.get(position);
        String method = checkinList.getType() == 0 ? "位置定位" : checkinList.getType() == 1 ? "人脸识别" : "手势签到";
        holder.method.setText(method);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        holder.startText.setText(format.format(checkinList.getStartTime()));
        long time = checkinList.getEndTime().getTime() - checkinList.getStartTime().getTime();
        holder.duration.setText(CommonUtil.longString(time));
        holder.state.setText(checkinList.getState());

        holder.view.setOnClickListener(v -> {
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            if (currentTime.before(checkinList.getStartTime())){
                Toast.makeText(v.getContext(), "考勤尚未开始", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent("TeacherAttendDetail");
            Bundle bundle = new Bundle();
            bundle.putSerializable("attend",checkinList);
            intent.putExtras(bundle);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return checkLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView method,startText,state,duration;

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
