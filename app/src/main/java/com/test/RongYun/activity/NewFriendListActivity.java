package com.test.RongYun.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.test.RongYun.R;
import com.test.RongYun.adapter.NewFriendListAdapter;
import com.test.RongYun.base.BaseActivity;
import com.test.RongYun.base.MyVolley;
import com.test.RongYun.db.Friend;
import com.test.RongYun.protocol.GetFriendListProtocol;
import com.test.RongYun.response.AgreeFriendsResponse;
import com.test.RongYun.response.UserRelationshipResponse;
import com.test.RongYun.server.BroadcastManager;
import com.test.RongYun.server.CharacterParser;
import com.test.RongYun.server.HttpException;
import com.test.RongYun.utils.CommonUtils;
import com.test.RongYun.utils.LogUtils;
import com.test.RongYun.utils.NToast;
import com.test.RongYun.utils.SealUserInfoManager;
import com.test.RongYun.utils.SharedPrefrenceUtils;
import com.test.RongYun.widget.LoadDialog;
import com.test.RongYun.widget.SealAppContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class NewFriendListActivity extends BaseActivity implements NewFriendListAdapter.OnItemButtonClick, View.OnClickListener {

    private static final int GET_ALL = 11;
    private static final int AGREE_FRIENDS = 12;
    public static final int FRIEND_LIST_REQUEST_CODE = 1001;
    private ListView shipListView;
    private NewFriendListAdapter adapter;
    private String friendId;
    private TextView isData;
    private UserRelationshipResponse userRelationshipResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friendlist);
        initView();
        if (!CommonUtils.isNetworkConnected(mContext)) {
            NToast.shortToast(mContext, R.string.check_network);
            return;
        }
        LoadDialog.show(mContext);
