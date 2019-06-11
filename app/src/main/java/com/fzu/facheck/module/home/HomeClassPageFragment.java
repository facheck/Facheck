package com.fzu.facheck.module.home;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.fzu.facheck.R;
import com.fzu.facheck.adapter.section.HomeClassPageSection;
import com.fzu.facheck.base.RxLazyFragment;
import com.fzu.facheck.entity.RollCall.RollCallInfo;
import com.fzu.facheck.module.common.MainActivity;
import com.fzu.facheck.network.RetrofitHelper;
import com.fzu.facheck.utils.ConstantUtil;
import com.fzu.facheck.utils.PreferenceUtil;
import com.fzu.facheck.widget.CustomEmptyView;
import com.fzu.facheck.widget.sectioned.SectionedRecyclerViewAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeClassPageFragment extends RxLazyFragment {
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

    private boolean mIsRefreshing = false;
    private SectionedRecyclerViewAdapter mSectionedAdapter;
    private RollCallInfo results = new RollCallInfo();
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home_class_page;
    }

    @Override
    public void finishCreateView(Bundle state) {
        initToolBar();
        initRefreshLayout();
        initRecyclerView();
    }

    private void initToolBar() {
        setHasOptionsMenu(true);
        mToolbar.setTitle("");
        mtoolbar_title.setText("班级");
        mToolbar.setOverflowIcon(getResources().getDrawable(R.drawable.add));
        ((MainActivity)getActivity()).setSupportActionBar(mToolbar);
    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.class_main,menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (menu != null) {
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                }
            }
        }
        super.onPrepareOptionsMenu(menu);
    }


    public static HomeClassPageFragment newInstance() {
        return new HomeClassPageFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.creat_class:
                Intent intent1=new Intent(getActivity(),CreateClassActivity.class);
                startActivity(intent1);
                break;
            case R.id.add_class:
                Intent intent2=new Intent(getActivity(),SearchActivity.class);
                startActivity(intent2);
                break;
        }
        return true;
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
        JSONObject userobject=new JSONObject();
        try {
            userobject.put("phoneNumber",PreferenceUtil.getString(ConstantUtil.PHONE_NUMBER,""));
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
                    results=resultBeans;
                    finishTask();
                }, throwable -> {
                    throwable.printStackTrace();initEmptyView();});
    }
    public void initEmptyView() {
        mSwipeRefreshLayout.setRefreshing(false);
        mCustomEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mCustomEmptyView.setEmptyImage(R.drawable.img_tips_error);
        mCustomEmptyView.setEmptyText("加载失败!请重试或检查网络连接");
    }
    public void hideEmptyView() {
        mCustomEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
    @Override
    protected void finishTask() {
        mSwipeRefreshLayout.setRefreshing(false);
        mIsRefreshing = false;
        hideEmptyView();
        mSectionedAdapter.addSection(new HomeClassPageSection(results.getClassInfo(),"jointed_data",getActivity()));
        mSectionedAdapter.addSection(new HomeClassPageSection(results.getClassInfo(),"created_data",getActivity()));
        mSectionedAdapter.notifyDataSetChanged();
    }

    private void clearData() {

        if (results != null|| results.getClassInfo()!=null) {
            if(results.getClassInfo().getManagedClassData()!=null)
                results.getClassInfo().getManagedClassData().clear();
            if(results.getClassInfo().getJoinedClassData()!=null)
                results.getClassInfo().getJoinedClassData().clear();
        }

        mIsRefreshing = true;
        mSectionedAdapter.removeAllSections();
    }
    private void setRecycleNoScroll() {
        mRecyclerView.setOnTouchListener((v, event) -> mIsRefreshing);
    }
}
