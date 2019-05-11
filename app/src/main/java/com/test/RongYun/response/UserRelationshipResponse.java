package com.test.RongYun.response;

import java.util.List;

/**
 * Created by AMing on 16/1/7.
 * Company RongCloud
 */
public class UserRelationshipResponse {

    /**
     * code : 200
     * result : [{"displayName":"","message":"手机号:18622222222昵称:的用户请求添加你为好友","status":11,"updatedAt":"2016-01-07T06:22:55.000Z","user":{"id":"i3gRfA1ml","nickname":"nihaoa","portraitUri":""}}]
     */

    private int code;
    /**
     * displayName :
     * message : 手机号:18622222222昵称:的用户请求添加你为好友
     * status : 11
     * updatedAt : 2016-01-07T06:22:55.000Z
     * user : {"id":"i3gRfA1ml","nickname":"nihaoa","portraitUri":""}
     */
    private String msg;

    private List<ResultEntity> dataList;

    public void setCode(int code) {
        this.code = code;
    }



    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<ResultEntity> getDataList() {
        return dataList;
    }

    public void setDataList(List<ResultEntity> dataList) {
        this.dataList = dataList;
    }

    public static class ResultEntity implements Comparable {

        public ResultEntity(String friendUserId, String nameRemark, String portraitUri, int status) {
            this.friendUserId = friendUserId;
            this.nameRemark = nameRemark;
            this.portraitUri = portraitUri;
            this.status = status;
        }

        public String getFriendUserId() {
            return friendUserId;
        }

        public void setFriendUserId(String friendUserId) {
            this.friendUserId = friendUserId;
        }

        public String getNameRemark() {
            return nameRemark;
        }

        public void setNameRemark(String nameRemark) {
            this.nameRemark = nameRemark;
        }

        public String getPortraitUri() {
            return portraitUri;
        }

        public void setPortraitUri(String portraitUri) {
            this.portraitUri = portraitUri;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        private String friendUserId;//	好友用户id
        private String nameRemark;//	昵称备注
        private String portraitUri;//	头像
        private int status;//	可以操作的动作，0显示已邀请，1可以接受
        private String userNo;//	好友号

        public String getUserNo() {
            return userNo;
        }

        public void setUserNo(String userNo) {
            this.userNo = userNo;
        }

        @Override
        public int compareTo(Object another) {
            return 0;
        }

        @Override
        public String toString() {
            return "ResultEntity{" +
                    "friendUserId='" + friendUserId + '\'' +
                    ", nameRemark='" + nameRemark + '\'' +
                    ", portraitUri='" + portraitUri + '\'' +
                    ", status=" + status +
                    ", userNo='" + userNo + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "UserRelationshipResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", dataList=" + dataList +
                '}';
    }
}
