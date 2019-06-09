package com.fzu.facheck.module.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fzu.facheck.R;
import com.fzu.facheck.base.RxLazyFragment;
import com.fzu.facheck.module.common.CommonActivity;
import com.fzu.facheck.utils.ConstantUtil;
import com.fzu.facheck.utils.PreferenceUtil;

import butterknife.BindView;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MyPageFragment extends RxLazyFragment {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mtoolbar_title;
    @BindView(R.id.nametext)
    TextView nameText;
    @BindView(R.id.phonetext)
    TextView phoneText;
    @BindView(R.id.user_photo)
    ImageView photoView;
    @BindView(R.id.identify)
    TextView identifyText;
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_my_pager;
    }

    @Override
    public void finishCreateView(Bundle state) {
        initToolBar();
        Glide.with(getActivity()).load(R.mipmap.photo).bitmapTransform(new RoundedCornersTransformation(getActivity()
        ,28,1,RoundedCornersTransformation.CornerType.ALL)).into(photoView);
        nameText.setText(PreferenceUtil.getString(ConstantUtil.EXTRA_NAME,""));
        phoneText.setText("手机号:"+PreferenceUtil.getString(ConstantUtil.PHONE_NUMBER,""));
    }
    @Override
    public void onResume(){
        super.onResume();
        if(PreferenceUtil.getBoolean(ConstantUtil.AUTHENTICATED,false)){
            identifyText.setText("已完成身份认证");
            identifyText.setClickable(false);
        }
    }
    private void initToolBar() {
        mToolbar.setTitle("");
        mtoolbar_title.setText("个人信息");
    }
    public static MyPageFragment newInstance() {
        return new MyPageFragment();
    }
    @OnClick({R.id.identify,R.id.exitlogin,R.id.theAbout})
    void onClick(View view){
        int data;
        Intent intent=new Intent(getActivity(),CommonActivity.class);
        switch(view.getId()){
            case R.id.identify:
                Intent intent1=new Intent(getActivity(),IdentifyActivity.class);
                startActivity(intent1);
                break;
            case R.id.theAbout:
                data=2;
                intent.putExtra("flag",data);
                startActivity(intent);
                break;
            case R.id.exitlogin:
                PreferenceUtil.putBoolean(ConstantUtil.KEY, false);
                getActivity().finish();
                break;
        }
    }
}
