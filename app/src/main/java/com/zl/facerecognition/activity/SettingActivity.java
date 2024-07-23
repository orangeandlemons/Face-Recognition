package com.zl.facerecognition.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.gyf.barlibrary.ImmersionBar;
import com.lxj.xpopup.XPopup;
import com.zl.facerecognition.R;
import com.zl.facerecognition.popup.ModifyPswPopup;


public class SettingActivity extends BaseActivity {
    private RelativeLayout rlModifyPsw;
    private RelativeLayout rlExitLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_setting);
        setTitle("设置");
        ImmersionBar.with(this).init();
        init();

    }

    private void init() {
        rlModifyPsw = findViewById(R.id.rl_modify_psw);
        rlExitLogin = findViewById(R.id.rl_exit_login);
        rlModifyPsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new XPopup.Builder(context).asCustom(new ModifyPswPopup(context)).show();
            }
        });
        rlExitLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,LoginActivity.class);
                startActivity(intent);
                SettingActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
