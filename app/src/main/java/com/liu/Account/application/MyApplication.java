package com.liu.Account.application;

import android.app.Application;
import android.content.Context;
import android.support.v7.widget.SearchView;

import com.liu.Account.network.NetworkManager;
import com.orm.SugarContext;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by deonte on 16-1-28.
 */
public class MyApplication extends Application {
    SearchView searchView;
    private  static Context context;

    public SearchView getSearchView() {
        return searchView;
    }

    public void setSearchView(SearchView searchView) {
        this.searchView = searchView;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = MyApplication.this;

        //初始化数据库
        SugarContext.init(this);

        NetworkManager.getInstance().init(context);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }

    public Context getContext() {
        return context;
    }
}
