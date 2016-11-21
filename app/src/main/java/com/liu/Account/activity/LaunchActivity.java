package com.liu.Account.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.liu.Account.Constants.Constants;
import com.liu.Account.Constants.MethodConstant;
import com.liu.Account.Constants.TagConstats;
import com.liu.Account.R;
import com.liu.Account.commonUtils.AppUtil;
import com.liu.Account.commonUtils.DateUtil;
import com.liu.Account.commonUtils.LogUtil;
import com.liu.Account.commonUtils.PrefsUtil;
import com.liu.Account.database.Bill;
import com.liu.Account.initUtils.Init;
import com.liu.Account.module.Hook.DefaultErrorHook;
import com.liu.Account.module.dataobject.InstallationDo;
import com.liu.Account.network.beans.JsonReceive;
import com.liu.Account.network.beans.ResponseHook;
import com.liu.Account.service.RepeatNetworkService;
import com.liu.Account.utils.DatabaseUtil;
import com.liu.Account.utils.UserSettingUtil;
import com.orm.SugarContext;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.zhanghai.android.patternlock.ConfirmPatternActivity;
import me.zhanghai.android.patternlock.PatternUtils;
import me.zhanghai.android.patternlock.PatternView;

/**
 *  @author liutanrong0425@163.com
 * Created by deonte on 15-11-4.
 */
public class LaunchActivity extends ConfirmPatternActivity {
    private Context context;
    private DatabaseUtil db;

    public final int MSG_FINISH_LAUNCHERACTIVITY = 500;
    public final int MSG_FINISH_LAUNCHERACTIVITY2 = 700;

    public Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FINISH_LAUNCHERACTIVITY:
                    //跳转到MainActivity，并结束当前的LauncherActivity
                    Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case MSG_FINISH_LAUNCHERACTIVITY2:
                    Intent intt = new Intent(LaunchActivity.this, MainActivity.class);
                    startActivity(intt);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);




        context=LaunchActivity.this;
        Init.Bmob(context);//初始化bmob
        Init.DbName(context);//获得数据库名称，为手机imei号
        Init.savePath();
        Init.Umeng(context);
        initDB();

        SugarContext.init(context);

        //默认Tag
        PrefsUtil dddd=new PrefsUtil(context,Constants.DefaultTag,Context.MODE_PRIVATE);
        TagConstats.defaultTag=dddd.getInt("tagPos",1);

        if (UserSettingUtil.getInstallationId(context)==null) {
            //获取installId
            InstallationDo installationDo = new InstallationDo();
            installationDo.setImei(AppUtil.getDeviceIMEI(context));
            //todo 和服务器关联,获取到installId
//            HttpUtil.post(MethodConstant.GET_INSTALLID, installationDo, new ResponseHook() {
//                @Override
//                public void deal(Context context, JsonReceive receive) {
//                    if (receive.getResponse() != null) {
//                        Long installId = Long.valueOf(receive.getResponse().toString());
//                        UserSettingUtil.setInstallationId(context, installId);
//                    }
//                }
//            }, new DefaultErrorHook());
        }


        //todo 上传访问纪律
