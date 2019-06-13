package com.fzu.facheck.module.common;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fzu.facheck.R;
import com.fzu.facheck.base.RxBaseActivity;
import com.fzu.facheck.entity.RollCall.StateInfo;
import com.fzu.facheck.network.RetrofitHelper;
import com.fzu.facheck.utils.TimeCountUtil;
import com.fzu.facheck.utils.ToastUtil;
import com.fzu.facheck.widget.CircleProgressView;
import com.mob.MobSDK;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GetNewPassActivity extends RxBaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mtoolbar;
    @BindView(R.id.toolbar_title)
    TextView title_text;
    @BindView(R.id.newpass)
    EditText newpassText;
    @BindView(R.id.newpass1)
    EditText newpassText1;
    @BindView(R.id.photo_input)
    EditText phoneText;
    @BindView(R.id.varifi)
    EditText varifyText;
    @BindView(R.id.get_varification)
    Button getVar;
    @BindView(R.id.circle_progress)
    CircleProgressView circleProgressView;
    @BindView(R.id.ok1)
    Button ok_Button;
    @Override
    public int getLayoutId() {
        return R.layout.get_pass;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        MobSDK.init(this,"2ad6399bb9865","55335739821de02f5fcfd8819903afea");
        final EventHandler eventHandler=new EventHandler(){
            public void afterEvent(int event,int result,Object data){
                if(result==SMSSDK.RESULT_COMPLETE){
                    if(event==SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){
                        JSONObject userobject=new JSONObject();
                        try {
                            userobject.put("phoneNumber",phoneText.getText().toString());
                            userobject.put("password",newpassText1.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), userobject.toString());
                        RetrofitHelper.getLoAPI().getserver("reset_password", requestBody)
                                .compose(bindToLifecycle())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<StateInfo>() {
                                    @Override
                                    public void onCompleted() { }
                                    @Override
                                    public void onError(Throwable e) {
                                        hideProgressBar();
                                        if (e instanceof HttpException) {
                                            HttpException httpException = (HttpException) e;
                                            if (httpException.code() == 500)
                                                ToastUtil.showShort(GetNewPassActivity.this, "服务器出错");
                                        } else
                                            ToastUtil.showShort(GetNewPassActivity.this, "请求失败");
                                    }
                                    @Override
                                    public void onNext(StateInfo stateInfo) {
                                        hideProgressBar();
                                        if (stateInfo.code.equals("0400")) {
                                            ToastUtil.showShort(GetNewPassActivity.this, "修改成功");
                                            finish();
                                        } else if (stateInfo.code.equals("0401"))
                                            ToastUtil.showShort(GetNewPassActivity.this, "修改失败");
                                        else
                                            ToastUtil.showShort(GetNewPassActivity.this, "未知情况错误");
                                    }
                                });
                    }
                    else if(event==SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        infomation("验证码已发送");
                    }
                    else;
                }
                else{
                    hideProgressBar();
                    infomation("验证码错误");
                }
            }
        };
        SMSSDK.registerEventHandler(eventHandler);
    }

    @Override
    public void initToolBar() {
        mtoolbar.setTitle("");
        title_text.setText("忘记密码");
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

    @OnClick({R.id.ok1,R.id.get_varification})
    void onClick(View view){
        String npt=newpassText.getText().toString();
        String npt1=newpassText1.getText().toString();
        String pt=phoneText.getText().toString();
        String vr=varifyText.getText().toString();
        switch (view.getId()) {
            case R.id.get_varification:
                if (TextUtils.isEmpty(pt)||pt.length() < 11)
                    ToastUtil.showShort(GetNewPassActivity.this,"请输入正确手机号");
                else {
                    TimeCountUtil time = new TimeCountUtil(60000, 1000, getVar);
                    SMSSDK.getVerificationCode("86",pt);
                    time.start();
                }
                break;
            case R.id.ok1:
                if(TextUtils.isEmpty(npt))
                    ToastUtil.showShort(GetNewPassActivity.this,"新密码不能为空");
                else if(npt.length()<6)
                    ToastUtil.showShort(GetNewPassActivity.this,"新密码过短");
                else if(TextUtils.isEmpty(npt1))
                    ToastUtil.showShort(GetNewPassActivity.this,"未确认新密码");
                else if(TextUtils.isEmpty(pt))
                    ToastUtil.showShort(GetNewPassActivity.this,"手机号不能为空");
                else if(TextUtils.isEmpty(vr))
                    ToastUtil.showShort(GetNewPassActivity.this,"验证码不能为空");
                else if(pt.length()<11)
                    ToastUtil.showShort(GetNewPassActivity.this,"手机号错误");
                else if(!npt.equals(npt1))
                    ToastUtil.showShort(GetNewPassActivity.this,"密码确认错误");
                else
                    showProgressBar();
                    SMSSDK.submitVerificationCode("86",pt,vr);
                break;
        }
    }
    private void infomation(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(GetNewPassActivity.this,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }
    @Override
    public void showProgressBar() {
        circleProgressView.setVisibility(View.VISIBLE);
        circleProgressView.spin();
        ok_Button.setClickable(false);
    }
    @Override
    public void hideProgressBar() {
        circleProgressView.setVisibility(View.GONE);
        circleProgressView.stopSpinning();
        ok_Button.setClickable(true);
    }
}
