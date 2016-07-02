package com.liu.Account.module.Hook;

import android.content.Context;

import com.android.volley.VolleyError;
import com.liu.Account.commonUtils.LogUtil;
import com.liu.Account.network.beans.ErrorHook;

/**
 * Created by pak2c on 16/4/11.
 */
public class DefaultErrorHook implements ErrorHook {
    @Override
    public void deal(Context context, VolleyError error) {
        LogUtil.d(error.toString());
    }
}
