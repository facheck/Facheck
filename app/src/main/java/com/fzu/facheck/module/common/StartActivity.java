package com.fzu.facheck.module.common;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.fzu.facheck.R;
import com.fzu.facheck.base.RxBaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class StartActivity extends RxBaseActivity {

    //登入按钮
    @BindView(R.id.login_button1)
    Button loginbtn;
    //注册按钮
    @BindView(R.id.register)
    Button regiterbtn;
    @Override
    public int getLayoutId() {
        return R.layout.start_layout;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        GradientDrawable myGrad= (GradientDrawable) regiterbtn.getBackground();
        myGrad.setColor(Color.parseColor("#B0E0E6"));
        myGrad=(GradientDrawable)loginbtn.getBackground();
        myGrad.setColor(Color.parseColor("#6ACCBD"));
    }

    @Override
    public void initToolBar() {
    }

    @Override
    public void initNavigationView() {

    }

    @OnClick({R.id.login_button1,R.id.register})
    void onClick(View view){
        switch (view.getId()){
            //启动登入活动
            case R.id.login_button1:
                Intent intent1=new Intent(StartActivity.this,LoginActivity.class);
                startActivity(intent1);
                break;
           //启动注册活动
            case R.id.register:
                Intent intent2=new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(intent2);
                break;
        }
    }
}
