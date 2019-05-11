package com.test.RongYun.activity;

import android.Manifest;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.google.gson.Gson;
import com.test.RongYun.R;
import com.test.RongYun.base.BaseActivity;
import com.test.RongYun.base.MyVolley;
import com.test.RongYun.protocol.GetCodeProtocol;
import com.test.RongYun.protocol.RegisterProtocol;
import com.test.RongYun.response.GetCodeResponse;
import com.test.RongYun.response.ImageResponse;
import com.test.RongYun.utils.BitmapUtils;
import com.test.RongYun.utils.Constants;
import com.test.RongYun.utils.DialogUtils;
import com.test.RongYun.utils.DownTimer;
import com.test.RongYun.utils.DownTimerListener;
import com.test.RongYun.utils.LogUtils;
import com.test.RongYun.utils.MD5Utils;
import com.test.RongYun.utils.MobileUtils;
import com.test.RongYun.utils.NToast;
import com.test.RongYun.utils.SharedPrefrenceUtils;
import com.test.RongYun.utils.UIUtils;
import com.test.RongYun.widget.ClearWriteEditText;
import com.test.RongYun.widget.LoadDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import io.rong.common.RLog;
import io.rong.imkit.RongConfigurationManager;
import io.rong.imkit.utilities.LangUtils;
import io.rong.imlib.RongIMClient;

/**
 * Created by AMing on 16/1/14.
 * Company RongCloud
 */
@SuppressWarnings("deprecation")
public class RegisterActivity extends BaseActivity implements View.OnClickListener, DownTimerListener {
    private final String TAG = RegisterActivity.class.getSimpleName();
    private final int REQUEST_CODE_SELECT_COUNTRY = 1;

    private static final int CHECK_PHONE = 1;
    private static final int SEND_CODE = 2;
    private static final int VERIFY_CODE = 3;
    private static final int REGISTER = 4;
    private static final int REGISTER_BACK = 1001;
    private ClearWriteEditText mPhoneEdit, mCodeEdit, mNickEdit, mPasswordEdit;
    private Button mGetCode, mConfirm;
    private String mPhone, mCode, mNickName, mPassword, mCodeToken;
    private boolean isRequestCode = false;
    private String mCountryNameCN, mCountryNameEN;
    private String mRegion;
    private LayoutInflater mInflater;
    private File mFile;
    private String timepath;
    private ImageView de_login_logo;

