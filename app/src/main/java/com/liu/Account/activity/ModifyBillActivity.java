package com.liu.Account.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liu.Account.BmobNetwork.BmobNetworkUtils;
import com.liu.Account.Constants.Constants;
import com.liu.Account.Constants.TagConstats;
import com.liu.Account.R;
import com.liu.Account.adapter.AddBillTagAdapter;
import com.liu.Account.commonUtils.DateUtil;
import com.liu.Account.commonUtils.LogUtil;
import com.liu.Account.commonUtils.ToastUtil;
import com.liu.Account.database.Bill;
import com.liu.Account.initUtils.StatusBarUtil;
import com.liu.Account.model.AddBillData;
import com.liu.Account.model.AddBillTagData;
import com.liu.Account.utils.DatabaseUtil;
import com.liu.Account.utils.HttpUtil;
import com.liu.Account.utils.NumberUtil;
import com.liu.Account.utils.UserSettingUtil;
import com.orm.SugarDb;
import com.orm.util.SugarCursor;
import com.squareup.timessquare.CalendarPickerView;
import com.umeng.analytics.MobclickAgent;
import com.zhy.autolayout.AutoLayoutActivity;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobUser;

/**
 * Created by deonte on 16-1-25.
 */
public class ModifyBillActivity extends AutoLayoutActivity {
    private Context context;
    private ImageView titleBack;
    private TextView topText;

    private RadioGroup typeRadio;
    private EditText moneyEdt;
    private EditText remarkEdt;
    private LinearLayout dateLin;
    private LinearLayout tagLin;
    private TextView dateText;
    private TextView tagText;
    private ImageView tagImage;
    private Button confirmBtn;


    AddBillData data=new AddBillData(true);

    private AlertDialog tagDialog;
    private ListView tagList;

