package com.zl.facerecognition.entity;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zl.facerecognition.utils.ValidateUtil;

/**
 * 网络访问返回的对象
 */
public class CommonResult {
    private String code;//状态
    private String msg;  //描述
    private String token;   //临时表及，表明用户登陆了
    private String data;//数据对象
    private JSONArray rows;     //列表
    private JSONArray value;    //列表
    private int total;      //array的数量

    @Override
    public String toString() {
        return "CommonResult{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", token='" + token + '\'' +
                ", data=" + data +
                ", rows=" + rows +
                ", value=" + value +
                ", total=" + total +
                '}';
    }

    //row转换字符串
    public String getRowsString(){
        return ValidateUtil.isJaValid(rows)?JSON.toJSONString(rows):"";
    }

    public String getValueString(){
        return ValidateUtil.isJaValid(value)?JSON.toJSONString(value):"";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public JSONArray getRows() {
        return rows;
    }

    public void setRows(JSONArray rows) {
        this.rows = rows;
    }

    public JSONArray getValue() {
        return value;
    }

    public void setValue(JSONArray value) {
        this.value = value;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
