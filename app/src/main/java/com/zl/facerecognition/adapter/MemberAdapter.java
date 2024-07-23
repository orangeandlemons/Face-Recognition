package com.zl.facerecognition.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zl.facerecognition.R;
import com.zl.facerecognition.entity.Student;
import com.zl.facerecognition.utils.Constants;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
    private List<Student> studentList;
    private Integer courseId;
    private Context mContext;

    public MemberAdapter(List<Student> studentList, Integer courseId) {
        this.studentList = studentList;
        this.courseId = courseId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_teacher_member_item,parent,false);
        mContext =parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.name.setText(student.getStudentName());
        holder.account.setText(student.getStudentAccount());

        Picasso.with(mContext)
                .load(Constants.SERVICE_PATH + student.getStudentAvatar())
                .fit()
                .error(R.drawable.ic_net_error)
                .into(holder.avatar);

        String userType = mContext.getSharedPreferences("localRecord", Context.MODE_PRIVATE).getString("userType","");

        if (userType.equals("1")) {
            holder.view.setOnClickListener(v -> {
                Intent intent = new Intent(".activity.MememberDetailActivity");
                Bundle bundle = new Bundle();
                bundle.putSerializable("student", student);
                intent.putExtras(bundle);
                intent.putExtra("courseId", courseId);
                v.getContext().startActivity(intent);
            });
        } else {
            holder.arrow.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView avatar;
        public TextView name,account;
        public ImageView arrow;
        public View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            avatar = itemView.findViewById(R.id.teacher_member_item_avatar);
            account = itemView.findViewById(R.id.teacher_member_item_account);
            name = itemView.findViewById(R.id.teacher_member_item_name);
            arrow = itemView.findViewById(R.id.member_arrow_right);
        }
    }
}
