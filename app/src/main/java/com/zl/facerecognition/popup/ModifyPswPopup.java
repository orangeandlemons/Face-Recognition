package com.zl.facerecognition.popup;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import com.lxj.xpopup.core.BottomPopupView;
import com.zl.facerecognition.R;
import com.zl.facerecognition.entity.AccountStudent;
import com.zl.facerecognition.utils.Constants;
import com.zl.facerecognition.utils.NetUtils;
import com.zl.facerecognition.utils.UiUtils;

import java.util.HashMap;
import java.util.Map;

public class ModifyPswPopup extends BottomPopupView implements CompoundButton.OnCheckedChangeListener {
    private EditText etModname;
    private EditText etModphone;
    private EditText etModpwd;
    private EditText etRemodpwd;
    private Button btnModify;
    private RadioButton modifyPasswordTeacher;
    private RadioButton modifyPasswordStudent;
    private String id;
    private Context mContext;
    private int userType;
    public ModifyPswPopup(Context Context) {
        super(Context);
    }

    private AccountStudent student;


    /**
     * 具体实现的类的布局
     *
     * @return
     */
    protected int getImplLayoutId() {
        return R.layout.activity_mod_psw;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        mContext=getContext();
        etModname = findViewById(R.id.et_modname);
        etModphone = findViewById(R.id.et_modphone);
        etModpwd = findViewById(R.id.et_modpwd);
        etRemodpwd = findViewById(R.id.et_remodpwd);
        btnModify = findViewById(R.id.btn_modify);
        modifyPasswordTeacher = findViewById(R.id.modify_password_teacher);
        modifyPasswordStudent = findViewById(R.id.modify_password_student);
        modifyPasswordStudent.setOnCheckedChangeListener(this);
        modifyPasswordTeacher.setOnCheckedChangeListener(this);
        btnModify.setOnClickListener(v -> {

            if (TextUtils.isEmpty(etModphone.getText())) {
                UiUtils.showError(mContext, "请填写手机号");
                return;
            }
            if (TextUtils.isEmpty(etModname.getText())) {
                UiUtils.showError(mContext, "请填写用户名");
                return;
            }
            if (TextUtils.isEmpty(etModpwd.getText()) || TextUtils.isEmpty(etRemodpwd.getText())) {
                UiUtils.showError(mContext, "请输入密码");
                return;
            }
            if (!etModpwd.getText().toString().equals(etRemodpwd.getText().toString())) {
                UiUtils.showError(mContext, "两次输入的密码不一致");
                return;
            }
            checkAccount();
        });
    }

    /*后台检查账号是否合法*/
    private void checkAccount() {
        String account = etModname.getText().toString();
        String phone = etModphone.getText().toString();
        Map<String, String> map = new HashMap<>();
        map.put("type",String.valueOf(userType));
        map.put("account",account);
        map.put("phone",phone);
        NetUtils.request(mContext, Constants.CHECK_ACCOUNT, map, result -> {
            if ("0".equals(result.getMsg())) {
                UiUtils.showError(mContext, "账号不存在或手机号输入错误");
                return;
            }else {
                id=result.getMsg();
                modify();
            }
        });
    }

    /**
     * 找回密码
     */
    private void modify() {
        String account = etModname.getText().toString();
        String password = etModpwd.getText().toString();
        String phone = etModphone.getText().toString();
        Map<String, String> params = new HashMap<>();
        params.put("account",account);
        params.put("password",password);
        params.put("phone",phone);
        params.put(userType == 1 ? "teacherId" : "studentId",id);
        Log.d("Net-->", String.valueOf(params));
        NetUtils.request(mContext, Constants.MODIFY+(userType == 1 ? "Teacher" : "Student"), params, result -> {
            if ("0".equals(result.getMsg())){
                UiUtils.showSuccess(mContext, "修改失败");
                return;
            }else {
                UiUtils.showSuccess(mContext,result.getMsg());
            }

            dismiss();
        });
    }

    /**
     * 监控单选结果
     *
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.modify_password_teacher:
                if (isChecked) {
                    userType = 1;
                }
                break;
            case R.id.modify_password_student:
                if (isChecked) {
                    userType = 2;
                }
                break;
            default:
                userType = 0;
                break;
        }
    }
}