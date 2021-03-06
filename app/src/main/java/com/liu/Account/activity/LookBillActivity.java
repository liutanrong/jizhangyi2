package com.liu.Account.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liu.Account.BmobNetwork.BmobNetworkUtils;
import com.liu.Account.Constants.Constants;
import com.liu.Account.R;
import com.liu.Account.commonUtils.DateUtil;
import com.liu.Account.commonUtils.LogUtil;
import com.liu.Account.commonUtils.ToastUtil;
import com.liu.Account.database.Bill;
import com.liu.Account.initUtils.Init;
import com.liu.Account.initUtils.StatusBarUtil;
import com.liu.Account.utils.DatabaseUtil;
import com.umeng.analytics.MobclickAgent;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobUser;

/**
 * Created by deonte on 16-1-25.
 */
public class LookBillActivity extends AutoLayoutActivity {
    private Context context;
    private ImageView titleBack;
    private TextView topText;
    private TextView rightText;

    private TextView mLookBillType;
    private TextView mLookBillMoney;
    private TextView mLookBillCreatTime;
    private TextView mLookBillOutTime;
    private TextView mLookBillTag;
    private TextView mLookBillRemark;
    private Button mLook_bill_modify;

    private String uniqueFlag="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_bill);

        context=LookBillActivity.this;
        StatusBarUtil.setTransparentStatusBar(this);
        initTop();
        bindViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent it=getIntent();
        uniqueFlag=it.getStringExtra("uniqueFlag");
        LogUtil.e("uniqueFlag:"+uniqueFlag);
        List<Bill> billList=Bill.find(Bill.class,"UNIQUE_FLAG=?",uniqueFlag);

        Bill bill=new Bill();
        if (billList!=null&&billList.size()>0){
            bill=billList.get(0);
        }
        mLookBillMoney.setText(bill.getSpendMoney()+"元");
        String createTime=DateUtil.getStringByFormat(bill.getGmtCreate(),DateUtil.dateFormatYMDHMS);
        mLookBillCreatTime.setText(createTime);
        String happenTime=DateUtil.getStringByFormat(bill.getHappenTime(),DateUtil.dateFormatYMDHMS);
        mLookBillOutTime.setText(happenTime);
        mLookBillRemark.setText(bill.getRemark());
        mLookBillTag.setText(bill.getTag());
        String moneyType="";
        if (bill.getMoneyType()==Bill.MONEY_TYPE_IN){
            moneyType="收入";
        }else {
            moneyType="支出";
        }
        mLookBillType.setText(moneyType);
        ////  16-1-25 查看账单
        Map<String,String> map = new HashMap<String,String>();
        try{
            BmobUser user=BmobUser.getCurrentUser(context);

            map.put("type",user.getObjectId()+"查看账单");
        }catch (Exception e){
            e.printStackTrace();
            map.put("type","查看账单");
        }
        MobclickAgent.onEventValue(context, "showAccount", map, 0);
        JSONObject dataJson=new JSONObject();
        //todo 发送访问记录   查看账单
//        HttpUtil.sendEventLog(context,HttpUtil.EVENT_SHOW, JSON.toJSONString(dataJson));
    }

    private void initTop() {
        titleBack = (ImageView) findViewById(R.id.title_back);
        topText= (TextView) findViewById(R.id.title_text);
        topText.setText(R.string.lookBill);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rightText= (TextView) findViewById(R.id.title_right);
        rightText.setText(getResources().getText(R.string.lookBillDelete));
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData();
            }
        });
    }

    private void deleteData() {
        ////16-1-25 删除账单
        Map<String,String> map = new HashMap<String,String>();
        try{
            BmobUser user=BmobUser.getCurrentUser(context);

            map.put("type",user.getObjectId()+"删除账单");
        }catch (Exception e){
            e.printStackTrace();
            map.put("type","删除账单");
        }
        MobclickAgent.onEventValue(context, "delAccount", map, 0);

        JSONObject dataJson=new JSONObject();

        //todo 发送操作纪律  删除账单
//        HttpUtil.sendEventLog(context,HttpUtil.EVENT_DELETE, JSON.toJSONString(dataJson));


        Dialog dialog =new AlertDialog.Builder(context)
                .setTitle(R.string.deleteBillTitle)
                .setMessage(R.string.deleteBillMessage)
                .setNegativeButton(R.string.deleteBillNegaBtm, null)
                .setPositiveButton(R.string.deleteBillPosBtm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<Bill> billList=Bill.find(Bill.class,"UNIQUE_FLAG=?", uniqueFlag);
                        LogUtil.i("将要删除的账单数:"+billList);
                        for (Bill bill:billList){
                            if (bill!=null){
                                bill.setIsDelete(true);
                                bill.setDeleteTime(new Date());
                                bill.save();
                            }
                        }

                        ToastUtil.showShort(context, getString(R.string.deleteBillSuccess));

                        finish();
                    }
                }).create();
        dialog.show();

    }

    private void bindViews() {

        mLookBillType = (TextView) findViewById(R.id.lookBillType);
        mLookBillMoney = (TextView) findViewById(R.id.lookBillMoney);
        mLookBillCreatTime = (TextView) findViewById(R.id.lookBillCreatTime);
        mLookBillOutTime = (TextView) findViewById(R.id.lookBillOutTime);
        mLookBillTag = (TextView) findViewById(R.id.lookBillTag);
        mLookBillRemark = (TextView) findViewById(R.id.lookBillRemark);
        mLook_bill_modify = (Button) findViewById(R.id.look_bill_modify);
        mLook_bill_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, ModifyBillActivity.class);
                it.putExtra("uniqueFlag",uniqueFlag);
                context.startActivity(it);
                finish();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("LookBillActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("LookBillActivity");
        MobclickAgent.onPause(this);
    }
    /**
     * 返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
        }
        return false;
    }
}
