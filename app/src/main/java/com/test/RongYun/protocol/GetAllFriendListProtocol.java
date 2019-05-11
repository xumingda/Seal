package com.test.RongYun.protocol;


import com.google.gson.Gson;
import com.test.RongYun.base.BaseProtocol;
import com.test.RongYun.response.UserRelationshipResponse;

public class GetAllFriendListProtocol extends BaseProtocol<UserRelationshipResponse> {
	protected static final String TAG = "DiscoveryByProtocol";
	private Gson gson;

	public GetAllFriendListProtocol() {
		gson = new Gson();
	}

	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected UserRelationshipResponse parseJson(String json) {
		UserRelationshipResponse userRelationshipResponse = gson.fromJson(json, UserRelationshipResponse.class);
		return userRelationshipResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "/user/getFriendList";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
