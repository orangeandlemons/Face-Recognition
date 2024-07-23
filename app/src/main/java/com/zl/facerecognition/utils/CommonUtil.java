package com.zl.facerecognition.utils;

import android.os.StrictMode;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {
    public static Map<String,String> object2Map(Object object){
        Map<String,String> params=new HashMap<>();
        //获得类的的属性名 数组
        Field[]fields=object.getClass().getDeclaredFields();
        try {


            for (Field field : fields) {
                field.setAccessible(true);
                String name = field.getName();
                params.put(name, String.valueOf(field.get(object)));

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return params;
    }


    /**
     * 将long转换成时长
     * @param time long型的时长
     * @return 考勤持续时长
     */
    public static String longString(long time){
        int day = (int) time / (1000 * 3600 * 24);
        time = time % (1000 * 3600 * 24);
        int hour = (int) time / (1000 * 3600);
        time = time % (1000 * 3600);
        int minute = (int) time / (1000 * 60);
        String result = "";
        if (day > 0){
            result = result + day + "天";
        }
        if (hour > 0){
            result = result + hour + "小时";
        }
        if (minute > 0){
            result = result + minute + "分钟";
        }
        return result;
    }

    /**
     * 验证手机号码
     * @param mobiles
     * @return
     */
    public static boolean isPhone(String mobiles){
        boolean flag = false;
        try{
            String pattern = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(mobiles);
            flag = m.matches();
        }catch(Exception e){
            e.printStackTrace();
        }
        return flag;
    }


    public static void initPhotoError(){
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }
}
