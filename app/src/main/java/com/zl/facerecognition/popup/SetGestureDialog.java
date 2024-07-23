package com.zl.facerecognition.popup;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ihsg.patternlocker.OnPatternChangeListener;
import com.github.ihsg.patternlocker.PatternLockerView;
import com.zl.facerecognition.R;
import com.zl.facerecognition.utils.CommonUtil;
import com.zl.facerecognition.utils.ViewUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SetGestureDialog extends Dialog implements View.OnClickListener{
    private TextView lockTitle;
    private PatternLockerView locker;
    private LinearLayout gestureLayout;
    private Button no;
    private Button yes;
    private String rightGesture;
    private String currentGesture;
    private onYesClickedListener yesClickedListener;

    public void setYesClickedListener(onYesClickedListener yesClickedListener) {
        this.yesClickedListener = yesClickedListener;
    }

    public void setRightGesture(String rightGesture) {
        this.rightGesture = rightGesture;
        gestureLayout.setVisibility(View.GONE);
    }
    public SetGestureDialog(Context context) {
        super(context,R.style.dialog);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_set_gesture, null);
        setContentView(inflate);
        lockTitle = (TextView) findViewById(R.id.lock_title);
        locker = (PatternLockerView) findViewById(R.id.locker);
        gestureLayout = (LinearLayout) findViewById(R.id.gesture_button_layout);
        no = (Button) findViewById(R.id.lock_no);
        yes = (Button) findViewById(R.id.lock_yes);

        //监听手势
        locker.setOnPatternChangedListener(new OnPatternChangeListener() {
            @Override//开始绘制图案时（即手指按下触碰到绘画区域时）会调用该方法
            public void onStart(@NotNull PatternLockerView patternLockerView) {
                //设置已绘制图案及状态
                locker.updateStatus(false);
            }
            @Override//图案绘制改变时（即手指在绘画区域移动时）会调用该方法
            public void onChange(@NotNull PatternLockerView patternLockerView, @NotNull List<Integer> list) {
            }
            @Override//图案绘制完成时（即手指抬起离开绘画区域时）会调用该方法
            public void onComplete(@NotNull PatternLockerView patternLockerView, @NotNull List<Integer> list) {
                String s = "";
                //循环获取list的值
                for (Integer integer : list) {
                    s = s + integer;
                }
                //若正确手势为空，则当前手势为s
                if (rightGesture == null) {
                    currentGesture = s;
                } else {
                    boolean right = s.equals(rightGesture);
                    locker.updateStatus(!right);
                    //弹窗提示
                    Toast.makeText(context, "手势" + (right ? "正确" : "错误"), Toast.LENGTH_SHORT).show();
                    if (right) {
                        yesClickedListener.yesClicked();
                    }
                }
            }
            @Override//已绘制的图案被清除时会调用该方法
            public void onClear(@NotNull PatternLockerView patternLockerView) {
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lock_yes:
                if (currentGesture.length() <= 4){
                    Toast.makeText(v.getContext(), "点数请勿低于5", Toast.LENGTH_SHORT).show();
                    break;
                }
                yesClickedListener.yesClicked(currentGesture);
                break;
            case R.id.lock_no:
                dismiss();
                break;
        }
    }

    public interface onYesClickedListener{
        void yesClicked(String list);
        void yesClicked();
    }

    @Override
    public void show() {
        super.show();
        ViewUtils.show(getWindow());
    }
}
