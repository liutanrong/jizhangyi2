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
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
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




        generateNegativeStackedData();
        return view;
    }

    private void generateNegativeStackedData() {

        int numSubcolumns = 2;
        int numColumns = 6;
        // Column can have many stacked subcolumns,
        // here I use 4 stacke subcolumn in each of 4 columns.
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;
        for (int i = 0; i < numColumns; ++i) {

            values = new ArrayList<SubcolumnValue>();
            values.add(new SubcolumnValue((float) Math.random() * 20f*-1, ChartUtils.COLOR_RED));
            values.add(new SubcolumnValue((float) Math.random() * 20f, ChartUtils.COLOR_GREEN));

            Column column = new Column(values);
            column.setHasLabels(true);
            column.setHasLabelsOnlyForSelected(false);
            columns.add(column);
        }

        columnChartData = new ColumnChartData(columns);

        // Set stacked flag.
        columnChartData.setStacked(true);


        Axis axisX = new Axis();
        List<AxisValue> valueList=new ArrayList<>();
        valueList.add(new AxisValue(1));
        valueList.add(new AxisValue(2));
        valueList.add(new AxisValue(3));
        valueList.add(new AxisValue(4));
        valueList.add(new AxisValue(5));
        valueList.add(new AxisValue(6));
        axisX.setValues(valueList);

        Axis axisY = new Axis().setHasLines(false);

        axisX.setName("月份");
        axisY.setName("金额");
        columnChartData.setAxisXBottom(axisX);
        columnChartData.setAxisYLeft(null);

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
