package com.liu.Account.utils;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.liu.Account.Constants.MethodConstant;
import com.liu.Account.commonUtils.AppUtil;
import com.liu.Account.initUtils.DeviceInformation;
import com.liu.Account.module.Hook.DefaultErrorHook;
import com.liu.Account.module.dataobject.AccessLogDo;
import com.liu.Account.module.dataobject.EventLogDo;
import com.liu.Account.module.dataobject.InstallationDo;
import com.liu.Account.network.FileNetworkManager;
import com.liu.Account.network.NetworkManager;
import com.liu.Account.network.beans.ErrorHook;
import com.liu.Account.network.beans.JsonReceive;
import com.liu.Account.network.beans.ResponseHook;
import com.liu.Account.network.beans.ResponseHookDeal;
import com.liu.Account.network.utils.Constants;
import com.liu.Account.network.utils.JsonParseUtil;

import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;

import cn.bmob.v3.BmobUser;

/**
 * Created by tanrong on 16/6/30.
 */
public class HttpUtil {
    /**
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
                    if (responses!=null&&responses.length>0) {
                        responseHook.deal(context, JsonParseUtil.jsonParseBean(
                                json, responses));
                    }
                    else {
                        responseHook.deal(context, JsonParseUtil.jsonParseBean(json));
                    }
                }
            }, errorHook, responses);

    }

    /**
     * 打开应用即上传信息
     * @param context
     */
    public static void sendAccessLog(Context context){
        AccessLogDo logDo=new AccessLogDo();
        Calendar calendar=Calendar.getInstance();
        logDo.setAccessTime(calendar.getTimeInMillis());
        logDo.setChannel(DeviceInformation.getMetaData(context,"UMENG_CHANNEL"));
        logDo.setInstallationId(UserSettingUtil.getInstallationId(context));
        logDo.setUserId(UserSettingUtil.getUserId(context));

        logDo.setVersionName(AppUtil.getAppVersionName(context));
        logDo.setVersionCode(AppUtil.getAppVersionCode(context));
        logDo.setAndroidVersion(AppUtil.getAndroidVersion());
        logDo.setAndroidAPI(AppUtil.getAndroidApi());
        logDo.setPhoneType(AppUtil.getPhoneModel());
        logDo.setImei(AppUtil.getDeviceIMEI(context));
        logDo.setImsi(AppUtil.getDeviceIMSI(context));
        logDo.setDeviceType();
        logDo.setNetworkOperatorName(AppUtil.getNetworkOperatorName(context));
        logDo.setNetworkStatus(AppUtil.getCurrentNetWorkStatus(context));
        logDo.setDeviceMacAddress(AppUtil.getDeviceMacAddress(context));

        String location="0,0";
        Location location1=LocationUtils.getLocation(context);
        if (location1!=null){
            location=location1.getLatitude()+","+location1.getLongitude();
        }
        logDo.setLocation(location);

        HttpUtil.post(MethodConstant.ADD_ACCESSLOG, logDo, new ResponseHook() {
            @Override
            public void deal(Context context, JsonReceive receive) {
                if (null != receive.getResponse()) {
                  //  ToastUtil.showShort(context,"发送成功"+receive.getResponse().toString());
                }
            }
        }, new DefaultErrorHook());


        InstallationDo installationDo=new InstallationDo();

        installationDo.setId(UserSettingUtil.getInstallationId(context));
        installationDo.setUserId(UserSettingUtil.getUserId(context));
        BmobUser bmobUser=BmobUser.getCurrentUser(context);
        if (bmobUser!=null){
            installationDo.setEmail(bmobUser.getEmail());
        }
        installationDo.setPhoneNum(AppUtil.getPhoneNum(context));
        installationDo.setPhoneType(logDo.getPhoneType());
        installationDo.setImei(logDo.getImei());
        installationDo.setImsi(logDo.getImsi());
        installationDo.setVersionName(logDo.getVersionName());
        installationDo.setVersionCode(logDo.getVersionCode());
        installationDo.setAndroidVersion(logDo.getAndroidVersion());
        installationDo.setAndroidAPI(logDo.getAndroidAPI());
        installationDo.setChannel(logDo.getChannel());
        installationDo.setDeviceType(logDo.getDeviceType());
        HttpUtil.post(MethodConstant.UPDDATE_INSTALLATION, installationDo, new ResponseHook() {
            @Override
            public void deal(Context context, JsonReceive receive) {
                if (null != receive.getResponse()) {
                  //  ToastUtil.showShort(context,"发送成功"+receive.getResponse().toString());
                }
            }
        }, new DefaultErrorHook());



    }


    public static final String EVENT_ADD="ADD_BILL";
    public static final String EVENT_DELETE="DELETE_BILL";
    public static final String EVENT_SHOW="SHOW_BILL";
    public static final String EVENT_MODIFY="MODIFY_BILL";


    public static final String EVENT_DATA_UP="UP_DATA";
    public static final String EVENT_DATA_AUTO_UP="AUTO_UP_DATE";
    public static final String EVENT_DATA_DOWN="DATA_DOWNLOAD";

    /**
     * 发送操作事件
     * @param context
     * @param operationName
     * @param afterOperation
     */
    public static void sendEventLog(Context context,String operationName,String afterOperation){
        if (context==null||operationName==null||afterOperation==null)return;

        EventLogDo eventLogDo=new EventLogDo();
        eventLogDo.setUserId(UserSettingUtil.getUserId(context));
        eventLogDo.setInstallationId(UserSettingUtil.getInstallationId(context));
        eventLogDo.setOperationName(operationName);
        eventLogDo.setExtension(afterOperation);

        HttpUtil.post(MethodConstant.ADD_EVENTLOG, eventLogDo, new ResponseHook() {
            @Override
            public void deal(Context context, JsonReceive receive) {

            }
        },new DefaultErrorHook());
    }

    public static void uploadFile(String filePath,ResponseHookDeal responseHookDeal){
        File file=new File(filePath);
        if (file.exists()){
            uploadFile(file,responseHookDeal);
        }else {
            Log.e("uploadFile","filePath do not have file");
        }
    }
    /**
     * 上传文件到服务器
     * @param file
     */
    public static void uploadFile(File file,ResponseHookDeal responseHook){
        String url= Constants.URL+MethodConstant.UPLOAD_FILE;
        FileNetworkManager.uploadFile(file, url,responseHook,new DefaultErrorHook());
    }
}
