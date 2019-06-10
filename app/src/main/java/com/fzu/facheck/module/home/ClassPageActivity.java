package com.fzu.facheck.module.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.fzu.facheck.R;
import com.fzu.facheck.adapter.section.HomeClassInfoSection;
import com.fzu.facheck.base.RxBaseActivity;
import com.fzu.facheck.entity.RollCall.ClassInfo;
import com.fzu.facheck.entity.RollCall.StateInfo;
import com.fzu.facheck.network.RetrofitHelper;
import com.fzu.facheck.utils.ToastUtil;
import com.fzu.facheck.widget.CustomEmptyView;
import com.fzu.facheck.widget.sectioned.SectionedRecyclerViewAdapter;
import com.thinkcool.circletextimageview.CircleTextImageView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ClassPageActivity extends RxBaseActivity {

    public String classname;
    public String classid;
    public boolean master;
    @BindView(R.id.toolbar)
    Toolbar mtoolbar;
    @BindView(R.id.toolbar_title)
    TextView title_text;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.empty_layout)
    CustomEmptyView mCustomEmptyView;
    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;
    @BindView(R.id.the_class_code)
    TextView codeText;
    @BindView(R.id.the_class_time)
    TextView timeText;
    @BindView(R.id.the_teacher)
    TextView teacherName;
    @BindView(R.id.delete_the_class)
    LinearLayout delete_Linear;
    @BindView(R.id.sign_in_rate)
    TextView signInRate;
    @BindView(R.id.circle_image)
    CircleTextImageView circleImage;
    private ClassInfo result = new ClassInfo();
    private boolean mIsRefreshing = false;
    private SectionedRecyclerViewAdapter mSectionedAdapter;
    @Override
    public int getLayoutId() {
        return R.layout.fragment_class_pager;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        Intent intent = getIntent();
        classid = intent.getStringExtra("classid");
        classname = intent.getStringExtra("classname");
        master = intent.getBooleanExtra("master", false);
        if (!master)
            delete_Linear.setVisibility(View.GONE);
        initToolBar();
        initRefreshLayout();
        initRecyclerView();
    }

    @Override
    public void initToolBar() {
        title_text.setText(classname);
        mtoolbar.setTitle("");
        setSupportActionBar(mtoolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void initNavigationView() {
    }

    @Override
    public void initRecyclerView() {
        mSectionedAdapter = new SectionedRecyclerViewAdapter();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mSectionedAdapter);
        setRecycleNoScroll();
    }
    @Override
    public void initRefreshLayout() {
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
    public void loadData() {
        JSONObject userobject = new JSONObject();
        try {
            userobject.put("classId", classid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), userobject.toString());
        RetrofitHelper.getClassInfo().getclassInfo("get_class_info", requestBody)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ClassInfo>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        initEmptyView();
                    }
                    @Override
                    public void onNext(ClassInfo classInfo) {
                        if (classInfo.code.equals("1500")) {
                            result = classInfo;
                            teacherName.setText("任课教师:   " + classInfo.teacherName);
                            codeText.setText("邀请码:   " + classInfo.classCode);
                            timeText.setText("课程时间：  " + classInfo.startTime + "——" + classInfo.endTime);
                            signInRate.setText("出勤率：  " + Float.parseFloat(classInfo.classAttendanceRate) * 100 + "%");
                            circleImage.setText(String.valueOf(classname.charAt(0)));
                            finishTask();
                        } else {
                            initEmptyView();
                        }
                    }
                });
    }
    @Override
    public void finishTask() {
        mSwipeRefreshLayout.setRefreshing(false);
        mIsRefreshing = false;
        hideEmptyView();
        mSectionedAdapter.addSection(new HomeClassInfoSection(result, "student", this));
        mSectionedAdapter.addSection(new HomeClassInfoSection(result, "records", this));
        mSectionedAdapter.notifyDataSetChanged();
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
    private void clearData() {
        if (result != null) {
            if (result.getStudents() != null)
                result.getStudents().clear();
            if (result.getRecords() != null)
                result.getRecords().clear();
        }
        mIsRefreshing = true;
        mSectionedAdapter.removeAllSections();
    }
    private void setRecycleNoScroll() {
        mRecyclerView.setOnTouchListener((v, event) -> mIsRefreshing);
    }
    @OnClick({R.id.delete_the_class})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_the_class:
                showDialog();
                break;
        }
    }

    private void showDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.AlertDialog);
        dialog.setMessage("确定删除班级：" + classname);
        dialog.setCancelable(false);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final JSONObject userobject = new JSONObject();
                try {
                    userobject.put("classId", classid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), userobject.toString());
                RetrofitHelper.getClassInfo().removeclass("remove_class", requestBody)
                        .compose(bindToLifecycle())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<StateInfo>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                ToastUtil.showShort(ClassPageActivity.this, "请求失败");
                            }

                            @Override
                            public void onNext(StateInfo stateInfo) {
                                if (stateInfo.code.equals("1700")) {
                                    ToastUtil.showShort(ClassPageActivity.this, "删除成功");
                                    finish();
                                } else
                                    ToastUtil.showShort(ClassPageActivity.this, "删除失败");
                            }
                        });
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
