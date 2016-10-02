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
import com.liu.Account.module.request.GetBillRequest;
import com.liu.Account.module.response.GetBillResponse;
import com.liu.Account.network.beans.JsonReceive;
import com.liu.Account.network.beans.ResponseHook;
import com.liu.Account.network.beans.ResponseHookDeal;
import com.liu.Account.utils.HttpUtil;
import com.liu.Account.utils.UserSettingUtil;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by tanrong on 16/9/23.
 */
public class BackupManager {

    public static void uploadData(Context context){
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
    }


    public static void downloadData(Context context){
        GetBillRequest request=new GetBillRequest();
        request.setUserId(UserSettingUtil.getUserId(context));
        Set<String> havedBill=new HashSet<>();
        List<Bill> billList=Bill.listAll(Bill.class);
        for (Bill bill :
                billList) {
            havedBill.add(bill.getUniqueFlag());
        }
        request.setHavedBill(havedBill);
        HttpUtil.post(MethodConstant.BILL_LIST_DOWNLOAD, request, new ResponseHook() {
            @Override
            public void deal(Context context, JsonReceive receive) {
                LogUtil.i("---账单下载完成--");
                JSONObject respo=JSON.parseObject(JSON.toJSONString(receive.getResponse()));
                GetBillResponse response=JSON.parseObject(JSON.toJSONString(respo.getJSONObject("response"))
                        ,GetBillResponse.class);
                if (response.getHasError()!=0){
                    List<Bill> billList=response.getData();
                    for (Bill b :
                            billList) {
                        b.save();
                    }
                }

            }
        },new DefaultErrorHook(),GetBillResponse.class);
    }
}
