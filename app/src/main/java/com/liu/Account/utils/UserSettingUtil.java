package com.liu.Account.utils;

import android.content.Context;

import com.liu.Account.Constants.Constants;
import com.liu.Account.commonUtils.PrefsUtil;

/**
 * Created by tanrong on 16/7/4.
 */
public class UserSettingUtil {
    public static void setInstallationId(Context context,Long installationId){
        PrefsUtil prefsUtil=new PrefsUtil(context, Constants.USER_SETTING_PRFIX,Context.MODE_PRIVATE);
        prefsUtil.putLong("installationId",installationId);
    }
    public static Long getInstallationId(Context context){
        PrefsUtil prefsUtil=new PrefsUtil(context, Constants.USER_SETTING_PRFIX,Context.MODE_PRIVATE);
        Long installationId=prefsUtil.getLong("installationId");
        if (installationId==0)return null;
        else return installationId;
    }

    public static void setUserId(Context context,Long userId){
        PrefsUtil prefsUtil=new PrefsUtil(context, Constants.USER_SETTING_PRFIX,Context.MODE_PRIVATE);
        prefsUtil.putLong("userId",userId);
    }
    public static Long getUserId(Context context){
        PrefsUtil prefsUtil=new PrefsUtil(context, Constants.USER_SETTING_PRFIX,Context.MODE_PRIVATE);
        Long userId=prefsUtil.getLong("userId");
        if (userId==0)return null;
        else return userId;
    }
}
