package com.zl.facerecognition.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.zl.facerecognition.R;
import com.zl.facerecognition.utils.Constants;
import com.zl.facerecognition.utils.NetUtils;
import com.zl.facerecognition.utils.UiUtils;
import com.zl.facerecognition.utils.ViewUtils;

import java.util.HashMap;
import java.util.Map;

public class UserInfoActivity extends BaseActivity {

    private SwipeRefreshLayout refreshInfo;
    private ImageView infoDetailAvatar;
    private TextView infoDetailAccount;
    private LinearLayout infoDetailClassLayout;
    private TextView infoDetailClass;
    private TextView infoDetailName;
    private TextView infoDetailSex;
    private TextView infoDetailPhone;
    private TextView infoDetailBirthday;
    private LinearLayout infoDetailFaceLayout;
    private LinearLayout infoDetailFacePrompt;
    private ImageView infoDetailFace;
    private Button infoDetailModify;
    private String type;
    private SharedPreferences preferences;
    private TextView info_AccountName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_user_info);
        setTitle("我的信息");
        ImmersionBar.with(this).init();
        init();
        initView();
        Picasso.with(context)
                .load(Constants.SERVICE_PATH + preferences.getString("avatar", ""))
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .error(R.drawable.ic_net_error)
                .into(infoDetailAvatar);
    }

    private void initView() {

        infoDetailPhone.setText(preferences.getString("phone", ""));
        infoDetailBirthday.setText(preferences.getString("birthday", ""));
        infoDetailSex.setText(preferences.getBoolean("sex", false) ? "男" : "女");
        infoDetailAccount.setText(preferences.getString("account", ""));
        infoDetailName.setText(preferences.getString("name", ""));
        type = preferences.getString("userType", "");
        info_AccountName.setText(type.equals("1") ? "工号" : "学号");
        if (type.equals("1")) {
            info_AccountName.setText("工号");
            infoDetailFaceLayout.setVisibility(View.GONE);
            infoDetailClassLayout.setVisibility(View.GONE);
        } else {
            info_AccountName.setText("学号");
            infoDetailClassLayout.setVisibility(View.VISIBLE);
            infoDetailClass.setText(preferences.getString("class", ""));
            if (preferences.getString("face", null) != null) {
                infoDetailFacePrompt.setVisibility(View.GONE);
                Picasso.with(context)
                        .load(Constants.SERVICE_PATH + preferences.getString("face", ""))
                        .fit().memoryPolicy(MemoryPolicy.NO_CACHE)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .error(R.drawable.ic_net_error)
                        .into(infoDetailFace);
            } else {
                infoDetailFacePrompt.setVisibility(View.VISIBLE);
            }
        }
    }

    private void init() {

        refreshInfo = findViewById(R.id.refresh_info);
        infoDetailAvatar = findViewById(R.id.info_detail_avatar);
        infoDetailAccount = findViewById(R.id.info_detail_account);
        info_AccountName = findViewById(R.id.info_detail_account_text);
        infoDetailClassLayout = findViewById(R.id.info_detail_class_layout);
        infoDetailClass = findViewById(R.id.info_detail_class);
        infoDetailName = findViewById(R.id.info_detail_name);
        infoDetailSex = findViewById(R.id.info_detail_sex);
        infoDetailPhone = findViewById(R.id.info_detail_phone);
        infoDetailBirthday = findViewById(R.id.info_detail_birthday);
        infoDetailFaceLayout = findViewById(R.id.info_detail_face_layout);
        infoDetailFacePrompt = findViewById(R.id.info_detail_face_prompt);
        infoDetailFace = findViewById(R.id.info_detail_face);
        infoDetailModify = findViewById(R.id.info_detail_modify);
        infoDetailModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ModifyInfo.class);
                startActivity(intent);
            }
        });


        preferences = context.getSharedPreferences("localRecord", Context.MODE_PRIVATE);
        refreshInfo.setColorSchemeColors(ViewUtils.getRefreshColor());
        refreshInfo.setOnRefreshListener(() -> {
            Map<String, String> map = new HashMap<>();
            map.put("type", type);
            map.put("account", infoDetailAccount.getText().toString());
            map.put("password", preferences.getString("password", ""));
            NetUtils.request(context, Constants.LOGIN, map, result -> {
                if (!result.getMsg().equals("")) {
                    String data = result.getData();
                    LoginActivity.updateLoginInfo(preferences, data, type);
                    initView();
                    Picasso.with(context)
                            .load(Constants.SERVICE_PATH + preferences.getString("avatar", ""))
                            .fit().memoryPolicy(MemoryPolicy.NO_CACHE)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .error(R.drawable.ic_net_error)
                            .into(infoDetailAvatar);
                } else {
                    UiUtils.showError(context, result.getMsg());
                }
                refreshInfo.setRefreshing(false);
            });
        });
    }
}
