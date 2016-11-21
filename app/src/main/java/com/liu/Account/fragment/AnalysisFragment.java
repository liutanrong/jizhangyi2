package com.liu.Account.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.liu.Account.R;
import com.liu.Account.activity.MonthAnalysisActivity;
import com.liu.Account.commonUtils.DateUtil;
import com.liu.Account.database.Bill;
import com.umeng.analytics.MobclickAgent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by deonte on 16-1-23.
 */
public class AnalysisFragment extends Fragment {
    View view;

    private ColumnChartView columnChart;
    private ColumnChartData columnChartData;

    private Button anlysis2MonthAnalysisBtn;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_analysis,container,false);
        columnChart = (ColumnChartView) view.findViewById(R.id.analysis_columnChart);
        columnChart.setValueTouchEnabled(false);
        columnChart.setZoomEnabled(false);

        anlysis2MonthAnalysisBtn= (Button) view.findViewById(R.id.analysis_2month);
        anlysis2MonthAnalysisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), MonthAnalysisActivity.class);
                getActivity().startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        int numColumns = 6;
        long endTime=DateUtil.getLastMonthDay(new Date());


        List<Column> columns = new ArrayList<Column>();

        List<SubcolumnValue> values;
        Date currentDate=null;
        for (int i = numColumns; i > 0; i--) {
            currentDate=new Date(endTime-(i-1)*2678400000L);

            InAndOutMoney money=totalValue(currentDate);

            values = new ArrayList<SubcolumnValue>();
            values.add(new SubcolumnValue(money.getInMoney(), ChartUtils.COLOR_RED));
            values.add(new SubcolumnValue(money.getOutMoney()*-1, ChartUtils.COLOR_GREEN));

            values.get(0).setLabel(getMonthFromDate(currentDate)+"\n"+money.getInMoney());
            Column column = new Column(values);
            column.setHasLabels(true);
            column.setHasLabelsOnlyForSelected(false);
            columns.add(column);
        }

        generateNegativeStackedData(columns);
    }
    private String getMonthFromDate(Date date){
        Calendar calendar=new GregorianCalendar();
        calendar.setTime(date);
        return (calendar.get(GregorianCalendar.MONTH)+1)+"月";
    }

    private InAndOutMoney totalValue(Date date){
        BigDecimal inMoney=new BigDecimal(0);
        BigDecimal outMoney=new BigDecimal(0);
        long startTime= DateUtil.getFirstMonthDay(date);

        long endTime=DateUtil.getLastMonthDay(date);

        List<Bill> billList= Bill.find(Bill.class," is_Delete=? and happen_time>=? and happen_time<=?",new String[]{"0",startTime+"",endTime+""},null,"HAPPEN_TIME desc",null);

        for (Bill bill:billList){
            if (bill.getMoneyType() ==Bill.MONEY_TYPE_IN){
                inMoney=inMoney.add(bill.getSpendMoney());
            }else if (bill.getMoneyType()==Bill.MONEY_TYPE_OUT){
                outMoney=outMoney.add(bill.getSpendMoney());
            }
        }
        InAndOutMoney record=new InAndOutMoney();
        record.setInMoney(inMoney.setScale(1,BigDecimal.ROUND_HALF_UP).floatValue());
        record.setOutMoney(outMoney.setScale(1,BigDecimal.ROUND_HALF_UP).floatValue());

        return record;
    }

    private void generateNegativeStackedData(List<Column> columns) {



        columnChartData = new ColumnChartData(columns);

        // Set stacked flag.
        columnChartData.setStacked(true);


        columnChart.setColumnChartData(columnChartData);
    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("AnalysisFragment"); //统计页面，"MainScreen"为页面名称，可自定义
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("AnalysisFragment");
    }
}
class InAndOutMoney{
    float inMoney;
    float outMoney;

    public float getInMoney() {
        return inMoney;
    }

    public void setInMoney(float inMoney) {
        this.inMoney = inMoney;
    }

    public float getOutMoney() {
        return outMoney;
    }

    public void setOutMoney(float outMoney) {
        this.outMoney = outMoney;
    }
}