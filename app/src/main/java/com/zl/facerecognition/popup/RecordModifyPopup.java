package com.zl.facerecognition.popup;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.lxj.xpopup.core.BottomPopupView;
import com.zl.facerecognition.R;

public class RecordModifyPopup extends BottomPopupView implements View.OnClickListener{

    private TextView title;
    private Spinner spinner;
    private Button no;
    private Button yes;
    public String result;
    private onYesClickedListener yesClickedListener;

    public void setYesClickedListener(onYesClickedListener yesClickedListener) {
        this.yesClickedListener = yesClickedListener;
    }

    public RecordModifyPopup(Context context, String type){
        super(context);
        this.result = type;
        Integer position =  type.equals("缺 勤") ? 0 : ( type.equals("请 假") ? 1 : 2);
        spinner.setSelection(position);

    }
    /**
     * 具体实现的类的布局
     *
     * @return
     */
    protected int getImplLayoutId() {
        return R.layout.dialog_modify_record;
    }


    @Override
    protected void onCreate() {
        super.onCreate();

        title = (TextView) findViewById(R.id.title);
        spinner = (Spinner) findViewById(R.id.spinner_record_result);
        no = (Button) findViewById(R.id.no);
        yes = (Button) findViewById(R.id.yes);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.yes:
                result = spinner.toString();
                yesClickedListener.onYesClicked();
                break;
            case R.id.no:
                dismiss();
                break;
        }
    }

    public interface onYesClickedListener{
        void onYesClicked();
    }

}
