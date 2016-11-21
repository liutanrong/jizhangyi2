//package com.liu.Account.network;
//
//import android.content.Context;
//import android.util.Log;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.liu.Account.network.beans.JsonPost;
//import com.liu.Account.network.beans.OkHook;
//import com.liu.Account.network.beans.OkReceive;
//import com.liu.Account.network.utils.Constants;
//import com.zhy.http.okhttp.OkHttpUtils;
//import com.zhy.http.okhttp.callback.StringCallback;
//
//import okhttp3.Call;
//import okhttp3.MediaType;
//
///**
// * Created by tanrong on 16/10/3.
// */
//public class OkHttpNetworkManager {
//
//    public static void post(final String method, Object request, final Context context,
//                            final OkHook responseHook){
//        JsonPost post=new JsonPost(method,context,request);
//        JSONObject jsonObject= JSON.parseObject(JSON.toJSONString(post));
//        Log.d("[封装完成"+method+"]",jsonObject.toString());
//        OkHttpUtils.postString().mediaType(MediaType.parse("application/json; charset=utf-8"))
//                .url(Constants.URL+method)
//                .content(jsonObject.toJSONString())
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        Log.d("[返回错误"+method+"]","");
//                    }
//
//                    @Override
//                    public void onResponse(String response, int id) {
//                        if(response!=null){
//                            OkReceive ok=JSON.parseObject(response,OkReceive.class);
//                            String data=ok.getResponse();
//                            Log.d("[返回数据"+method+"]",data);
//                            if(responseHook!=null){
//                                responseHook.deal(context,data);
//                            }
//                        }
//                    }
//                });
//    }
//}
