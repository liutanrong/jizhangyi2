//package com.liu.Account.network;
//
//
//import android.content.Context;
//
//import com.android.volley.VolleyError;
//import com.liu.Account.application.MyApplication;
//import com.liu.Account.commonUtils.LogUtil;
//import com.liu.Account.network.beans.ErrorHook;
//import com.liu.Account.network.beans.ResponseHookDeal;
//import com.zhy.http.okhttp.OkHttpUtils;
//import com.zhy.http.okhttp.callback.StringCallback;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.File;
//
//import okhttp3.Call;
//
///**
// * Created by tanrong on 16/7/5.
// */
//public class FileNetworkManager {
//    private static final String TAG = "FileNetworkManager";
//    /**
//     * android上传文件到服务器
//     * @param file  需要上传的文件
//     * @param requestURL  请求的rul
//     * @return  返回响应的内容
//     */
//    public static void uploadFile(final File file, final String requestURL, final ResponseHookDeal responseHook, final ErrorHook errorHook)
//    {
//        if(!file.exists()){
//            LogUtil.i(TAG+" file not exist");
//            return;
//        }
//        OkHttpUtils.post()
//                .addFile("file",file.getName(), file)//
//                .url(requestURL)
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int i) {
//                        MyApplication myApplication =new MyApplication();
//
//                        Context context= myApplication.getContext();
//                        if (errorHook != null) {
//                            errorHook.deal(context,new VolleyError(e),"file:"+file.getName());
//                        }
//                    }
//
//                    @Override
//                    public void onResponse(String s, int i) {
//                        LogUtil.i("response:"+s+" i:"+i);
//                        MyApplication myApplication =new MyApplication();
//
//                        Context context= myApplication.getContext();
//                        if (responseHook != null) {
//                            JSONObject object=null;
//                            try {
//                                object=new JSONObject(s);
//
//                                LogUtil.i("s1:"+object.toString());
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            if (object!=null)
//                                responseHook.deal(context, object);
//
//                        }
//                    }
//                });
//
//    }
//}
