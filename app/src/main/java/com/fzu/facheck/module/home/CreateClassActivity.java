package com.fzu.facheck.module.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.fzu.facheck.R;
import com.fzu.facheck.base.RxBaseActivity;
import com.fzu.facheck.entity.RollCall.CreateClassInfo;
import com.fzu.facheck.network.RetrofitHelper;
import com.fzu.facheck.utils.ConstantUtil;
import com.fzu.facheck.utils.PreferenceUtil;
import com.fzu.facheck.utils.TimeUtil;
import com.fzu.facheck.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CreateClassActivity extends RxBaseActivity {

    @BindView(R.id.class_name)
    EditText classnameText;
    @BindView(R.id.the_begin_time)
    TextView begintimeText;
    @BindView(R.id.the_end_time)
    TextView endtimeText;
    @BindView(R.id.class_code)
    EditText codeText;
    @BindView(R.id.the_work_date)
    TextView worddateText;
    ArrayList<String> wordData=new ArrayList<>();
    private static final int SET_TIME = 1;
    @Override
    public int getLayoutId() {
        return R.layout.layout_create_calss;
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
    @OnClick({R.id.cancel,R.id.commit
            ,R.id.the_begin_time,R.id.the_end_time,R.id.the_work_date})
    void onClick(View view){
        switch (view.getId()){
            case R.id.the_begin_time:
                selectTime(begintimeText);
                break;
            case R.id.the_end_time:
                selectTime(endtimeText);
                break;
            case R.id.the_work_date:
                Intent intent=new Intent(CreateClassActivity.this,SelectClassTimeActivity.class);
                intent.putStringArrayListExtra("times",wordData);
                startActivityForResult(intent,SET_TIME);
                break;
            case R.id.cancel:
                finish();
                break;
            case R.id.commit:
                sendCreateRequest();
                break;
        }
    }
    private void selectTime(TextView vv){
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        startDate.set(2019,0,1);
        endDate.set(2022,11,31);
        TimePickerView pvtime=new TimePickerBuilder(this,new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                vv.setText(TimeUtil.formatDate(date));
            }
        }).setType(new boolean[]{true,true,true,false,false,false})
                .setCancelText("取消")
                .setSubmitText("确定")
                .setTitleText("请选择时间")
                .setDate(selectedDate)
                .setRangDate(startDate,endDate)
                .setLabel("年","月","日",
                        null,null,null)
                .isCenterLabel(false)
                .setOutSideCancelable(false)
                .build();
        pvtime.show();
    }
    private void sendCreateRequest(){
        String classname=classnameText.getText().toString();
        String begintime=begintimeText.getText().toString();
        String endtime=endtimeText.getText().toString();
        String code=codeText.getText().toString();
        String worddate=getTime();
        if(TextUtils.isEmpty(classname)||TextUtils.isEmpty(code)||worddate==null)
            ToastUtil.showShort(CreateClassActivity.this,"请填入完整信息");
        else if(begintime.equals("未设置 >")||endtime.equals("未设置 >"))
            ToastUtil.showShort(CreateClassActivity.this,"请填入完整信息");
        else{
            final JSONObject classobject=new JSONObject();
            try {
                classobject.put("className",classname);
                classobject.put("classTime",worddate);
                classobject.put("classCode",code);
                classobject.put("startTime",begintime);
                classobject.put("endTime",endtime);
                classobject.put("phoneNumber",PreferenceUtil.getString(ConstantUtil.PHONE_NUMBER,""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody requestBody=RequestBody.create(MediaType.parse("application/json;charset=utf-8"),classobject.toString());
            RetrofitHelper.getClassInfo().getclassserver("create_class",requestBody)
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<CreateClassInfo>() {
                        @Override
                        public void onCompleted() { }
                        @Override
                        public void onError(Throwable e) { ToastUtil.showShort(CreateClassActivity.this,"请求失败");}
                        @Override
                        public void onNext(CreateClassInfo classInfo) {
                            if(classInfo.code.equals("0500")){
                                wordData.clear();
                                ToastUtil.showShort(CreateClassActivity.this,"创建成功");
                                finish();
                            }
                            else if(classInfo.code.equals("0501"))
                                ToastUtil.showShort(CreateClassActivity.this,"创建失败");
                            else
                                ToastUtil.showShort(CreateClassActivity.this,"未知情况错误");
                        }
                    });
        }
    }
//    private void showupPopWindow(){
//        View contentView=LayoutInflater.from(this).inflate(R.layout.popupwin_layout,null);
//        PopupWindow mpopWindow=new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT,true);
//        mpopWindow.setContentView(contentView);
//        TextView tv1=(TextView)contentView.findViewById(R.id.select_one);
//        tv1.setText("添加");
//        TextView tv2=(TextView)contentView.findViewById(R.id.select_two);
//        tv2.setText("清除");
//        TextView tv3=(TextView)contentView.findViewById(R.id.cancel_select);
//        tv1.setOnClickListener((v)->{
//            mpopWindow.dismiss();
//            selectWordDate();
//        });
//        tv2.setOnClickListener((v)->{
//            worddateText.setText("未设置 >");
//            wordData.clear();
//            mpopWindow.dismiss();
//        });
//        tv3.setOnClickListener((v)->{
//            mpopWindow.dismiss();
//        });
//        View rootView=LayoutInflater.from(this).inflate(R.layout.activity_main,null);
//        mpopWindow.showAtLocation(rootView,Gravity.BOTTOM,0,0);
//    }
//    private void selectWordDate(){
//        String[] week={"星期一","星期二","星期三","星期四","星期五","星期六","星期日"};
//        String[] duringtime={"上午8:20-10:00","上午10:20-12:00","下午14:00-15:40","下午15:50-17:30"
//                ,"晚上19:00-20:40"};
//        OptionsPickerView pvOptions=new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
//            @Override
//            public void onOptionsSelect(int options1, int options2, int options3, View v) {
//                wordData.add(week[options1]+" "+duringtime[options2]);
//                String s="";
//                for(String dt:wordData)
//                    s=s+dt+";";
//                worddateText.setText(s);
//            }
//        }).setTitleText("上课时间")
//                .setCancelText("取消")
//                .setSubmitText("确定")
//                .setOutSideCancelable(false)
//                .setSelectOptions(4)
//                .build();
//        pvOptions.setNPicker(Arrays.asList(week),Arrays.asList(duringtime),null);
//        pvOptions.show();
//    }
    private String getTime(){
        String s="";
        if(wordData.size()==0)
            return null;
        else{
            for(int i=0;i<wordData.size();i++){
                s+=wordData.get(i);
                if(i!=wordData.size()-1)
                    s+=";";
            }
        }
        return s;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case SET_TIME:
                if(resultCode==RESULT_OK){
                    if(data!=null) {
                        wordData = data.getStringArrayListExtra("times");
                        if(wordData!=null&&wordData.size()!=0)
                            worddateText.setText("已设置 >");
                        else
                            worddateText.setText("未设置 >");
                    }
                }
                break;
            default:
                break;
        }
    }
}
