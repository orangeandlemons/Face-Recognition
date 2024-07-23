package com.zl.facerecognition.popup;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.core.CenterPopupView;
import com.zl.facerecognition.R;
import com.zl.facerecognition.utils.Constants;
import com.zl.facerecognition.utils.NetUtils;
import com.zl.facerecognition.utils.UiUtils;
import com.zl.facerecognition.utils.ViewUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class ShowFacePop extends CenterPopupView implements View.OnClickListener {
    private String name;
    private File file;
    private String location;
    private TextView title;
    private ImageView image;
    private Button no;
    private Button yes;
    private onRecordSuccess recordSuccess;

    public void setRecordSuccess(onRecordSuccess recordSuccess) {
        this.recordSuccess = recordSuccess;
    }
    /**
     * 具体实现的类的布局
     *
     * @return
     */
    protected int getImplLayoutId() {
        return R.layout.dialog_show_face;
    }


    public ShowFacePop(Context context, String path, String name, String location) {
        super(context);
        this.name = name;
        this.location = location;
        file = new File(path);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        no = (Button) findViewById(R.id.face_no);
        yes = (Button) findViewById(R.id.face_yes);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        title = (TextView) findViewById(R.id.title);
        title.setText("开始签到");
        image = (ImageView) findViewById(R.id.image);

        if (!file.exists()) {
            no.performClick();
        }
        image.setImageURI(Uri.fromFile(file));
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.face_yes:
                if (!file.exists()) {
                    Toast.makeText(v.getContext(), "未选择图片", Toast.LENGTH_SHORT).show();
                    break;
                }
                try {
                    sendFace(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.face_no:
                if (file.exists()) {
                    file.delete();
                }
                dismiss();
                break;
        }
    }

    public void sendFace(File file) throws FileNotFoundException {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("photo", file);
        params.put("type", "5");
        params.put("id", name);
        client.post(Constants.SERVICE_PATH + "/document/saveImage", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String studentId = name.substring(0, name.indexOf("_"));
                String attendId = name.substring(name.indexOf("_") + 1);
                Map<String, String> map = new HashMap<>();
                map.put("studentId", studentId);
                map.put("attendId", attendId);
                map.put("time", new Timestamp(System.currentTimeMillis()).toString());
                map.put("location", location);
                NetUtils.request(getContext(), "/record/doRecord", map, result -> {

                    if (result.getCode().equals("200")) {
                        UiUtils.showSuccess(getContext(), result.getMsg());
                        dismiss();

                    }else {
                        UiUtils.showError(getContext(), result.getMsg());
                        dismiss();
                    }
                    return;
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                UiUtils.showError(getContext(), "上传失败");
            }
        });
    }

    public interface onRecordSuccess {
        void closeActivity();
    }

}
