package com.liu.Account.utils;

import android.content.Context;
import android.util.Log;

import com.liu.Account.Constants.MethodConstant;
import com.liu.Account.commonUtils.ToastUtil;
import com.liu.Account.module.Hook.DefaultErrorHook;
import com.liu.Account.module.dataobject.AccessLogDo;
import com.liu.Account.network.NetworkManager;
import com.liu.Account.network.beans.ErrorHook;
import com.liu.Account.network.beans.JsonReceive;
import com.liu.Account.network.beans.ResponseHook;
import com.liu.Account.network.beans.ResponseHookDeal;
import com.liu.Account.network.utils.JsonParseUtil;

import org.json.JSONObject;

/**
 * Created by tanrong on 16/6/30.
 */
public class HttpUtil {
    /**
     * 在原来的post基础上添加了网络状态的判断
     *
     * @param method
     * @param request
     * @param responseHook
     * @param errorHook
     * @param responses
     */
    public static void post(String method, Object request,final ResponseHook responseHook, ErrorHook
            errorHook, final Class<?>... responses) {

            NetworkManager.getInstance().post(method, request, new ResponseHookDeal() {
                @Override
                public void deal(Context context, JSONObject json) {
                    if (responses!=null&&responses.length>0)
                        responseHook.deal(context, JsonParseUtil.jsonParseBean(
                                    json, responses));
                    else
                        responseHook.deal(context,JsonParseUtil.jsonParseBean(json));
                }
            }, errorHook, responses);

    }
    public static void sendAccessLog(AccessLogDo logDo){

        HttpUtil.post(MethodConstant.ADD_ACCESSLOG, logDo, new ResponseHook() {
            @Override
            public void deal(Context context, JsonReceive receive) {
                if (null != receive.getResponse()) {
                    ToastUtil.showShort(context,"发送成功"+receive.getResponse().toString());
                }
            }
        }, new DefaultErrorHook());
    }
}
