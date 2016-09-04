package com.liu.Account.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.liu.Account.Constants.Constants;
import com.liu.Account.Constants.TagConstats;
import com.liu.Account.R;
import com.liu.Account.activity.LookBillActivity;
import com.liu.Account.adapter.AllBillListAdapter;
import com.liu.Account.commonUtils.LogUtil;
import com.liu.Account.database.Bill;
import com.liu.Account.model.AllBillListGroupData;
import com.liu.Account.model.HomeListViewData;
import com.liu.Account.utils.DatabaseUtil;
import com.liu.Account.utils.NumberUtil;
import com.orm.util.Collection;
import com.umeng.analytics.MobclickAgent;

import org.codehaus.jackson.map.util.Comparators;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by deonte on 16-1-23.
 */
public class AllBillFragment extends Fragment implements ExpandableListView.OnChildClickListener{
    View view;
    private Activity activity;
    private ExpandableListView listView;
    private AllBillListAdapter adapter;
    private List<AllBillListGroupData> groupDatas = new ArrayList<>();

    private DatabaseUtil db;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_all_bill,container,false);
        activity = getActivity();

        listView= (ExpandableListView) view.findViewById(R.id.allBill_list);
        adapter = new AllBillListAdapter(activity,groupDatas);
        listView.setAdapter(adapter);
        listView.setOnChildClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initArray();
        try{

            listView.expandGroup(0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initArray() {
        groupDatas.clear();
        adapter.notifyDataSetChanged();
        List<Bill> billList=Bill.find(Bill.class,"is_delete=?",new String[]{"0"},null,"happen_time desc",null);
        Set<String> yearAndMonthSet=new HashSet<>();
        for (Bill bill:billList){
            Date date=bill.getHappenTime();
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(date);
            String year=calendar.get(Calendar.YEAR)+"";
            String month=calendar.get(Calendar.MONTH)+"";
            yearAndMonthSet.add(year+","+month);
        }
        List<String> yearAndMonthList=new ArrayList<>();
        for (String entry:yearAndMonthSet){
            yearAndMonthList.add(entry);
        }

        Collections.sort(yearAndMonthList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int o1Year=Integer.valueOf(o1.split(",")[0]);
                int o2Year=Integer.valueOf(o2.split(",")[0]);
                if (o1Year>o2Year)
                    return -1;
                else if (o1Year<o2Year)
                    return 1;
                else
                    return 0;
            }
        });
        Collections.sort(yearAndMonthList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int o1Year=Integer.valueOf(o1.split(",")[0]);
                int o2Year=Integer.valueOf(o2.split(",")[0]);
                if(o1Year==o2Year){
                    int o1Month=Integer.valueOf(o1.split(",")[1]);
                    int o2Month=Integer.valueOf(o2.split(",")[1]);
                    if (o1Month>o2Month)
                        return -1;
                    else if (o1Month<o2Month)
                        return 1;
                    else
                        return 0;
                }
                return 0;
            }
        });
        for (String yearAndMonth:yearAndMonthList){
            String year =yearAndMonth.split(",")[0];
            String month =yearAndMonth.split(",")[1];
            String[] count=getCount(year, month,billList);
            //返回 count0 是收支金额  count1 是收入或支出 count2 为总支出 count3 为总收入

            List<HomeListViewData> child=new ArrayList<>();

            HomeListViewData d=new HomeListViewData();
            d.setTotalMoney(count[0]);
            d.setAllInMoney(count[3]);
            d.setAllOutMoney(count[2]);
            child.add(d);
            // child.add(d);
            initChild(year, month, child,billList);

            AllBillListGroupData entity =new AllBillListGroupData();
            entity.setMoneyType(count[1]);
            entity.setTotalMoney(count[0]);
            int monthh=Integer.valueOf(month)+1;
            entity.setAllTime(year + "年" + monthh + "月");
            entity.setChild(child);
            groupDatas.add(entity);
            adapter.notifyDataSetChanged();
        }

    }
    /**
     * @param year,month,child,order,way 参数
     *
     * **/

    private void initChild(String year, String month, List<HomeListViewData> child,List<Bill> billList) {
        Calendar calendarStart=Calendar.getInstance();
        calendarStart.set(Calendar.YEAR,Integer.valueOf(year));
        calendarStart.set(Calendar.MONTH,Integer.valueOf(month));
        calendarStart.set(Calendar.DAY_OF_MONTH,0);
        calendarStart.set(Calendar.HOUR,0);
        calendarStart.set(Calendar.MINUTE,0);
        calendarStart.set(Calendar.SECOND,0);

        int monthInt=Integer.valueOf(month);
        Calendar calendarEnd=Calendar.getInstance();
        if (monthInt<=11){

            calendarStart.set(Calendar.YEAR,Integer.valueOf(year));
            calendarStart.set(Calendar.MONTH,monthInt+1);
        }else {

            calendarStart.set(Calendar.YEAR,Integer.valueOf(year)+1);
            calendarStart.set(Calendar.MONTH,0);
        }
        calendarStart.set(Calendar.DAY_OF_MONTH,0);
        calendarStart.set(Calendar.HOUR,0);
        calendarStart.set(Calendar.MINUTE,0);
        calendarStart.set(Calendar.SECOND,0);

        for (Bill bill:billList){
            if (bill.getHappenTime().getTime()>=calendarStart.getTime().getTime()
                    &&bill.getHappenTime().getTime()<calendarEnd.getTime().getTime()){

                String remark = bill.getRemark();
                BigDecimal spendMoney =bill.getSpendMoney();

                String tag=bill.getTag();


                HomeListViewData data=new HomeListViewData();
                data.setGmtCreate(bill.getGmtCreate());
                data.setRemark(remark);
                data.setSpendMoney(spendMoney);
                data.setHappenTime(bill.getHappenTime());
                data.setMoneyType(bill.getMoneyType());
                data.setTag(tag);
                for (int i=0;i< TagConstats.tagList.length;i++){
                    if (tag.equals(TagConstats.tagList[i]))
                        data.setTagId(TagConstats.tagImage[i]);
                }
                child.add(data);

            }
        }
    }

    /**
     * @param month 要查询的月份
     * @param year 要查询的年份
     * @return count[4]  0 总收支 1 收支类别(收入或支出) 2 总支出 3 总收入
     * **/
    private String[] getCount(String year,String month,List<Bill> billList) {
        float InCount = 0;
        float OutCount = 0;
        float allCount;
        String[] count = new String[4];
        Calendar calendarStart=Calendar.getInstance();
        calendarStart.set(Calendar.YEAR,Integer.valueOf(year));
        calendarStart.set(Calendar.MONTH,Integer.valueOf(month));
        calendarStart.set(Calendar.DAY_OF_MONTH,0);
        calendarStart.set(Calendar.HOUR,0);
        calendarStart.set(Calendar.MINUTE,0);
        calendarStart.set(Calendar.SECOND,0);

        int monthInt=Integer.valueOf(month);


        Calendar calendarEnd=Calendar.getInstance();
        if (monthInt<=11){

            calendarStart.set(Calendar.YEAR,Integer.valueOf(year));
            calendarStart.set(Calendar.MONTH,monthInt+1);
        }else {

            calendarStart.set(Calendar.YEAR,Integer.valueOf(year)+1);
            calendarStart.set(Calendar.MONTH,0);
        }
        calendarStart.set(Calendar.DAY_OF_MONTH,0);
        calendarStart.set(Calendar.HOUR,0);
        calendarStart.set(Calendar.MINUTE,0);
        calendarStart.set(Calendar.SECOND,0);
       for (Bill bill:billList){
           if (bill.getHappenTime().getTime()>=calendarStart.getTime().getTime()
                   &&bill.getHappenTime().getTime()<calendarEnd.getTime().getTime()){

               String spendMoney =bill.getSpendMoney().toString();
               Integer moneyType=bill.getMoneyType();
               if (moneyType==null||moneyType==Bill.MONEY_TYPE_OUT) {
                   OutCount = OutCount + Float.parseFloat(spendMoney);
               } else {

                   InCount = InCount + Float.parseFloat(spendMoney);
               }
           }
       }

        allCount = OutCount - InCount;

        OutCount=NumberUtil.roundHalfUp(OutCount);
        allCount=NumberUtil.roundHalfUp(allCount);

        if (allCount > 0){
            count[1] = getString(R.string.MoneyOut);
        }else {
            count[1]=getString(R.string.MoneyIn);
        }

        allCount=Math.abs(allCount);


        if (allCount>10000){
            allCount= NumberUtil.roundHalfUp(allCount/10000);
            count[0] = String.valueOf(allCount)+"万";
        }else {
            allCount= NumberUtil.roundHalfUp(allCount);
            count[0] = String.valueOf(allCount);
        }

        if (OutCount>10000){
            OutCount= NumberUtil.roundHalfUp(OutCount/10000);
            count[2] = String.valueOf(OutCount)+"万";
        }else {
            OutCount= NumberUtil.roundHalfUp(OutCount);
            count[2]= String.valueOf(OutCount);
        }

        if (InCount>10000){
            InCount= NumberUtil.roundHalfUp(InCount/10000);
            count[3] = String.valueOf(InCount)+"万";
        }else {
            InCount= NumberUtil.roundHalfUp(InCount);
            count[3]= String.valueOf(InCount);
        }


        return count;
    }

    String unixTime,tag;
    String  date, remark, money,moneyType,creatTime=null;
    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        if (childPosition==0)
            return false;
        print("当前点击了第" + groupPosition + " "+childPosition);
        TextView n = (TextView) view.findViewById(R.id.unixTime);
        TextView da = (TextView) view.findViewById(R.id.dateInList);
        TextView re = (TextView) view.findViewById(R.id.remarkInList);
        TextView mo = (TextView) view.findViewById(R.id.spendMoneyInList);
        TextView sp= (TextView) view.findViewById(R.id.money_type);
        TextView cr= (TextView) view.findViewById(R.id.creatTime);
        TextView ta= (TextView) view.findViewById(R.id.tagText);
        unixTime = n.getText().toString();
        date = da.getText().toString();
        remark = re.getText().toString();
        money = mo.getText().toString();
        creatTime=cr.getText().toString();
        tag=ta.getText().toString();
        String mon=sp.getText().toString();
        if (mon.equals("+")){
            moneyType="收入";
        }else {
            moneyType="支出";
        }
        Bundle bundle=new Bundle();
        bundle.putString("money",money);
        bundle.putString("remark",remark);
        bundle.putString("date",date);
        bundle.putString("tag",tag);
        bundle.putString("moneyType",moneyType);
        bundle.putString("creatTime", creatTime);
        bundle.putString("unixTime", unixTime);
        Intent it=new Intent(activity,LookBillActivity.class);
        it.putExtras(bundle);
        activity.startActivity(it);

        return false;
    }
    private void print(String s){
        LogUtil.i(s);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("AllBillFragment"); //统计页面，"MainScreen"为页面名称，可自定义
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("AllBillFragment");
    }
}
