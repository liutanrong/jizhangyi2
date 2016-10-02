package com.liu.Account.network;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.liu.Account.Constants.MethodConstant;
import com.liu.Account.commonUtils.LogUtil;
import com.liu.Account.database.Bill;
import com.liu.Account.module.Hook.DefaultErrorHook;
import com.liu.Account.network.beans.JsonReceive;
import com.liu.Account.network.beans.ResponseHook;
import com.liu.Account.utils.HttpUtil;

import java.util.List;

/**
 * Created by tanrong on 16/9/23.
 */
public class BackupManager {

    public static boolean uploadData(){
        boolean flag=true;
        List<Bill> billList=Bill.listAll(Bill.class);
        HttpUtil.post(MethodConstant.BILL_LIST_UPDATE, billList, new ResponseHook() {
            @Override
            public void deal(Context context, JsonReceive receive) {
                LogUtil.i("---账单上传完成--");
                LogUtil.i(JSON.toJSONString(receive));
            }
        },new DefaultErrorHook());
        return flag;
    }


    public static boolean downloadData(){
        boolean flag=true;
        return flag;
    }
}
