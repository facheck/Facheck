package com.fzu.facheck.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fzu.facheck.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * @date: 2019/4/23
 * @author: wyz
 * @version:
 * @description: 自定义LineChart
 */
public class DynamicLineChartManager {

    private LineDataSet set1;
    private YAxis leftAxis;
    private YAxis rightAxis;
    private XAxis xAxis;
    private LineChart lineChart;
    private int lineColor;
    private int fillColor;


    public DynamicLineChartManager(LineChart lineChart, int lineColor,int fillColor) {
        this.lineChart = lineChart;
        leftAxis = lineChart.getAxisLeft();
        rightAxis = lineChart.getAxisRight();
        xAxis = lineChart.getXAxis();
        this.lineColor = lineColor;
        this.fillColor = fillColor;
        initLineChart();

    }
    /**
     * 初始化LineChar
     */
    private void initLineChart() {
        lineChart.setDrawGridBackground(false);
        lineChart.getDescription().setEnabled(false);
        //设置支持触控手势
        lineChart.setTouchEnabled(true);
        //设置缩放
        lineChart.setDragEnabled(true);
        //设置推动
        lineChart.setScaleEnabled(true);
        //折线图例 标签 设置
        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);


        //X轴设置显示位置在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(5);

        //保证Y轴从0开始，不然会上移一点
        leftAxis.setAxisMinimum(0f);
        rightAxis.setAxisMinimum(0f);



    }


    /**
     * 设置Y轴值
     *
     * @param max
     * @param min
     * @param labelCount
     */
    public void setYAxis(float max, float min, int labelCount) {
        if (max < min) { return; }
        leftAxis.setAxisMaximum(max);
        leftAxis.setAxisMinimum(min);
        leftAxis.setLabelCount(labelCount, false);

        rightAxis.setAxisMaximum(max);
        rightAxis.setAxisMinimum(min);
        rightAxis.setLabelCount(labelCount, false);
        lineChart.invalidate();
    }

    /**
     * 动态添加数据
     *
     * @param values
     */
    public void setData(ArrayList<Entry> values) {
        if (lineChart.getData() != null && lineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            // 创建一个数据集
            set1 = new LineDataSet(values, "");

            // 设置线

            set1.setColor(lineColor);
            set1.setCircleColor(lineColor);
            set1.setLineWidth(1.5f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormSize(15.f);
            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);

            if (Utils.getSDKInt() >= 18) {
                // 填充背景只支持18以上

                set1.setFillColor(fillColor);
            } else {
                set1.setFillColor(Color.BLACK);
            }
            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            //添加数据集
            dataSets.add(set1);

            //创建一个数据集的数据对象
            LineData data = new LineData(dataSets);

            lineChart.setData(data);
            //设置在曲线图中显示的最大数量
            lineChart.setVisibleXRangeMaximum(5);
            lineChart.moveViewToX(data.getEntryCount() - 3);

        }

        lineChart.invalidate();
    }


}
