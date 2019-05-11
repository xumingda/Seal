package com.test.RongYun.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.TaskStackBuilder;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.test.RongYun.R;
import com.test.RongYun.base.BaseActivity;
import com.test.RongYun.base.MyVolley;
import com.test.RongYun.protocol.GetUserInfoProtocol;
import com.test.RongYun.protocol.LoginProtocol;
import com.test.RongYun.protocol.RegisterProtocol;
import com.test.RongYun.response.GetCodeResponse;
import com.test.RongYun.response.GetUserInfoByIdResponse;
import com.test.RongYun.response.LoginResponse;
import com.test.RongYun.utils.AMUtils;
import com.test.RongYun.utils.DownTimer;
import com.test.RongYun.utils.LogUtils;
import com.test.RongYun.utils.MD5Utils;
import com.test.RongYun.utils.NLog;
import com.test.RongYun.utils.NToast;
import com.test.RongYun.utils.SealConst;
import com.test.RongYun.utils.SealUserInfoManager;
import com.test.RongYun.utils.SharedPrefrenceUtils;
import com.test.RongYun.widget.ClearWriteEditText;
import com.test.RongYun.widget.LoadDialog;

import java.util.HashMap;
import java.util.Locale;


import io.rong.common.RLog;
import io.rong.imkit.RongConfigurationManager;
import io.rong.imkit.RongIM;
import io.rong.imkit.utilities.LangUtils;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

