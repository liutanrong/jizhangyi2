package com.liu.Account.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.liu.Account.Constants.Constants;
import com.liu.Account.commonUtils.LogUtil;
import com.liu.Account.commonUtils.PrefsUtil;

/**
 * Created by deonte on 16-1-26.
 */
public class LocationUtils {
    /**
     * 获取位置，优先返回GPS定位的位置信息，其次返回网络定位的位置信息
     *
     * @param context
     * @return
     */
    public static Location getLocation(Context context) {
        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // : Consider calling
                Location location1 = locationManager
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location location2 = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                return location1 == null ? location2 : location1;
            }
        }catch (Exception e){

        }
        return null;
    }
    public static String getAmapLocation(final Context context){
        //声明AMapLocationClient类对象
        AMapLocationClient mLocationClient = null;
        //声明定位回调监听器
        AMapLocationListener mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                StringBuilder s=new StringBuilder();
                s.append(aMapLocation.getLongitude());
                s.append(",");
                s.append(aMapLocation.getLatitude());
                PrefsUtil prefsUtil=new PrefsUtil(context, Constants.AutoUpdatePrefsName,Context.MODE_PRIVATE);
                prefsUtil.putString("lastLocation",s.toString());
                LogUtil.i("amapLocatopn:"+s.toString());
            }

        };
        //初始化定位
        mLocationClient = new AMapLocationClient(context);
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //声明mLocationOption对象
        AMapLocationClientOption mLocationOption = null;
//初始化定位参数
        mLocationOption = new AMapLocationClientOption();
//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
//设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(false);
//设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);

        if(mLocationOption.isOnceLocationLatest()){
            mLocationOption.setOnceLocationLatest(true);
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。
        //如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会。
        }

        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(false);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(20000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
        PrefsUtil prefsUtil=new PrefsUtil(context, Constants.AutoUpdatePrefsName,Context.MODE_PRIVATE);
        return prefsUtil.getString("lastLocation",null);
    }
}
