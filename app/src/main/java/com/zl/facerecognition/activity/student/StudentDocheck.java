package com.zl.facerecognition.activity.student;

import androidx.annotation.Nullable;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.PathUtils;
import com.gyf.barlibrary.ImmersionBar;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.soundcloud.android.crop.Crop;
import com.zl.facerecognition.R;
import com.zl.facerecognition.activity.BaseActivity;
import com.zl.facerecognition.entity.CheckinList;
import com.zl.facerecognition.popup.SetGestureDialog;
import com.zl.facerecognition.popup.ShowFacePop;
import com.zl.facerecognition.utils.NetUtils;
import com.zl.facerecognition.utils.UiUtils;

import java.io.File;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentDocheck extends BaseActivity {

    private CheckinList check;
    private LatLng attendLocation;
    private LatLng currentLocation;
    private Boolean isFirst = true;

    private String pictureDir;
    private String picturePath;

    private LocationClient locationClient;
    private BaiduMap baiduMap;
    private GeoCoder search = GeoCoder.newInstance();
    private Button mapStartRecord;
    private Button mapMyLocation;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        SDKInitializer.setAgreePrivacy(getApplicationContext(), true);
//        LocationClient.setAgreePrivacy(true);
        SDKInitializer.initialize(getApplicationContext());
        setView(R.layout.activity_student_docheck);
        ImmersionBar.with(this).init();
        setTitle("签到");
        init();
        Intent intent = getIntent();
        check = (CheckinList) intent.getExtras().getSerializable("attend");
        attendLocation = new LatLng(check.getLatitude(), check.getLongitude());

        pictureDir = PathUtils.getExternalAppPicturesPath();
        FileUtils.createOrExistsDir(pictureDir);
        picturePath = pictureDir + "/temp.png";
//        String text = "大四大三多" + "时代大厦" + "50" + "米";


        try {
            locationClient = new LocationClient(getApplicationContext());
            locationClient.registerLocationListener(new MyLocationListener());
            baiduMap = mapView.getMap();
            baiduMap.setMyLocationEnabled(true);
            search.setOnGetGeoCodeResultListener(onGetGeoCoderResultListener);
            requestLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void init() {
        mapStartRecord = (Button) findViewById(R.id.map_start_record);
        mapMyLocation = (Button) findViewById(R.id.map_my_location);
        mapView = (MapView) findViewById(R.id.mapView);
        mapStartRecord.setOnClickListener(this);
        mapMyLocation.setOnClickListener(this);
    }

    public void requestLocation() {
        initLocation();
        locationClient.start();
    }

    public void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationPurpose(LocationClientOption.BDLocationPurpose.SignIn);
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        locationClient.setLocOption(option);
    }

    public void navigateTo(BDLocation location) {
        if (isFirst) {
            isFirst = false;
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(ll, 19f);
            baiduMap.animateMapStatus(update);
        }
        MyLocationData.Builder builder = new MyLocationData.Builder();
        builder.latitude(location.getLatitude());
        builder.longitude(location.getLongitude());
        MyLocationData locationData = builder.build();
        baiduMap.setMyLocationData(locationData);

        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(ImageUtils.getBitmap(R.drawable.ic_location_on));
        OverlayOptions option = new MarkerOptions().position(attendLocation)
                .icon(bitmap).animateType(MarkerOptions.MarkerAnimateType.grow)
                .alpha(1f).visible(true);
        baiduMap.addOverlay(option);
        drawCircle(attendLocation);
    }

    private void drawCircle(LatLng point) {
        CircleOptions circle = new CircleOptions().fillColor(0x384d73b3).
                center(point).stroke(new Stroke(3, 0x784d73b3))
                .radius(100).visible(true);
        baiduMap.addOverlay(circle);
    }

    //    获取当前位置的名称
    public OnGetGeoCoderResultListener onGetGeoCoderResultListener = new OnGetGeoCoderResultListener() {
        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            List<PoiInfo> poiList = reverseGeoCodeResult.getPoiList();
            if (!poiList.isEmpty()) {
                PoiInfo poiInfo = poiList.get(0);
                String text = poiInfo.name + poiInfo.direction + poiInfo.distance + "米";

                if (check.getType() == 1) {
                    String name = getSharedPreferences("localRecord", MODE_PRIVATE).getString("id", "") + "_" + check.getAttendId();
                    new XPopup.Builder(StudentDocheck.this).asCustom(new ShowFacePop(StudentDocheck.this, picturePath, name, text)).show();
                    ShowFacePop shop = new ShowFacePop(StudentDocheck.this, picturePath, name, text);
//                    shop.setRecordSuccess(() -> finish());
                } else {
                    BasePopupView loadingView = new XPopup.Builder(context).asLoading("正在签到中").show();
                    Map<String, String> map = new HashMap<>();
                    map.put("attendId", String.valueOf(check.getAttendId()));
                    map.put("studentId", getSharedPreferences("localRecord", MODE_PRIVATE).getString("id", ""));
                    map.put("result", "2");
                    map.put("time", new Timestamp(System.currentTimeMillis()).toString());
                    map.put("location", text);
                    NetUtils.request(context, "/record/modifyRecord", map, result -> {
                        if (result.getCode().equals("200")) {
                            UiUtils.showSuccess(context, "签到成功");
                            loadingView.dismiss();
                            finish();
                        } else {
                            UiUtils.showError(context, result.getMsg());
                        }
                        return;
                    });
                }
            }
        }
    };

    //    定位我的位置
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation.getLocType() == BDLocation.TypeGpsLocation || bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
                currentLocation = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                Log.d("location-->", bdLocation.getLatitude() + "," + bdLocation.getLongitude());
                navigateTo(bdLocation);
            } else {
                UiUtils.showText(context,"定位错误","无法定位，请打开GPS或网络重试");
                BasePopupView tishi=new XPopup.Builder(context).asConfirm("定位错误","无法定位，请打开GPS或网络重试",
                        "", "知道了", null, null, true).show();
                while (!tishi.isDismiss()){
                    finish();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.map_start_record:
                //获取当前位置并与签到位置比对
                if (DistanceUtil.getDistance(currentLocation, attendLocation) > 100) {
                    UiUtils.showKnowDialog(context,"提示","不在考勤范围内，请在考勤范围内签到");
                } else {
//                    打开相机
                    if (check.getType() == 1) {
                        openCamera();
                    } else if (check.getType() == 2) {
                        showGestureDialog();
                    } else {
                        search.reverseGeoCode(new ReverseGeoCodeOption().location(currentLocation));
                    }
                }
                break;
            case R.id.map_my_location:
                Toast.makeText(this, "我的位置", Toast.LENGTH_SHORT).show();
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(currentLocation, 19f);
                baiduMap.animateMapStatus(update);
                break;
        }
    }


    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(picturePath)));
        startActivityForResult(intent, 171);
    }

    public void showGestureDialog() {
        SetGestureDialog gestureDialog = new SetGestureDialog(this);
        gestureDialog.setRightGesture(check.getGesture());
        gestureDialog.setCancelable(true);
        gestureDialog.setCanceledOnTouchOutside(true);
        gestureDialog.setYesClickedListener(new SetGestureDialog.onYesClickedListener() {
            @Override
            public void yesClicked(String list) {

            }

            @Override
            public void yesClicked() {
                search.reverseGeoCode(new ReverseGeoCodeOption().location(currentLocation));
                gestureDialog.dismiss();
            }
        });
        gestureDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        File tempFile = new File(picturePath);
        switch (requestCode) {
            case 171:
                if (resultCode == RESULT_OK) {
                    Crop.of(Uri.fromFile(tempFile), Uri.fromFile(tempFile)).asSquare().withAspect(500, 500).start(this);
                }
                break;
            case Crop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
//                    获得当前位置的中文描述
                    search.reverseGeoCode(new ReverseGeoCodeOption().location(currentLocation));
                }
                break;

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        locationClient.stop();
        search.destroy();
        baiduMap.setMyLocationEnabled(false);
    }

}