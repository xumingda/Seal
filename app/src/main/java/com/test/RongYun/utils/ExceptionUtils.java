package com.test.RongYun.utils;

import android.util.Log;

import com.android.volley.error.AuthFailureError;
import com.android.volley.error.NetworkError;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.ServerError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.lidroid.xutils.exception.HttpException;

import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

//import com.android.volley.error.AuthFailureError;
//import com.android.volley.error.NetworkError;
//import com.android.volley.error.NoConnectionError;
//import com.android.volley.error.ServerError;
//import com.android.volley.error.TimeoutError;
//import com.android.volley.error.VolleyError;

public class ExceptionUtils {

	private static String TAG=ExceptionUtils.class.getSimpleName();
	public static final int TIME_OUT_EXCEPTION=1;
	public static final int SERVER_NOT_FOUND=2;
	public static int getExceptionType(Exception e){
		Log.e(TAG,"exception:"+e.getMessage());
		if(e instanceof UnknownHostException){
			return SERVER_NOT_FOUND;
		}else if(e instanceof TimeoutException){
			return TIME_OUT_EXCEPTION;
		}else if(e instanceof HttpException){
			int code=((HttpException)e).getExceptionCode();
			Log.e(TAG,"HttpException code:"+code);
			if(code==0||code==404) return SERVER_NOT_FOUND;
			return code;
		}
		return 0;
	}
	
	public static String getExceptionMsg(VolleyError e){
		if(e instanceof TimeoutError){
			return "连接服务器超时";
		}else if(e instanceof ServerError){
			return "服务器内部错误";
		}else if(e instanceof AuthFailureError){
			return "授权访问失败";
		}else if(e instanceof NoConnectionError){
			return "网络连接已经断开，请稍后重试";
		}else if(e instanceof NetworkError){
			return "网络错误";
		}else{
			return e.getMessage();
		}
	}
}
