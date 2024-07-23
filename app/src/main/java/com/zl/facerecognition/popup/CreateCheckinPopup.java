package com.zl.facerecognition.popup;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lxj.xpopup.core.BottomPopupView;
import com.zl.facerecognition.R;
import com.zl.facerecognition.utils.NetUtils;
import com.zl.facerecognition.utils.TimeUtil;
import com.zl.facerecognition.utils.UiUtils;

import org.angmarch.views.NiceSpinner;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class CreateCheckinPopup extends BottomPopupView implements View.OnClickListener {

    private final Activity activity;
    private TextView checkinNo;
    private TextView checkinYes;
    private NiceSpinner spinnerCheckin;
    private TextView checkinTimeStart;
    private TextView checkinTimeEnd;
    private static TextView checkinLocationChoose;
    private Integer courseId;
    private Timestamp startTime,endTime;
    private static Double longitude;
    private static Double latitude;
    private Integer type;
    private static onChoose choose;
//   private String[] strArr;
//   private ArrayAdapter<CharSequence> adapter;

    public CreateCheckinPopup(Context mContext, Integer courseId, Activity activity) {
        super(mContext);
        this.courseId = courseId;
        this.activity=activity;
    }

    /**
     * 具体实现的类的布局
     *
     * @return
     */
    protected int getImplLayoutId() {
        return R.layout.popview_createcheckin;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        checkinNo = (TextView) findViewById(R.id.checkin_no);
        checkinYes = (TextView) findViewById(R.id.checkin_yes);
        spinnerCheckin =  findViewById(R.id.spinner_checkin);
        checkinTimeStart = (TextView) findViewById(R.id.checkin_time_start);
        checkinTimeEnd = (TextView) findViewById(R.id.checkin_time_end);
        checkinLocationChoose = (TextView) findViewById(R.id.checkin_location_choose);
        checkinNo.setOnClickListener(this);
        checkinYes.setOnClickListener(this);
        checkinLocationChoose.setOnClickListener(this);
        checkinTimeStart.setOnClickListener(this);
        checkinTimeEnd.setOnClickListener(this);
        spinnerCheckin.setSelectedIndex(0);
//        //创建一个适配器，并加入数据源
//        adapter = ArrayAdapter.createFromResource(getContext(),R.array.spinner_checkin, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        //为Spinner设置设配齐
//        spinnerCheckin.setAdapter(adapter);
        //设置加载spinner时不选择选项
//        spinnerCheckin.setSelection(0);

        /*spinnerCheckin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("TEST", "选择的是： " + strArr[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

    }

    public interface onChoose {
        void onChoose();
    }

    public static void setChoose(onChoose choose) {
        CreateCheckinPopup.choose = choose;
    }

    public static void setLongitude(Double longitude) {
        CreateCheckinPopup.longitude = longitude;
    }

    public static void setLatitude(Double latitude) {
        CreateCheckinPopup.latitude = latitude;
    }

    public static void updateLocation(String s){
        checkinLocationChoose.setText(s);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkin_no:
                UiUtils.showInfo(getContext(), "不签了嘿嘿嘿");
                dismiss();
                break;
            case R.id.checkin_location_choose:
                if (choose != null) {
                    choose.onChoose();
                }
                break;
            case R.id.checkin_time_end:
                TimeUtil.selectDateTime(activity, "请选择截止时间", false, true, true, (d, view) -> {
                    endTime = new Timestamp(d.getTime());
                    checkinTimeEnd.setText(TimeUtil.getDateTimeText(d, "yyyy-MM-dd HH:mm:ss"));
                });
                break;
            case R.id.checkin_time_start:
                TimeUtil.selectDateTime(activity, "请选择开始时间", false, true, true, (d, view) -> {
                    startTime = new Timestamp(d.getTime());
                    checkinTimeStart.setText(TimeUtil.getDateTimeText(d, "yyyy-MM-dd HH:mm:ss"));
                });
                break;
            case R.id.checkin_yes:
                if (checkinTimeStart == null || checkinTimeEnd == null) {
                    UiUtils.showInfo(v.getContext(), "时间信息未填写哦");
                    return;
                }
                if (longitude == null || latitude == null) {
                    UiUtils.showWarning(v.getContext(), "定位信息未填写哦");
                    return;
                }
                if (TimeUtil.isDate2Bigger(checkinTimeEnd.getText().toString(), checkinTimeStart.getText().toString())) {
                    UiUtils.showWarning(v.getContext(), "开始时间不能晚于截止时间");
                    return;
                }
                if (TimeUtil.isDate2Bigger(checkinTimeEnd.getText().toString(), TimeUtil.getCurrentTime("yyyy-MM-dd HH:mm:ss"))) {
                    Toast.makeText(v.getContext(), "不能晚于现在哦", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TimeUtil.string2Timestamp(checkinTimeEnd.getText().toString(), "yyyy-MM-dd HH:mm:ss") - TimeUtil.string2Timestamp(checkinTimeStart.getText().toString(), "yyyy-MM-dd HH:mm:ss") < 600000) {
                    Toast.makeText(v.getContext(), "签到时间至少也得10分钟吧", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, String> map = new HashMap<>();
                map.put("courseId", courseId.toString());
                map.put("start", startTime.toString());
                map.put("end", endTime.toString());
                map.put("longitude", longitude.toString());
                map.put("latitude", latitude.toString());
                map.put("location", checkinLocationChoose.getText().toString());
                type = spinnerCheckin.getSelectedIndex();
                map.put("type", String.valueOf(type));
//            当是2时说明此时选择考勤种类为手势
                if (type == 2) {
                    SetGestureDialog gestureDialog = new SetGestureDialog(v.getContext());
                    gestureDialog.setYesClickedListener(new SetGestureDialog.onYesClickedListener() {
                        @Override
                        public void yesClicked(String list) {
                            gestureDialog.setOnDismissListener(dialog -> {
                                map.put("gesture",list);
                                NetUtils.request(getContext(), "/attend/addAttend", map, result -> {
                                    if (result.getCode().equals("200")) {
                                        UiUtils.showSuccess(getContext(), result.getMsg());
                                        dismiss();
                                    }
                                    UiUtils.showSuccess(getContext(), result.getMsg());
                                });

                            });gestureDialog.dismiss();
                        }

                        @Override
                        public void yesClicked() {

                        }
                    });
                    gestureDialog.show();
                } else {
                    NetUtils.request(getContext(), "/attend/addAttend", map, result -> {
                        if (result.getCode().equals("200")) {
                            UiUtils.showSuccess(getContext(), result.getMsg());
                            dismiss();
                        }
                        UiUtils.showSuccess(getContext(), result.getMsg());
                    });
                }
            break;
        }
    }
}





