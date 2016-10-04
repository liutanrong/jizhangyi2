package com.liu.Account.network.beans;

import android.content.Context;

/**
 * Created by xing on 2016/9/5.
 * OkHttp网络请求的回调接口
 */
public interface OkHook {
    void deal(Context context, String response);
}
