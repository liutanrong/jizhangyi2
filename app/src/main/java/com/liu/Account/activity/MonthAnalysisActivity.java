package com.liu.Account.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.liu.Account.Constants.TagConstats;
import com.liu.Account.R;
import com.liu.Account.commonUtils.DateUtil;
import com.liu.Account.commonUtils.LogUtil;
import com.liu.Account.database.Bill;
import com.zhy.autolayout.AutoLayoutActivity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by tanrong on 2016/11/21.
 */

public class MonthAnalysisActivity extends AutoLayoutActivity {


    private ImageView titleBack;
    private TextView topText;

    private PieChartView pieChart;
    private PieChartData pieChartData;
    private Button dateSelectButton;

    private Context mContext;

    TimePickerView pvTime;

    private Float totalOutMoney;
    private Date dateTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_analysis);
        mContext=MonthAnalysisActivity.this;
        initTop();


        //时间选择器
        pvTime = new TimePickerView(mContext, TimePickerView.Type.YEAR_MONTH);
        //控制时间范围
        Calendar calendar = Calendar.getInstance();
        pvTime.setRange(2015, calendar.get(Calendar.YEAR)+1);//要在setTime 之前才有效果哦
        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);
        pvTime.setTitle("请选择月份");

        //时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                initDate(date);
            }
        });
        pieChart= (PieChartView)findViewById(R.id.month_analysis_pieChart);
        dateSelectButton= (Button) findViewById(R.id.month_analysis_Date);
        dateSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pvTime.show();
            }
        });

    }
    private void initTop() {
        titleBack = (ImageView) findViewById(R.id.title_back);
        topText= (TextView) findViewById(R.id.title_text);
        topText.setText(R.string.menu_analysis);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        initDate(new Date());

    }

    private void initDate(Date date){


        pieChartData=new PieChartData();

        dateSelectButton.setText(DateUtil.getStringByFormat(date,DateUtil.dateFormatYmDot));

        long startTime= DateUtil.getFirstMonthDay(date);

        long endTime=DateUtil.getLastMonthDay(date);
        LogUtil.i("startTime:"+startTime);
        LogUtil.i("endTime:"+endTime);

        List<Bill> billList= Bill.find(Bill.class,"MONEY_TYPE=? and is_Delete=? and happen_time>=? and happen_time<=?",new String[]{"1","0",startTime+"",endTime+""},null,"HAPPEN_TIME desc",null);

        Map<String,Double> valueMap=new HashMap<>();
        for (String s: TagConstats.tagList){
            valueMap.put(s,0.0);
        }

        for (Bill bill:billList){
            String tag=bill.getTag();
            valueMap.put(tag,new BigDecimal(valueMap.get(tag)).add(bill.getSpendMoney()).doubleValue());
        }

        Iterator<Map.Entry<String, Double>> mapIterator=valueMap.entrySet().iterator();


        List<SliceValue> values = new ArrayList<SliceValue>();
        float total= (float) 0.0;
        while (mapIterator.hasNext()){
            Map.Entry<String,Double> entry=mapIterator.next();
            SliceValue sliceValue = new SliceValue(entry.getValue().floatValue(), ChartUtils.pickColor());
            sliceValue.setLabel(entry.getKey()+":"+entry.getValue());
            total+=entry.getValue().floatValue();
            values.add(sliceValue);
        }

        dateTime=date;
        totalOutMoney=total;
        generatePieChartData(values);
    }

    private void generatePieChartData(List<SliceValue> values) {


        pieChart.setValueSelectionEnabled(false);

        pieChart.setCircleFillRatio(1.0f);




        pieChartData = new PieChartData(values);
        pieChartData.setHasLabels(true);
        pieChartData.setHasLabelsOnlyForSelected(false);
        pieChartData.setHasLabelsOutside(false);
        pieChartData.setHasCenterCircle(true);


        pieChartData.setCenterText1(totalOutMoney.toString()+"元");

        Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Italic.ttf");

        pieChartData.setCenterText1Typeface(tf);
        pieChartData.setCenterText1FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                (int) getResources().getDimension(R.dimen.pie_chart_text1_size)));
        pieChart.setPieChartData(pieChartData);

        String s=DateUtil.getStringByFormat(dateTime,DateUtil.dateFormatYmDot);
        pieChartData.setCenterText2(s+"支出");

        pieChartData.setCenterText2Typeface(tf);
        pieChartData.setCenterText2FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                (int) getResources().getDimension(R.dimen.pie_chart_text2_size)));
    }

}
