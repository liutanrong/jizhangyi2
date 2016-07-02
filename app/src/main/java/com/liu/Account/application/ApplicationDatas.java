package com.liu.Account.application;

import android.app.Application;
import android.content.Context;
import android.support.v7.widget.SearchView;

import com.liu.Account.network.NetworkManager;

/**
 * Created by deonte on 16-1-28.
 */
public class ApplicationDatas extends Application {
    private static Context context;
    SearchView searchView;

    public SearchView getSearchView() {
        return searchView;
    }

    public void setSearchView(SearchView searchView) {
        this.searchView = searchView;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
        NetworkManager.getInstance().init(context);
    }
}
