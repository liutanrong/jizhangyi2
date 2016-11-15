package com.liu.Account.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.liu.Account.R;
import com.liu.Account.model.AllBillFragmentData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanrong on 16/10/17.
 */

public class AllBillFragmentAdapter extends BaseAdapter {
    private Context context;
    private List<AllBillFragmentData> datas = new ArrayList<>();
    private ViewHolder holder = null;

    public AllBillFragmentAdapter(Context context, List<AllBillFragmentData> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        if (datas==null)return 0;
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (convertView==null){
            convertView=View.inflate(context, R.layout.item_fragment_allbill_group,null);
            holder =new ViewHolder();
            holder.moneyType= (TextView) convertView.findViewById(R.id.money_type);
            holder.totalMoney=(TextView)convertView.findViewById(R.id.total_money);
            holder.dateTime= (TextView) convertView.findViewById(R.id.allTime);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        AllBillFragmentData entity=this.datas.get(i);
        if (entity!=null){
            holder.moneyType.setText(entity.getMoneyType());
            holder.totalMoney.setText(entity.getTotalMoney());
            holder.dateTime.setText(entity.getDateTime());
        }
        return convertView;
    }
    private static class ViewHolder {
        TextView totalMoney,dateTime,moneyType;
    }
}