    private static final String IMAGE_UNSPECIFIED = "image/*";

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private List<Bitmap> bitmapList;
    /**
     * 照片参数
     */
    private static final int PHOTO_GRAPH = 10;// 拍照
    private static final int PHOTO_ZOOM = 20; // 缩放
    // 图片储存成功后
    protected static final int INTERCEPT = 30;
    private String path;
    /**
     * 照相选择界面
     */
    private PopupWindow pWindow;
    private View root;
    private LinearLayout ll_main;
    private String portraitUri;
    private Bitmap new_photo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setHeadVisibility(View.GONE);
        if (mInflater == null) {
            mInflater = (LayoutInflater) getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
        }
        initDate();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }



    private void initDate() {
        ll_main=(LinearLayout)findViewById(R.id.main);
        mPhoneEdit = (ClearWriteEditText) findViewById(R.id.reg_phone);
        mCodeEdit = (ClearWriteEditText) findViewById(R.id.reg_code);
        mNickEdit = (ClearWriteEditText) findViewById(R.id.reg_username);
        mPasswordEdit = (ClearWriteEditText) findViewById(R.id.reg_password);
        mGetCode = (Button) findViewById(R.id.reg_getcode);
        mConfirm = (Button) findViewById(R.id.reg_button);
        de_login_logo=(ImageView )findViewById(R.id.de_login_logo);

        mGetCode.setOnClickListener(this);
        mGetCode.setClickable(false);
        mConfirm.setOnClickListener(this);

        TextView goLogin = (TextView) findViewById(R.id.reg_login);
        TextView goForget = (TextView) findViewById(R.id.reg_forget);
        goLogin.setOnClickListener(this);
        goForget.setOnClickListener(this);



        addEditTextListener();

        LangUtils.RCLocale appLocale = LangUtils.getAppLocale(this);
        //拍照
        root = mInflater.inflate(R.layout.alert_dialog, null);
        pWindow = new PopupWindow(root, ActionBar.LayoutParams.FILL_PARENT,
                ActionBar.LayoutParams.FILL_PARENT);
        root.findViewById(R.id.btn_Phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pWindow.dismiss();
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_UNSPECIFIED);
                startActivityForResult(intent, PHOTO_ZOOM);
            }
        });
        root.findViewById(R.id.btn_TakePicture)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pWindow.dismiss();
                        timepath = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                            if (!dir.exists()) {
                                dir.mkdir();
                            }
                            mFile = new File(dir, timepath + ".jpg");
                            if (ContextCompat.checkSelfPermission(RegisterActivity.this,
                                    Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED) {

                                ActivityCompat.requestPermissions(RegisterActivity.this,
                                        new String[]{Manifest.permission.CAMERA},
                                        MY_PERMISSIONS_REQUEST_CAMERA);
                            } else {
                                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
                                        MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile)), PHOTO_GRAPH);
                            }
                        }
                    }
                });
        root.findViewById(R.id.bg_photo).getBackground().setAlpha(100);

        root.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pWindow.dismiss();
            }
        });
        de_login_logo.setOnClickListener(this);

    }
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case INTERCEPT:
                    addBitmap(new_photo);
                    uploadImage();
                    break;

            }
        }
    };

    /**
     * 上传图片
     */
    private void uploadImage() {
        String url = Constants.SERVER_URL + "/common/uploadImg";
        Map<String, String> paramMap = new HashMap<String, String>();

        Map<String, String> filesMap = new HashMap<String, String>();
        filesMap.put("picFile", path);
        MyVolley.uploadWithFileWholeUrl(url, paramMap, filesMap, null, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                Gson gson = new Gson();
                ImageResponse imageResponse = gson.fromJson(json, ImageResponse.class);

                LogUtils.e("baseResponse:" + json.toString());
                if (imageResponse.code == 0) {

                    portraitUri=imageResponse.picUrl;
                    UIUtils.showToastCenter(RegisterActivity.this, "上传成功");
                } else {
                    DialogUtils.showAlertDialog(RegisterActivity.this,
                            imageResponse.msg);

                }

            }

            @Override
            public void dealWithError(String address, String error) {
                DialogUtils.showAlertDialog(RegisterActivity.this,
                        error);
            }
            @Override
            public void dealTokenOverdue() {
                DialogUtils.showAlertDialog(RegisterActivity.this,
                        "登录超时，请重新登录！");
            }
        });

    }

    private void addBitmap(Bitmap bitmap){
        de_login_logo.setImageBitmap(bitmap);
    }
    public Uri geturi(android.content.Intent intent) {
        Uri uri = intent.getData();
        String type = intent.getType();
        if (uri.getScheme().equals("file") && (type.contains("image/"))) {
            String path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = this.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=")
                        .append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[] { MediaStore.Images.ImageColumns._ID },
                        buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    // set _id value
                    index = cur.getInt(index);
                }
                if (index == 0) {
                    // do nothing
                } else {
                    Uri uri_temp = Uri
                            .parse("content://media/external/images/media/"
                                    + index);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }
        return uri;
    }
    private void addEditTextListener() {
        mPhoneEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && isBright) {
                    mPhone = s.toString().trim();
                    mGetCode.setClickable(true);
                    mGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_blue));
                } else {
                    mGetCode.setClickable(false);
                    mGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_gray));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mCodeEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.length() == 6) {
//                    AMUtils.onInactive(RegisterActivity.this, mCodeEdit);
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mPasswordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 5) {
                    mConfirm.setClickable(true);
                    mConfirm.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_blue));
                } else {
                    mConfirm.setClickable(false);
                    mConfirm.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_gray));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }






    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.de_login_logo:{
                pWindow.setAnimationStyle(R.style.AnimBottom);
                pWindow.showAtLocation(ll_main,
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            }
            case R.id.reg_login:
                toLogin();
                break;
            case R.id.reg_forget:
//                startActivity(new Intent(this, ForgetPasswordActivity.class));
                finish();
                break;
            case R.id.reg_getcode:
                if (TextUtils.isEmpty(mPhoneEdit.getText().toString().trim())) {
                    NToast.longToast(RegisterActivity.this, R.string.phone_number_is_null);
                } else {
                    LoadDialog.show(RegisterActivity.this);
                    mGetCode.setClickable(false);
                    mGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_gray));
                    runGetGode();
                }
                break;
            case R.id.reg_button:
                mPhone = mPhoneEdit.getText().toString().trim();
                mCode = mCodeEdit.getText().toString().trim();
                mNickName = mNickEdit.getText().toString().trim();
                mPassword = mPasswordEdit.getText().toString().trim();


                if (TextUtils.isEmpty(mNickName)) {
                    NToast.shortToast(RegisterActivity.this, getString(R.string.name_is_null));
                    mNickEdit.setShakeAnimation();
                    return;
                }
                if (mNickName.contains(" ")) {
                    NToast.shortToast(RegisterActivity.this, getString(R.string.name_contain_spaces));
                    mNickEdit.setShakeAnimation();
                    return;
                }

                if (TextUtils.isEmpty(mPhone)) {
                    NToast.shortToast(RegisterActivity.this, getString(R.string.phone_number_is_null));
                    mPhoneEdit.setShakeAnimation();
                    return;
                }
                if (TextUtils.isEmpty(mCode)) {
                    NToast.shortToast(RegisterActivity.this, getString(R.string.code_is_null));
                    mCodeEdit.setShakeAnimation();
                    return;
                }
                if (TextUtils.isEmpty(mPassword)) {
                    NToast.shortToast(RegisterActivity.this, getString(R.string.password_is_null));
                    mPasswordEdit.setShakeAnimation();
                    return;
                }
                if (mPassword.contains(" ")) {
                    NToast.shortToast(RegisterActivity.this, getString(R.string.password_cannot_contain_spaces));
                    mPasswordEdit.setShakeAnimation();
                    return;
                }