//        request(GET_ALL);
        runGetFriendList();
        adapter = new NewFriendListAdapter(mContext);
        shipListView.setAdapter(adapter);
    }

    protected void initView() {
        setTitle(R.string.new_friends);
        shipListView = (ListView) findViewById(R.id.shiplistview);
        isData = (TextView) findViewById(R.id.isData);
        Button rightButton = getHeadRightButton();
        rightButton.setBackgroundDrawable(getResources().getDrawable(R.mipmap.de_address_new_friend));
        rightButton.setOnClickListener(this);
    }


    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode) {
            case GET_ALL:
                return action.getAllUserRelationship();
            case AGREE_FRIENDS:
                return action.agreeFriends(friendId);
        }
        return super.doInBackground(requestCode, id);
    }


    @Override
    @SuppressWarnings("unchecked")
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            switch (requestCode) {
                case GET_ALL:
                    userRelationshipResponse = (UserRelationshipResponse) result;

                    if (userRelationshipResponse.getDataList().size() == 0) {
                        isData.setVisibility(View.VISIBLE);
                        LoadDialog.dismiss(mContext);
                        return;
                    }

                    Collections.sort(userRelationshipResponse.getDataList(), new Comparator<UserRelationshipResponse.ResultEntity>() {

                        @Override
                        public int compare(UserRelationshipResponse.ResultEntity lhs, UserRelationshipResponse.ResultEntity rhs) {
                            Date date1 = stringToDate(lhs);
                            Date date2 = stringToDate(rhs);
                            if (date1.before(date2)) {
                                return 1;
                            }
                            return -1;
                        }
                    });

                    List<UserRelationshipResponse.ResultEntity> userList = new ArrayList<>();
                    for (UserRelationshipResponse.ResultEntity entity : userRelationshipResponse.getDataList()) {
                        if (entity != null) {
                            userList.add(entity);
                        }
                    }
                    adapter.removeAll();
                    adapter.addData(userList);

                    adapter.notifyDataSetChanged();
                    adapter.setOnItemButtonClick(this);
                    LoadDialog.dismiss(mContext);
                    break;
                case AGREE_FRIENDS:
                    AgreeFriendsResponse afres = (AgreeFriendsResponse) result;
                    if (afres.getCode() == 200) {
                        UserRelationshipResponse.ResultEntity bean = userRelationshipResponse.getDataList().get(index);
                        SealUserInfoManager.getInstance().addFriend(new Friend(bean.getFriendUserId(),
                                bean.getNameRemark(),
                                Uri.parse(bean.getPortraitUri()),
                                bean.getNameRemark(),
                                String.valueOf(bean.getStatus()),
                                null,
                                null,
                                null,
                                CharacterParser.getInstance().getSpelling(bean.getNameRemark()),
                                CharacterParser.getInstance().getSpelling(bean.getNameRemark())));
                        // 通知好友列表刷新数据
                        NToast.shortToast(mContext, R.string.agreed_friend);
                        LoadDialog.dismiss(mContext);
                        BroadcastManager.getInstance(mContext).sendBroadcast(SealAppContext.UPDATE_FRIEND);
//                        request(GET_ALL); //刷新 UI 按钮
                        runGetFriendList();
                    }

            }
        }
    }


    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode) {
            case GET_ALL:
                break;

        }
    }


    @Override
    protected void onDestroy() {
        if (adapter != null) {
            adapter = null;
        }
        super.onDestroy();
    }

    private int index;

    @Override
    public boolean onButtonClick(int position, View view, int status) {
        index = position;
        switch (status) {
            case 11: //收到了好友邀请
                if (!CommonUtils.isNetworkConnected(mContext)) {
                    NToast.shortToast(mContext, R.string.check_network);
                    break;
                }
                LoadDialog.show(mContext);
//                friendId = null;
                friendId = userRelationshipResponse.getDataList().get(position).getFriendUserId();
                request(AGREE_FRIENDS);
                break;
            case 10: // 发出了好友邀请
                break;
            case 21: // 忽略好友邀请
                break;
            case 20: // 已是好友
                break;
            case 30: // 删除了好友关系
                break;
        }
        return false;
    }

    private Date stringToDate(UserRelationshipResponse.ResultEntity resultEntity) {
        //xmd暂时没有时间
//        String updatedAt = resultEntity.getUpdatedAt();
//        String updatedAtDateStr = updatedAt.substring(0, 10) + " " + updatedAt.substring(11, 16);
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        Date updateAtDate = null;
//        try {
//            updateAtDate = simpleDateFormat.parse(updatedAtDateStr);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(NewFriendListActivity.this, SearchFriendActivity.class);
        startActivityForResult(intent, FRIEND_LIST_REQUEST_CODE);
    }

    public void runGetFriendList() {
        GetFriendListProtocol getFriendListProtocol = new GetFriendListProtocol();
        HashMap<String, String> map = new HashMap<>();
        String url = getFriendListProtocol.getApiFun();
        map.put("authorization", SharedPrefrenceUtils.getString(mContext, "authorization"));

        MyVolley.uploadNoFile(MyVolley.POST, url, map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();

                userRelationshipResponse = (UserRelationshipResponse) gson.fromJson(json, UserRelationshipResponse.class);
                LogUtils.e("getUserInfoByIdResponse:" + userRelationshipResponse.toString());
                if (userRelationshipResponse.getCode() == 0) {
                    if (userRelationshipResponse.getDataList().size() == 0) {
                        isData.setVisibility(View.VISIBLE);
                        LoadDialog.dismiss(mContext);
                        return;
                    }

                    Collections.sort(userRelationshipResponse.getDataList(), new Comparator<UserRelationshipResponse.ResultEntity>() {

                        @Override
                        public int compare(UserRelationshipResponse.ResultEntity lhs, UserRelationshipResponse.ResultEntity rhs) {
                            Date date1 = stringToDate(lhs);
                            Date date2 = stringToDate(rhs);
                            if (date1.before(date2)) {
                                return 1;
                            }
                            return -1;
                        }
                    });

                    List<UserRelationshipResponse.ResultEntity> userList = new ArrayList<>();
                    for (UserRelationshipResponse.ResultEntity entity : userRelationshipResponse.getDataList()) {
                        if (entity != null) {
                            userList.add(entity);
                        }
                    }
                    adapter.removeAll();
                    adapter.addData(userList);

                    adapter.notifyDataSetChanged();
                    adapter.setOnItemButtonClick(NewFriendListActivity.this);
                    LoadDialog.dismiss(mContext);
                } else {
                    NToast.shortToast(mContext, userRelationshipResponse.getMsg());
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
}
