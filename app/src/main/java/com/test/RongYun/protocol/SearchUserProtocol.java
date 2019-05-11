package com.test.RongYun.protocol;


import com.google.gson.Gson;
import com.test.RongYun.base.BaseProtocol;
import com.test.RongYun.response.GetCodeResponse;
import com.test.RongYun.response.GetUserInfoByPhoneResponse;

public class SearchUserProtocol extends BaseProtocol<GetUserInfoByPhoneResponse> {
	protected static final String TAG = "DiscoveryByProtocol";
	private Gson gson;

	public SearchUserProtocol() {
		gson = new Gson();
	}

	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetUserInfoByPhoneResponse parseJson(String json) {
		GetUserInfoByPhoneResponse getUserInfoByPhoneResponse = gson.fromJson(json, GetUserInfoByPhoneResponse.class);
		return getUserInfoByPhoneResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "/user/searchUser";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
