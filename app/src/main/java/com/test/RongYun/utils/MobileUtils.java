package com.test.RongYun.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @作者: 周岐同
 * @创建时间: 2015-4-9下午4:51:42
 * @版权: 微位科技版权所有
 * @描述: 获取手机状态
 * @更新人:
 * @更新时间:
 * @更新内容: TODO
 */
public class MobileUtils {
	public static int controlWidth=0;
	public static int controlHeight=0;
	public static int getSDKVersionNumber() {
		int sdkVersion;
		try {
			sdkVersion = Build.VERSION.SDK_INT;
		} catch (NumberFormatException e) {
			sdkVersion = 0;
		}
		return sdkVersion;
	}
	/**
	 * @param context
	 * @return 手机当前是否连接网络
	 */
	public static boolean isHaveInternet(final Context context) {
		try {
			ConnectivityManager manger = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

			NetworkInfo info = manger.getActiveNetworkInfo();
			return (info != null && info.isConnected());
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * @return 屏幕宽度像素
	 */
	public static int getScreenWidth() {
		return UIUtils.getContext().getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * @return 屏幕高度像素
	 */
	public static int getScreenHeight() {
		return UIUtils.getContext().getResources().getDisplayMetrics().heightPixels;
	}

	/**
	 * 获取application中指定的meta-data
	 * 
	 * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
	 */
	public static String getAppMetaData(Context ctx, String key) {
		if (ctx == null || TextUtils.isEmpty(key)) {
			return null;
		}
		String resultData = null;
		try {
			PackageManager packageManager = ctx.getPackageManager();
			if (packageManager != null) {
				ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
				if (applicationInfo != null) {
					if (applicationInfo.metaData != null) {
						resultData = applicationInfo.metaData.getString(key);
					}
				}
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		return resultData;
	}
	public static String getDeviceInfo(Context context) {
	    try{
	      org.json.JSONObject json = new org.json.JSONObject();
	      android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
	          .getSystemService(Context.TELEPHONY_SERVICE);

	      String device_id = tm.getDeviceId();

	      WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

	      String mac = wifi.getConnectionInfo().getMacAddress();
	      json.put("mac", mac);

	      if( TextUtils.isEmpty(device_id) ){
	        device_id = mac;
	      }

	      if( TextUtils.isEmpty(device_id) ){
	        device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
	      }

	      json.put("device_id", device_id);

	      return json.toString();
	    }catch(Exception e){
	      e.printStackTrace();
	    }
	  return null;
	}

	/**
	 * @return 控件宽度像素
	 */
	public static int getcontrolWidth() {

		return controlWidth;
	}

	/**
	 * @return 控件高度像素
	 */
	public static int getcontrolHeight() {
		return controlHeight;
	}

	public static void setControlWidth(int width){
		controlWidth=width;
	}

	public static void setControlHeight(int height){
		controlHeight=height;
	}

	public static String getIP(Context context){
		//获取wifi服务
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		//判断wifi是否开启
		if (wifiManager.isWifiEnabled()) {
//			wifiManager.setWifiEnabled(true);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			int ipAddress = wifiInfo.getIpAddress();
			String ip = intToIp(ipAddress);
			return ip;
		}
		else{
			try
			{
				for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
				{
					NetworkInterface intf = en.nextElement();
					for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
					{
						InetAddress inetAddress = enumIpAddr.nextElement();
						if (!inetAddress.isLoopbackAddress())
						{
							return inetAddress.getHostAddress().toString();
						}
					}
				}
			}
			catch (SocketException ex)
			{
			}
			return null;
		}

	}

	private static String intToIp(int i) {

		return (i & 0xFF ) + "." +
				((i >> 8 ) & 0xFF) + "." +
				((i >> 16 ) & 0xFF) + "." +
				( i >> 24 & 0xFF) ;
	}
}
