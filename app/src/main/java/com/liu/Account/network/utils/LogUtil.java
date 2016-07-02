package com.liu.Account.network.utils;

import android.util.Log;

/**
 * 打印Log的工具
 */
public class LogUtil {

	/**
	 * 默认打印Log的标签
	 */
	private static final String DEFAULT_TAG = "LogUtil";

	/**
	 * 打印Log的标签
	 */
	public static String TAG = DEFAULT_TAG;

	/**
	 * 默认打印Log的等级
	 */
	private static final LogType DEFAULT_LEVEL = LogType.DEBUG;

	/**
	 * 打印Log的最低等级 LEVEL=1 打印debug、warning、error LEVEL=2 打印warning、error LEVEL=3
	 * 打印error LEVEL>3 不打印
	 */
	public static LogType LEVEL = DEFAULT_LEVEL;

	/**
	 * 使用默认的TAG打印
	 */
	public static void log(LogType type, Class<?> clazz, String info) {
		log(TAG, type, clazz, info);
	}

	/**
	 * 使用自定义的TAG打印
	 */
	public static void log(String Tag, LogType type, Class<?> clazz, String info) {
		if (LEVEL.level > type.level)
			return;
		switch (type) {
		case DEBUG:
			Log.d(Tag, "[DEBUG]" + clazz.getSimpleName() + ":" + info);
			break;
		case WARNING:
			Log.w(Tag, "[WARNING]" + clazz.getSimpleName() + ":" + info);
			break;
		case ERROR:
			Log.e(Tag, "[ERROR]" + clazz.getSimpleName() + ":" + info);
			break;
		case NEVER:
			break;
		}
	}
}