//        HttpUtil.sendAccessLog(context);
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateDb();
        Intent intent=new Intent(context, RepeatNetworkService.class);
        context.startService(intent);
    }

    private void initDB() {

    }
    /**
     * 更新数据库
     *
     */
    private void updateDb() {
        ProgressDialog progDialog=new ProgressDialog(context);

        File file=context.getDatabasePath(Constants.DBNAME);
        if (!file.exists()){
            LogUtil.e("old database not exists");
            return;
        }

        try {
            progDialog.setTitle("正在更新数据");
            progDialog.setMessage("请稍候...");
            progDialog.show();
            DatabaseUtil databaseUtil=new DatabaseUtil(context,Constants.DBNAME,1);
            Cursor cursor=databaseUtil.queryCursor("select * from "+Constants.tableName, null);
            LogUtil.i("账单总数" + cursor.getCount());
            List<Bill> billList=new ArrayList<>();
            while (cursor.moveToNext()) {
                //遍历
                try {
                    String remark = cursor.getString(cursor.getColumnIndex("remark"));
                    String date = cursor.getString(cursor.getColumnIndex("date"));
                    String unixtime = cursor.getString(cursor.getColumnIndex("unixTime"));
                    String spendMoney = cursor.getString(cursor.getColumnIndex("spendMoney"));
                    String moneyType=cursor.getString(cursor.getColumnIndex("moneyType"));
                    String creatTime=cursor.getString(cursor.getColumnIndex("creatTime"));
                    String tag=cursor.getString(cursor.getColumnIndex("Tag"));
                    Integer id=cursor.getInt(cursor.getColumnIndex("_Id"));

                    Bill bill=new Bill();
                    bill.setRemark(remark);
                    java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#.00");
                    bill.setSpendMoney(new BigDecimal(df.format(Double.valueOf(spendMoney))));
                    bill.setIsDelete(false);
                    Long createDateTimeStamp=0L;
                    if (creatTime.contains("--"))
                        createDateTimeStamp=Long.valueOf(unixtime);
                    else
                        createDateTimeStamp= DateUtil.getMilliseconds(creatTime,DateUtil.dateFormatYMDHMD);

                    if (createDateTimeStamp==0){
                        createDateTimeStamp=978278500000L;
                    }
                    int rad=(int)(Math.random()*10000);

                    bill.setGmtCreate(new Date(createDateTimeStamp-5000+rad));
                    bill.setGmtModified(new Date(createDateTimeStamp-5001+rad));
                    bill.setHappenTime(new Date(Long.valueOf(unixtime)));
                    bill.setTag(tag);
                    bill.setInstallationId(UserSettingUtil.getInstallationId(context));
                    bill.setUserId(UserSettingUtil.getUserId(context));
                    String uniqueFlag="";
                    if (bill.getGmtCreate().getTime()!=0){
                        uniqueFlag=bill.getInstallationId()+"_"+DateUtil.getStringByFormat(bill.getGmtCreate(),"yyyy_MM_dd_HH_mm_ss");

                    }else {
                        uniqueFlag=bill.getInstallationId()+"_"+DateUtil.getStringByFormat(bill.getHappenTime(),"yyyy_MM_dd_HH_mm_ss");
                    }

                    bill.setUniqueFlag(uniqueFlag);
                    int type=2;
                    if (moneyType.equals("支出")) type=1;
                    bill.setMoneyType(type);
                    billList.add(bill);
                } catch (Exception e) {
                    LogUtil.i(e.toString());
                }

            }
            for (Bill bill:billList){
                bill.save();
            }
            LogUtil.i("当前数据库数目:"+Bill.count(Bill.class));
            if (file.exists()){
                boolean f=false;
                boolean t=false;
                while (!f){
                    f=file.renameTo(new File(file.getPath()+"_bak"));
                }
                while (!t){
                    t=file.delete();
                }
            }
        }catch (Exception e){
            LogUtil.e("database table not exists");
        }
        progDialog.dismiss();
    }


/**
    public static String getDeviceInfo(Context context) {
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            String device_id = tm.getDeviceId();

            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            String mac = wifi.getConnectionInfo().getMacAddress();
            json.put("mac", mac);

            if( TextUtils.isEmpty(device_id) ){
                device_id = mac;
            }

            if( TextUtils.isEmpty(device_id) ){
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
            }

            json.put("device_id", device_id);

            return json.toString();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
**/
    @Override
    protected boolean isStealthModeEnabled() {
    // Return the value from SharedPreferences
        PrefsUtil d=new PrefsUtil(LaunchActivity.this, Constants.PatternLock,Context.MODE_PRIVATE);
        if (!d.getBoolean("isPatternOn",false)) {
            //不开着屏幕锁
            setContentView(R.layout.activity_launch);
            mHandler.sendEmptyMessageDelayed(MSG_FINISH_LAUNCHERACTIVITY, 1000);
        }
        return d.getBoolean("isPatternOn",false);
}

    @Override
    protected boolean isPatternCorrect(List<PatternView.Cell> pattern) {
        //
        String patternSha1 = null;
        PrefsUtil d=new PrefsUtil(LaunchActivity.this, Constants.PatternLock,Context.MODE_PRIVATE);
        patternSha1=d.getString("sha1");
        boolean i=TextUtils.equals(PatternUtils.patternToSha1String(pattern), patternSha1);
        if (i){
            mHandler.sendEmptyMessageDelayed(MSG_FINISH_LAUNCHERACTIVITY2, 0);
        }else {
            //// : 16-1-28 Bug待解决
            return false;
        }
        return true;
    }

    @Override
    protected void onForgotPassword() {

        startActivity(new Intent(this, ResetPatternActivity.class));

        // Finish with RESULT_FORGOT_PASSWORD.
        super.onForgotPassword();
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("LaunchActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("LaunchActivity");
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
