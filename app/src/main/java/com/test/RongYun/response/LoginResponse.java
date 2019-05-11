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
public class LoginResponse {
	/** 服务器响应码 */
	public int code;
	/** 服务器返回消息 */
	public String msg;
	public String authorization;




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

	public String getToken() {
		return authorization;
	}

	public void setToken(String authorization) {
		this.authorization = authorization;
	}

	@Override
	public String toString() {
		return "LoginResponse{" +
				"code=" + code +
				", msg='" + msg + '\'' +
				", authorization='" + authorization + '\'' +
				'}';
	}
}
