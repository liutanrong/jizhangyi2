package com.liu.Account.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.liu.Account.BmobRespose.BmobUsers;
import com.liu.Account.Constants.Constants;
import com.liu.Account.R;
import com.liu.Account.activity.LoginActivity;
import com.liu.Account.commonUtils.DateUtil;
import com.liu.Account.commonUtils.LogUtil;
import com.liu.Account.commonUtils.PrefsUtil;
import com.liu.Account.commonUtils.ToastUtil;
import com.liu.Account.BmobNetwork.BmobNetworkUtils;
import com.liu.Account.database.Bill;
import com.liu.Account.network.BackupManager;
import com.liu.Account.utils.CSVUtil;
import com.liu.Account.utils.UserSettingUtil;
import com.umeng.analytics.MobclickAgent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * Created by deonte on 16-1-23.
 */
public class SyncFragment extends Fragment implements View.OnClickListener {

    View view;
    private Activity activity;
    private TextView lastUpdateTime;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_sync,container,false);

        activity=getActivity();
        initView();

        return view;
    }

    private void initView() {
        lastUpdateTime= (TextView) view.findViewById(R.id.sync_lastUpdateTime);
        Button update = (Button) view.findViewById(R.id.sync_update);
        Button downland = (Button) view.findViewById(R.id.sync_downland);
        Button export= (Button) view.findViewById(R.id.sync_export);
        Button import1= (Button) view.findViewById(R.id.sync_import);
        update.setOnClickListener(this);
        downland.setOnClickListener(this);
        export.setOnClickListener(this);
        import1.setOnClickListener(this);


    }


    @Override
    public void onStart() {
        super.onStart();
        Long userId= UserSettingUtil.getUserId(activity);
        PrefsUtil prefsUtil=new PrefsUtil(activity, Constants.UPDATE_TIME_SP, Context.MODE_PRIVATE);
        Long lastUpdateTim=prefsUtil.getLong("uploadTime");
        Long lastInsertTim=prefsUtil.getLong("insertTime");
        Long max=lastInsertTim<lastUpdateTim?lastUpdateTim:lastInsertTim;
        if (userId!=null&&max!=0){
            lastUpdateTime.setText(DateUtil.getStringByFormat(new Date(max),DateUtil.dateFormatYMDHM));
        }else {
            lastUpdateTime.setText("---------");
        }

    }

    @Override
    public void onClick(View v) {
        BmobUsers user=BmobUser.getCurrentUser(activity,BmobUsers.class);
        switch (v.getId()){
            case R.id.sync_update:{
                ////  16-1-26 上传数据
                if (user!=null){
                    BackupManager backupManager=new BackupManager(activity);
                    backupManager.uploadData(true);

                }else {
                    Dialog dialog =new AlertDialog.Builder(activity)
                            .setTitle("需要登陆")
                            .setMessage("登陆后才能进行同步操作,是否去登陆")
                            .setPositiveButton("去登录", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    activity.startActivity(new Intent(activity, LoginActivity.class));
                                }
                            })
                            .setNegativeButton("取消",null).create();
                    dialog.show();
                }
                break;
            }case R.id.sync_downland:{
                ////  16-1-26 下载数据
                if (user!=null){
                    BackupManager backupManager=new BackupManager(activity);
                    backupManager.downloadData();
                }else {
                    Dialog dialog =new AlertDialog.Builder(activity)
                            .setTitle("需要登陆")
                            .setMessage("登陆后才能进行同步操作,是否去登陆")
                            .setPositiveButton("去登录", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    activity.startActivity(new Intent(activity, LoginActivity.class));
                                }
                            })
                            .setNegativeButton("取消",null).create();
                    dialog.show();
                }



                break;

            }case R.id.sync_export:{
                Dialog dialog =new AlertDialog.Builder(activity)
                        .setTitle("导出数据")
                        .setMessage("导出的数据将存在sd卡jizhangyi目录下")
                        .setPositiveButton("导出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Cursor cursor= Bill.getCursor(Bill.class,null,null,null,null,null);
                                String filePath= Constants.FileName+"jizhangyiBackup"+DateUtil.getCurrentDate(DateUtil.dateFormatYMDHMS)+".csv";
                                boolean flag=CSVUtil.ExportToCSV(cursor,filePath);
                                if (flag){
                                    ToastUtil.showShort(activity,"导出成功,文件名为"+filePath);
                                }else {
                                    ToastUtil.showShort(activity,"导出失败");
                                }
                            }
                        })
                        .setNegativeButton("取消",null).create();
                dialog.show();

                break;
            }case R.id.sync_import:{
                Dialog dialog =new AlertDialog.Builder(activity)
                        .setTitle("导入数据")
                        .setMessage("本操作将删除您本地所有数据\n\n请将备份文件放置在sd卡jizhangyi目录下并重命名为jizhangyiBackup.csv")
                        .setPositiveButton("删除本地数据并导入", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String filePath= Constants.FileName+"jizhangyiBackup.csv";
                                List<Bill> billList=CSVUtil.importFromCSV(filePath);
                                if (billList!=null){

                                    Bill.deleteAll(Bill.class);
                                    Bill.saveInTx(billList);
                                    ToastUtil.showShort(activity,"导入成功");
                                }else {
                                    ToastUtil.showShort(activity,"导入失败");
                                }
                            }
                        })
                        .setNegativeButton("取消",null).create();
                dialog.show();

                break;
            }
        }
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SyncFragment"); //统计页面，"MainScreen"为页面名称，可自定义
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SyncFragment");
    }

}
