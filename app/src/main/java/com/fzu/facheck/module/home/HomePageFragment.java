package com.fzu.facheck.module.home;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.fzu.facheck.R;
import com.fzu.facheck.adapter.section.HomeClassSection;
import com.fzu.facheck.base.RxLazyFragment;
import com.fzu.facheck.entity.RollCall.RollCallInfo;
import com.fzu.facheck.module.common.MainActivity;
import com.fzu.facheck.network.RetrofitHelper;
import com.fzu.facheck.utils.ConstantUtil;
import com.fzu.facheck.utils.PreferenceUtil;
import com.fzu.facheck.utils.TimeUtil;
import com.fzu.facheck.widget.CustomEmptyView;
import com.fzu.facheck.widget.sectioned.SectionedRecyclerViewAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
/**
 * @date: 2019/5/3
 * @author: wyz
 * @version:
 * @description: 主页
 */
public class HomePageFragment extends RxLazyFragment  {


    private String TAG  = "HomePager";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mtoolbar_title;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_layout)
    CustomEmptyView mCustomEmptyView;


    private String date = getTime();
    private boolean mIsRefreshing = false;

    private boolean isFirstCreated=true;

    private SectionedRecyclerViewAdapter mSectionedAdapter;
    private RollCallInfo results = new RollCallInfo();

    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA};


    public static HomePageFragment newInstance() {
        return new HomePageFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home_pager;
    }

    @Override
    public void finishCreateView(Bundle state) {
        setHasOptionsMenu(true);
        initToolBar();
        initRefreshLayout();
        initRecyclerView();

    }


    private void initToolBar() {
        mToolbar.setTitle("");
        mtoolbar_title.setText(date);
        ((MainActivity) getActivity()).setSupportActionBar(mToolbar);

    }

    private String getTime() {
        Date date = new Date();
        String timePart1 = TimeUtil.parseDate(Calendar.getInstance().getTimeInMillis());
        String timePart2 = TimeUtil.getWeek(date);

        return timePart1 + "(" + timePart2 + ")";
    }


    @Override
    protected void initRecyclerView() {
        mSectionedAdapter = new SectionedRecyclerViewAdapter();
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mSectionedAdapter);
        setRecycleNoScroll();


    }



    @Override
    public void onResume() {
        super.onResume();
        if(!isFirstCreated){
            clearData();
            loadData();
        }
        else
            isFirstCreated=false;
    }

    @Override
    protected void initRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            mIsRefreshing = true;
            loadData();
        });
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            clearData();
            loadData();
        });
    }

    @Override
    protected void loadData() {
        SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(getActivity());
        JSONObject userobject = new JSONObject();
        String phone_number = PreferenceUtil.getString(ConstantUtil.PHONE_NUMBER, "wrong");
        try {
            userobject.put("phoneNumber", phone_number);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), userobject.toString());

        RetrofitHelper.getRollCallAPI()
                .getRollCallInfoById(requestBody)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultBeans -> {

                    switch (resultBeans.getCode()){
                        case "0900":
                        case "0901":
                        case "0902":
                            results = resultBeans;
                            finishTask(resultBeans.getCode());
                            break;
                        case "0903":
                        case "0904":
                            initEmptyView(resultBeans.getCode());
                            break;

                    }



                }, throwable -> initEmptyView("0904"));


    }

    protected void finishTask(String code) {

        mSwipeRefreshLayout.setRefreshing(false);
        mIsRefreshing = false;
        hideEmptyView();

        switch (code){
            case "0900":
                mSectionedAdapter.addSection(new HomeClassSection(results.getClassInfo(), "jointed_data", mSwipeRefreshLayout,getActivity()));
                mSectionedAdapter.addSection(new HomeClassSection(results.getClassInfo(), "created_data",mSwipeRefreshLayout, getActivity()));
                break;
            case "0901":
                mSectionedAdapter.addSection(new HomeClassSection(results.getClassInfo(), "jointed_data",mSwipeRefreshLayout, getActivity()));
                break;
            case "0902":
                mSectionedAdapter.addSection(new HomeClassSection(results.getClassInfo(), "created_data",mSwipeRefreshLayout, getActivity()));
                break;
        }

        mSectionedAdapter.notifyDataSetChanged();
    }


    public void initEmptyView(String code) {


        mSwipeRefreshLayout.setRefreshing(false);
        mCustomEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);

        switch (code){
            case "0903":
                mCustomEmptyView.setEmptyImage(R.drawable.img_tips_error);
                mCustomEmptyView.setEmptyText("目前尚无数据，请到 班级 -> 创建班级／加入班级");
                break;
            case "0904":
                mCustomEmptyView.setEmptyImage(R.drawable.img_tips_error);
                mCustomEmptyView.setEmptyText("加载失败!请重试或检查网络连接");
                break;

        }

    }


    public void hideEmptyView() {
        mCustomEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void clearData() {

        mIsRefreshing = true;
        mSectionedAdapter.removeAllSections();
    }


    private void setRecycleNoScroll() {
        mRecyclerView.setOnTouchListener((v, event) -> mIsRefreshing);
    }






}
