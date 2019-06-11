package com.fzu.facheck.module.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fzu.facheck.R;
import com.fzu.facheck.base.RxBaseActivity;
import com.fzu.facheck.entity.RollCall.StateInfo;
import com.fzu.facheck.network.RetrofitHelper;
import com.fzu.facheck.utils.ToastUtil;
import com.fzu.facheck.widget.CustomEmptyView;
import com.fzu.facheck.widget.MyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class StudentinfoActivity extends RxBaseActivity {
    private String studentname;
    private String phoneNumber;
    private String classname;
    private String classid;
    private boolean master;
    @BindView(R.id.toolbar)
    Toolbar mtoolbar;
    @BindView(R.id.toolbar_title)
    TextView title_text;
    @BindView(R.id.empty_layout)
    CustomEmptyView mCustomEmptyView;
    @BindView(R.id.student_info)
    LinearLayout linearLayout;

    @BindView(R.id.student_name)
    TextView nameText;
    @BindView(R.id.phone_number)
    TextView phoneText;
    @BindView(R.id.class_name)
    TextView classText;
    @BindView(R.id.sign_rate)
    TextView rateText;
    @BindView(R.id.exitclass)
    LinearLayout exitclass;
    @BindView(R.id.student_id)
    TextView idText;
    @Override
    public int getLayoutId() {
        return R.layout.layout_student;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        Intent intent=getIntent();
        studentname=intent.getStringExtra("studentname");
        phoneNumber=intent.getStringExtra("phoneNumber");
        classname=intent.getStringExtra("classname");
        classid=intent.getStringExtra("classid");
        master=intent.getBooleanExtra("master",false);
        nameText.setText(studentname);
        phoneText.setText(phoneNumber);
        classText.setText(classname);
        loadData();
    }

    @Override
    public void initToolBar() {
        mtoolbar.setNavigationIcon(R.drawable.backicon);
        title_text.setText(studentname);
        mtoolbar.setNavigationOnClickListener(v->finish());
    }
    @Override
    public void loadData(){
        final JSONObject userobject=new JSONObject();
        try {
            userobject.put("phoneNumber",phoneNumber);
            userobject.put("classId",classid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody=RequestBody.create(MediaType.parse("application/json;charset=utf-8"),userobject.toString());
        RetrofitHelper.getStudentInfo()
                .getclassserver("get_single_attendance",requestBody)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultbean->{
                    if(resultbean.code.equals("1800")){
                        hideEmptyView();
                        rateText.setText(resultbean.attendanceRatio);
                        idText.setText(resultbean.studentId);
                    }
                    else
                        initEmptyView();
                },throwable -> initEmptyView());
    }
    @Override
    public void initNavigationView() {
    }
    public void hideEmptyView() {
        mCustomEmptyView.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);
        if(master)
            exitclass.setVisibility(View.VISIBLE);
        else
            exitclass.setVisibility(View.GONE);
    }
    public void initEmptyView() {
        mCustomEmptyView.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);
        exitclass.setVisibility(View.GONE);
        mCustomEmptyView.setEmptyImage(R.drawable.img_tips_error);
        mCustomEmptyView.setEmptyText("加载失败!请重试或检查网络连接");
    }
    @OnClick({R.id.exitclass})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.exitclass:
                showDialog();
                break;
        }
    }
    private void showDialog(){
        MyDialog.Builder builder=new MyDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMsg("确定移除学生："+studentname);
        builder.setLeft(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setRight(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final JSONObject userobject=new JSONObject();
                try {
                    userobject.put("classId",classid);
                    userobject.put("phoneNumber",phoneNumber);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody requestBody=RequestBody.create(MediaType.parse("application/json;charset=utf-8"),userobject.toString());
                RetrofitHelper.getClassInfo().removeclass("remove_student",requestBody)
                        .compose(bindToLifecycle())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<StateInfo>() {
                            @Override
                            public void onCompleted() {
                            }
                            @Override
                            public void onError(Throwable e) {
                                ToastUtil.showShort(StudentinfoActivity.this,"请求失败");
                            }
                            @Override
                            public void onNext(StateInfo stateInfo) {
                                if(stateInfo.code.equals("1600")) {
                                    ToastUtil.showShort(StudentinfoActivity.this, "该同学已被成功移除班级");
                                    dialog.dismiss();
                                    finish();
                                }
                                else
                                    ToastUtil.showShort(StudentinfoActivity.this,"移除失败");
                            }
                        });
            }
        });
        builder.create(2).show();
    }
}
