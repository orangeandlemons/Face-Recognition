package com.zl.facerecognition.activity.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.StringUtils;
import com.gyf.barlibrary.ImmersionBar;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.squareup.picasso.Picasso;
import com.zl.facerecognition.R;
import com.zl.facerecognition.activity.BaseActivity;
import com.zl.facerecognition.entity.Student;
import com.zl.facerecognition.utils.Constants;
import com.zl.facerecognition.utils.NetUtils;
import com.zl.facerecognition.utils.UiUtils;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MememberDetailActivity extends BaseActivity implements View.OnClickListener{
    private ImageView memberDetailAvatar;
    private TextView memberDetailName;
    private TextView memberDetailSex;
    private TextView memberDetailAccount;
    private TextView memberDetailJoinTime;
    private TextView memberDetailClass;
    private TextView memberDetailPhone;
    private TextView memberDetailBirthday;
    private TextView memberDetailFacePrompt;
    private ImageView memberDetailFace;
    private Button memberDetailDelete;
    private Student student;
    private Integer courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ImmersionBar.with(this).init();
        setView(R.layout.activity_member_detail);
        setTitle("学生详情");

        init();
        Intent intent = getIntent();
        student = (Student) intent.getExtras().getSerializable("student");
        courseId = (Integer) intent.getIntExtra("courseId",-1);


        initView();
    }

    private void initView() {
        memberDetailName.setText(student.getStudentName());
        memberDetailSex.setText(student.getStudentSex() == true ? "男" : "女");
        memberDetailAccount.setText(student.getStudentAccount());
        memberDetailClass.setText(student.getStudentClass());
        memberDetailPhone.setText(student.getStudentPhone());
        memberDetailBirthday.setText(student.getStudentBirthday());
        memberDetailJoinTime.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(student.getJoinTime()));

        Picasso.with(this)
                .load(Constants.SERVICE_PATH + student.getStudentAvatar())
                .fit()
                .error(R.drawable.ic_net_error)
                .into(memberDetailAvatar);

        if (student.getStudentFace() != null) {
            Picasso.with(this)
                    .load(Constants.SERVICE_PATH + student.getStudentFace())
                    .fit()
                    .error(R.drawable.ic_net_error)
                    .into(memberDetailFace);
        } else {
            memberDetailFace.setVisibility(View.GONE);
            memberDetailFacePrompt.setVisibility(View.VISIBLE);
        }
    }

    private void init(){
        memberDetailAvatar = (ImageView) findViewById(R.id.member_detail_avatar);
        memberDetailName = (TextView) findViewById(R.id.member_detail_name);
        memberDetailSex = (TextView) findViewById(R.id.member_detail_sex);
        memberDetailAccount = (TextView) findViewById(R.id.member_detail_account);
        memberDetailJoinTime = (TextView) findViewById(R.id.member_detail_join_time);
        memberDetailClass = (TextView) findViewById(R.id.member_detail_class);
        memberDetailPhone = (TextView) findViewById(R.id.member_detail_phone);
        memberDetailBirthday = (TextView) findViewById(R.id.member_detail_birthday);
        memberDetailFacePrompt = (TextView) findViewById(R.id.member_detail_face_prompt);
        memberDetailFace = (ImageView) findViewById(R.id.member_detail_face);
        memberDetailDelete = (Button) findViewById(R.id.member_detail_delete);
        memberDetailDelete.setOnClickListener(this);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.member_detail_delete:
                UiUtils.showConfirmDialog(this, "注意", "是否删除？", new OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        Map<String, String> map = new HashMap<>();
                        map.put("courseId",String.valueOf(courseId));
                        map.put("studentId",String.valueOf(student.getStudentId()));
                    }
                });

                        Map<String, String> map = new HashMap<>();
                        map.put("courseId",String.valueOf(courseId));
                        map.put("studentId",String.valueOf(student.getStudentId()));
                NetUtils.request(this,"/courseStudent/deleteCourseStudent",map,result->{
                    if(result.getCode().equals("200")) {
                        finish();
                        UiUtils.showSuccess(this,result.getMsg());
                    }else {
                    UiUtils.showError(this,result.getMsg());}
                });

                break;
            default:break;
        }
    }
}
