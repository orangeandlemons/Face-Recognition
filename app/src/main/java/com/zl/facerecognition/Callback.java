package com.zl.facerecognition;

import com.zl.facerecognition.entity.CommonResult;

/**
 * 网络访问类的回调接口
 */
public interface Callback {
    /*网络访问类必须执行的方法*/
    void fun(CommonResult map);
}
