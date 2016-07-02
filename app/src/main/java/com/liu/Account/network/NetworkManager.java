package com.liu.Account.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.liu.Account.network.beans.ErrorHook;
import com.liu.Account.network.beans.ResponseHook;
import com.liu.Account.network.utils.Constants;
import com.liu.Account.network.utils.JsonParseUtil;
import com.liu.Account.network.utils.LogType;
import com.liu.Account.network.utils.LogUtil;

import org.json.JSONObject;

/**
 * <b>网络请求操作单例类</b>
 * <p/>
 * <br/>
 * 使用getInstance方法获得单例类 <br/>
 * 在程序发送网络请求之前需调用init方法 <br/>
 * 调用post方法发送网络请求 <br/>
 * 在程序退出时需调用remove方法
 */
public class NetworkManager {

    private Context mContext;

    /**
     * 网络请求队列
     */
    private RequestQueue mQueue;

    /**
     * 静态内部类的方法实现单例模式
     */
    private static class Holder {
        private static final NetworkManager INSTANCE = new NetworkManager();
    }

    /**
     * 获取发送器单例
     */
    public static NetworkManager getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context 上下文
     */
    public void init(Context context) {
        mContext = context;
        mQueue = Volley.newRequestQueue(mContext.getApplicationContext());
    }

//    public static void hideProgressWait(){
//        if(BaseActivity.mDialog!=null){
//            BaseActivity.mDialog.dismiss();
//            BaseActivity.mDialog=null;
//        }
//    }

    /**
     * 向服务器发送请求
     *
     * @param method       请求的接口名
     * @param request      请求的实体
     * @param responseHook 响应成功的接口class
     * @param errorHook    响应失败的接口class
     * @param responses    响应的实体类class数组
     */
    public void post(final String method, final Object request,
                     final ResponseHook responseHook, final ErrorHook errorHook,
                     final Class<?>... responses) {
        new Thread() {
            @Override
            public void run() {
                if (mQueue != null) {
                    // 将request响应实体加上公共部分，转化成JSONObject
                    JSONObject jsonRequest = JsonParseUtil.beanParseJson(
                            method, request, mContext);
                    Log.i("NetworkManager",
                            "请求封装完成：" + jsonRequest.toString());
                    JsonObjectRequest req = new JsonObjectRequest(Method.POST,
                            Constants.URL, jsonRequest,
                            new Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject json) {
                                    // 请求成功响应，将JSONObject转换成JsonReceive
                                    LogUtil.log("NetworkManager",
                                            LogType.DEBUG, getClass(), "请求成功："
                                                    + json.toString());
//                                    if (JsonParseUtil.jsonParseBean(
//                                            json, responses).getStatus() == 333) {
//                                        LogUtil.log("NetworkManager", LogType.DEBUG, getClass(),
//                                                "重复登录：错误码333");
//                                        ToastUtils.showShort(BimuApplication.getContext(), "您的帐户已在其他设备登录");
//                                        LoginUtils.setLoginStatus(false);
//                                        hideProgressWait();
//                                        return;
//                                    }
                                    if (responseHook != null) {
                                        responseHook.deal(mContext,
                                                JsonParseUtil.jsonParseBean(
                                                        json, responses));
                                    }
                                }
                            }, new ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // 请求失败
                            LogUtil.log("NetworkManager",
                                    LogType.ERROR, getClass(), "请求失败: \n" + error.toString());
                            if (errorHook != null) {
                                errorHook.deal(mContext, error);
                            }
                        }
                    });
                    // 添加到请求队列
                    mQueue.add(req);
                } else {
                    LogUtil.log("NetworkManager", LogType.ERROR, getClass(),
                            "RequestQueue未初始化");
                }
            }
        }.start();
    }

    /**
     * 移除所有的请求
     */
    public void remove() {
        if (mQueue != null) {
            mQueue.cancelAll(mContext.getApplicationContext());
        } else {
            LogUtil.log("NetworkManager", LogType.ERROR, getClass(),
                    "RequestQueue未初始化");
        }
    }

    /**
     * 设置NetworkImageView的资源异步加载
     *
     * @param iv            要设置的NetworkImageView
     * @param url           资源的链接
     * @param defaultImgRes 默认的图片资源
     * @param errorImgRes   网络错误的图片资源
     */
    public void setImageUrl(NetworkImageView iv, String url, int defaultImgRes,
                            int errorImgRes) {
        if (mQueue != null) {
            // 设置默认图片资源
            iv.setDefaultImageResId(defaultImgRes);
            // 设置网络错误图片资源
            iv.setErrorImageResId(errorImgRes);
            // 设置网络图片资源
            iv.setImageUrl(url, new ImageLoader(mQueue, new BitmapLruCache()));
        } else {
            LogUtil.log("NetworkManager", LogType.ERROR, getClass(),
                    "RequestQueue未初始化");
        }
    }

    /**
     * 获取请求队列
     *
     * @return 请求队列
     */
    public RequestQueue getRequestQueue() {
        return mQueue;
    }

}
