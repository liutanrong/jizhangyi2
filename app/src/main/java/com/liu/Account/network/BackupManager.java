package com.liu.Account.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liu.Account.Constants.Constants;
import com.liu.Account.Constants.MethodConstant;
import com.liu.Account.commonUtils.LogUtil;
import com.liu.Account.commonUtils.PrefsUtil;
import com.liu.Account.commonUtils.ToastUtil;
import com.liu.Account.database.Bill;
import com.liu.Account.module.Hook.DefaultErrorHook;
import com.liu.Account.module.request.GetBillRequest;
import com.liu.Account.module.response.GetBillResponse;
import com.liu.Account.network.beans.JsonReceive;
import com.liu.Account.network.beans.OkHook;
import com.liu.Account.network.beans.ResponseHook;
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

    private ProgressDialog pro;
    private Context mContext;

    public BackupManager(Context context) {
        this.mContext=context;
        pro= new ProgressDialog(context);
    }

    public void uploadData(boolean isShow){
        if (isShow){
            pro.setTitle("正在上传");
            pro.setMessage("请稍候...");
            pro.show();
        }
        List<Bill> billList=Bill.listAll(Bill.class);
        List<Bill> billListInsert=new ArrayList<>();
        List<Bill> billListUpdate=new ArrayList<>();
        PrefsUtil prefsUtil=new PrefsUtil(mContext, Constants.UPDATE_TIME_SP,Context.MODE_PRIVATE);
        Long uploadTime=prefsUtil.getLong("uploadTime",0);
        Long insertTime=prefsUtil.getLong("insertTime",0);
        for (Bill bill:billList) {
            if (bill==null)continue;
            Long userId=UserSettingUtil.getUserId(mContext);
            if (userId==null)continue;
            bill.setUserId(userId);

            if (bill.getGmtCreate()!=null&&bill.getGmtCreate().getTime()>insertTime){
                billListInsert.add(bill);
            }else if (bill.getGmtModified()!=null&&bill.getGmtModified().getTime()>=uploadTime){
                billListUpdate.add(bill);
            }
        }
        JSONObject requestObject=new JSONObject();
        requestObject.put("insert",billListInsert);
        requestObject.put("update",billListUpdate);
        LogUtil.i("billSize:"+billList.size()+"  insertSize:"+billListInsert.size()+"  updateSize:"+billListUpdate.size());
        OkHttpNetworkManager.post(MethodConstant.BILL_LIST_UPDATE, requestObject, mContext, new OkHook() {
            @Override
            public void deal(Context context, String responseStr) {
                LogUtil.i("---账单上传完成--");
                LogUtil.i(JSON.toJSONString(responseStr));
                PrefsUtil prefsUtil=new PrefsUtil(context, Constants.UPDATE_TIME_SP,Context.MODE_PRIVATE);
                Integer code= Integer.valueOf(responseStr);
                pro.dismiss();
                switch (code){
                    case 1:{
                        //全部成功
                        prefsUtil.putLong("insertTime",new Date().getTime());
                        prefsUtil.putLong("uploadTime",new Date().getTime());
                        ToastUtil.showShort(mContext,"数据上传成功");
                        break;
                    }case 2:{
                        //insert成功 update不成功
                        prefsUtil.putLong("insertTime",new Date().getTime());
                        ToastUtil.showShort(mContext,"数据上传成功");
                        break;
                    }case 3:{
                        //insert不成功 update成功
                        prefsUtil.putLong("uploadTime",new Date().getTime());
                        ToastUtil.showShort(mContext,"数据上传成功");
                        break;
                    }case 0:{
                        //全部失败
                        ToastUtil.showShort(mContext,"数据上传失败,请稍后再试");
                        break;
                    }
                }
            }
        });
    }


    public void downloadData(){
        pro.setTitle("正在上传");
        pro.setMessage("请稍候...");
        pro.show();


        GetBillRequest request=new GetBillRequest();
        request.setUserId(UserSettingUtil.getUserId(mContext));
        Set<String> havedBill=new HashSet<>();
        List<Bill> billList=Bill.listAll(Bill.class);
        for (Bill bill :
                billList) {
            havedBill.add(bill.getUniqueFlag());
        }
        request.setHavedBill(havedBill);
        OkHttpNetworkManager.post(MethodConstant.BILL_LIST_DOWNLOAD, request, mContext, new OkHook() {
            @Override
            public void deal(Context context, String response) {
                LogUtil.i(response);
                GetBillResponse response1=JSON.parseObject(response,GetBillResponse.class);
                pro.dismiss();
                if (response1==null){
                    LogUtil.i("null");
                    ToastUtil.showShort(mContext,"数据下载失败,请稍后再试");
                }else {
                    if (response1.getExeSuccess()==1){
                        List<Bill> billList=response1.getDataBill();
                        Bill.saveInTx(billList);
                        PrefsUtil prefsUtil=new PrefsUtil(context, Constants.UPDATE_TIME_SP,Context.MODE_PRIVATE);

                        prefsUtil.putLong("insertTime",response1.getLastInsertDate()+1);
                        prefsUtil.putLong("uploadTime",response1.getLastUpdateDate()+1);
                        ToastUtil.showShort(mContext,"数据下载成功");
                    }else {
                        ToastUtil.showShort(mContext,"数据下载失败,请稍后再试");
                    }
                }

            }
        });
    }
}
