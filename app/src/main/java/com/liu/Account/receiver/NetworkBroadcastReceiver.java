package com.liu.Account.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.liu.Account.commonUtils.AppUtil;
import com.liu.Account.service.RepeatNetworkService;

/**
 * Created by tanrong on 16/7/7.
 */
public class NetworkBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (ACTION.equals(intent.getAction())) {
            int networkStatus=AppUtil.getCurrentNetWorkStatus(context);
//            if (networkStatus==AppUtil.NETWORK_CLASS_UNKNOWN){
//                Toast.makeText(context, "无网络！", Toast.LENGTH_SHORT).show();
//                return;
//            }
            if (networkStatus==AppUtil.NETWORK_CLASS_3_G||networkStatus==AppUtil.NETWORK_CLASS_4_G||networkStatus==AppUtil.NETWORK_WIFI){
                Intent intent1=new Intent(context, RepeatNetworkService.class);
                context.startService(intent1);
            }
//            if (networkStatus==AppUtil.NETWORK_CLASS_2_G) {
////                context.startService(intent2);
//                Toast.makeText(context, "手机网络2G连接成功！", Toast.LENGTH_SHORT).show();
//            } else if (networkStatus==AppUtil.NETWORK_CLASS_3_G) {
////                context.startService(intent2);
//                Toast.makeText(context, "手机网络3G连接成功！", Toast.LENGTH_SHORT).show();
//            } else if (networkStatus==AppUtil.NETWORK_CLASS_4_G) {
////                context.startService(intent2);
//                Toast.makeText(context, "手机网络4G连接成功！", Toast.LENGTH_SHORT).show();
//            } else if (networkStatus==AppUtil.NETWORK_WIFI) {
////                context.startService(intent2);
//                Toast.makeText(context, "手机网络WIFI连接成功！", Toast.LENGTH_SHORT).show();
//            }
        }
    }
}
