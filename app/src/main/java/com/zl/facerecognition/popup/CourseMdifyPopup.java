package com.zl.facerecognition.popup;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.StringUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.core.BottomPopupView;
import com.zl.facerecognition.R;
import com.zl.facerecognition.utils.NetUtils;
import com.zl.facerecognition.utils.UiUtils;

import java.util.HashMap;
import java.util.Map;

public class CourseMdifyPopup extends BottomPopupView implements View.OnClickListener {

    public EditText popcourseName;
    public   EditText popcourseIntroduce;
    private TextView nomodify;
    private TextView yesmodify;
    private  String oldName;
    private  String oldIntroduce;
    private Integer courseId;
    private Context mContext;
    public CourseMdifyPopup(Context context, String name, String introduce, Integer courseId) {
        super(context);
        mContext=context;
        oldIntroduce = introduce;
        oldName = name;
        this.courseId = courseId;

    }

    /**
     * 具体实现的类的布局
     *
     * @return
     */
    protected int getImplLayoutId() {
        return R.layout.popview_coursemodify;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        popcourseName = (EditText) findViewById(R.id.popcourse_name);
        popcourseIntroduce = (EditText) findViewById(R.id.popcourse_introduce);
        nomodify = (TextView) findViewById(R.id.nomodify);
        yesmodify = (TextView) findViewById(R.id.yesmodify);
        yesmodify.setOnClickListener(this);
        nomodify.setOnClickListener(this);
        popcourseName.setText(oldName);
        popcourseIntroduce.setText(oldIntroduce);

    }



    private void modifyCourse() {
        String newName = popcourseName.getText().toString();
        String newIntroduce = popcourseIntroduce.getText().toString();
        if (newName.length() < 1 || newIntroduce.length() < 1){
            UiUtils.showError(mContext, "输入内容为空");
            return;
        }
        Boolean modifyFlag = false;
        Map<String, String> map = new HashMap<>();
        map.put("courseId",String.valueOf(courseId));
        if (!newName.equals(oldName)){
            map.put("courseName",newName);
            modifyFlag = true;
        }
        if (!newIntroduce.equals(oldIntroduce)){
            map.put("courseIntroduce",newIntroduce);
            modifyFlag = true;
        }
        if (modifyFlag) {
            NetUtils.request(mContext,"/course/modifyCourse",map,result->{
                if(result.getCode().equals("200")){
                    dismiss();
                }
                //TODO 没办法了
                UiUtils.showSuccess(mContext,result.getMsg()+",退出课程刷新即可");
                return;
            });
        } else {
            UiUtils.showError(mContext,"内容未修改");
            dismiss();
        }
        return;


    }
    public  String getName(){
        return popcourseName.getText().toString().length() < 1 ? oldName : popcourseName.getText().toString();
    }

    public  String getIntroduce(){
        return popcourseIntroduce.getText().toString().length() < 1 ? oldIntroduce : popcourseIntroduce.getText().toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.yesmodify:
                modifyCourse();
                break;
            case R.id.nomodify:
                dismiss();
                break;
        }
    }
}
