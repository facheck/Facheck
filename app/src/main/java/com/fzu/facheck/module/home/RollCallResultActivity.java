package com.fzu.facheck.module.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.fzu.facheck.R;
import com.fzu.facheck.adapter.section.RollCallResultSection;
import com.fzu.facheck.base.RxBaseActivity;
import com.fzu.facheck.entity.RollCall.RollCallResult;
import com.fzu.facheck.network.RetrofitHelper;
import com.fzu.facheck.utils.ConstantUtil;
import com.fzu.facheck.utils.SnackbarUtil;
import com.fzu.facheck.widget.CircleProgressView;
import com.fzu.facheck.widget.CustomEmptyView;
import com.fzu.facheck.widget.sectioned.SectionedRecyclerViewAdapter;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RollCallResultActivity extends RxBaseActivity {

    String TAG = "RollCallResult";
//    @BindView(R.id.navigation)
//    BottomNavigationView bottomNavigationView;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.pie_chart)
    PieChart mPieChart;
    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_layout)
    CustomEmptyView mCustomEmptyView;
    @BindView(R.id.circle_progress)
    CircleProgressView mCircleProgressView;

    private String mTitle;
    private SectionedRecyclerViewAdapter mSectionedAdapter;
    private boolean mIsRefreshing = false;
    private String mRecordId;
    private RollCallResult mRollCallResult;


    @Override
    public int getLayoutId() {
        return R.layout.activity_roll_call_result;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        Intent intent = getIntent();

        if (intent != null) {
            mTitle = intent.getStringExtra(ConstantUtil.EXTRA_CLASS_TITLE);
            mRecordId = intent.getStringExtra(ConstantUtil.EXTRA_RECORD_ID);
        }
        initRecyclerView();
        loadData();

    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle("");
        mToolbarTitle.setText(mTitle);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public void initNavigationView() {

    }

    @Override
    public void loadData() {
        JSONObject userobject = new JSONObject();
        try {
            userobject.put("recordId", mRecordId);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), userobject.toString());

        RetrofitHelper.getRollCallAPI()
                .getRollCallResultById(requestBody)
                .compose(bindToLifecycle())
                .doOnSubscribe(this::showProgressBar)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultBeans -> {


                    if (resultBeans.getCode().equals("1000")) {

                        mRollCallResult = resultBeans;
                        finishTask();
                    } else {

                        initEmptyView();
                    }

                }, throwable -> {
                    hideProgressBar();
                    initEmptyView();

                });

    }

    @Override
    public void showProgressBar() {
        mPieChart.setVisibility(View.INVISIBLE);
        mCircleProgressView.setVisibility(View.VISIBLE);
        mCircleProgressView.spin();
    }

    @Override
    public void hideProgressBar() {
        mPieChart.setVisibility(View.VISIBLE);
        mCircleProgressView.setVisibility(View.GONE);
        mCircleProgressView.stopSpinning();
    }

    @Override
    public void initRecyclerView() {

        mSectionedAdapter = new SectionedRecyclerViewAdapter();
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 1);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mSectionedAdapter);

    }

    @Override
    public void finishTask() {

        hideProgressBar();
        hideEmptyView();
        initPieChart();
        mSectionedAdapter.addSection(new RollCallResultSection(mRollCallResult, "not_signed", this));
        mSectionedAdapter.addSection(new RollCallResultSection(mRollCallResult, "signed", this));
        mSectionedAdapter.notifyDataSetChanged();


    }

    public void initPieChart() {

        mPieChart.setUsePercentValues(true);
        mPieChart.setDrawHoleEnabled(true);
        mPieChart.setCenterText("班级出勤率");
        mPieChart.setCenterTextColor(R.color.font_gray);
        mPieChart.setHoleRadius(45f);

        Description description = new Description();
        description.setText("");
        mPieChart.setDescription(description);
        mPieChart.setHighlightPerTapEnabled(true);
        mPieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        mPieChart.setExtraOffsets(0f,10f,0f,0f);

        List<PieEntry> strings = new ArrayList<>();
        strings.add(new PieEntry(Float.parseFloat(mRollCallResult.getRecordInfo().getAttendanceRate()), "已签到"));
        strings.add(new PieEntry(1-Float.parseFloat(mRollCallResult.getRecordInfo().getAttendanceRate()), "未签到"));

        PieDataSet dataSet = new PieDataSet(strings, "");

        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(getResources().getColor(R.color.colorPrimary));
        colors.add(getResources().getColor(R.color.gray));
        dataSet.setColors(colors);
        dataSet.setValueLinePart1OffsetPercentage(70f);
        dataSet.setValueLineColor(R.color.font_gray);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setSliceSpace(1f);
        dataSet.setHighlightEnabled(true);

        Legend legend = mPieChart.getLegend();
        legend.setEnabled(false);


        PieData pieData = new PieData(dataSet);
        pieData.setDrawValues(true);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(R.color.font_gray);

        mPieChart.setData(pieData);
        mPieChart.invalidate();


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    public void initEmptyView() {

        mCustomEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mPieChart.setVisibility(View.GONE);
        hideProgressBar();
        mCustomEmptyView.setEmptyImage(R.drawable.img_tips_error);
        mCustomEmptyView.setEmptyText("加载失败!");
        SnackbarUtil.showMessage(mRecyclerView, "数据加载失败,请重新加载或者检查网络是否链接");
    }

    public void hideEmptyView() {
        mCustomEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }


}
