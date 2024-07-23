package com.zl.facerecognition.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.PermissionUtils;
import com.gyf.barlibrary.ImmersionBar;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.core.BottomPopupView;
import com.zl.facerecognition.R;
import com.zl.facerecognition.entity.AccountStudent;
import com.zl.facerecognition.entity.CommonResult;
import com.zl.facerecognition.popup.ModifyPswPopup;
import com.zl.facerecognition.utils.CommonUtil;
import com.zl.facerecognition.utils.Constants;
import com.zl.facerecognition.utils.Consts;
import com.zl.facerecognition.utils.NetUtils;
import com.zl.facerecognition.utils.SPUtils;
import com.zl.facerecognition.utils.TimeUtil;
import com.zl.facerecognition.utils.UiUtils;
import com.zl.facerecognition.utils.ValidateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LoginActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    private EditText et_username;
    private EditText et_psw;
    private String account;


    /*登录*/
    private Button btn_login;
    /*注册用户*/
    private TextView tv_register;
    /*找回密码*/
    private TextView tv_findpsw;
    /*设置地址*/
    private TextView tv_url;
    /*弹出窗口*/
    private BasePopupView urlPopup;
    /*记住密码*/
    private Switch sw_remember_pwd;
    /*暂不登录*/
    private TextView tv_youke;
    /**
     * 学生
     */
    private RadioButton rd_teacher;
    /**
     * 教师
     */
    private RadioButton rd_student;
    private int userType;
    private String password;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_login);
