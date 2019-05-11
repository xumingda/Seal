package com.test.RongYun.response;

/**
 * Created by AMing on 16/1/4.
 * Company RongCloud
 */
public class GetUserInfoByPhoneResponse {


    /**
     * code : 200
     * result : {"id":"10YVscJI3","nickname":"阿明","portraitUri":""}
     */

    private int code;
    private String msg;
    /**
     * id : 10YVscJI3
     * nickname : 阿明
     * portraitUri :
     */

    private ResultEntity data;

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ResultEntity getData() {
        return data;
    }

    public void setData(ResultEntity data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }


    public static class ResultEntity {
        private String userId;
        private String userNo;
        private String name;
        private String portraitUri;
        private String phoneNumber;
        private String createTime;
        private int  isFriend;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserNo() {
            return userNo;
        }

        public void setUserNo(String userNo) {
            this.userNo = userNo;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPortraitUri() {
            return portraitUri;
        }

        public void setPortraitUri(String portraitUri) {
            this.portraitUri = portraitUri;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public int getIsFriend() {
            return isFriend;
        }

        public void setIsFriend(int isFriend) {
            this.isFriend = isFriend;
        }

        @Override
        public String toString() {
            return "ResultEntity{" +
                    "userId='" + userId + '\'' +
                    ", userNo='" + userNo + '\'' +
                    ", name='" + name + '\'' +
                    ", portraitUri='" + portraitUri + '\'' +
                    ", phoneNumber='" + phoneNumber + '\'' +
                    ", createTime='" + createTime + '\'' +
                    ", isFriend=" + isFriend +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "GetUserInfoByPhoneResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
