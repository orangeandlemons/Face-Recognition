package com.zl.facerecognition.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zl.facerecognition.R;
import com.squareup.picasso.Picasso;
import com.zl.facerecognition.entity.CourseList;
import com.zl.facerecognition.utils.Constants;
import com.zl.facerecognition.utils.MyTransForm;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.ViewHolder> {
    private List<CourseList> cbl;
    private Context mContext;

    public CourseListAdapter(List<CourseList> courseLists) {
        this.cbl = courseLists;
    }
    /**
     * 设置数据
     * @param cbl
     */
    public void setData(List<CourseList> cbl){
        this.cbl=cbl;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_course_list,parent,false);
        mContext=parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CourseListAdapter.ViewHolder holder, int position) {
        CourseList courseList = cbl.get(position);
        holder.lecturer.setText(courseList.getUesrName());
        holder.name.setText(courseList.getCourseName());

        Picasso.with(mContext)
                .load(Constants.SERVICE_PATH + courseList.getCourseAvatar())
                .fit()
                .transform(new MyTransForm.RoundCornerTransForm(30f))
                .error(R.drawable.ic_net_error)
                .into(holder.img);

        holder.view.setOnClickListener(v -> {
            SharedPreferences localRecord = v.getContext().getSharedPreferences("localRecord", Context.MODE_PRIVATE);
            String userType = localRecord.getString("userType", "");
            Intent intent = new Intent();
            if (userType.equals("2")){
                //TODO 课程详情
                intent.setAction(".activity.StudentCouDetail");
            }else {
                //TODO 课程详情
                intent.setAction("activity.TeacherCourseDetail");
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable("course",courseList);
            intent.putExtras(bundle);
            mContext.startActivity(intent);
        });
    }



    @Override
    public int getItemCount() {
        return cbl.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public ImageView img;
        public TextView name;
        public TextView lecturer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            img = itemView.findViewById(R.id.course_list_avatar);
            name = itemView.findViewById(R.id.course_list_course_name);
            lecturer = itemView.findViewById(R.id.course_list_teacher_name);
        }
    }
}