//        ImmersionBar.with(this).init();
        setTitle("登录");
        rl_title_bar.setVisibility(View.GONE);
        preferences = getSharedPreferences("localRecord", MODE_PRIVATE);
        init();
    }

    private void init() {
        et_username = findViewById(R.id.et_login_username);
        et_psw = findViewById(R.id.et_login_psw);
        btn_login = findViewById(R.id.btn_login);
        sw_remember_pwd = findViewById(R.id.sw_remember_pwd);
        tv_register = findViewById(R.id.tv_login_register);
        tv_findpsw = findViewById(R.id.tv_login_findpsw);
        tv_url = findViewById(R.id.tv_url);
        rd_student = findViewById(R.id.login_radio_student);
        rd_teacher = findViewById(R.id.login_radio_teacher);
        rd_student.setOnCheckedChangeListener(this);
        rd_teacher.setOnCheckedChangeListener(this);
        tv_url.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        tv_findpsw.setOnClickListener(this);
        //记住密码开关
        boolean rememberPwd = SPUtils.getPrefBoolean(context, Consts.REMEMBER_PWD, false);
        sw_remember_pwd.setChecked(rememberPwd);
        sw_remember_pwd.setOnCheckedChangeListener(((buttonView, isChecked) -> SPUtils.setPrefBoolean(context, Consts.REMEMBER_PWD, isChecked)));
        if (rememberPwd) {
            et_username.setText(preferences.getString("account", ""));
            et_psw.setText(preferences.getString("password", ""));
        }

    }


    @Override
    public void onClick(View v) {
        if (initPermission()) {
            return;
        }
        switch (v.getId()) {
            case R.id.tv_url:
                showUrlPopup();
                break;
            case R.id.iv_back:
                LoginActivity.this.finish();
                break;
            //立即注册控件的点击事件
            case R.id.tv_login_register:
                new XPopup.Builder(context).asCustom(new RegisterPopup()).show();
                break;
            //找回密码控件的点击事件
            case R.id.tv_login_findpsw:
                new XPopup.Builder(context).asCustom(new ModifyPswPopup(context)).show();
                break;
            //登录按钮的点击事件
            case R.id.btn_login:
                String baseUrl = SPUtils.getPrefString(context, Consts.BASE_URL, "");
                account = et_username.getText().toString().trim();
                password = et_psw.getText().toString().trim();

//                spPsw = readPsw(account);
                if (ValidateUtil.isStringValid(baseUrl)) {
                    if (TextUtils.isEmpty(account)) {
                        Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (TextUtils.isEmpty(password)) {
                        Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (userType == 0) {
                        Toast.makeText(LoginActivity.this, "请选择角色", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        login();
                    }
                } else {
                    UiUtils.showKnowDialog(context, "提示", "请先填写服务器地址");
                }
                break;
        }
    }

    /**
     * 登录
     */
    private void login() {

        Map<String, String> params = new HashMap<>();
        params.put("account", account);
        params.put("password", password);
        params.put("type", String.valueOf(userType));
        NetUtils.request(context, Constants.LOGIN, params, (CommonResult result) -> {
            /*String token = result.getToken();
            SPUtils.setPrefString(context, Consts.TOKEN, token);
            SPUtils.setPrefString(context, Consts.ACCOUNT, account);
            SPUtils.setPrefString(context, Consts.PASSWORD, password);
            SPUtils.setPrefInt(context, Consts.USERTYPE, userType);
            SPUtils.setPrefBoolean(context, Consts.IS_LOGIN, true);*/
            if (result.getCode().equals("200")) {
                Intent intent = new Intent(context, MainActivity.class);
                updateLoginInfo(preferences, result.getData(), String.valueOf(userType));
                Toast.makeText(this, result.getMsg(), Toast.LENGTH_SHORT).show();
                finish();
                startActivity(intent);
            } else {
                UiUtils.showError(context, result.getMsg());
                return;
            }
        });
    }

    /*设置地址*/
    private void showUrlPopup() {
        String baseUrl = SPUtils.getPrefString(context, Consts.BASE_URL, Constants.SERVICE_PATH + "/");
        urlPopup = new XPopup.Builder(context).autoDismiss(false).asInputConfirm(
                "请填写服务器地址", "", baseUrl, "请填写", text -> {
                    SPUtils.setPrefString(context, Consts.BASE_URL, text);
                    UiUtils.showSuccess(context, "设置成功");
                    urlPopup.dismiss();
                }
        ).show();
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
            case R.id.login_radio_teacher:
                if (isChecked) {
                    userType = 1;
                }
                break;
            case R.id.login_radio_student:
                if (isChecked) {
                    userType = 2;
                }
                break;
            default:
                userType = 0;
                break;
        }
    }

    //    将此次的登录信息记录到SharedPreferences中
    public static void updateLoginInfo(SharedPreferences preferences, String data, String type) {
        JSONObject account = JSONObject.parseObject(String.valueOf(data));

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userType", type);
        if (type.equals("1")) {
            editor.putString("id", account.getString("teacherId"));
            editor.putString("account", account.getString("teacherAccount"));
            editor.putString("password", account.getString("teacherPassword"));
            editor.putString("name", account.getString("teacherName"));
            editor.putBoolean("sex", account.getBoolean("teacherSex"));
            editor.putString("phone", account.getString("teacherPhone"));
            editor.putString("birthday", account.getString("teacherBirthday"));
            editor.putString("avatar", account.getString("teacherAvatar"));
        } else {
            editor.putString("id", account.getString("studentId"));
            editor.putString("account", account.getString("studentAccount"));
            editor.putString("password", account.getString("studentPassword"));
            editor.putString("name", account.getString("studentName"));
            editor.putBoolean("sex", account.getBoolean("studentSex"));
            editor.putString("phone", account.getString("studentPhone"));
            editor.putString("birthday", account.getString("studentBirthday"));
            editor.putString("avatar", account.getString("studentAvatar"));
            editor.putString("class", account.getString("studentClass"));
            editor.putString("face", account.getString("studentFace"));
        }
        editor.apply();
    }


    private String[] PM_MULTIPLE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION
    };

    private boolean initPermission() {

        if (Build.VERSION.SDK_INT >= 23) {
            ArrayList<String> pmList = new ArrayList<>();
            //获取当前未授权的权限列表
            for (String permission : PM_MULTIPLE) {
                int nRet = ContextCompat.checkSelfPermission(this, permission);
                if (nRet != PackageManager.PERMISSION_GRANTED) {
                    pmList.add(permission);
                }
            }
            if (pmList.size() > 0) {
                String[] sList = pmList.toArray(new String[0]);
                ActivityCompat.requestPermissions(this, sList, 10000);
                return false;
            }
            return true;

        } else {
            List<String> permissionList = new ArrayList<>();
            if (!PermissionUtils.isGranted(Manifest.permission.READ_PHONE_STATE)) {
                permissionList.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (!PermissionUtils.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!PermissionUtils.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (!permissionList.isEmpty()) {
                String[] permissions = permissionList.toArray(new String[permissionList.size()]);
                PermissionUtils.permission(permissions).request();
                return false;
            }
            return true;
        }
    }

    public class RegisterPopup extends BottomPopupView {
        private RadioGroup rg;          //性别
        private EditText etName;        //姓名
        private EditText etPhone;       //手机号
        private TextView tvBirthday;    //出生年月
        private EditText etCourse;
        private EditText etUsername;    //用户名
        private EditText etPwd;         //密码
        private EditText etRepwd;       //再次输入密码
        private Button btnRegister;     //注册按钮
        private AccountStudent student;

        public RegisterPopup() {
            super(context);
        }

        /**
         * 具体实现的类的布局
         *
         * @return
         */
        protected int getImplLayoutId() {
            return R.layout.activity_register;
        }

        @Override
        protected void onCreate() {
            super.onCreate();
            rg = findViewById(R.id.rg);
            etName = findViewById(R.id.et_name);
            etPhone = findViewById(R.id.et_phone);
            tvBirthday = findViewById(R.id.tv_birthday);
            tvBirthday.setOnClickListener(v -> selectDate());
            etUsername = findViewById(R.id.et_username);
            etCourse = findViewById(R.id.et_course);
            etPwd = findViewById(R.id.et_pwd);
            etRepwd = findViewById(R.id.et_repwd);
            btnRegister = findViewById(R.id.btn_register);
            btnRegister.setOnClickListener(v -> {
                if (TextUtils.isEmpty(etName.getText())) {
                    UiUtils.showError(context, "请填写姓名");
                    return;
                }
                if (TextUtils.isEmpty(etPhone.getText())) {
                    UiUtils.showError(context, "请填写手机号");
                    return;
                }
                if (TextUtils.isEmpty(tvBirthday.getText())) {
                    UiUtils.showError(context, "请选择出生年月");
                    return;
                }
                if (TextUtils.isEmpty(etCourse.getText())) {
                    UiUtils.showError(context, "请填写班级号");
                    return;
                }
                if (TextUtils.isEmpty(etUsername.getText())) {
                    UiUtils.showError(context, "请填写用户名");
                    return;
                }
                if (TextUtils.isEmpty(etPwd.getText()) || TextUtils.isEmpty(etRepwd.getText())) {
                    UiUtils.showError(context, "请输入密码");
                    return;
                }
                if (!etPwd.getText().toString().equals(etRepwd.getText().toString())) {
                    UiUtils.showError(context, "两次输入的密码不一致");
                    return;
                }
                checkAccount();
            });
        }

        /*后台检查账号是否合法*/
        private void checkAccount() {
            Map<String, String> params = new HashMap<>();
            params.put("type", "2");
            params.put("account", etUsername.getText().toString());
            NetUtils.request(context, Constants.CHECK_ACCOUNT, params, result -> {
                if ("1".equals(result.getMsg())) {
//                Toast.makeText(context,"账户已存在", Toast.LENGTH_SHORT).show();
                    UiUtils.showError(context, "账户已存在");
                    return;
                }

            });
            register();
        }

        /**
         * 注册
         */
        private void register() {
            String name = etName.getText().toString();
            String account = etUsername.getText().toString();
            String major = etCourse.getText().toString();
            String password = etPwd.getText().toString();
            String phone = etPhone.getText().toString();
            String birthday = tvBirthday.getText().toString();


            int sex = Integer.parseInt(rg.getCheckedRadioButtonId() == R.id.rb_male ? "1" : "0");
            student = new AccountStudent(name, account, password, sex, major, phone, birthday);
            Map<String, String> params = CommonUtil.object2Map(student);
            Log.d("NETsssss-->", String.valueOf(params));
            NetUtils.request(context, Constants.REGISTER, params, result -> {
                if (result.getCode().equals("200")) {
                    UiUtils.showSuccess(context, "注册成功");
//                    Intent intent=new Intent(context,MainActivity.class);
//                    updateLoginInfo(preferences, result.getData(), "2");
//                    finish();
//                    startActivity(intent);
                    dismiss();
                } else {
                    UiUtils.showError(context, result.getMsg());
                }
            });
        }

        private void selectDate() {
            TimeUtil.selectDateTime(LoginActivity.this, "请选择出生年月", false, true, false, (d, view) -> {
                tvBirthday.setText(TimeUtil.getDateTimeText(d, "yyyy-MM-dd"));
            });
        }
    }
}
