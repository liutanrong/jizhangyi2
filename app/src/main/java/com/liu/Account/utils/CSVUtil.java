package com.liu.Account.utils;

import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import com.liu.Account.database.Bill;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by tanrong on 2016/11/15.
 */

public class CSVUtil {

    /**
     * 将数据导出到csv文件
     * @param c
     * @param filePath
     * @return
     */
    public static boolean ExportToCSV(Cursor c, String filePath) {
        boolean flag=false;
        int rowCount = 0;
        int colCount = 0;
        OutputStreamWriter fw;
        BufferedWriter bfw;
        File sdCardDir = Environment.getExternalStorageDirectory();
        File saveFile = new File(sdCardDir, filePath);
        try {

            rowCount = c.getCount();
            colCount = c.getColumnCount();
            fw = new OutputStreamWriter(new FileOutputStream(saveFile, true),"UTF-8");

            bfw = new BufferedWriter(fw);

            if (rowCount > 0) {
                c.moveToFirst();
                // 写入表头
                for (int i = 0; i < colCount; i++) {
                    if (i != colCount - 1)
                        bfw.write(c.getColumnName(i) + ',');
                    else
                        bfw.write(c.getColumnName(i));
                }
                // 写好表头后换行
                bfw.newLine();
                // 写入数据
                for (int i = 0; i < rowCount; i++) {
                    c.moveToPosition(i);
                    // Toast.makeText(mContext, "正在导出第"+(i+1)+"条",
                    // Toast.LENGTH_SHORT).show();
                    Log.v("导出数据", "正在导出第" + (i + 1) + "条");
                    for (int j = 0; j < colCount; j++) {
                        if (j != colCount - 1)
                            bfw.write(c.getString(j) + ',');
                        else
                            bfw.write(c.getString(j));
                    }
                    // 写好每条记录后换行
                    bfw.newLine();
                }
            }
            // 将缓存数据写入文件
            bfw.flush();
            // 释放缓存
            bfw.close();
            // Toast.makeText(mContext, "导出完毕！", Toast.LENGTH_SHORT).show();
            Log.v("导出数据", "导出完毕！");
            flag=true;
        } catch (IOException e) {
            //  Auto-generated catch block
            e.printStackTrace();
        } finally {
            c.close();
        }
        return flag;
    }


    public  static List<Bill> importFromCSV(String filePath){
        File sdCardDir = Environment.getExternalStorageDirectory();
        File file=new File(sdCardDir,filePath);
        if (!file.exists()){
            return null;
        }
        List<Bill> billList=new ArrayList<>();
        try {
            BufferedReader br=new BufferedReader(new FileReader(file));
            String line="";
            String title=br.readLine();
            if (!checkCSVFile(title)){
                return null;
            }

            ArrayList<String> lists=new ArrayList<String>();
            while ((line = br.readLine()) != null) {//一次一行，lists.size()=14,28,42...
                // 把一行数据分割成多个字段
                lists.clear();
                StringTokenizer st = new StringTokenizer(line, ",");
                while(st.hasMoreTokens()){//一次一个 lists.size()=1
                    String str=st.nextToken();
                    lists.add(str);
                }

                Bill bill=new Bill();
                if (!lists.get(1).equalsIgnoreCase("null"))
                    bill.setDeleteTime(new Date(Long.valueOf(lists.get(1))));
                bill.setGmtCreate(new Date(Long.valueOf(lists.get(2))));
                bill.setGmtModified(new Date(Long.valueOf(lists.get(3))));
                bill.setHappenTime(new Date(Long.valueOf(lists.get(4))));
                bill.setInstallationId(Long.valueOf(lists.get(5)));
                if (lists.get(6).equalsIgnoreCase("0"))
                    bill.setIsDelete(false);
                else
                    bill.setIsDelete(true);
                bill.setMoneyType(Integer.valueOf(lists.get(7)));
                bill.setRemark(lists.get(8));
                bill.setSpendMoney(new BigDecimal(lists.get(9)));
                bill.setTag(lists.get(10));
                if (!lists.get(11).equalsIgnoreCase("null"))
                    bill.setTagId(Integer.valueOf(lists.get(11)));
                bill.setUniqueFlag(lists.get(12));
                bill.setUserId(Long.valueOf(lists.get(13)));
                billList.add(bill);


            }
        } catch (IOException e) {
            e.printStackTrace();
            billList=null;
        }
        return billList;
    }
    private static boolean checkCSVFile(String titleLine){
        String title="ID,DELETE_TIME,GMT_CREATE,GMT_MODIFIED,HAPPEN_TIME,INSTALLATION_ID,IS_DELETE,MONEY_TYPE,REMARK,SPEND_MONEY,TAG,TAG_ID,UNIQUE_FLAG,USER_ID";
        return title.equalsIgnoreCase(titleLine);
    }
}
