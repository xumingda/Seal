package com.test.RongYun.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.test.RongYun.R;
import com.test.RongYun.base.BaseActivity;
import com.test.RongYun.base.BaseApplication;
import com.test.RongYun.base.MyVolley;
import com.test.RongYun.db.Friend;
import com.test.RongYun.protocol.AddUserProtocol;
import com.test.RongYun.protocol.LoginProtocol;
import com.test.RongYun.protocol.SearchUserProtocol;
import com.test.RongYun.response.FriendInvitationResponse;
import com.test.RongYun.response.GetUserInfoByPhoneResponse;
import com.test.RongYun.response.LoginResponse;
import com.test.RongYun.server.HttpException;
import com.test.RongYun.server.async.AsyncTaskManager;
import com.test.RongYun.utils.CommonUtils;
import com.test.RongYun.utils.LogUtils;
import com.test.RongYun.utils.MD5Utils;
import com.test.RongYun.utils.NToast;
import com.test.RongYun.utils.SealConst;
import com.test.RongYun.utils.SealUserInfoManager;
import com.test.RongYun.utils.SharedPrefrenceUtils;
import com.test.RongYun.widget.DialogWithYesOrNoUtils;
import com.test.RongYun.widget.LoadDialog;
import com.test.RongYun.widget.SealAppContext;
import com.test.RongYun.widget.SelectableRoundedImageView;

import java.util.HashMap;
import java.util.Locale;


import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.RongConfigurationManager;
import io.rong.imkit.utilities.LangUtils;
import io.rong.imlib.model.UserInfo;

public class SearchFriendActivity extends BaseActivity implements View.OnClickListener  {
    private static final int REQUEST_CODE_SELECT_COUNTRY = 1000;
    private static final int CLICK_CONVERSATION_USER_PORTRAIT = 1;
    private static final int SEARCH_PHONE = 10;
    private static final int ADD_FRIEND = 11;
    private EditText mEtSearch;
    private EditText mSearchEt;
    private LinearLayout searchItem;
    private TextView searchName;
    private TextView mCountryNameTv, mCountryCodeTv;
    private LinearLayout mSearchContainerLl;
    private SelectableRoundedImageView searchImage;
    private String mPhone;
    private String mRegion;
    private String addFriendMessage;
    private String mFriendId;
    private Friend mFriend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle((R.string.search_friend));

        mHeadRightText.setVisibility(View.GONE);
        mHeadRightText.setText(getString(R.string.cancel));
        mHeadRightText.setOnClickListener(this);