    private DatabaseUtil db;
    private int year_i,month_i,day_i;
    private String unixTime,creatTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bill);


        StatusBarUtil.setTransparentStatusBar(this);

        context = ModifyBillActivity.this;
        initTop();//初始化顶部栏
        initTagDialog();
        initView();
        db=new DatabaseUtil(context, Constants.DBNAME,1);
    }
    //选择tag的弹出框
    private void initTagDialog() {
        List<AddBillTagData> datas=new ArrayList<>();
        AddBillTagAdapter adapter=new AddBillTagAdapter(context,datas);

        LinearLayout tagSelectLayout= (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_add_bill_tag, null);
        tagList= (ListView) tagSelectLayout.findViewById(R.id.list_add_bill_tag);
        tagList.setAdapter(adapter);
        for (int i=0;i< TagConstats.tagList.length;i++){
            AddBillTagData data=new AddBillTagData();
            data.setText(TagConstats.tagList[i]);
            data.set_id(TagConstats.tagImage[i]);
            datas.add(data);
        }
        adapter.notifyDataSetChanged();

        tagList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.item_add_bill_tag_list_text);
                ImageView imageView = (ImageView) view.findViewById(R.id.item_add_bill_tag_list_image);
                data.setTag(textView.getText().toString());

                tagText.setText(textView.getText().toString());
                tagImage.setImageDrawable(imageView.getDrawable());
                tagDialog.dismiss();
            }
        });

        tagDialog=new AlertDialog.Builder(context).setView(tagSelectLayout).create();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent it=getIntent();
        Bundle bundle=it.getExtras();

        //金额
        moneyEdt.setText(bundle.getString("money"));
        //时间
        unixTime=bundle.getString("unixTime");
        creatTime=bundle.getString("creatTime");
        long unixTim=Long.parseLong(unixTime);
        try {

            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy");
            year_i=Integer.valueOf(mSimpleDateFormat.format(unixTim));
            SimpleDateFormat m = new SimpleDateFormat("MM");
            month_i=Integer.valueOf(m.format(unixTim));
            SimpleDateFormat d = new SimpleDateFormat("dd");
            day_i=Integer.valueOf(d.format(unixTim));
        } catch (Exception e) {
            e.printStackTrace();
        }
        data.setYear(year_i);
        data.setMonth(month_i);
        data.setDayOfMonth(day_i);
        dateText.setText(year_i+"/"+month_i+"/"+day_i);
        //备注
        remarkEdt.setText(bundle.getString("remark"));
        //tag
        for (int i=0;i<TagConstats.tagList.length;i++)
            if (bundle.getString("tag").equals(TagConstats.tagList[i]))
                changeTag(i);
        //moneyType
        RadioButton out= (RadioButton) findViewById(R.id.add_bill_money_out);
        RadioButton in= (RadioButton) findViewById(R.id.add_bill_money_in);
        if (bundle.getString("moneyType").equals(getResources().getString(R.string.MoneyIn))){
            in.setChecked(true);
            out.setChecked(false);
        }else {
            in.setChecked(false);
            out.setChecked(true);
        }
    }

    /**
     * 将tag更改为指定位置的tag
     * */
    private void changeTag(int position) {
        data.setTag(TagConstats.tagList[position]);
        tagText.setText(TagConstats.tagList[position]);
        tagImage.setImageResource(TagConstats.tagImage[position]);
    }

    private void initTop() {
        titleBack = (ImageView) findViewById(R.id.title_back);
        topText= (TextView) findViewById(R.id.title_text);
        topText.setText(R.string.modifyBillActivity);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {

        typeRadio= (RadioGroup) findViewById(R.id.add_bill_type);
        moneyEdt= (EditText) findViewById(R.id.add_bill_money);
        remarkEdt= (EditText) findViewById(R.id.add_bill_remark);
        dateLin= (LinearLayout) findViewById(R.id.add_bill_date_lin);
        tagLin= (LinearLayout) findViewById(R.id.add_bill_tag_lin);
        dateText= (TextView) findViewById(R.id.add_bill_date);
        tagText= (TextView) findViewById(R.id.add_bill_tag);
        tagImage= (ImageView) findViewById(R.id.add_bill_tag_pic);
        confirmBtn= (Button) findViewById(R.id.add_bill_confirm);


    }
    public void click(View v){
        switch (v.getId()){
            case R.id.add_bill_date_lin:{
                // 点击日期框事件 选择日期
                final CalendarPickerView dialogView= (CalendarPickerView) getLayoutInflater().inflate(R.layout.dialog_timepick,null,false);
                Calendar startTime=Calendar.getInstance();
                startTime.add(Calendar.YEAR,-100);
                Calendar endTime = Calendar.getInstance();
                endTime.add(Calendar.YEAR, 1);

                Calendar calendar=Calendar.getInstance();
                try{
                    calendar.set(Calendar.YEAR,data.getYear());
                    calendar.set(Calendar.MONTH,data.getMonth());
                    calendar.set(Calendar.DAY_OF_MONTH,data.getDayOfMonth());
                }catch (Exception e){
                    e.printStackTrace();
                }
                calendar.add(Calendar.MONTH,-1);
                dialogView.init(startTime.getTime(),
                        endTime.getTime()) //
                        .inMode(CalendarPickerView.SelectionMode.SINGLE)
                        .withSelectedDate(calendar.getTime());
                //
                AlertDialog theDialog = new AlertDialog.Builder(context) //
                        .setTitle("请选取日期")
                        .setView(dialogView)
                        .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                long sd=dialogView.getSelectedDate().getTime();
                                String tt=DateUtil.getStringByFormat(sd, DateUtil.dateFormatYMDD);
                                dateText.setText(tt);

                                Calendar calendar=Calendar.getInstance();
                                calendar.setTimeInMillis(sd);
                                calendar.add(Calendar.MONTH,1);
                                data.setYear(calendar.get(Calendar.YEAR));
                                data.setDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
                                data.setMonth(calendar.get(Calendar.MONTH));
                                data.setIsSelectTime(true);
                                //Toast.makeText(getApplicationContext(), sd + "", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .create();
                theDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        //Log.d(TAG, "onShow: fix the dimens!");
                        dialogView.fixDialogDimens();
                    }
                });
                theDialog.show();

                break;
            }case R.id.add_bill_tag_lin:{
                // 16-1-24 点击标签对话框事件 选择标签
                tagDialog.show();//弹出对话框解决
                break;
            }case R.id.add_bill_confirm:{
                //  点击确定事件 提交
                confirm();
                break;
            }
        }
    }

    private void confirm() {
        Calendar calendar=Calendar.getInstance();

        //选择账单类别 收入还是支出
        if (typeRadio.getCheckedRadioButtonId()==R.id.add_bill_money_in)//收入
            data.setType(Bill.MONEY_TYPE_IN);
        else//支出
            data.setType(Bill.MONEY_TYPE_OUT);

        //账单金额
        if (moneyEdt.getText().toString().length()==0){
            ToastUtil.showShort(context, R.string.addBillMoneyNull);
            return;
        }else {
            String temp=moneyEdt.getText().toString().trim();
            data.setMoney(new BigDecimal(NumberUtil.roundHalfUp(temp)));
        }

        //账单备注
        data.setRemark(remarkEdt.getText().toString().trim());


        //当前日期是否为选择日期
        if (data.getIsSelectTime())
        {//选择过日期
            Calendar c=Calendar.getInstance();
            c.set(Calendar.YEAR,data.getYear());
            c.set(Calendar.MONTH,data.getMonth()-1);
            c.set(Calendar.DAY_OF_MONTH,data.getDayOfMonth());
            data.setHappenTime(new Date(c.getTimeInMillis()));
            data.setCreateTime(new Date(c.getTimeInMillis()));
        }else {//没有选择日期
            data.setHappenTime(new Date(Long.parseLong(unixTime)));
            data.setCreateTime(new Date(Long.parseLong(unixTime)));
        }

        LogUtil.i(
                "\n年：" + data.getYear() + "  月:" + data.getMonth() + "  日：" + data.getDayOfMonth() +
                "\n账单类型:" + data.getType() +
                "\n账单标签:" + data.getTag() +
                "\n账单金额:" + data.getMoney() +
                "\n账单备注:" + data.getRemark());

        List<Bill> billList=Bill.find(Bill.class,"HAPPEN_TIME=?",new String[]{data.getHappenTime().getTime()+""});
        Bill bill=new Bill();
        if (billList!=null&&billList.size()>0)
            bill=billList.get(0);

        bill.setSpendMoney(data.getMoney());
        bill.setRemark(data.getRemark());
        bill.setHappenTime(data.getHappenTime());
        bill.setGmtCreate(data.getCreateTime());
        bill.setIsDelete(false);
        bill.setTag(data.getTag());
        bill.setGmtModified(new Date());
        bill.setInstallationId(UserSettingUtil.getInstallationId(context));
        bill.setUserId(UserSettingUtil.getUserId(context));
        bill.setMoneyType(data.getType());
        bill.save();




        ////  16-1-25 在云端修改
        //// 16-1-28 修改账单
        Map<String,String> map = new HashMap<String,String>();
        try{
            BmobUser user=BmobUser.getCurrentUser(context);

            map.put("type",user.getObjectId()+"修改账单");
        }catch (Exception e){
            e.printStackTrace();
            map.put("type","修改账单");
        }
        MobclickAgent.onEventValue(context, "modefiAccount", map, 0);
        JSONObject dataJson=new JSONObject();
        dataJson.put("creatTime",data.getCreateTime());
        dataJson.put("type",data.getType());
        dataJson.put("tag",data.getTag());
        dataJson.put("money",data.getMoney());
        dataJson.put("remark",data.getRemark());
        HttpUtil.sendEventLog(context,HttpUtil.EVENT_MODIFY, JSON.toJSONString(dataJson));



        Thread thread=new Thread() {
            @Override
            public void run() {
                super.run();
                BmobNetworkUtils d = new BmobNetworkUtils(context);
                d.upDatesToBmob(context,false);
            }
        };
        thread.run();
        finish();
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ModifyBillActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ModifyBillActivity");
        MobclickAgent.onPause(this);
    }
    /**
     * 返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
        }
        return false;
    }
}