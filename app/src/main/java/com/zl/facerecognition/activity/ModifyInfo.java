package com.zl.facerecognition.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import cz.msebera.android.httpclient.Header;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.PathUtils;
import com.gyf.barlibrary.ImmersionBar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.zl.facerecognition.R;
import com.zl.facerecognition.utils.CommonUtil;
import com.zl.facerecognition.utils.Constants;
import com.zl.facerecognition.utils.NetUtils;
import com.zl.facerecognition.utils.TimeUtil;
import com.zl.facerecognition.utils.UiUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;


public class ModifyInfo extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    private LinearLayout linearLayout2;
    private ImageView infoDetailAvatar;
    private LinearLayout linearLayout1;
    private EditText infoDetailName;
    private LinearLayout infoDetailClassLayout;
    private EditText infoDetailClass;
    private TextView tv_birthday;
//    private RadioGroup infoSex;
    private RadioButton male;
    private RadioButton female;


    private EditText et_phone;
    private LinearLayout faceLayout;
    private ImageView infoDetailFace;
    private Button infoDetailModify;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String type;
    private String id;
    private int id1;
    private boolean sexold;
    private String newName;
    private String newPhone;
    private Date newBirthday;
    private String newClass;
    private Map<String, String> map = new HashMap<>();
    private String oldPhone;
    public String picDir;
    public String albumPath;
    public String cameraPath;

    private boolean isCamera;
    //    判断哪个图像更改了
    private boolean avatarChanged = false;
    private boolean faceChanged = false;

    //    记录当前图像上传状态
    private boolean avatarFinish = false;
    private boolean faceFinish = false;
    private String sex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_modify_info);
        ImmersionBar.with(this).init();
        setTitle("修改信息");
        init();
        initView();

    }

    private void modify() {
        newName = infoDetailName.getText().toString();
        newPhone = et_phone.getText().toString();
        newBirthday = Date.valueOf(tv_birthday.getText().toString());
        if (TextUtils.isEmpty(newName)) {
            UiUtils.showError(this, "姓名不可为空");
            return;
        }
        if (!CommonUtil.isPhone(newPhone)) {
            UiUtils.showError(this, "手机号格式错误");
            return;
        }
        if (TextUtils.isEmpty(newBirthday.toString())) {
            UiUtils.showError(this, "生日不能为空");
            return;
        }
        if (type.equals("2")) {
            newClass = infoDetailClass.getText().toString();
            if (TextUtils.isEmpty(newClass)) {
                UiUtils.showError(this, "班级格式错误");
                return;
            }
        }
        if (faceChanged) {
            long time = System.currentTimeMillis() - preferences.getLong("modifyTime", 0);
            if (time < 1000 * 3600 * 24) {
                UiUtils.showError(this, "修改人脸信息过于频繁");
            }
        }
        map.put("name", newName);
        map.put("sex", sex);
        map.put("phone", newPhone);
        map.put("birthday", String.valueOf(newBirthday));


        if (avatarChanged) {
            sendImage(new File(albumPath), Integer.parseInt(type), id);
        }
        if (faceChanged) {
            sendImage(new File(cameraPath), 4, id);
        }
        if (!(avatarChanged || faceChanged)) {
            if (type.equals("1")) {
                map.put("teacherId", id);
                NetUtils.request(context,Constants.MODIFY+"Teacher",map,result->{
                    UiUtils.showSuccess(this,result.getMsg());
                    finish();
                });
            } else {
                map.put("studentId", id);

                NetUtils.request(context,Constants.MODIFY+"Student",map,result->{
                    UiUtils.showSuccess(this,result.getMsg());
                    finish();
                });
            }

        }
    }


    private void initView() {
        preferences = getSharedPreferences("localRecord", MODE_PRIVATE);
        type = preferences.getString("userType", "");
        id = preferences.getString("id", "");
        oldPhone = preferences.getString("phone", "");
        picDir = PathUtils.getExternalAppPicturesPath();
        FileUtils.createOrExistsDir(picDir);
        albumPath = picDir + "/album.png";
        cameraPath = picDir + "/camera.png";
        CommonUtil.initPhotoError();

        Picasso.with(context)
                .load(Constants.SERVICE_PATH + preferences.getString("avatar", ""))
                .fit().memoryPolicy(MemoryPolicy.NO_STORE)
                .error(R.drawable.ic_net_error)
                .into(infoDetailAvatar);

        sexold = preferences.getBoolean("sex",true);
        id1 = sexold? R.id.male : R.id.female;
        infoDetailName.setText(preferences.getString("name", ""));
        findViewById(id1).performClick();
        et_phone.setText(preferences.getString("phone", ""));
        tv_birthday.setText(preferences.getString("birthday", ""));

        if (type.equals("2")) {
            infoDetailClassLayout.setVisibility(View.VISIBLE);
            infoDetailClass.setText(preferences.getString("class", ""));

            faceLayout.setVisibility(View.VISIBLE);
            Picasso.with(this)
                    .load(Constants.SERVICE_PATH + preferences.getString("face", ""))
                    .fit().memoryPolicy(MemoryPolicy.NO_STORE)
                    .error(R.drawable.ic_net_error)
                    .into(infoDetailFace);
        }
    }

    public void sendImage(File file, Integer type, String id) {
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("photo", file);
            params.put("type", type);
            params.put("id", id);
            client.post(Constants.SERVICE_PATH + "/document/saveImage", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    JSONObject object = JSON.parseObject(new String(responseBody));
                    String imgPath = object.getString("msg");
                    Message message = new Message();
                    message.what = type;
                    message.obj = imgPath;
                    uploadHandler.sendMessage(message);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    UiUtils.showError(context,"文件上传失败，请重试");
                }

            });
        } catch (FileNotFoundException e) {
            UiUtils.showError(context,"未找到文件");
        }
    }

    Handler uploadHandler = new Handler(msg -> {
        String img = (String) msg.obj;
        switch (msg.what){
//            上传教师头像
            case 1:
                map.put("teacherId",id);
                map.put("avatar",img);
                NetUtils.request(this,Constants.MODIFY+"Teacher",map,result->{
                    UiUtils.showSuccess(this,result.getMsg());
                    finish();
                });
                break;
//            上传学生头像
            case 2:
                avatarFinish = true;
                map.put("major",newClass);
                map.put("studentId",id);
                map.put("avatar",img);
                if (faceChanged){
                    if (faceFinish){
//                        执行逻辑
                        NetUtils.request(this,Constants.MODIFY+"Student",map,result->{
                            UiUtils.showSuccess(this,result.getMsg());
                            finish();
                        });
                    }
                } else {
//                    开始上传
                    NetUtils.request(this,Constants.MODIFY+"Student",map,result->{
                        UiUtils.showSuccess(this,result.getMsg());
                        finish();
                    });
                }
                break;
//            上传人脸信息
            case 4:
                faceFinish = true;
                map.put("studentId",id);
                map.put("face",img);
//                editor.putLong("modifyTime",System.currentTimeMillis());
//                editor.apply();
                if (avatarChanged){
                    if (avatarFinish){
                        NetUtils.request(this,Constants.MODIFY+"Student",map,result->{
                            UiUtils.showSuccess(this,result.getMsg());
                            finish();
                        });
                    }
                } else {
//                    开始上传
                    NetUtils.request(this,Constants.MODIFY+"Student",map,result->{
                        UiUtils.showSuccess(this,result.getMsg());
                        finish();
                    });
                }
                break;
        }
        return false;
    });

    private void init() {

        infoDetailAvatar = findViewById(R.id.info_avatar);
        infoDetailName = findViewById(R.id.info_detail_name);
        infoDetailClassLayout = findViewById(R.id.info_detail_class_layout);
        infoDetailClass = findViewById(R.id.info_detail_class);
//        infoSex = findViewById(R.id.sex);
        et_phone = findViewById(R.id.info_detail_phone);
        tv_birthday = findViewById(R.id.info_detail_birthday);
        tv_birthday.setOnClickListener(v -> selectDate());
        faceLayout = findViewById(R.id.face_layout);
        infoDetailFace = findViewById(R.id.info_face);
        infoDetailModify = findViewById(R.id.info_modify);
        infoDetailModify.setOnClickListener(this);
        infoDetailAvatar.setOnClickListener(this);
        infoDetailFace.setOnClickListener(this);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);
        male.setOnCheckedChangeListener(this);
        female.setOnCheckedChangeListener(this);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 151:
                if (resultCode == RESULT_OK) {
                    File tempFile = new File(albumPath);
                    Crop.of(data.getData(), Uri.fromFile(tempFile)).asSquare().withAspect(500, 500).start(this);
                }
                break;
            case 161:
                if (resultCode == RESULT_OK) {
                    File tempFile = new File(cameraPath);
                    Crop.of(Uri.fromFile(tempFile), Uri.fromFile(tempFile)).asSquare().withAspect(500, 500).start(this);
                }
                break;
            case Crop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    if (isCamera) {
                        faceChanged = true;
                        infoDetailFace.setImageBitmap(ImageUtils.getBitmap(cameraPath));
                    } else {
                        avatarChanged = true;
                        infoDetailAvatar.setImageBitmap(ImageUtils.getBitmap(albumPath));
                    }
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.info_avatar:
                openAlbum();
                break;
            case R.id.info_face:
                openCamera();
                break;
            case R.id.info_modify:
                BasePopupView loadingView = new XPopup.Builder(context).asLoading("修改中").show();
                modify();
                loadingView.dismiss();
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void selectDate() {
        TimeUtil.selectDateTime(ModifyInfo.this, "请选择出生年月", false, true, false, (d, view) -> {
            tv_birthday.setText(TimeUtil.getDateTimeText(d, "yyyy-MM-dd"));
        });
    }

    public void openAlbum() {
        isCamera = false;
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, 151);
    }

    public void openCamera() {
        isCamera = true;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(cameraPath)));
        startActivityForResult(intent, 161);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.male:
                if (isChecked) {
                    sex="1";
                }
                break;
            case R.id.female:
                if (isChecked) {
                    sex="0";
                }
                break;

        }
    }
}
