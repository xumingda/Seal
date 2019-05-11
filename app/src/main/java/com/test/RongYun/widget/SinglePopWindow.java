package com.test.RongYun.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.test.RongYun.R;
import com.test.RongYun.db.BlackList;
import com.test.RongYun.db.Friend;
import com.test.RongYun.server.HttpException;
import com.test.RongYun.server.SealAction;
import com.test.RongYun.server.async.AsyncTaskManager;
import com.test.RongYun.server.async.OnDataListener;
import com.test.RongYun.utils.NToast;
import com.test.RongYun.utils.SealUserInfoManager;

import io.rong.imkit.RongIM;
import io.rong.imkit.utilities.PromptPopupDialog;
import io.rong.imlib.RongIMClient;

/**
 * Created by AMing on 16/8/1.
 * Company RongCloud
 */
public class SinglePopWindow extends PopupWindow {
    private static final int ADDBLACKLIST = 167;
    private static final int REMOVEBLACKLIST = 168;
    private View conentView;
    private AsyncTaskManager asyncTaskManager;


    @SuppressLint("InflateParams")
    public SinglePopWindow(final Activity context, final Friend friend, final RongIMClient.BlacklistStatus blacklistStatus) {
        LayoutInflater inflater = (LayoutInflater) context
                                  .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.popupwindow_more, null);
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);

        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);
        asyncTaskManager = AsyncTaskManager.getInstance(context);
        RelativeLayout blacklistStatusRL = (RelativeLayout) conentView.findViewById(R.id.blacklist_status);
        final TextView blacklistText = (TextView) conentView.findViewById(R.id.blacklist_text_status);

        if (blacklistStatus == RongIMClient.BlacklistStatus.IN_BLACK_LIST) {
            blacklistText.setText(R.string.remove_from_blacklist);
        } else {
            blacklistText.setText(R.string.join_the_blacklist);
        }


        blacklistStatusRL.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (blacklistStatus == RongIMClient.BlacklistStatus.IN_BLACK_LIST) {
                    RongIM.getInstance().removeFromBlacklist(friend.getUserId(), new RongIMClient.OperationCallback() {
                        @Override
                        public void onSuccess() {
                            asyncTaskManager.request(ADDBLACKLIST, new OnDataListener() {
                                @Override
                                public Object doInBackground(int requestCode, String parameter) throws HttpException {
                                    return new SealAction(context).removeFromBlackList(friend.getUserId());
                                }

                                @Override
                                public void onSuccess(int requestCode, Object result) {
                                    SealUserInfoManager.getInstance().deleteBlackList(friend.getUserId());
                                    NToast.shortToast(context, context.getString(R.string.remove_successful));
                                }

                                @Override
                                public void onFailure(int requestCode, int state, Object result) {

                                }
                            });
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                            NToast.shortToast(context, context.getString(R.string.remove_failed));
                        }
                    });
                } else {
                    PromptPopupDialog.newInstance(context, context.getString(R.string.join_the_blacklist),
                    context.getString(R.string.des_add_friend_to_black_list)).setPromptButtonClickedListener(new PromptPopupDialog.OnPromptButtonClickedListener() {
                        @Override
                        public void onPositiveButtonClicked() {
                            RongIM.getInstance().addToBlacklist(friend.getUserId(), new RongIMClient.OperationCallback() {
                                @Override
                                public void onSuccess() {

                                    asyncTaskManager.request(REMOVEBLACKLIST, new OnDataListener() {
                                        @Override
                                        public Object doInBackground(int requestCode, String parameter) throws HttpException {
                                            return new SealAction(context).addToBlackList(friend.getUserId());
                                        }

                                        @Override
                                        public void onSuccess(int requestCode, Object result) {
                                            SealUserInfoManager.getInstance().addBlackList(new BlackList(
                                                        friend.getUserId(),
                                                        null,
                                                        null
                                                    ));
                                            NToast.shortToast(context, context.getString(R.string.join_success));
                                        }

                                        @Override
                                        public void onFailure(int requestCode, int state, Object result) {

                                        }
                                    });
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {
                                    NToast.shortToast(context, context.getString(R.string.join_failed));
                                }
                            });
                        }
                    }).show();
                }
                SinglePopWindow.this.dismiss();
            }

        });

    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent, 0, 0);
        } else {
            this.dismiss();
        }
    }
}
