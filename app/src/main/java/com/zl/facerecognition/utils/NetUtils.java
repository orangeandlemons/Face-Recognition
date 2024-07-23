package com.zl.facerecognition.utils;


import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zl.facerecognition.Callback;
import com.zl.facerecognition.entity.CommonResult;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;


/**
 * 网络访问类
 */
public class NetUtils {
    /**
     * 访问后台
     * context:上下文,即环境   Map<String, String> params
     * url 相对地址
     */
    public static void request(Context context, String url, Map<String, String> params, Callback callback) {
        //等待弹窗
        BasePopupView loadingView = new XPopup.Builder(context).asLoading("网络请求中").show();
//        String token = SPUtils.getPrefString(context, Consts.TOKEN, "");
//        Message message = new Message();
//        JSONObject jsonObject=new JSONObject();
//        String data=jsonObject.toJSONString(params);
        //如果有token,那么把它放到参数里
    /*    if (ValidateUtil.isStringValid(token)) {
            if (params == null) {
                params = new HashMap<>();
            }
            params.put(Consts.PTOKEN, token);
        }*/
        //使用OKHTTP访问后台
//        OkHttpUtils.postString().url(getUrl(context, url)).mediaType(MediaType.parse("application/json")).content(data).build().execute(new StringCallback() {
        OkHttpUtils.post().url(getUrl(context, url)).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                //关闭等待弹窗
                loadingView.dismiss();
                //弹窗提示
                UiUtils.showError(context, "请求失败1：" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                //关闭等待弹窗
                loadingView.dismiss();
                //将response的Json字符串转化为相应的对象
                CommonResult result = JSON.parseObject(response, CommonResult.class);
                //日志输出
                Log.d("NET-->", String.valueOf(result));
                //请求码不为空的话说明有响应
                if (result.getCode() != null) {
                    //回调接口
                    callback.fun(result);
                } else {
                    //弹窗提示
                    UiUtils.showKnowDialog(context, "请求失败2:", result.getMsg());
                }
            }
        });
    }

    /**
     * 给url加上根地址
     */
    private static String getUrl(Context context, String url) {
        String baseUrl = SPUtils.getPrefString(context, Consts.BASE_URL, "");
        if (ValidateUtil.isStringValid(baseUrl)) {
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
        }
        return baseUrl + url;
    }

}
