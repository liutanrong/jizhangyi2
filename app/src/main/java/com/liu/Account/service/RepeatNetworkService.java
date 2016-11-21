package com.liu.Account.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.liu.Account.Constants.Constants;
import com.liu.Account.commonUtils.PrefsUtil;
import com.liu.Account.module.Hook.DefaultErrorHook;
import com.liu.Account.network.beans.ResponseHookDeal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

/**
 * Created by tanrong on 16/7/7.
 */
public class RepeatNetworkService extends Service {
    private Context context;
    private static String key;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        PrefsUtil prefsUtil=new PrefsUtil(context, Constants.ERROR_NETWORK_SP,Context.MODE_PRIVATE);
        Map<String,?> errorMap=prefsUtil.getAll();
        Set<? extends Map.Entry<String, ?>> values= errorMap.entrySet();

        for (Map.Entry<String, ?> o:values){
            key=o.getKey();
            String value= (String) o.getValue();


            JSONObject object=null;
            try {
                object=new JSONObject(convertStandardJSONString(value));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (key!=null) {
                prefsUtil.removeKey(key);
            }
            if (object==null)continue;
            //TODO 消息发送失败之后重发
        }




        return super.onStartCommand(intent, flags, startId);

    }
    public static String convertStandardJSONString(String data_json) {
        data_json = data_json.replaceAll("\\\\r\\\\n", "");
        data_json = data_json.replace("\"{", "{");
        data_json = data_json.replace("}\",", "},");
        data_json = data_json.replace("}\"", "}");
        return data_json;
    }

}
