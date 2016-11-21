package com.liu.Account.network.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * 网络请求常量类
 */
public class Constants {

	/**
	 * 设备的sn码
	 * */
	private static String sn = "";
	/**
	 * 网络请求的url
	 */
	// : 2016/3/21 这里根据比目的具体数据修改
	public static String URL = "http://114.215.92.152:8026/api/jizhangyi/";
//	public static String URL = "http://192.168.100.4:8026/api/jizhangyi/";
	public static String APPKEY = "888";
	public static final int RTIMES = 1;
	public static String SECRET = "567745674567544";

	/**
	 * 获取设备的sn码
	 * */
	public static String getSN(Context context) {
		if (sn.equals("")) {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			sn = tm.getSimSerialNumber()==null ? "" : tm.getSimSerialNumber();
		}
		return sn;
	}
}
