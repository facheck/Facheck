package com.fzu.facheck.module.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fzu.facheck.R;
import com.fzu.facheck.adapter.section.SearchClassSection;
import com.fzu.facheck.base.RxBaseActivity;
import com.fzu.facheck.entity.RollCall.SearchClassInfo;
import com.fzu.facheck.network.RetrofitHelper;
import com.fzu.facheck.utils.ConstantUtil;
import com.fzu.facheck.utils.PreferenceUtil;
import com.fzu.facheck.utils.ToastUtil;
import com.fzu.facheck.widget.CustomEmptyView;
import com.fzu.facheck.widget.sectioned.SectionedRecyclerViewAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchActivity extends RxBaseActivity {

    @BindView(R.id.search_edit)
    EditText searchText;
    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_layout)
    CustomEmptyView mCustomEmptyView;
    @BindView(R.id.hintEmpty)
    TextView hintText;
    @BindView(R.id.result_hint)
    TextView rhintText;
    public String phoneNumber;
    private SectionedRecyclerViewAdapter mSectionedAdapter;
    private SearchClassInfo results=new SearchClassInfo();
    @Override
    public int getLayoutId() {
        return R.layout.search_layout;
    }
    @Override
    public void initViews(Bundle savedInstanceState) {
        initRecyclerView();
        mCustomEmptyView.setVisibility(View.GONE);
        hintText.setVisibility(View.GONE);
        rhintText.setVisibility(View.GONE);
    }
    @Override
    public void initToolBar() {
    }

    @Override
    public void initNavigationView() {

    }

    @Override
    public void initRecyclerView(){
        mSectionedAdapter = new SectionedRecyclerViewAdapter();
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mSectionedAdapter);
    }
    @OnClick({R.id.cancel_search,R.id.search_classes})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.cancel_search:
                finish();
                break;
            case R.id.search_classes:
                phoneNumber=PreferenceUtil.getString(ConstantUtil.PHONE_NUMBER,"");
                String inputtext=searchText.getText().toString();
                if(TextUtils.isEmpty(inputtext))
                    ToastUtil.showShort(this,"请输入搜索信息");
                else{
                    final JSONObject userobject=new JSONObject();
                    try {
                        userobject.put("phoneNumber",phoneNumber);
                        userobject.put("className",inputtext);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RequestBody requestBody=RequestBody.create(MediaType.parse("application/json;charset=utf-8"),userobject.toString());
                    RetrofitHelper.getClassInfo().searchclass("search_class",requestBody)
                            .compose(bindToLifecycle())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(resultbean->{
                                clearData();
                                results=resultbean;
                                if(results.code.equals("1300")) {
                                    if (results.getResultClasses() == null || results.getResultClasses().size() == 0) {
                                        rhintText.setVisibility(View.GONE);
                                        mCustomEmptyView.setVisibility(View.GONE);
                                        mRecyclerView.setVisibility(View.GONE);
                                        hintText.setVisibility(View.VISIBLE);
                                    } else {
                                        finishTask();
                                    }
                                }
                            },throwable -> {
                                throwable.printStackTrace();initEmptyView();});
                }
                break;
        }
    }
    public void initEmptyView() {
        rhintText.setVisibility(View.GONE);
        mCustomEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        hintText.setVisibility(View.GONE);
        mCustomEmptyView.setEmptyImage(R.drawable.img_tips_error);
        mCustomEmptyView.setEmptyText("加载失败!请重试或检查网络连接");
    }
    public void hideEmptyView() {
        mCustomEmptyView.setVisibility(View.GONE);
        hintText.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
    @Override
    public void finishTask() {
        rhintText.setVisibility(View.VISIBLE);
        hideEmptyView();
        mSectionedAdapter.addSection(new SearchClassSection(results,this));
        mSectionedAdapter.notifyDataSetChanged();
    }
    private void clearData(){
        if(results.getResultClasses()!=null)
            results.getResultClasses().clear();
        mSectionedAdapter.removeAllSections();
    }
}
