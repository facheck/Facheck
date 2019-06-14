package com.fzu.facheck.module.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.fzu.facheck.R;
import com.fzu.facheck.base.RxBaseActivity;
import com.fzu.facheck.module.home.TheAboutFragment;
import com.fzu.facheck.utils.ToastUtil;

import butterknife.BindView;

public class CommonActivity extends RxBaseActivity {

    String title;
    @BindView(R.id.toolbar)
    Toolbar mtoolbar;
    @BindView(R.id.toolbar_title)
    TextView title_text;
    @Override
    public int getLayoutId() {
        return R.layout.common_layout;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        Intent intent=getIntent();
        int flag=intent.getIntExtra("flag",0);
        switch (flag){
            case 0:
                ToastUtil.showShort(this,"程序运行出错");
                break;
            case 2:
                title="关于我们";
                replaceFragment(new TheAboutFragment());
                break;
        }
    }

    @Override
    public void initToolBar() {
        mtoolbar.setTitle("");
        title_text.setText(title);
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

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.common,fragment);
        transaction.commit();
    }
}
