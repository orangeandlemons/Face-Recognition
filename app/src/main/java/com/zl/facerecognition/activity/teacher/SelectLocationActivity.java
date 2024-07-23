package com.zl.facerecognition.activity.teacher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.common.BaiduMapSDKException;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.blankj.utilcode.util.ImageUtils;
import com.gyf.barlibrary.ImmersionBar;
import com.zl.facerecognition.R;

import java.util.ArrayList;
import java.util.List;

public class SelectLocationActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView ivBack;
    private TextView tvMainTitle;
    private Button mapSelectLocation;
    private Button mapLocation;
    private MapView mapView;
    //存储选择坐标的信息
    private String location;
    private Double latitude, longitude;
    private LatLng currentLocation;
    private LocationClient locationClient;
    private BaiduMap baiduMap;
    private GeoCoder search = GeoCoder.newInstance();
    //地图覆盖物基类
    private List<Overlay> overlays = new ArrayList<>();
    private Boolean isFirst = true;
    public void setInfo(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        SDKInitializer.setAgreePrivacy(getApplicationContext(), true);
//        LocationClient.setAgreePrivacy(true);
        try {
            // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
            SDKInitializer.initialize(getApplicationContext());
        } catch (BaiduMapSDKException e) {

        }
        setContentView(R.layout.activity_select_location);
        init();
        initView();
    }

    private void initView() {
        try {
            locationClient = new LocationClient(getApplicationContext());
            locationClient.registerLocationListener(new MyLocationListener());
            baiduMap = mapView.getMap();
            baiduMap.setMyLocationEnabled(true);
            baiduMap.setOnMapClickListener(onMapClickListener);
            search.setOnGetGeoCodeResultListener(onGetGeoCoderResultListener);
            requestLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        mapSelectLocation = (Button) findViewById(R.id.map_select_location);
        mapLocation = (Button) findViewById(R.id.map_location);
        mapView = (MapView) findViewById(R.id.mapView);
        mapLocation.setOnClickListener(this);
        mapSelectLocation.setOnClickListener(this);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvMainTitle = (TextView) findViewById(R.id.tv_main_title);
        ivBack.setOnClickListener(this);
        tvMainTitle.setText("选择定位");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.map_location:
                Toast.makeText(this, "我的位置", Toast.LENGTH_SHORT).show();
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(currentLocation, 19f);
                baiduMap.animateMapStatus(update);
//                执行点击地图操作
                onMapClickListener.onMapClick(currentLocation);
                break;
            case R.id.map_select_location:
                Intent intent = new Intent();
                intent.putExtra("location", location);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
        }
    }

    public void requestLocation() {
        initLocation();
        //获取定位经纬度
        locationClient.start();
    }

    public void initLocation() {
        LocationClientOption option = new LocationClientOption();
        //设置定位场景，根据定位场景快速生成对应的定位参数
        option.setLocationPurpose(LocationClientOption.BDLocationPurpose.SignIn);
        //设置返回经纬度坐标类型,bd09ll是百度经纬度坐标
        option.setCoorType("bd09ll");
        //设置是否需要地址信息
        option.setIsNeedAddress(true);
        locationClient.setLocOption(option);
    }

    public void navigateTo(BDLocation location) {
        if (isFirst) {
            isFirst = false;
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            //描述地图状态将要发生的变化
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(ll, 19f);
            //以动画方式更新地图状态，默认动画耗时 300 ms
            baiduMap.animateMapStatus(update);
        }
        //定位数据建造器,获取经纬度
        MyLocationData.Builder builder = new MyLocationData.Builder();
        builder.latitude(location.getLatitude());
        builder.longitude(location.getLongitude());
        MyLocationData locationData = builder.build();
        baiduMap.setMyLocationData(locationData);
    }

    private void drawCircle(LatLng point) {
        CircleOptions circle = new CircleOptions().fillColor(0x384d73b3).
                center(point).stroke(new Stroke(3, 0x784d73b3))
                .radius(100).visible(true);
        overlays.add(baiduMap.addOverlay(circle));
    }

    private void setInfoWindow(String message) {
        Button button = new Button(getApplicationContext());
        button.setBackgroundResource(R.drawable.style_ellipse_recycler_white);
        button.setPadding(10, 0, 10, 0);
        button.setText(message);
        button.setTextSize(13f);
        baiduMap.showInfoWindow(new InfoWindow(button, new LatLng(latitude, longitude), -130));
        location = message;
    }

    //    定义地图点击事件
    public BaiduMap.OnMapClickListener onMapClickListener = new BaiduMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            //移除地图基类
            baiduMap.removeOverLays(overlays);
            //根据 Bitmap 创建描述信息
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromBitmap(ImageUtils.getBitmap(R.drawable.ic_location_on));
            //地图覆盖物选型基类获取信息。marker添加动画，设置 marker 覆盖物的位置坐标
            OverlayOptions option = new MarkerOptions().position(latLng)
                    //设置生长动画
                    .icon(bitmap).animateType(MarkerOptions.MarkerAnimateType.grow)
                    //设置 Marker 覆盖物图标的透明度，取值为[0,1]，默认1.0
                    .alpha(1f).visible(true);
            //添加到地图基类
            overlays.add(baiduMap.addOverlay(option));
            //写入信息
            setInfo(latLng.latitude, latLng.longitude);
            //画一个圆表示范围
            drawCircle(latLng);
            //发起反地理编码请求(经纬度->地址信息)
            search.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
        }
        @Override
        public void onMapPoiClick(MapPoi mapPoi) {
            LatLng latLng = mapPoi.getPosition();
            baiduMap.removeOverLays(overlays);
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromBitmap(ImageUtils.getBitmap(R.drawable.ic_location_on));
            //地图覆盖物选型基类
            OverlayOptions option = new MarkerOptions().position(latLng)
                    .icon(bitmap).animateType(MarkerOptions.MarkerAnimateType.grow)
                    .alpha(1f).visible(true);
            overlays.add(baiduMap.addOverlay(option));
            setInfo(latLng.latitude, latLng.longitude);
            drawCircle(latLng);
            setInfoWindow(mapPoi.getName());
        }
    };

    //    获取当前位置的名称
    public OnGetGeoCoderResultListener onGetGeoCoderResultListener = new OnGetGeoCoderResultListener() {
        //地理编码查询结果回调函数
        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
        }
        @Override
        //反地理编码查询结果回调函数
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            //poi信息类
            List<PoiInfo> poiList = reverseGeoCodeResult.getPoiList();
            if (!poiList.isEmpty()) {
                PoiInfo poiInfo = poiList.get(0);
                //得到位置名称、方向、距离
                String text = poiInfo.name + poiInfo.direction + poiInfo.distance + "米";
                setInfoWindow(text);
            }
        }
    };

    //定位我的位置
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            //从GPS或WIFI获取经纬度信息
            if (bdLocation.getLocType() == BDLocation.TypeGpsLocation ||
                    bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
                //将经纬度写入当前位置中
                currentLocation = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                //导航位置
                navigateTo(bdLocation);
            }
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
