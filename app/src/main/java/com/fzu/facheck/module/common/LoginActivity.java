package com.fzu.facheck.module.common;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.fzu.facheck.R;
import com.fzu.facheck.base.RxBaseActivity;
import com.fzu.facheck.entity.RollCall.LoginInfo;
import com.fzu.facheck.entity.RollCall.StateInfo;
import com.fzu.facheck.network.RetrofitHelper;
import com.fzu.facheck.utils.ConstantUtil;
import com.fzu.facheck.utils.PreferenceUtil;
import com.fzu.facheck.utils.ToastUtil;
import com.fzu.facheck.widget.CircleProgressView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends RxBaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mtoolbar;
    @BindView(R.id.toolbar_title)
    TextView title_text;

    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.circle_progress)
    CircleProgressView circleProgressView;
    @BindView(R.id.login_button)
    Button loginButton;
    @BindView(R.id.remeber_pass)
    CheckBox remeberpass;
    @Override
    public int getLayoutId() {
        return R.layout.login_layout;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        if(PreferenceUtil.getBoolean(ConstantUtil.REMEMBER_PASSWORD,false)){
            username.setText(PreferenceUtil.getString(ConstantUtil.PHONE_NUMBER,""));
            password.setText(PreferenceUtil.getString(ConstantUtil.PASSWORD,""));
            remeberpass.setChecked(true);
        }
        else{
            username.setText(null);
            password.setText(null);
        }
    }

    @Override
    public void initToolBar() {
        mtoolbar.setTitle("");
        title_text.setText("登陆账号");
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

    @OnClick({R.id.login_button,R.id.forget_pass})
    void onClick(View view){
        switch (view.getId()){
            case R.id.login_button:
                //获取用户输入的电话号码、和密码
                String un=username.getText().toString();
                String pw=password.getText().toString();
                if(TextUtils.isEmpty(un)||TextUtils.isEmpty(pw))
                    ToastUtil.showShort(LoginActivity.this,"手机号或密码不能为空");
                else if(un.length()<11)
                    ToastUtil.showShort(LoginActivity.this,"手机号错误");
                else if(pw.length()<6)
                    ToastUtil.showShort(LoginActivity.this,"密码过短");
                else{
                    final JSONObject userobject=new JSONObject();
                    try {
                        userobject.put("phoneNumber",un);
                        userobject.put("password",pw);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    showProgressBar();
                    //发送请求
                    RequestBody requestBody=RequestBody.create(MediaType.parse("application/json;charset=utf-8"),userobject.toString());
                    RetrofitHelper.getLoAPI().get_login_server("login",requestBody)
                            .compose(bindToLifecycle())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<LoginInfo>() {
                                @Override
                                public void onCompleted() { }
                                @Override
                                public void onError(Throwable e) {
                                    hideProgressBar();
                                    if(e instanceof HttpException){
                                        HttpException httpException=(HttpException)e;
                                        if(httpException.code()==500)
                                            ToastUtil.showShort(LoginActivity.this,"服务器出错");
                                    }
                                    else
                                        ToastUtil.showShort(LoginActivity.this,"请求失败");
                                }
                                @Override
                                public void onNext(LoginInfo loginInfo) {
                                    hideProgressBar();
                                    if(loginInfo.code.equals("0100")){
                                        //登入成功，启动主页面
                                        saveData(un,pw,loginInfo.username,loginInfo.isAuthenticated);
                                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                                        startActivity(intent);
                                    }
                                    else if(loginInfo.code.equals("0101"))
                                        ToastUtil.showShort(LoginActivity.this,"密码错误");
                                    else
                                        ToastUtil.showShort(LoginActivity.this,"账号不存在");
                                }
                            });
                }
                break;
            //启动忘记密码活动
            case R.id.forget_pass:
                Intent intent=new Intent(LoginActivity.this,GetNewPassActivity.class);
                startActivity(intent);
                break;
        }
    }
    @Override
    public void showProgressBar() {
        circleProgressView.setVisibility(View.VISIBLE);
        circleProgressView.spin();
        loginButton.setClickable(false);
    }
    @Override
    public void hideProgressBar() {
        circleProgressView.setVisibility(View.GONE);
        circleProgressView.stopSpinning();
        loginButton.setClickable(true);
    }
    private void saveData(String phone,String password,String name,boolean authenticated){
        SharedPreferences.Editor editor=PreferenceUtil.getPreferences().edit();
        if(remeberpass.isChecked())
            editor.putBoolean(ConstantUtil.REMEMBER_PASSWORD,true);
        else
            editor.putBoolean(ConstantUtil.REMEMBER_PASSWORD,false);
        editor.putBoolean(ConstantUtil.KEY, true);
        editor.putString(ConstantUtil.PHONE_NUMBER,phone);
        editor.putString(ConstantUtil.PASSWORD,password);
        editor.putString(ConstantUtil.EXTRA_NAME,name);
        editor.putBoolean(ConstantUtil.AUTHENTICATED,authenticated);
        editor.apply();
    }
}
