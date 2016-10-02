package com.liu.Account.network;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liu.Account.Constants.Constants;
import com.liu.Account.Constants.MethodConstant;
import com.liu.Account.commonUtils.LogUtil;
import com.liu.Account.commonUtils.PrefsUtil;
import com.liu.Account.database.Bill;
import com.liu.Account.module.Hook.DefaultErrorHook;
import com.liu.Account.network.beans.JsonReceive;
import com.liu.Account.network.beans.ResponseHook;
import com.liu.Account.utils.HttpUtil;
import com.liu.Account.view.emojicon.emoji.Objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tanrong on 16/9/23.
 */
public class BackupManager {

    public static boolean uploadData(Context context){
        boolean flag=true;
        List<Bill> billList=Bill.listAll(Bill.class);
        List<Bill> billListInsert=new ArrayList<>();
        List<Bill> billListUpdate=new ArrayList<>();
        PrefsUtil prefsUtil=new PrefsUtil(context, Constants.UPDATE_TIME_SP,Context.MODE_PRIVATE);
        for (Bill bill:billList) {
            Long uploadTime=prefsUtil.getLong("uploadTime",0);
            Long insertTime=prefsUtil.getLong("insertTime",0);
            if (bill.getGmtCreate().getTime()>insertTime){
                billListInsert.add(bill);
            }else if (bill.getGmtModified().getTime()>=uploadTime){
                billListUpdate.add(bill);
            }
        }
        JSONObject requestObject=new JSONObject();
        requestObject.put("insert",billListInsert);
        requestObject.put("update",billListUpdate);
        LogUtil.i("billSize:"+billList.size()+"  insertSize:"+billListInsert.size()+"  updateSize:"+billListUpdate.size());
        HttpUtil.post(MethodConstant.BILL_LIST_UPDATE, requestObject, new ResponseHook() {
            @Override
            public void deal(Context context, JsonReceive receive) {
                LogUtil.i("---账单上传完成--");
                LogUtil.i(JSON.toJSONString(receive));
                PrefsUtil prefsUtil=new PrefsUtil(context, Constants.UPDATE_TIME_SP,Context.MODE_PRIVATE);
                Object ob=receive.getResponse();
                Integer code=Integer.valueOf((String)ob);
                switch (code){
                    case 1:{
                        //全部成功
                        prefsUtil.putLong("insertTime",new Date().getTime());
                        prefsUtil.putLong("uploadTime",new Date().getTime());
                        break;
                    }case 2:{
                        //insert成功 update不成功
                        prefsUtil.putLong("insertTime",new Date().getTime());
                        break;
                    }case 3:{
                        //insert不成功 update成功
                        prefsUtil.putLong("uploadTime",new Date().getTime());
                        break;
                    }case 0:{
                        //全部失败
                        break;
                    }
                }
            }
        },new DefaultErrorHook());
        return flag;
    }


    public static boolean downloadData(){
        boolean flag=true;
        return flag;
    }
}
