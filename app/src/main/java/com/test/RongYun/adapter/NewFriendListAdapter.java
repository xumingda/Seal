package com.test.RongYun.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.test.RongYun.R;
import com.test.RongYun.base.BaseApplication;
import com.test.RongYun.response.UserRelationshipResponse;
import com.test.RongYun.utils.SealUserInfoManager;
import com.test.RongYun.widget.SelectableRoundedImageView;

import io.rong.imageloader.core.ImageLoader;
import io.rong.imlib.model.UserInfo;

@SuppressWarnings("deprecation")
public class NewFriendListAdapter extends BaseAdapters {

    public NewFriendListAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.rs_ada_user_ship, parent, false);
            holder.mName = (TextView) convertView.findViewById(R.id.ship_name);
            holder.mMessage = (TextView) convertView.findViewById(R.id.ship_message);
            holder.mHead = (SelectableRoundedImageView) convertView.findViewById(R.id.new_header);
            holder.mState = (TextView) convertView.findViewById(R.id.ship_state);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final UserRelationshipResponse.ResultEntity bean = (UserRelationshipResponse.ResultEntity) dataSet.get(position);
        holder.mName.setText(bean.getNameRemark());
        String portraitUri = null;
        if (bean != null ) {

            portraitUri = SealUserInfoManager.getInstance().getPortraitUri(new UserInfo(
                    bean.getFriendUserId(), bean.getNameRemark(), Uri.parse(bean.getPortraitUri())));
        }
        ImageLoader.getInstance().displayImage(portraitUri, holder.mHead, BaseApplication.getOptions());
        //xmd暂时没有
//        holder.mMessage.setText(bean.getMessage());
        holder.mState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemButtonClick != null) {
                    mOnItemButtonClick.onButtonClick(position, v, bean.getStatus());
                }
            }
        });

        switch (bean.getStatus()) {
            case 11: //收到了好友邀请
                holder.mState.setText(R.string.agree);
                holder.mState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.de_add_friend_selector));
                break;
            case 10: // 发出了好友邀请
                holder.mState.setText(R.string.request);
                holder.mState.setBackgroundDrawable(null);
                break;
            case 21: // 忽略好友邀请
                holder.mState.setText(R.string.ignore);
                holder.mState.setBackgroundDrawable(null);
                break;
            case 20: // 已是好友
                holder.mState.setText(R.string.added);
                holder.mState.setBackgroundDrawable(null);
                break;
            case 30: // 删除了好友关系
                holder.mState.setText(R.string.deleted);
                holder.mState.setBackgroundDrawable(null);
                break;
        }
        return convertView;
    }

    /**
     * displayName :
     * message : 手机号:18622222222昵称:的用户请求添加你为好友
     * status : 11
     * updatedAt : 2016-01-07T06:22:55.000Z
     * user : {"id":"i3gRfA1ml","nickname":"nihaoa","portraitUri":""}
     */

    class ViewHolder {
        SelectableRoundedImageView mHead;
        TextView mName;
        TextView mState;
        //        TextView mtime;
        TextView mMessage;
    }

    OnItemButtonClick mOnItemButtonClick;


    public void setOnItemButtonClick(OnItemButtonClick onItemButtonClick) {
        this.mOnItemButtonClick = onItemButtonClick;
    }

    public interface OnItemButtonClick {
        boolean onButtonClick(int position, View view, int status);

    }
}
