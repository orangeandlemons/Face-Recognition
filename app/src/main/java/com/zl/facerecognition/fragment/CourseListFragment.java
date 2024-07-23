package com.zl.facerecognition.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import cz.msebera.android.httpclient.Header;

import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.PathUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.zl.facerecognition.R;
import com.zl.facerecognition.activity.LoginActivity;
import com.zl.facerecognition.adapter.CourseListAdapter;
import com.zl.facerecognition.entity.CommonResult;

import com.zl.facerecognition.popup.AddPopup;
import com.zl.facerecognition.utils.CommonUtil;
import com.zl.facerecognition.utils.Constants;
import com.zl.facerecognition.utils.NetUtils;
import com.zl.facerecognition.utils.UiUtils;
import com.zl.facerecognition.utils.ViewUtils;
import com.zl.facerecognition.viewModel.CourseListViewModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class CourseListFragment extends Fragment implements View.OnClickListener {

    private SwipeRefreshLayout fragmentCourseRefresh;
    private EditText search;
    private TextView courseListPrompt;
    private RecyclerView recyclerCourseList;
    private LinearLayout contentNotFoundLayout;
    private FloatingActionButton courseCreateButton;
    private CourseListViewModel viewModel;



    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private String userType;
    private String id;
    public String picDir;
    public String picPath;
    public UpfacePopup pop;
//    public Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_courselist, container, false);
        //fragment抓控件两种方法：1.view.

        fragmentCourseRefresh = view.findViewById(R.id.fragment_course_refresh);
        //2.getActivity()
        search = view.findViewById(R.id.search);
        courseListPrompt = view.findViewById(R.id.course_list_prompt);
        recyclerCourseList = view.findViewById(R.id.recycler_course_list);
        contentNotFoundLayout = view.findViewById(R.id.content_not_found_layout);
        courseCreateButton = view.findViewById(R.id.course_create_button);
        courseCreateButton.setOnClickListener(this);

        //创建存储图片的临时文件地址
        picDir = PathUtils.getExternalAppPicturesPath();
        FileUtils.createOrExistsDir(picDir);
        picPath = picDir + "/temp.png";

        CommonUtil.initPhotoError();

        //进行网络请求返回所有与用户相关的课程
        SharedPreferences preferences = getActivity().getSharedPreferences("localRecord", Context.MODE_PRIVATE);
        userType = preferences.getString("userType", "");
        id = preferences.getString("id", "");
        if (userType.equals("2")) {
            String face = preferences.getString("face", "");
            uploadFace(face);
        }
        //下拉刷新的颜色
        fragmentCourseRefresh.setColorSchemeColors(ViewUtils.getRefreshColor());

        Map<String, String> params = new HashMap<>();
        if (userType.equals("2")) {
            courseListPrompt.setText("我学的课");
            params.put("studentId", id);
            NetUtils.request(getContext(), Constants.FIND_COURSE_BYSTUID, params, (CommonResult result) -> {
                //TODO:测试
                if (!result.getMsg().equals("")) {
                    viewModel.updateCourses(result.getData());
                }
//                Toast.makeText(getActivity(), result.getMsg(), Toast.LENGTH_SHORT).show();

                fragmentCourseRefresh.setRefreshing(false);
            });

            //刷新
            fragmentCourseRefresh.setOnRefreshListener(() -> {
                fragmentCourseRefresh.setRefreshing(false);
                NetUtils.request(getContext(), Constants.FIND_COURSE_BYSTUID, params, (CommonResult result) -> {
                    if (result.getMsg() != null) {
                        viewModel.updateCourses(result.getData());
                    }
                    Toast.makeText(getActivity(), result.getMsg(), Toast.LENGTH_SHORT).show();
                    System.out.println(result.getMsg());
                    fragmentCourseRefresh.setRefreshing(false);
                });
            });

            //搜索
            search.setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    String name = search.getText().toString();
                    Map<String, String> map = new HashMap<>();
                    map.put("studentId", id);
                    map.put("name", name);
                    NetUtils.request(getContext(), Constants.FIND_COURSE_BYSTUIDNA, map, result -> {
                        if (!result.getMsg().equals("")) {
                            viewModel.updateCourses(result.getData());
                        }
                        Toast.makeText(getActivity(), result.getMsg(), Toast.LENGTH_SHORT).show();
                        System.out.println(result.getMsg());
                        fragmentCourseRefresh.setRefreshing(false);
                    });
                }
                return false;
            });
        } else {
            courseListPrompt.setText("我教的课");
            params.put("teacherId", preferences.getString("id", ""));
            NetUtils.request(getContext(), Constants.FIND_COURSE_BYTEAID, params, (CommonResult result) -> {
                //TODO:测试
                if (!result.getMsg().equals("")) {
                    viewModel.updateCourses(result.getData());
                }
                Toast.makeText(getActivity(), result.getMsg(), Toast.LENGTH_SHORT).show();
                System.out.println(result.getMsg());
                fragmentCourseRefresh.setRefreshing(false);
            });
            fragmentCourseRefresh.setOnRefreshListener(() -> {
                fragmentCourseRefresh.setRefreshing(false);
                NetUtils.request(getContext(), Constants.FIND_COURSE_BYTEAID, params, (CommonResult result) -> {
                    //TODO:测试
                    if (!result.getMsg().equals("")) {
                        viewModel.updateCourses(result.getData());
                    }
                    Toast.makeText(getActivity(), result.getMsg(), Toast.LENGTH_SHORT).show();
                    System.out.println(result.getMsg());
                    fragmentCourseRefresh.setRefreshing(false);

                });
            });
            search.setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    String name = search.getText().toString();
                    Map<String, String> map = new HashMap<>();
                    map.put("teacherId", id);
                    map.put("name", name);
                    NetUtils.request(getContext(), Constants.FIND_COURSE_BYTEAIDNA, map, (CommonResult result) -> {
                        if (!result.getMsg().equals("")) {
                            viewModel.updateCourses(result.getData());
                        }
                        Toast.makeText(getActivity(), result.getMsg(), Toast.LENGTH_SHORT).show();
                        System.out.println(result.getMsg());
                        fragmentCourseRefresh.setRefreshing(false);
                    });
                }
                return false;
            });
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CourseListViewModel.class);
        viewModel.getCourseLists().observe(getViewLifecycleOwner(), courseLists -> {
            if (courseLists.size() < 1) {
                contentNotFoundLayout.setVisibility(View.VISIBLE);
            } else {
                contentNotFoundLayout.setVisibility(View.GONE);
                ViewUtils.setRecycler(getActivity(), R.id.recycler_course_list, new CourseListAdapter(courseLists));
            }
        });
    }

    public void uploadFace(String face) {
        if (face.equals("")){
            pop=new UpfacePopup(getContext());
//            new XPopup.Builder(getContext()).asCustom(pop);

            pop.setNoClickedListener(view -> {
                pop.dismiss();
                BasePopupView tishi=new XPopup.Builder(getContext()).asConfirm("提示",
                            "未注册人脸信息，将返回登录",
                            "", "知道了",
                            new OnConfirmListener() {
                                @Override
                                public void onConfirm() {
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            }, new OnCancelListener() {
                                @Override
                                public void onCancel() {
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            }, true).show();
            });

            pop.setSelectedClickedListener(view -> {
                /**
                 * 跳转到相机界面，在拍完照之后跳转到剪切界面剪切完成后返回到dialog并将照片显示在预览中
                 */
                openCamera();
            });

            pop.setYesClickedListener(view -> {
                //执行上传操作
                if (pop.getPicturePath() == null){
                    Toast.makeText(view.getContext(), "未选择图片", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    sendFace(new File(picPath));
                } catch (FileNotFoundException e){
                    Toast.makeText(view.getContext(), "未找到文件", Toast.LENGTH_SHORT).show();
                }
            });
            pop.show();
        }
    }

    public void sendFace(File file) throws FileNotFoundException {
        BasePopupView loadingView = new XPopup.Builder(getContext()).asLoading("人脸信息上传中").show();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("photo",file);
        params.put("type","4");
        params.put("id",String.valueOf(id));
        client.post(Constants.SERVICE_PATH + "/document/saveImage", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject object = JSON.parseObject(new String(responseBody));
                String face = object.getString("msg");
                Map<String, String> map = new HashMap<>();
                map.put("face",face);
                map.put("studentId",id);
                NetUtils.request(getContext(),"/account/modifyStudent",map,result->{
                    if (result.getCode().equals("200")){
                        loadingView.dismiss();
                        UiUtils.showSuccess(getContext(),"上传成功");
                        getActivity().getSharedPreferences("localRecord",Context.MODE_PRIVATE).edit().putString("face",face).apply();
                        pop.dismiss();
                    } else {
                        UiUtils.showError(getContext(),result.getMsg());
                    }
                    return ;
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                UiUtils.showError(getContext(),"图片上传失败，请重试");
                loadingView.dismiss();
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (userType.equals("1")) {
            new XPopup.Builder(v.getContext()).asCustom(new CreatPopup(v.getContext())).show();
            setChooseClickListener(this::openAlbum);
        } else {
            //加入课程
            //做XPopup还是做Dialog，这是个问题
            new XPopup.Builder(v.getContext()).asCustom(new AddPopup(v.getContext(),id)).show();
//            CourseAddDialog addDialog = new CourseAddDialog(v.getContext(), id);
//            addDialog.show();


        }
    }

    private static onChooseClickListener chooseClickListener;

    public interface onChooseClickListener {
        public void onChooseClick();
    }

    public void setChooseClickListener(onChooseClickListener chooseClickListener) {
        this.chooseClickListener = chooseClickListener;
    }

    public static String imgPath;

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public static class CreatPopup extends BottomPopupView implements View.OnClickListener {
        private TextView tv_No;
        private TextView tv_Yes;
        private EditText et_Name;
        private EditText et_Introduce;
        private TextView tv_AvatarChoose;
        private TextView tv_AvatarDefault;
        public static ImageView iv_AvatarPreview;
        public static TextView tv_AvatarState;


        public CreatPopup(Context context) {
            super(context);
        }


        /**
         * 具体实现的类的布局
         *
         * @return
         */
        protected int getImplLayoutId() {
            return R.layout.activity_course_create;
        }

        @Override
        protected void onCreate() {
            super.onCreate();
            tv_No = findViewById(R.id.course_register_no);
            tv_Yes = findViewById(R.id.course_register_yes);
            et_Name = findViewById(R.id.course_register_name);
            et_Introduce = findViewById(R.id.course_register_introduce);
            tv_AvatarChoose = findViewById(R.id.course_register_avatar_choose);
            tv_AvatarDefault = findViewById(R.id.course_register_avatar_default);
            iv_AvatarPreview = findViewById(R.id.course_register_avatar_preview);
            tv_AvatarState = findViewById(R.id.course_register_avatar_state);
            tv_No.setOnClickListener(this);
            tv_Yes.setOnClickListener(this);
            tv_AvatarDefault.setOnClickListener(this);
            tv_AvatarChoose.setOnClickListener(this);

            et_Introduce.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    KeyboardUtils.showSoftInput();
                } else {
                    KeyboardUtils.hideSoftInput(v);
                }
            });

            et_Name.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    KeyboardUtils.showSoftInput();
                } else {
                    KeyboardUtils.hideSoftInput(v);
                }
            });


        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.course_register_no:
                    dismiss();
                    break;
                case R.id.course_register_yes:
                    //执行创建课程操作
                    String name = et_Name.getText().toString();
                    String introduce = et_Introduce.getText().toString();
                    if (name.isEmpty() || introduce.isEmpty()) {
                        Toast.makeText(v.getContext(), "请补全信息", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (imgPath == null) {
                        Toast.makeText(v.getContext(), "请选择课程封面", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    SharedPreferences preferences = v.getContext().getSharedPreferences("localRecord", Context.MODE_PRIVATE);
                    String id = preferences.getString("id", "");
                    Map<String, String> map = new HashMap<>();
                    map.put("teacherId", id);
                    map.put("name", name);
                    map.put("avatar", imgPath);
                    map.put("introduce", introduce);
                    NetUtils.request(v.getContext(), Constants.ADDCOURSE, map, result -> {
                        if (!result.getMsg().equals("")) {
                            dismiss();
                            UiUtils.showText(v.getContext(), "课程验证码为：" + result.getData());
                        } else {
                            UiUtils.showText(v.getContext(), result.getMsg());
                        }
                    });
//                    UiUtils.showText(v.getContext(), "创建课程", "请求执行中……");
                    break;
                case R.id.course_register_avatar_default:
                    imgPath = "/image/coursedefault.png";
                    Toast.makeText(v.getContext(), "使用默认头像", Toast.LENGTH_SHORT).show();
                    Picasso.with(v.getContext())
                            .load(Constants.SERVICE_PATH+imgPath)
                            .error(R.drawable.ic_net_error)
                            .fit().into(iv_AvatarPreview, new Callback() {
                        @Override
                        public void onSuccess() {
                            tv_AvatarState.setTextColor(ColorUtils.getColor(R.color.green));
                            tv_AvatarState.setText("选取成功");
                        }

                        @Override
                        public void onError() {
                            tv_AvatarState.setTextColor(ColorUtils.getColor(R.color.red1));
                            tv_AvatarState.setText("网络异常！图片无法加载");
                        }
                    });
                    break;
                case R.id.course_register_avatar_choose:
                    if (chooseClickListener != null) {
                        chooseClickListener.onChooseClick();
                    }
                    break;

            }
        }
    }

    public void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, 151);
    }
//TODO
    public void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(picPath)));
        startActivityForResult(intent, 161);

    }

    /**
     * 调用系统的裁剪功能
     *弃用,使用android-crop依赖
     * @param
     */
    /*private void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 500);
        intent.putExtra("outputY", 500);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 520);
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        File tempFile = new File(picPath);
        switch (requestCode) {
            case 151:
                if (resultCode == RESULT_OK) {
//                    cropPhoto(Uri.fromFile(tempFile));
                    //调用crop需要加权限
                    Crop.of(data.getData(),Uri.fromFile(tempFile)).asSquare().withAspect(500,500).start(getContext(),this);
                }
                break;
            case 161:
                if (resultCode == RESULT_OK) {
//                    cropPhoto(Uri.fromFile(tempFile));
                    Crop.of(Uri.fromFile(tempFile),Uri.fromFile(tempFile)).asSquare().withAspect(500,500).start(getContext(),this);
                }
                break;
            case Crop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    try {
                        if (userType.equals("1")) {
                            sendImage(tempFile);
                        } else {
                            //TODO 人脸上传
                            pop.facePreview.setImageURI(Uri.fromFile(tempFile));
                            pop.setPicturePath(picPath);
                            pop.invisibleButton();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;

        }
    }


    //文件上传
    public void sendImage(File file) throws FileNotFoundException {
        BasePopupView loadingView = new XPopup.Builder(getContext()).asLoading("文件正在上传中").show();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("photo",file);
        params.put("type","3");
        params.put("id","temp");
        client.post(Constants.SERVICE_PATH+"/document/saveImage", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                loadingView.dismiss();
                UiUtils.showSuccess(getContext(), "文件上传成功");
                JSONObject parseObject = JSON.parseObject(new String(responseBody));
                setImgPath(parseObject.getString("msg"));
                CreatPopup.iv_AvatarPreview.setImageBitmap(ImageUtils.getBitmap(new File(picPath)));
                CreatPopup.tv_AvatarState.setTextColor(ColorUtils.getColor(R.color.green));
                CreatPopup.tv_AvatarState.setText("选取成功");
            }
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                loadingView.dismiss();
                UiUtils.showError(getContext(),"文件上传失败");
                CreatPopup.iv_AvatarPreview.setImageBitmap(ImageUtils.getBitmap(R.drawable.ic_net_error));
                CreatPopup.tv_AvatarState.setTextColor(ColorUtils.getColor(R.color.red));
                CreatPopup.tv_AvatarState.setText("上传失败");
            }
        });


    }

    public static class UpfacePopup extends Dialog implements View.OnClickListener{
        private TextView no;
        private TextView yes;
        private TextView faceCollection;
        public  ImageView facePreview;
        private String picturePath;


        public UpfacePopup(Context context) {
            super(context,R.style.dialog);
            View view = LayoutInflater.from(context).inflate(R.layout.activity_up_face, null);
            setContentView(view);
            setCancelable(false);
            setCanceledOnTouchOutside(false);
        }
//        /**
//         * 具体实现的类的布局
//         *
//         * @return
//         */
//        protected int getImplLayoutId() {
//            return R.layout.activity_up_face;
//        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            no = (TextView) findViewById(R.id.no);
            yes = (TextView) findViewById(R.id.yes);
            faceCollection = (TextView) findViewById(R.id.face_collection);
            facePreview = (ImageView) findViewById(R.id.face_preview);
            no.setOnClickListener(this);
            yes.setOnClickListener(this);
            faceCollection.setOnClickListener(this);
        }
        private onNoClickedListener noClickedListener;
        private onYesClickedListener yesClickedListener;
        private onSelectedClickedListener selectedClickedListener;

        public String getPicturePath() {
            return picturePath;
        }

        public  void setPicturePath(String picturePath) {
            this.picturePath = picturePath;
        }

        public void setNoClickedListener(onNoClickedListener noClickedListener) {
            this.noClickedListener = noClickedListener;
        }

        public void setYesClickedListener(onYesClickedListener yesClickedListener) {
            this.yesClickedListener = yesClickedListener;
        }

        public void setSelectedClickedListener(onSelectedClickedListener selectedClickedListener) {
            this.selectedClickedListener = selectedClickedListener;
        }

        public  void invisibleButton(){
            findViewById(R.id.face_collection).setVisibility(View.INVISIBLE);
        }
        public interface onNoClickedListener{
            void onNoClicked(View view);
        }

        public interface onYesClickedListener{
            void onYesClicked(View view);
        }

        public interface onSelectedClickedListener{
            void onSelectedClicked(View view);
        }
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.yes:
                    yesClickedListener.onYesClicked(v);
                    break;
                case R.id.no:
                    noClickedListener.onNoClicked(v);
                    break;
                case R.id.face_collection:
                    selectedClickedListener.onSelectedClicked(v);
                    break;

            }
        }
        @Override
        public void show() {
            super.show();
            ViewUtils.show(getWindow());
        }
    }
}


