package com.zl.facerecognition.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ColorUtils;
import com.lxj.xpopup.XPopup;
import com.squareup.picasso.Picasso;
import com.zl.facerecognition.R;
import com.zl.facerecognition.entity.Record;
import com.zl.facerecognition.popup.CreateCheckinPopup;
import com.zl.facerecognition.popup.RecordModifyPopup;
import com.zl.facerecognition.popup.ShowImageDialog;
import com.zl.facerecognition.utils.Constants;
import com.zl.facerecognition.utils.NetUtils;
import com.zl.facerecognition.utils.UiUtils;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CheckDetailAdapter  extends RecyclerView.Adapter<CheckDetailAdapter.ViewHolder> {
    private List<Record> records;
    private Integer type;
    private onResultChangedListener resultChangedListener;
    private Context mContext;

    public void setResultChangedListener(onResultChangedListener resultChangedListener) {
        this.resultChangedListener = resultChangedListener;
    }

    public interface onResultChangedListener{
        void onResultChanged();
    }

    public CheckDetailAdapter(List<Record> records, Integer type) {
        this.records = records;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_record_item, parent,false);
        mContext=parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Record record = records.get(position);
        holder.name.setText(record.getRecordName());
        holder.account.setText(record.getRecordAccount());

        if (record.getRecordTime() != null){
            holder.time.setText(new SimpleDateFormat("yyyy-MM-dd HH-mm").format(record.getRecordTime()));
            holder.location.setText(record.getRecordLocation());
        } else {
            holder.time.setText("---");
            holder.location.setText("---");
        }

        String temp = "";
        switch (record.getRecordResult()){
            case "0":
                temp = "缺 勤";
                holder.result.setTextColor(ColorUtils.getColor(R.color.gray0));
                break;
            case "1":
                temp = "失 败";
                holder.result.setTextColor(ColorUtils.getColor(R.color.red));
                break;
            case "2":
                temp = "成 功";
                holder.result.setTextColor(ColorUtils.getColor(R.color.green));
                break;
            case "3":
                temp = "请 假";
                holder.result.setTextColor(ColorUtils.getColor(R.color.yellow));
                break;
        }
        holder.result.setText(temp);

        Picasso.with(holder.view.getContext())
                .load(Constants.SERVICE_PATH + record.getAvatarUrl())
                .fit()
                .error(R.drawable.ic_net_error)
                .into(holder.avatar);
            //TODO
        if (type == 1 && (record.getRecordResult().equals("1") || record.getRecordResult().equals("2"))) {
           /* holder.view.setOnClickListener(v -> {
                ShowImageDialog imageDialog = new ShowImageDialog(v.getContext());
                imageDialog.setImage(Constants.SERVICE_PATH + record.getRecordPhoto());
                imageDialog.show();*/
//            });
        }

        holder.view.setLongClickable(true);
        final String initial = temp;
        holder.view.setOnLongClickListener(v -> {
//            new XPopup.Builder(mContext).asCustom(new RecordModifyPopup(mContext,initial)).show();
            RecordModifyPopup popup=new RecordModifyPopup(mContext,initial);
            popup.setYesClickedListener(() -> {
                String result = getResult(popup.result);
                Map<String, String> map = new HashMap<>();
                map.put("attendId",record.getAttendId());
                map.put("studentId",record.getStudentId());
                map.put("result",result);
                NetUtils.request(mContext,"record/modifyRecord",map,results->{
                    if(results.getCode().equals("200")){
                        record.setRecordResult(result);
                        resultChangedListener.onResultChanged();
                    }else {
                        UiUtils.showError(mContext,results.getMsg());
                    }
                    popup.dismiss();
                    return;
                });

            });
            popup.show();
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name,account,time,location,result;
        public ImageView avatar;
        public View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            name = itemView.findViewById(R.id.record_item_name);
            account = itemView.findViewById(R.id.record_item_account);
            time = itemView.findViewById(R.id.record_item_time);
            location = itemView.findViewById(R.id.record_item_location);
            result = itemView.findViewById(R.id.record_item_result);
            avatar = itemView.findViewById(R.id.record_item_avatar);
        }
    }

    public String getResult(String s){
        switch (s){
            case "成 功":
                return "2";
            case "缺 勤":
                return "0";
            case "请 假":
                return "3";
        }
        return "";
    }
}
