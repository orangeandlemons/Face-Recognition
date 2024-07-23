package com.zl.facerecognition.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.zl.facerecognition.R;
import com.zl.facerecognition.activity.LoginActivity;
import com.zl.facerecognition.activity.SettingActivity;
import com.zl.facerecognition.activity.UserInfoActivity;
import com.zl.facerecognition.utils.Constants;
import com.zl.facerecognition.utils.NetUtils;
import com.zl.facerecognition.utils.UiUtils;
import com.zl.facerecognition.utils.ViewUtils;

import java.util.HashMap;
import java.util.Map;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MyInfoView implements View.OnClickListener {

    private Activity mContext;
    private LayoutInflater mInflater;
    private View mCurrentView;
    private ImageView iv_head_icon;
    private RelativeLayout rl_myInfo;
    private SharedPreferences preferences;
    private SwipeRefreshLayout refreshMyinfo;
    private RelativeLayout rl_setting;

    public MyInfoView(Activity context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    private void createView() {
        init();
    }

    /**
     * 初始化控件
     */
    private void init() {
        //设置布局文件
        mCurrentView = mInflater.inflate(R.layout.main_view_myinfo, null);
        refreshMyinfo = mCurrentView.findViewById(R.id.refresh_myinfo);
        iv_head_icon = mCurrentView.findViewById(R.id.iv_head_icon);
        rl_myInfo = mCurrentView.findViewById(R.id.rl_myinfo);
        rl_setting = mCurrentView.findViewById(R.id.rl_setting);
        rl_myInfo.setOnClickListener(this);
        rl_setting.setOnClickListener(this);
        preferences = mContext.getSharedPreferences("localRecord", Context.MODE_PRIVATE);
        Picasso.with(getView().getContext())
                .load(Constants.SERVICE_PATH + preferences.getString("avatar", ""))
                .fit() .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .error(R.mipmap.head)
                .into(iv_head_icon);
        refreshMyinfo.setColorSchemeColors(ViewUtils.getRefreshColor());
        refreshMyinfo.setOnRefreshListener(() -> {
            Map<String, String> map = new HashMap<>();
            map.put("type", preferences.getString("userType", ""));
            map.put("account", preferences.getString("account", ""));
            map.put("password", preferences.getString("password", ""));
            NetUtils.request(mContext, Constants.LOGIN, map, result -> {
                if (result.getCode().equals("200")) {
                    String data = result.getData();
                    LoginActivity.updateLoginInfo(preferences, data, preferences.getString("userType", ""));
                    initView();
                } else {
                    UiUtils.showError(mContext, result.getMsg());
                }
                refreshMyinfo.setRefreshing(false);;
            });
        });


    }

    private void initView() {
        Picasso.with(getView().getContext())
                .load(Constants.SERVICE_PATH + preferences.getString("avatar", ""))
                .fit().memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .error(R.mipmap.head)
                .into(iv_head_icon);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_myinfo:
                Intent intent1 = new Intent(mContext, UserInfoActivity.class);
                mContext.startActivity(intent1);
                break;
            case R.id.rl_setting:
                Intent intent = new Intent(mContext, SettingActivity.class);
                mContext.startActivity(intent);
                break;
        }
    }

    /**
     * 获取当前在导航栏上方显示的View
     */
    public View getView() {
        if (mCurrentView == null) {
            createView();
        }
        return mCurrentView;
    }

    /**
     * 显示当前导航栏上方所对应的View
     */
    public void showView() {
        if (mCurrentView == null) {
            createView();
        }
        mCurrentView.setVisibility(View.VISIBLE);
    }

}




