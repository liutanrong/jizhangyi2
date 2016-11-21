package com.liu.Account.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.liu.Account.R;
import com.liu.Account.adapter.AllBillFragmentAdapter;
import com.liu.Account.commonUtils.LogUtil;
import com.liu.Account.database.Bill;
import com.liu.Account.model.AllBillFragmentData;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by deonte on 16-1-23.
 */
public class AllBillFragment extends Fragment{
    View view;
    private Activity activity;
    private ListView listView;
    private AllBillFragmentAdapter adapter;
    private List<AllBillFragmentData> groupDatas = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_all_bill_fragment,container,false);
        activity = getActivity();

        listView= (ListView) view.findViewById(R.id.allBill_list_fragment);
        adapter = new AllBillFragmentAdapter(activity,groupDatas);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initArray();

    }

    private void initArray() {
        groupDatas.clear();
        adapter.notifyDataSetChanged();
        Calendar calendar=Calendar.getInstance();
        List<Bill> billList=Bill.find(Bill.class,"is_delete=?",new String[]{"0"},null,"happen_time desc",null);

        for (int s=1;s<4;s++){
            AllBillFragmentData data=new AllBillFragmentData();
            data.setDateTime("1026-"+s);
            data.setMoneyType("支出");
            data.setTotalMoney(String.valueOf(s*s*s));
            groupDatas.add(data);
        }
        adapter.notifyDataSetChanged();

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
