package com.liu.Account.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import com.squareup.timessquare.CalendarPickerView;
import com.umeng.analytics.MobclickAgent;
import com.zhy.autolayout.AutoLayoutActivity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobUser;

/**
 * Created by deonte on 16-1-24.
 */
public class AddBillActivity extends AutoLayoutActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bill);


        StatusBarUtil.setTransparentStatusBar(this);

        context = AddBillActivity.this;
        initTop();//初始化顶部栏
        initTagDialog();
        initView();
        initArray();
    }
    //选择tag的弹出框
    private void initTagDialog() {
        List<AddBillTagData> datas=new ArrayList<>();
        AddBillTagAdapter adapter=new AddBillTagAdapter(context,datas);

        LinearLayout tagSelectLayout= (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_add_bill_tag, null);
        tagList= (ListView) tagSelectLayout.findViewById(R.id.list_add_bill_tag);
        tagList.setAdapter(adapter);
        for (int i=0;i<TagConstats.tagList.length;i++){
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

    private void initArray() {
        data.setHappenTime(new Date());
        String temp=DateUtil.getStringByFormat(data.getHappenTime(),DateUtil.dateFormatYMDD);
        dateText.setText(temp);

        changeTag(TagConstats.defaultTag);
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
        topText.setText(R.string.addBill);
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
                calendar.setTime(data.getHappenTime());

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
                                data.setHappenTime(dialogView.getSelectedDate());


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

        //creatTime
        data.setCreateTime(new Date(calendar.getTimeInMillis()));



        String log=
                "\n日期："+DateUtil.getStringByFormat(data.getHappenTime(),DateUtil.dateFormatYMDD)+
                "\n账单类型:"+data.getType()+
                "\n账单标签:"+data.getTag() +
                "\n账单金额:"+data.getMoney()+
                "\n账单备注:"+data.getRemark();
        LogUtil.i(log);
        //{"_Id","spendMoney","remark","date","unixTime","creatTime","moneyType","Tag","year_date","month_date","day_year"};
        ////  16-1-25 写入数据库


        Bill bill=new Bill();
        bill.setSpendMoney(data.getMoney());
        bill.setRemark(data.getRemark());
        bill.setHappenTime(data.getHappenTime());
        bill.setGmtCreate(data.getCreateTime());
        bill.setGmtModified(new Date());
        bill.setIsDelete(false);
        bill.setTag(data.getTag());
        bill.setInstallationId(UserSettingUtil.getInstallationId(context));
        bill.setUserId(UserSettingUtil.getUserId(context));
        bill.setMoneyType(data.getType());
        String uniqueFlag=bill.getInstallationId()+"_"+DateUtil.getStringByFormat(bill.getGmtCreate(),"yyyy_MM_dd_HH_mm_ss");
        bill.setUniqueFlag(uniqueFlag);

        bill.save();

        String logg=
                        " 账单类型:"+data.getType()+
                        " 账单标签:"+data.getTag() +
                        " 账单金额:"+data.getMoney()+
                        " 账单备注:"+data.getRemark();
        Log.i("上传字节数:", "" + logg.getBytes().length);
        LogUtil.i("上传字节数:" + logg.getBytes().length);
        if (logg.getBytes().length>200){
            Map<String,String> mapP = new HashMap<String,String>();
            try{
                BmobUser users=BmobUser.getCurrentUser(context);

                mapP.put("type",users.getObjectId()+"  新增  (字节超过200)");
            }catch (Exception e){
                e.printStackTrace();
                mapP.put("type","  新增  (字节超过200)");
            }
            MobclickAgent.onEventValue(context, "addAccountBig", mapP, 0);
        }
        Map<String,String> map = new HashMap<String,String>();
        try{
            BmobUser user=BmobUser.getCurrentUser(context);

            map.put("type",user.getObjectId()+"  新增  "+logg);
        }catch (Exception e){
            e.printStackTrace();
            map.put("type","新增  "+logg);
        }
        MobclickAgent.onEventValue(context, "addAccount", map, 0);


        JSONObject dataJson=new JSONObject();
        dataJson.put("creatTime",data.getCreateTime().getTime());
        dataJson.put("type",data.getType());
        dataJson.put("tag",data.getTag());
        dataJson.put("money",data.getMoney());
        dataJson.put("remark",data.getRemark());
        HttpUtil.sendEventLog(context,HttpUtil.EVENT_ADD, JSON.toJSONString(dataJson));
        finish();
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("AddBillActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("AddBillActivity");
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
