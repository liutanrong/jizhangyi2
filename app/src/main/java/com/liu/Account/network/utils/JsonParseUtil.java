package com.liu.Account.network.utils;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.liu.Account.network.beans.JsonPost;
import com.liu.Account.network.beans.JsonReceive;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Bean类和JsonObject互相转化的工具类
 */
public class JsonParseUtil {

	private static ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * 先将request封装成JsonPost，再将JsonPost对象放入JsonObject中
	 * 
	 * @param method
	 *            请求的接口名
	 * @param request
	 *            请求的实体类
	 * @param mContext
	 *            应用的上下文
	 * @return 封装后的JsonObject
	 */
	public static JSONObject beanParseJson(String method, Object request,
			Context mContext) {
		// 实例化一个JsonPost,填充数据
		JsonPost post = new JsonPost(method, mContext, request);
		// 将JsonPost中的所有元素放入json中
		JSONObject json = null;
		try {
			String str = objectMapper.writeValueAsString(post);
			json = new JSONObject(str);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 将JsonObject对象解析为JsonReceive
	 * 
	 * @param json
	 *            服务器返回的JSONObject
	 * @param responses
	 *            响应的实体类class数组
	 * @return 解析出来的JsonReceive
	 */
	public static JsonReceive jsonParseBean(JSONObject json,
			Class<?>... responses) {
		JsonReceive receive = new JsonReceive();
		try {
//			receive.setMethod(json.getString("method"));
//			receive.setStatus(json.getInt("status"));
//			receive.setTimes_used(json.getLong("times_used"));
			receive.setTimestamp(json.getLong("timestamp"));
//			receive.setError(json.getString("error"));
			Object response = null;
			if(responses.length > 0){
				Log.i("NetworkManager","11");
				response= json.get("response");
			}else{
				Log.i("NetworkManager","12");
				response=json.get("response");
			}
			receive.setResponse(response);
		} catch (JSONException e) {
			e.printStackTrace();
			LogUtil.log(LogType.ERROR, JsonParseUtil.class, "响应的json串解析错误");
		}
		return receive;
	}

}
