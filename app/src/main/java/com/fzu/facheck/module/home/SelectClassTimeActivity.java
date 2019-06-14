package com.fzu.facheck.module.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.fzu.facheck.R;
import com.fzu.facheck.adapter.section.SelectClassTimeSection;
import com.fzu.facheck.base.RxBaseActivity;
import com.fzu.facheck.widget.sectioned.SectionedRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;

public class SelectClassTimeActivity extends RxBaseActivity {
    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;
    @BindView(R.id.the_line)
    LinearLayout line;
    private SectionedRecyclerViewAdapter mSectionedAdapter;
    public ArrayList<String> times;
    private SelectClassTimeSection selectClassTimeSection;
    final String[] week={"星期一","星期二","星期三","星期四","星期五","星期六","星期日"};
    final String[] duringtime={"上午8:20-10:00","上午10:20-12:00","下午14:00-15:40","下午15:50-17:30"
            ,"晚上19:00-21:35"};
    @Override
    public int getLayoutId() {
        return R.layout.layout_select_class_time;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        times=getIntent().getStringArrayListExtra("times");
        initRecyclerView();
        selectClassTimeSection=new SelectClassTimeSection(this);
        mSectionedAdapter.addSection(selectClassTimeSection);
        refreshData();
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
    @OnClick({R.id.cancel_time,R.id.save_time,R.id.add_time})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.cancel_time:
                finish();
                break;
            case R.id.save_time:
                Intent intent=new Intent();
                intent.putStringArrayListExtra("times",times);
                setResult(RESULT_OK,intent);
                finish();
                break;
            case R.id.add_time:
                selectWordDate();
                break;
        }
    }
    private void refreshData(){
        if(times==null||times.size()==0)
            line.setVisibility(View.GONE);
        else
            line.setVisibility(View.VISIBLE);
        mSectionedAdapter.notifyDataSetChanged();
    }
    public void delete_time(int position){
        if(times!=null&&times.size()!=0)
            times.remove(position);
        refreshData();
    }
    public void add_time(String time){
        times.add(time);
        refreshData();
    }
    private void selectWordDate(){
        OptionsPickerView pvOptions=new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                add_time(String.valueOf(options1)+String.valueOf(options2));
            }
        }).setTitleText("上课时间")
                .setCancelText("取消")
                .setSubmitText("确定")
                .setOutSideCancelable(false)
                .setSelectOptions(4)
                .build();
        pvOptions.setNPicker(Arrays.asList(week),Arrays.asList(duringtime),null);
        pvOptions.show();
    }
    public String gettime(int position){
        Log.d("SelectClassTimeActivity",position+"");
        int index=Integer.parseInt(times.get(position));
        int index1=index%10;
        int index2=(index-index1)/10;
        return week[index2]+" "+duringtime[index1];
    }
}