        mEtSearch = (EditText) findViewById(R.id.search_edit);
        searchItem = (LinearLayout) findViewById(R.id.search_result);
        searchName = (TextView) findViewById(R.id.search_name);
        searchImage = (SelectableRoundedImageView) findViewById(R.id.search_header);
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchItem.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mHeadRightText.performClick();
                }
                return false;
            }
        });

        mCountryNameTv = (TextView)findViewById(R.id.search_country_name);
        mCountryCodeTv = (TextView)findViewById(R.id.search_country_code);
        mSearchEt = (EditText)findViewById(R.id.search_phone);
        TextView searchTv = (TextView)findViewById(R.id.search_search);

        mSearchContainerLl = (LinearLayout)findViewById(R.id.search_search_container);
        searchTv.setOnClickListener(this);
    }

    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode) {
            case SEARCH_PHONE:

                return action.getUserInfoFromPhone(mRegion, mPhone);
            case ADD_FRIEND:
                return action.sendFriendInvitation(mFriendId, addFriendMessage);
        }
        return super.doInBackground(requestCode, id);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            switch (requestCode) {
                case SEARCH_PHONE:
                    final GetUserInfoByPhoneResponse userInfoByPhoneResponse = (GetUserInfoByPhoneResponse) result;
                    LogUtils.e("userInfoByPhoneResponse:"+userInfoByPhoneResponse.toString());
                    if (userInfoByPhoneResponse.getCode() == 0) {
                        mSearchContainerLl.setVisibility(View.GONE);
                        mHeadRightText.setVisibility(View.VISIBLE);

                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, "success");
                        mFriendId = userInfoByPhoneResponse.getData().getUserId();
                        searchItem.setVisibility(View.VISIBLE);
                        String portraitUri = null;
                        if (userInfoByPhoneResponse.getData() != null) {
                            GetUserInfoByPhoneResponse.ResultEntity userInfoByPhoneResponseResult = userInfoByPhoneResponse.getData();
                            UserInfo userInfo = new UserInfo(userInfoByPhoneResponseResult.getUserId(),
                                    userInfoByPhoneResponseResult.getName(),
                                    Uri.parse(userInfoByPhoneResponseResult.getPortraitUri()));
                            portraitUri = SealUserInfoManager.getInstance().getPortraitUri(userInfo);
                        }
                        ImageLoader.getInstance().displayImage(portraitUri, searchImage, BaseApplication.getOptions());
                        searchName.setText(userInfoByPhoneResponse.getData().getName());
                        searchItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isFriendOrSelf(mFriendId)) {
//                                    Intent intent = new Intent(SearchFriendActivity.this, UserDetailActivity.class);
//                                    intent.putExtra("friend", mFriend);
//                                    intent.putExtra("type", CLICK_CONVERSATION_USER_PORTRAIT);
//                                    startActivity(intent);
//                                    SealAppContext.getInstance().pushActivity(SearchFriendActivity.this);
                                    return;
                                }
                                DialogWithYesOrNoUtils.getInstance().showEditDialog(mContext, getString(R.string.add_text), getString(R.string.add_friend), new DialogWithYesOrNoUtils.DialogCallBack() {
                                    @Override
                                    public void executeEvent() {

                                    }

                                    @Override
                                    public void updatePassword(String oldPassword, String newPassword) {

                                    }

                                    @Override
                                    public void executeEditEvent(String editText) {
                                        if (!CommonUtils.isNetworkConnected(mContext)) {
                                            NToast.shortToast(mContext, R.string.network_not_available);
                                            return;
                                        }
                                        addFriendMessage = editText;
                                        if (TextUtils.isEmpty(editText)) {
                                            addFriendMessage = getString(R.string.inivte_firend_descprtion_format,SharedPrefrenceUtils.getString(mContext,"name"));
                                        }
                                        if (!TextUtils.isEmpty(mFriendId)) {
                                            LoadDialog.show(mContext);
                                            runAddUser();
                                        } else {
                                            NToast.shortToast(mContext, "id is null");
                                        }
                                    }
                                });
                            }
                        });

                    }
                    break;
                case ADD_FRIEND:
                    FriendInvitationResponse fres = (FriendInvitationResponse) result;
                    if (fres.getCode() == 200) {
                        NToast.shortToast(mContext, getString(R.string.request_success));
                        LoadDialog.dismiss(mContext);
                    } else {
                        NToast.shortToast(mContext, mContext.getString(R.string.quest_failed_error_code) + fres.getCode());
                        LoadDialog.dismiss(mContext);
                    }
                    break;
            }
        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode) {
            case ADD_FRIEND:
                NToast.shortToast(mContext, mContext.getString(R.string.you_are_already_friends));
                LoadDialog.dismiss(mContext);
                break;
            case SEARCH_PHONE:
                if (state == AsyncTaskManager.HTTP_ERROR_CODE || state == AsyncTaskManager.HTTP_NULL_CODE) {
                    super.onFailure(requestCode, state, result);
                } else {
                    NToast.shortToast(mContext, mContext.getString(R.string.account_not_exist));
                }
                LoadDialog.dismiss(mContext);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        hintKbTwo();
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    private boolean isFriendOrSelf(String id) {
        String inputPhoneNumber = mPhone;

        String selfPhoneNumber = SharedPrefrenceUtils.getString(mContext, "phone");
        if (inputPhoneNumber != null) {
            if (inputPhoneNumber.equals(selfPhoneNumber)) {
                mFriend = new Friend(SharedPrefrenceUtils.getString(mContext,"userid"),
                        SharedPrefrenceUtils.getString(mContext,"name"),
                        Uri.parse(SharedPrefrenceUtils.getString(mContext,"url")));
                return true;
            } else {
                mFriend = SealUserInfoManager.getInstance().getFriendByID(id);
                if (mFriend != null) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.text_right:
                mSearchContainerLl.setVisibility(View.VISIBLE);
                mHeadRightText.setVisibility(View.GONE);
                searchItem.setVisibility(View.GONE);
                break;
            case R.id.search_search:
                String phone = mSearchEt.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    NToast.shortToast(mContext, getString(R.string.phone_number_is_null));
                    return;
                }
                mPhone = phone;
                String region = SharedPrefrenceUtils.getString(mContext,"authorization");
                if(region.startsWith("+")){
                    region = region.substring(1);
                }
                mRegion = region;
                hintKbTwo();
                LoadDialog.show(mContext);
                runSearchUser(mRegion, mPhone);
                break;

        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_SELECT_COUNTRY && resultCode == RESULT_OK){
            String zipCode = data.getStringExtra("zipCode");
            String countryName = data.getStringExtra("countryName");
            mCountryCodeTv.setText(zipCode);
            mCountryNameTv.setText(countryName);
        }
    }

    public void runSearchUser(String authorization, String phone) {
        SearchUserProtocol searchUserProtocol = new SearchUserProtocol();
        HashMap<String,String> map=new HashMap<>();
        String url = searchUserProtocol.getApiFun();
        map.put("authorization", authorization);
        map.put("searchKey", phone);

        MyVolley.uploadNoFile(MyVolley.POST, url, map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                GetUserInfoByPhoneResponse getUserInfoByPhoneResponse = gson.fromJson(json, GetUserInfoByPhoneResponse.class);
                LogUtils.e("getUserInfoByPhoneResponse:" + getUserInfoByPhoneResponse.toString());
                LoadDialog.dismiss(mContext);
                if (getUserInfoByPhoneResponse.getCode() == 0) {
                    mSearchContainerLl.setVisibility(View.GONE);
                    mHeadRightText.setVisibility(View.VISIBLE);

                    LoadDialog.dismiss(mContext);
                    NToast.shortToast(mContext, "success");
                    mFriendId = getUserInfoByPhoneResponse.getData().getUserId();
                    searchItem.setVisibility(View.VISIBLE);
                    String portraitUri = null;
                    if (getUserInfoByPhoneResponse.getData() != null) {
                        GetUserInfoByPhoneResponse.ResultEntity userInfoByPhoneResponseResult = getUserInfoByPhoneResponse.getData();
                        UserInfo userInfo = new UserInfo(userInfoByPhoneResponseResult.getUserId(),
                                userInfoByPhoneResponseResult.getName(),
                                Uri.parse(userInfoByPhoneResponseResult.getPortraitUri()));
                            portraitUri = userInfoByPhoneResponseResult.getPortraitUri();
                    }
                    ImageLoader.getInstance().displayImage(portraitUri, searchImage, BaseApplication.getOptions());
                    searchName.setText(getUserInfoByPhoneResponse.getData().getName());
                    searchItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isFriendOrSelf(mFriendId)) {
                                    Intent intent = new Intent(SearchFriendActivity.this, UserDetailActivity.class);
                                    intent.putExtra("friend", mFriend);
                                    intent.putExtra("type", CLICK_CONVERSATION_USER_PORTRAIT);
                                    startActivity(intent);
                                    SealAppContext.getInstance().pushActivity(SearchFriendActivity.this);
                                return;
                            }
                            DialogWithYesOrNoUtils.getInstance().showEditDialog(mContext, getString(R.string.add_text), getString(R.string.add_friend), new DialogWithYesOrNoUtils.DialogCallBack() {
                                @Override
                                public void executeEvent() {

                                }

                                @Override
                                public void updatePassword(String oldPassword, String newPassword) {

                                }

                                @Override
                                public void executeEditEvent(String editText) {
                                    if (!CommonUtils.isNetworkConnected(mContext)) {
                                        NToast.shortToast(mContext, R.string.network_not_available);
                                        return;
                                    }
                                    addFriendMessage = editText;
                                    if (TextUtils.isEmpty(editText)) {
                                        addFriendMessage = getString(R.string.inivte_firend_descprtion_format, getSharedPreferences("config", MODE_PRIVATE).getString(SealConst.SEALTALK_LOGIN_NAME, ""));
                                    }
                                    if (!TextUtils.isEmpty(mFriendId)) {
                                        LoadDialog.show(mContext);
                                        //添加好友
//                                        request(ADD_FRIEND);
                                        runAddUser();
                                    } else {
                                        NToast.shortToast(mContext, "id is null");
                                    }
                                }
                            });
                        }
                    });
                } else {
                    NToast.shortToast(mContext, getUserInfoByPhoneResponse.getMsg());
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                NToast.shortToast(mContext, error);
                LoadDialog.dismiss(mContext);
            }

            @Override
            public void dealTokenOverdue() {
                LoadDialog.dismiss(mContext);
            }
        });
    }

    public void runAddUser() {
        AddUserProtocol addUserProtocol = new AddUserProtocol();
        HashMap<String,String> map=new HashMap<>();
        String url = addUserProtocol.getApiFun();
        map.put("authorization", SharedPrefrenceUtils.getString(mContext,"authorization"));
        map.put("friendUserId",  mFriendId);
        map.put("remark",addFriendMessage);


        MyVolley.uploadNoFile(MyVolley.POST, url, map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                FriendInvitationResponse friendInvitationResponse = gson.fromJson(json, FriendInvitationResponse.class);
                LogUtils.e("friendInvitationResponse:" + friendInvitationResponse.toString());
                LoadDialog.dismiss(mContext);
                if (friendInvitationResponse.getCode() == 0) {
                    NToast.shortToast(mContext, getString(R.string.request_success));
                    LoadDialog.dismiss(mContext);
                } else {
                    NToast.shortToast(mContext, friendInvitationResponse.getMsg());
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                NToast.shortToast(mContext,error);
                LoadDialog.dismiss(mContext);
            }

            @Override
            public void dealTokenOverdue() {
                LoadDialog.dismiss(mContext);
            }
        });
    }
}
