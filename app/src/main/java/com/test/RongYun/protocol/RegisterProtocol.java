package com.test.RongYun.protocol;


import com.google.gson.Gson;
import com.test.RongYun.base.BaseProtocol;
import com.test.RongYun.response.LoginResponse;

public class RegisterProtocol extends BaseProtocol<LoginResponse> {
	private Gson gson;

	public RegisterProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected LoginResponse parseJson(String json) {
		LoginResponse loginresponse = gson.fromJson(json, LoginResponse.class);
		return loginresponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "/user/reg";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
