package com.liu.Account.Constants;

import com.liu.Account.commonUtils.PrefsUtil;

/**
 * Created by deonte on 16-1-25.
 */
public class Constants {
    public static String DBNAME="UNDEFAED";
    public static final String tableName="billdata";
    public static final String[] column=
            {"_Id","spendMoney","remark","date","unixTime","creatTime","moneyType","Tag","year_date","month_date","day_year"};
    public static final String AutoUpdatePrefsName ="syncFile";
    public static final String DefaultTag ="defaultTag";
    public static String AppSavePath=null;
    public static String FileName="jizhangyi/";

    public static final String PatternLock="patternLock";

    public static final String USER_SETTING_PRFIX="user_setting_sp";
    public static final String UPDATE_TIME_SP="update_time_sp";
    public static final String ERROR_NETWORK_SP="error_network_sp";

}
