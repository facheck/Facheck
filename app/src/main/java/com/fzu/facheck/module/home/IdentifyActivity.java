package com.fzu.facheck.module.home;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.fzu.facheck.R;
import com.fzu.facheck.base.RxBaseActivity;
import com.fzu.facheck.entity.RollCall.StateInfo;
import com.fzu.facheck.network.RetrofitHelper;
import com.fzu.facheck.utils.ConstantUtil;
import com.fzu.facheck.utils.PreferenceUtil;
import com.fzu.facheck.utils.ToastUtil;
import com.fzu.facheck.widget.MyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class IdentifyActivity extends RxBaseActivity {
    @BindView(R.id.you_name)
    EditText nameText;
    @BindView(R.id.you_school_id)
    EditText idText;
    @BindView(R.id.admission_year)
    TextView yearText;
    @Override
    public int getLayoutId() {
        return R.layout.identify_layout;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
    }
    @Override
    public void initToolBar() {
    }
    @Override
    public void initNavigationView() {
    }
    @OnClick({R.id.cancel_msg,R.id.submit_msg,R.id.admission_year})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.cancel_msg:
                finish();
                break;
            case R.id.admission_year:
                selectTime(yearText);
                break;
            case R.id.submit_msg:
                sendRequest();
                break;
        }
    }
    private void selectTime(TextView vv){
        List<String> years=new ArrayList<>();
        for(int i=0;i<8;i++){
            years.add(2014+i+"");
        }
        OptionsPickerView pickerView=new OptionsPickerBuilder(this,
                (options1, option2, options3, v1)->{vv.setText(years.get(options1));})
                .setCancelText("取消")
                .setSubmitText("确定")
                .setTitleText("选择入学年份")
                .setSelectOptions(4)
                .build();
        pickerView.setPicker(years);
        pickerView.show();
    }
    private void sendRequest(){
        String name=nameText.getText().toString();
        String id=idText.getText().toString();
        String admission_year=yearText.getText().toString();
        if(TextUtils.isEmpty(name)||TextUtils.isEmpty(id)||
                admission_year.equals("未设置 >"))
            ToastUtil.showShort(IdentifyActivity.this,"请将信息填写完整");
        else{
            final JSONObject userobject=new JSONObject();
            try {
                userobject.put("phoneNumber",PreferenceUtil.getString(ConstantUtil.PHONE_NUMBER,""));
                userobject.put("name",name);
                userobject.put("authenticationId",id);
                userobject.put("admissionYear",admission_year);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody requestBody=RequestBody.create(MediaType.parse("application/json;charset=utf-8"),userobject.toString());
            RetrofitHelper.getLoAPI().getserver("identity_authenticate",requestBody)
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<StateInfo>() {
                        @Override
                        public void onCompleted() { }
                        @Override
                        public void onError(Throwable e) {
                            ToastUtil.showShort(IdentifyActivity.this,"请求失败");
                        }
                        @Override
                        public void onNext(StateInfo stateInfo) {
                            if(stateInfo.code.equals("0200")){
                                ToastUtil.showShort(IdentifyActivity.this,"认证成功");
                                PreferenceUtil.putBoolean(ConstantUtil.AUTHENTICATED,true);
                                finish();
                            }
                            else if(stateInfo.code.equals("0201"))
                                ToastUtil.showShort(IdentifyActivity.this,"认证失败");
                            else if(stateInfo.code.equals("0203"))
                                ToastUtil.showShort(IdentifyActivity.this,"该学号已经被认证");
                            else
                                ToastUtil.showShort(IdentifyActivity.this,"其他情况错误");
                        }
                    });
        }
    }
}
