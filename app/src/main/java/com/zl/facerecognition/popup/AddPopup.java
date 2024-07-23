package com.zl.facerecognition.popup;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.core.BottomPopupView;
import com.zl.facerecognition.R;
import com.zl.facerecognition.activity.student.AddCourseActivity;
import com.zl.facerecognition.utils.Constants;
import com.zl.facerecognition.utils.NetUtils;
import com.zl.facerecognition.utils.UiUtils;

import java.util.HashMap;
import java.util.Map;

public class AddPopup extends BottomPopupView implements View.OnClickListener {
    private EditText et_addCode;
    private TextView no;
    private TextView yes;
    private final String studentId;

    public AddPopup(Context context,String studentId) {
        super(context);
        this.studentId=studentId;
    }


    /**
     * 具体实现的类的布局
     *
     * @return
     */
    protected int getImplLayoutId() {
        return R.layout.popview_courseadd;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        et_addCode = findViewById(R.id.course_add_code);
        no = findViewById(R.id.no);
        yes = findViewById(R.id.yes);
        no.setOnClickListener(this);
        yes.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.yes:
                if (TextUtils.isEmpty(et_addCode.getText())) {
                    UiUtils.showError(v.getContext(), "请填写课程码");
                    return;
                }
                String code=et_addCode.getText().toString();
                BasePopupView loadingView = new XPopup.Builder(v.getContext()).asLoading("加入课程中").show();
                Map<String, String> map = new HashMap<>();
                map.put("code",code);
                NetUtils.request(v.getContext(),Constants.FIND_COU_BYCODE,map,result->{
                    if(result.getMsg()!=null){
                        String data = result.getData();
                        if (data == null){
                            loadingView.dismiss();
                            UiUtils.showError(v.getContext(),"该课程码不存在");
                        } else {
                            Intent intent = new Intent(v.getContext(),AddCourseActivity.class);
                            intent.putExtra("data",data);
                            v.getContext().startActivity(intent);
                            loadingView.dismiss();
                            dismiss();
                        }
                    }
                });
                break;
            case R.id.no:
                dismiss();
                break;
        }
    }
}
