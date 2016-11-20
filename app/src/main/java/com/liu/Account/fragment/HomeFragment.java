package com.liu.Account.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.liu.Account.Constants.Constants;
import com.liu.Account.Constants.TagConstats;
import com.liu.Account.R;
import com.liu.Account.activity.AddBillActivity;
import com.liu.Account.activity.LookBillActivity;
import com.liu.Account.adapter.HomeListViewAdapter;
import com.liu.Account.commonUtils.DateUtil;
import com.liu.Account.commonUtils.LogUtil;
import com.liu.Account.database.Bill;
import com.liu.Account.model.HomeListViewData;
import com.liu.Account.utils.DatabaseUtil;
import com.liu.Account.utils.NumberUtil;
import com.umeng.analytics.MobclickAgent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by deonte on 16-1-23.
 */
public class HomeFragment extends Fragment implements View.OnClickListener,AdapterView.OnItemClickListener{
    private Activity activity;

    View view;
    private FloatingActionButton add;

    private ListView listView;
    private HomeListViewAdapter adapter;
    private List<HomeListViewData> mDataArrays = new ArrayList<>();

    private TextView moneyIn,moneyOut,moneyAll,date;
    private int currentYear,currentMonth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_home, container, false);

        activity=getActivity();
        initView();//初始化控件并设置点击
        return view;
    }


    private void initView() {

        add= (FloatingActionButton) view.findViewById(R.id.add);
        add.setOnClickListener(this);

        moneyAll= (TextView) view.findViewById(R.id.home_money);
        moneyIn= (TextView) view.findViewById(R.id.home_money_in);
        moneyOut= (TextView) view.findViewById(R.id.home_money_out);
        date= (TextView) view.findViewById(R.id.home_date);

        listView= (ListView) view.findViewById(R.id.home_listview);
        adapter=new HomeListViewAdapter(activity,mDataArrays);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        moneyIn.setText(getResources().getText(R.string.homeMoneyDefault));
        moneyOut.setText(getResources().getText(R.string.homeMoneyDefault));
        moneyAll.setText(getResources().getText(R.string.homeMoneyDefault));


        mDataArrays.clear();
        Calendar calendar=Calendar.getInstance();
        currentYear=calendar.get(Calendar.YEAR);
        currentMonth=calendar.get(Calendar.MONTH)+1;
        date.setText(currentYear + "." + currentMonth);


        List<Bill> billList= Bill.find(Bill.class,"is_Delete=? and happen_time>=?",new String[]{"0",DateUtil.getFirstDayOfMonth()+""},null,"HAPPEN_TIME desc",null);
        LogUtil.i("首页数据");
        initArray(billList);
        getMoneyCount(billList);
    }

    private void getMoneyCount(List<Bill> billList) {
        if (billList==null||billList.size()==0)
            return;
        BigDecimal _moneyIn=new BigDecimal(0);
        BigDecimal _moneyOut=new BigDecimal(0);
        BigDecimal _moneyAll=new BigDecimal(0);

        for (Bill bill:billList){
            BigDecimal spendMoney =bill.getSpendMoney();
            Integer moneyType=bill.getMoneyType();
            if (moneyType==null||moneyType==Bill.MONEY_TYPE_OUT){
                _moneyOut=_moneyOut.add(spendMoney);
            }else if (moneyType==Bill.MONEY_TYPE_IN){
                _moneyIn=_moneyIn.add(spendMoney);
            }

            _moneyAll=_moneyIn.subtract(_moneyOut);
        }

        if (_moneyIn.floatValue()>10000||_moneyIn.floatValue()<-10000){
            String temp=String.valueOf(NumberUtil.roundHalfUp(_moneyIn.floatValue()/ 10000));
            moneyIn.setText(temp+"万");
        }else {
            moneyIn.setText(String.valueOf(NumberUtil.roundHalfUp(_moneyIn.floatValue())));
        }

        if (_moneyOut.floatValue()>10000||_moneyOut.floatValue()<-10000){
            String s=String.valueOf(NumberUtil.roundHalfUp(_moneyOut.floatValue()/10000));
            moneyOut.setText(s+"万");
        }else {
            moneyOut.setText(String.valueOf(NumberUtil.roundHalfUp(_moneyOut.floatValue())));
        }

        if (_moneyAll.floatValue()>10000||_moneyAll.floatValue()<-10000){
            String ss=String.valueOf(NumberUtil.roundHalfUp(_moneyAll.floatValue()/10000));
            moneyAll.setText(ss+"万");
        }else {

            moneyAll.setText(String.valueOf(NumberUtil.roundHalfUp(_moneyAll.floatValue())));
        }
        LogUtil.i("收入:" + _moneyIn + "\n支出：" + _moneyOut + "\n总计:" + _moneyAll);
    }



    /**
     * 初始化本地数据库内数据
     * **/
    private void initArray(List<Bill> billList) {
        if (billList==null||billList.size()==0)
            return;
        LogUtil.i(currentYear + "." + currentMonth + "数据共有" +billList.size() + "条");
        for (Bill bill:billList){
            String remark = bill.getRemark();
            BigDecimal spendMoney =bill.getSpendMoney();

            String tag=bill.getTag();
            addToList(bill.getGmtCreate(),remark,spendMoney,bill.getHappenTime(),bill.getMoneyType(),tag,bill.getUniqueFlag());
        }
        adapter.notifyDataSetChanged();
    }
    /**
     * 添加数据到列表
     * **/
    private void addToList(Date createDate,String remark,BigDecimal spendMoney,Date happenTime,int moneyType,String tag,String uniqueFlag){

        HomeListViewData data=new HomeListViewData();
        data.setGmtCreate(createDate);
        data.setRemark(remark);
        data.setSpendMoney(spendMoney);
        data.setHappenTime(happenTime);
        data.setMoneyType(moneyType);
        data.setTag(tag);
        data.setUniqueFlag(uniqueFlag);
        for (int i=0;i< TagConstats.tagList.length;i++){
            if (tag.equals(TagConstats.tagList[i]))
                data.setTagId(TagConstats.tagImage[i]);
        }

        mDataArrays.add(data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add:{
                //添加的点击事件
                startActivity(new Intent(activity, AddBillActivity.class));
            }
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView uniqueFlag = (TextView) view.findViewById(R.id.uniqueFlag);

        String uniqueFlagStr = uniqueFlag.getText().toString();
        LogUtil.e("uniqueFlagStr:"+uniqueFlagStr);
        Intent it=new Intent(activity,LookBillActivity.class);
        it.putExtra("uniqueFlag",uniqueFlagStr);
        activity.startActivity(it);
     }
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("HomeFragment"); //统计页面，"MainScreen"为页面名称，可自定义
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("HomeFragment");
    }
}
