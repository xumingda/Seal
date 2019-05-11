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
public class RegisterResponse {
	/** 服务器响应码 */
	public String code;
	/** 服务器返回消息 */
	public String msg;
	public String authorization;




	public String getCode() {
		return code;
	}

	public void setCode(String code) {
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


}
