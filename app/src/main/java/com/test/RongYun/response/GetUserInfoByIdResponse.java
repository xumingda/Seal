package com.test.RongYun.response;

/**
 * Created by AMing on 16/1/4.
 * Company RongCloud
 */
public class GetUserInfoByIdResponse {

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


    public int getCode() {
        return code;
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

    @Override
    public String toString() {
        return "GetUserInfoByIdResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public static class ResultEntity {
        private String userNo;//	用户号，登录的时候可以用到

        private String token;//	用户在融云注册后的token
        private String phoneNumber;//	手机号
        private String createTime;//	注册时间
        private int status;//	状态 1正常,2禁用

        private String userId;
        private String name;
        private String portraitUri;

        public String getUserNo() {
            return userNo;
        }

        public void setUserNo(String userNo) {
            this.userNo = userNo;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
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

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
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

        @Override
        public String toString() {
            return "ResultEntity{" +
                    "userNo=" + userNo +
                    ", token='" + token + '\'' +
                    ", phoneNumber='" + phoneNumber + '\'' +
                    ", createTime='" + createTime + '\'' +
                    ", status=" + status +
                    ", userId='" + userId + '\'' +
                    ", name='" + name + '\'' +
                    ", portraitUri='" + portraitUri + '\'' +
                    '}';
        }
    }
}