//                if (!isRequestCode) {
//                    NToast.shortToast(RegisterActivity.this, getString(R.string.not_send_code));
//                    return;
//                }

                LoadDialog.show(RegisterActivity.this);
                runRegister();

                break;

        }
    }


    private void toLogin(){
        Intent loginIntent = new Intent(this, LoginActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntent(loginIntent);
        taskStackBuilder.startActivities();
    }

    boolean isBright = true;



//    @Override
//    public void onFinish() {
//        mGetCode.setText(R.string.get_code);
//        mGetCode.setClickable(true);
//        mGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_blue));
//        isBright = true;
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_SELECT_COUNTRY && resultCode == RESULT_OK){
            String zipCode = data.getStringExtra("zipCode");
            String countryName = data.getStringExtra("countryName");
            String countryNameCN = data.getStringExtra("countryNameCN");
            String countryNameEN = data.getStringExtra("countryNameEN");

            mCountryNameCN = countryNameCN;
            mCountryNameEN = countryNameEN;
        }
        // 拍照
        if (requestCode == PHOTO_GRAPH) {

            // 设置文件保存路径
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            path = dir + "/" + timepath + ".jpg";
            File file = new File(path);
            String img_path = path;
            if (file.exists()) {
                new Thread() {
                    public void run() {
                        try {
                            Bitmap photo = BitmapUtils.getSmallBitmap(path);
                            if (photo != null) {
                                Log.e("photo","photo:"+photo);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                new_photo = BitmapUtils.rotateBitmapByDegree(photo, BitmapUtils.getBitmapDegree(path));


                                String suoName = new SimpleDateFormat("yyyyMMdd_HHmmss")
                                        .format(new Date());
                                path = BitmapUtils.saveMyBitmap(suoName, new_photo);

                                handler.sendEmptyMessage(INTERCEPT);

                            }
                        } catch (Exception e) {
                            Log.e("photo","photo:进"+e.getMessage().toString());
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    ;
                }.start();
                //通知相册刷新
                Uri uriData = Uri.parse("file://" + img_path);
                UIUtils.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uriData));
            }

        }

        // 读取相册缩放图片
        if (requestCode == PHOTO_ZOOM && data != null) {
            if (data.getData() != null) {
                // 图片信息需包含在返回数据中
                String[] proj = {MediaStore.Images.Media.DATA};
                Uri uri = data.getData();
                if(uri==null){
                    geturi(getIntent());
                }
                // 获取包含所需数据的Cursor对象
                @SuppressWarnings("deprecation")
                Cursor cursor = null;
                try {
                    cursor = managedQuery(uri, proj, null, null, null);
                    if (cursor == null) {
                        uri = BitmapUtils.getPictureUri(data, RegisterActivity.this);
                        cursor = managedQuery(uri, proj, null, null, null);
                    }
                    if (cursor != null) {
                        // 获取索引
                        int photocolumn = cursor
                                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        // 将光标一直开头
                        cursor.moveToFirst();
                        // 根据索引值获取图片路径
                        path = cursor.getString(photocolumn);
                    } else {
                        path = uri.getPath();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        if (MobileUtils.getSDKVersionNumber() < 14) {
                            cursor.close();
                        }
                    }
                }


                if (!TextUtils.isEmpty(path)) {

                    new Thread() {
                        public void run() {
                            try {
                                Bitmap photo = BitmapUtils.getSmallBitmap(path);
                                if (photo != null) {
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    new_photo = BitmapUtils.rotateBitmapByDegree(photo, BitmapUtils.getBitmapDegree(path));

                                    String suoName = new SimpleDateFormat("yyyyMMdd_HHmmss")
                                            .format(new Date());
                                    path = BitmapUtils.saveMyBitmap(suoName, new_photo);
                                    handler.sendEmptyMessage(INTERCEPT);


                                }

                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }

            }
        }
    }

    //获取验证码
    public void runGetGode() {
        GetCodeProtocol getCodeProtocol = new GetCodeProtocol();
        HashMap<String,String> map=new HashMap<>();
        String url = getCodeProtocol.getApiFun();
        map.put("phoneNumber", mPhone);
        map.put("type", "1");
        MyVolley.uploadNoFile(MyVolley.POST, url, map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LoadDialog.dismiss(mContext);
                Gson gson = new Gson();
                GetCodeResponse getCodeResponse = gson.fromJson(json, GetCodeResponse.class);
                LogUtils.e("appSendMsgResponse:" + getCodeResponse.toString());
                if (getCodeResponse.code == 0) {
                    isRequestCode=true;
                    DownTimer downTimer = new DownTimer();
                    downTimer.setListener(RegisterActivity.this);
                    downTimer.startDown(60 * 1000);
                    NToast.shortToast(RegisterActivity.this, R.string.messge_send);
                } else {
                    NToast.shortToast(RegisterActivity.this, getCodeResponse.msg);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                NToast.shortToast(RegisterActivity.this,error);
                LoadDialog.dismiss(mContext);
            }

            @Override
            public void dealTokenOverdue() {
                LoadDialog.dismiss(mContext);
            }
        });
    }

    //获取注册
    public void runRegister() {

        RegisterProtocol registerProtocol = new RegisterProtocol();
        HashMap<String,String> map=new HashMap<>();
        String url = registerProtocol.getApiFun();
        map.put("name", mNickName);
        map.put("portraitUri", portraitUri);
        map.put("phoneNumber", mPhone);
        map.put("code", mCode);
        map.put("pwd", MD5Utils.MD5(mPassword));

        MyVolley.uploadNoFile(MyVolley.POST, url, map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                GetCodeResponse getCodeResponse = gson.fromJson(json, GetCodeResponse.class);
                LogUtils.e("appSendMsgResponse:" + getCodeResponse.toString());
                LoadDialog.dismiss(mContext);
                if (getCodeResponse.code == 0) {
                    DownTimer downTimer = new DownTimer();
                    downTimer.setListener(RegisterActivity.this);
                    downTimer.startDown(60 * 1000);
                    NToast.shortToast(RegisterActivity.this, R.string.messge_send);
                } else {
                    NToast.shortToast(RegisterActivity.this, getCodeResponse.msg);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                NToast.shortToast(RegisterActivity.this,error);
                LoadDialog.dismiss(mContext);
            }

            @Override
            public void dealTokenOverdue() {
                LoadDialog.dismiss(mContext);
            }
        });
    }


    @Override
    public void onTick(long millisUntilFinished) {
        mGetCode.setText(String.valueOf(millisUntilFinished / 1000) + "s");
        mGetCode.setClickable(false);
        mGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_gray));
        isBright = false;
    }

    @Override
    public void onFinish() {
        mGetCode.setText(R.string.get_code);
        mGetCode.setClickable(true);
        mGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_blue));
        isBright = true;
    }
}
