package com.liu.Account.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.liu.Account.R;
import com.liu.Account.commonUtils.DateUtil;
import com.liu.Account.database.Bill;
import com.liu.Account.model.HomeListViewData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liu on 15-10-11.
 * */
public class HomeListViewAdapter extends BaseAdapter {
    private Context context;
    private List<HomeListViewData> datas = new ArrayList<>();
    private ViewHolder holder = null;


    public HomeListViewAdapter(Context context, List<HomeListViewData> datas) {
        this.context = context;
        this.datas=datas;
    }

    @Override
    public int getCount() {
        if (datas == null)
            return 0;
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_home_list,null);
            holder=new ViewHolder();
            holder.unixTime= (TextView) convertView.findViewById(R.id.unixTime);
            holder.remarkInList=(TextView) convertView.findViewById(R.id.remarkInList);
            holder.dateInList=(TextView)convertView.findViewById(R.id.dateInList);
            holder.spendMoneyInList=(TextView)convertView.findViewById(R.id.spendMoneyInList);
            holder.moneyType= (TextView) convertView.findViewById(R.id.money_type);
            holder.creatTime= (TextView) convertView.findViewById(R.id.creatTime);
            holder.tag= (ImageView) convertView.findViewById(R.id.tag);
            holder.tagText= (TextView) convertView.findViewById(R.id.tagText);
            holder.uniqueFlag= (TextView) convertView.findViewById(R.id.uniqueFlag);
            convertView.setTag(holder);

        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        HomeListViewData entity = datas.get(position);

        holder.unixTime.setText(entity.getHappenTime().getTime()+"");
        holder.remarkInList.setText(entity.getRemark());
        String dateStr= DateUtil.getStringByFormat(entity.getHappenTime(),DateUtil.dateFormatYMDHMD);
        holder.dateInList.setText(dateStr==null?"------":dateStr);
        String money=entity.getSpendMoney().setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
        holder.spendMoneyInList.setText(money);
        String moneyType="";
        if (entity.getMoneyType()==null||entity.getMoneyType()== Bill.MONEY_TYPE_IN){
            moneyType=context.getString(R.string.MoneyInShow);
        }else if (entity.getMoneyType()==Bill.MONEY_TYPE_OUT){
            moneyType=context.getString(R.string.MoneyOutShow);
        }
        holder.moneyType.setText(moneyType);
        holder.creatTime.setText(entity.getGmtCreate().getTime()+"");
        holder.tag.setImageResource(entity.getTagId());
        holder.tagText.setText(entity.getTag());
        holder.uniqueFlag.setText(entity.getUniqueFlag());

        return convertView;
    }
    static class ViewHolder {
        TextView unixTime,remarkInList,dateInList,spendMoneyInList,moneyType,creatTime,uniqueFlag;
        ImageView tag;
        TextView tagText;
    }

}
