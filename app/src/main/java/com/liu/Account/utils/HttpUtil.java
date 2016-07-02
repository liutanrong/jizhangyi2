package com.liu.Account.utils;

import com.liu.Account.network.NetworkManager;
import com.liu.Account.network.beans.ErrorHook;
import com.liu.Account.network.beans.ResponseHook;

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
    public static void post(String method, Object request, ResponseHook responseHook, ErrorHook
            errorHook, Class<?>... responses) {

            NetworkManager.getInstance().post(method, request, responseHook, errorHook, responses);

    }
}
