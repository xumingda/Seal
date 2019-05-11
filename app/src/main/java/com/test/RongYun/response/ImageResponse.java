package com.test.RongYun.response;

/**
 * @作者: 许明达
 * @创建时间: 2016-3-23下午15:43:20
 * @版权: 特速版权所有
 * @描述: 封装服务器返回列表的参数
 * @更新人:
 * @更新时间:
 * @更新内容: TODO
 */
public class ImageResponse {
    /**
     * 服务器响应码
     */
    public int code;
    /**描述*/
    public String msg;
    /**会员id*/
    public String picUrl;

    @Override
    public String toString() {
        return "ImageResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", picUrl='" + picUrl + '\'' +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}
