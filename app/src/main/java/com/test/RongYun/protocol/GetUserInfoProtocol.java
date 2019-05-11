package com.test.RongYun.protocol;


import com.google.gson.Gson;
import com.test.RongYun.base.BaseProtocol;
import com.test.RongYun.response.GetCodeResponse;
import com.test.RongYun.response.GetUserInfoByIdResponse;

public class GetUserInfoProtocol extends BaseProtocol<GetUserInfoByIdResponse> {
	protected static final String TAG = "DiscoveryByProtocol";
	private Gson gson;

	public GetUserInfoProtocol() {
		gson = new Gson();
	}

	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetUserInfoByIdResponse parseJson(String json) {
		GetUserInfoByIdResponse getcoderesponse = gson.fromJson(json, GetUserInfoByIdResponse.class);
		return getcoderesponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "/user/getPersonalUserInfo";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