/**
 * Created by AMing on 16/1/15.
 * Company RongCloud
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = "LoginActivity";
    private static final int LOGIN = 5;
    private static final int GET_TOKEN = 6;
    private static final int SYNC_USER_INFO = 9;
    private static final int REQUEST_CODE_SELECT_COUNTRY = 1000;

    private ClearWriteEditText mPhoneEdit, mPasswordEdit;
    private String phoneString;
    private String passwordString;
    private String connectResultId;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String loginToken;
    private String mCountryNameCN, mCountryNameEN;
    private String mRegion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setHeadVisibility(View.GONE);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        editor = sp.edit();
        initDate();
    }

    private void initDate() {
        mPhoneEdit = (ClearWriteEditText) findViewById(R.id.de_login_phone);
        mPasswordEdit = (ClearWriteEditText) findViewById(R.id.de_login_password);
        Button mConfirm = (Button) findViewById(R.id.de_login_sign);
        TextView mRegister = (TextView) findViewById(R.id.de_login_register);
        TextView forgetPassword = (TextView) findViewById(R.id.de_login_forgot);


        forgetPassword.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
        mRegister.setOnClickListener(this);


        mPhoneEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 11) {
                    AMUtils.onInactive(LoginActivity.this, mPhoneEdit);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


//        String oldPhone = SharedPrefrenceUtils.getString(mContext,"phone");
//        String oldPassword =  SharedPrefrenceUtils.getString(mContext,"password");
//        String oldRegion = SharedPrefrenceUtils.getString(mContext,"authorization");
        String oldPhone = sp.getString(SealConst.SEALTALK_LOGING_PHONE, "");
        String oldPassword = sp.getString(SealConst.SEALTALK_LOGING_PASSWORD, "");
        String oldRegion = sp.getString(SealConst.SEALTALK_LOGIN_AUTUOR,"");

        if (!TextUtils.isEmpty(oldPhone)
                && !TextUtils.isEmpty(oldPassword)) {
            mPhoneEdit.setText(oldPhone);
            mPasswordEdit.setText(oldPassword);
        }

        if (getIntent().getBooleanExtra("kickedByOtherClient", false)) {
            final AlertDialog dlg = new AlertDialog.Builder(LoginActivity.this).create();
            dlg.show();
            Window window = dlg.getWindow();
            window.setContentView(R.layout.other_devices);
            TextView text = (TextView) window.findViewById(R.id.ok);
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dlg.cancel();
                }
            });
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.de_login_sign:
                phoneString = mPhoneEdit.getText().toString().trim();
                passwordString = mPasswordEdit.getText().toString().trim();

                if (TextUtils.isEmpty(phoneString)) {
                    NToast.shortToast(LoginActivity.this, R.string.phone_number_is_null);
                    mPhoneEdit.setShakeAnimation();
                    return;
                }

//                if (!AMUtils.isMobile(phoneString)) {
//                    NToast.shortToast(LoginActivity.this, R.string.Illegal_phone_number);
//                    mPhoneEdit.setShakeAnimation();
//                    return;
//                }

                if (TextUtils.isEmpty(passwordString)) {
                    NToast.shortToast(LoginActivity.this, R.string.password_is_null);
                    mPasswordEdit.setShakeAnimation();
                    return;
                }
                if (passwordString.contains(" ")) {
                    NToast.shortToast(LoginActivity.this, R.string.password_cannot_contain_spaces);
                    mPasswordEdit.setShakeAnimation();
                    return;
                }
                LoadDialog.show(LoginActivity.this);
                editor.putBoolean("exit", false);
                editor.commit();
                String oldPhone = sp.getString(SealConst.SEALTALK_LOGING_PHONE, "");
                LoadDialog.show(LoginActivity.this);
                runLogin();

                break;
            case R.id.de_login_register:
                startActivityForResult(new Intent(this, RegisterActivity.class), 1);
                break;
            case R.id.de_login_forgot:
//                startActivityForResult(new Intent(this, ForgetPasswordActivity.class), 2);
                break;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2 && data != null) {
            String phone = data.getStringExtra("phone");
            String password = data.getStringExtra("password");
            String region = data.getStringExtra("region");
            String countryName = data.getStringExtra("country");
            String countryNameCN = data.getStringExtra("countryCN");
            String countryNameEN = data.getStringExtra("countryEN");
            if(!TextUtils.isEmpty(countryNameCN) && !TextUtils.isEmpty(countryNameEN)){
                mCountryNameCN = countryNameCN;
                mCountryNameEN = countryNameEN;

            }
            mPhoneEdit.setText(phone);
            mPasswordEdit.setText(password);
        } else if (data != null && requestCode == 1) {
            String phone = data.getStringExtra("phone");
            String password = data.getStringExtra("password");
            String id = data.getStringExtra("id");
            String nickname = data.getStringExtra("nickname");
            String region = data.getStringExtra("region");
            String countryName = data.getStringExtra("country");
            String countryNameCN = data.getStringExtra("countryCN");
            String countryNameEN = data.getStringExtra("countryEN");
            if(!TextUtils.isEmpty(countryNameCN) && !TextUtils.isEmpty(countryNameEN)){
                mCountryNameCN = countryNameCN;
                mCountryNameEN = countryNameEN;

            }
            if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(id) && !TextUtils.isEmpty(nickname)) {
                mPhoneEdit.setText(phone);
                mPasswordEdit.setText(password);
                editor.putString(SealConst.SEALTALK_LOGING_PHONE, phone);
                editor.putString(SealConst.SEALTALK_LOGING_PASSWORD, password);
                editor.putString(SealConst.SEALTALK_LOGIN_ID, id);
                editor.putString(SealConst.SEALTALK_LOGIN_NAME, nickname);
                if(!TextUtils.isEmpty(mCountryNameCN) && !TextUtils.isEmpty(mCountryNameEN)){
                    editor.putString(SealConst.SEALTALK_LOGIN_COUNTRY_CN, mCountryNameCN);
                    editor.putString(SealConst.SEALTALK_LOGIN_COUNTRY_EN, mCountryNameEN);

                }
                editor.commit();
            }
        } else if(requestCode == REQUEST_CODE_SELECT_COUNTRY && resultCode == RESULT_OK){
            String zipCode = data.getStringExtra("zipCode");
            String countryName = data.getStringExtra("countryName");
            String countryNameCN = data.getStringExtra("countryNameCN");
            String countryNameEN = data.getStringExtra("countryNameEN");
            mCountryNameCN = countryNameCN;
            mCountryNameEN = countryNameEN;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void goToMain() {
//        editor.putString("loginToken", loginToken);
//        editor.putString(SealConst.SEALTALK_LOGING_PHONE, phoneString);
//        editor.putString(SealConst.SEALTALK_LOGING_PASSWORD, passwordString);
//        if(!TextUtils.isEmpty(mCountryNameCN) && !TextUtils.isEmpty(mCountryNameEN)){
//            editor.putString(SealConst.SEALTALK_LOGIN_REGION, mRegion);
//            editor.putString(SealConst.SEALTALK_LOGIN_COUNTRY_CN, mCountryNameCN);
//            editor.putString(SealConst.SEALTALK_LOGIN_COUNTRY_EN, mCountryNameEN);
//        }
//        editor.commit();
        LoadDialog.dismiss(LoginActivity.this);
        NToast.shortToast(LoginActivity.this, R.string.login_success);
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    public void runLogin() {
        LoginProtocol loginProtocol = new LoginProtocol();
        HashMap<String,String> map=new HashMap<>();
        String url = loginProtocol.getApiFun();
        map.put("account", phoneString);
        map.put("pwd", MD5Utils.MD5(passwordString));

        MyVolley.uploadNoFile(MyVolley.POST, url, map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                LoginResponse loginResponse = gson.fromJson(json, LoginResponse.class);
                LogUtils.e("loginResponse:" + loginResponse.toString());
                LoadDialog.dismiss(LoginActivity.this);
                if (loginResponse.code == 0) {
                    runGetuserInfo(loginResponse.authorization);
                    editor.putString("loginToken", loginToken);
                    editor.putString(SealConst.SEALTALK_LOGING_PHONE, phoneString);
                    editor.putString(SealConst.SEALTALK_LOGING_PASSWORD, passwordString);
                    editor.putString(SealConst.SEALTALK_LOGIN_AUTUOR, loginResponse.authorization);
                    editor.commit();
                    SharedPrefrenceUtils.setString(LoginActivity.this,"phone",phoneString);
                    SharedPrefrenceUtils.setString(LoginActivity.this,"password",passwordString);
                    SharedPrefrenceUtils.setString(LoginActivity.this,"authorization",loginResponse.authorization);
                } else {
                    NToast.shortToast(LoginActivity.this, loginResponse.msg);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                NToast.shortToast(LoginActivity.this,error);
                LoadDialog.dismiss(LoginActivity.this);
            }

            @Override
            public void dealTokenOverdue() {
                LoadDialog.dismiss(LoginActivity.this);
            }
        });
    }
    public void runGetuserInfo(String authorization) {
        GetUserInfoProtocol getUserInfoProtocol = new GetUserInfoProtocol();
        HashMap<String,String> map=new HashMap<>();
        String url = getUserInfoProtocol.getApiFun();
        map.put("authorization", authorization);

        MyVolley.uploadNoFile(MyVolley.POST, url, map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                final GetUserInfoByIdResponse getUserInfoByIdResponse = gson.fromJson(json, GetUserInfoByIdResponse.class);
                LogUtils.e("getUserInfoByIdResponse:" + getUserInfoByIdResponse.toString());
                LoadDialog.dismiss(LoginActivity.this);
                if (getUserInfoByIdResponse.getCode() == 0) {
                    String loginToken = getUserInfoByIdResponse.getData().getToken();
                    if (!TextUtils.isEmpty(loginToken)) {
                        RongIM.connect(loginToken, new RongIMClient.ConnectCallback() {
                            @Override
                            public void onTokenIncorrect() {
                                NLog.e("connect", "onTokenIncorrect");
                            }

                            @Override
                            public void onSuccess(String s) {
                                connectResultId = s;
                                NLog.e("connect", "onSuccess userid:" + s);
                                editor.putString("loginToken", getUserInfoByIdResponse.getData().getToken());
                                editor.putString(SealConst.SEALTALK_LOGIN_ID, getUserInfoByIdResponse.getData().getUserId());
                                editor.putString(SealConst.SEALTALK_LOGIN_NAME, getUserInfoByIdResponse.getData().getName());
                                editor.putString(SealConst.SEALTALK_LOGING_PORTRAIT, getUserInfoByIdResponse.getData().getPortraitUri());
                                editor.commit();
                                editor.commit();
                                SealUserInfoManager.getInstance().openDB();
                                //不继续在login界面同步好友,群组,群组成员信息
                                SealUserInfoManager.getInstance().getAllUserInfo();
                                goToMain();
                                SharedPrefrenceUtils.setString(LoginActivity.this,"token",getUserInfoByIdResponse.getData().getToken());
                                SharedPrefrenceUtils.setString(LoginActivity.this,"name",getUserInfoByIdResponse.getData().getName());
                                SharedPrefrenceUtils.setString(LoginActivity.this,"userid",getUserInfoByIdResponse.getData().getUserId());
                                SharedPrefrenceUtils.setString(LoginActivity.this,"url",getUserInfoByIdResponse.getData().getPortraitUri());
                                RongIM.getInstance().refreshUserInfoCache(new UserInfo(getUserInfoByIdResponse.getData().getUserId(), getUserInfoByIdResponse.getData().getName(), Uri.parse(getUserInfoByIdResponse.getData().getPortraitUri())));
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {
                                NLog.e("connect", "onError errorcode:" + errorCode.getValue());
                            }
                        });
                    }

                } else {
                    NToast.shortToast(LoginActivity.this, getUserInfoByIdResponse.getMsg());
                }


            }
            @Override
            public void dealWithError(String address, String error) {
                NToast.shortToast(LoginActivity.this,error);
                LoadDialog.dismiss(LoginActivity.this);
            }

            @Override
            public void dealTokenOverdue() {
                LoadDialog.dismiss(LoginActivity.this);
            }
        });
    }
}
