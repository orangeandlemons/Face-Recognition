package com.zl.facerecognition.utils;

/**
 * 和后台的接口地址 p10.12:25
 */
public class Constants {
    /*登录验证*/
    public static String LOGIN= "/account/login";
    /*验证账号是否已存在*/
    public static String CHECK_ACCOUNT= "/account/confirmAccount";
    /*学生注册*/
    public static String REGISTER= "/account/addStudent";
    /*找回密码*/
    public static String MODIFY= "/account/modify";
    /*根据学生学号查找课程*/
    public static String FIND_COURSE_BYSTUID= "/course/findCourseByStudentId";
    /*根据学生学号和课程名查找课程*/
    public static String FIND_COURSE_BYSTUIDNA= "/course/findCourseByStudentIdWithName";
    /*根据教师学号姓名查找课程*/
    public static String FIND_COURSE_BYTEAID= "/course/findCourseByTeacherId";
    /*根据教师学号和课程名查找课程*/
    public static String FIND_COURSE_BYTEAIDNA= "/course/findCourseByTeacherIdWithName";
    /*添加课程*/
    public static String ADDCOURSE= "/course/addCourse";
    /*保存图像*/
    public static String SAVE_IMAGE= "/document/saveImage";
    //baseURl
    public static final String SERVICE_PATH = "http://192.168.0.108:8111";
    //加入课程
    public static final String FIND_COU_BYCODE = "/course/findCourseByCode";

    //加入课程
    public static final String ADD_COURSE_STUDENT = "/courseStudent/addCourseStudent";

    //退出登录
    public static final String EXIT_APP = "exit_app";

}
