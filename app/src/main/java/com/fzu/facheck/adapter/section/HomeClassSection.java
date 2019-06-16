package com.fzu.facheck.adapter.section;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.fzu.facheck.R;
import com.fzu.facheck.entity.RollCall.RollCallInfo;
import com.fzu.facheck.module.home.RollCallResultActivity;
import com.fzu.facheck.module.home.SignInActivity;
import com.fzu.facheck.network.RetrofitHelper;
import com.fzu.facheck.utils.ConstantUtil;
import com.fzu.facheck.utils.PreferenceUtil;
import com.fzu.facheck.utils.RxTimerUtil;
import com.fzu.facheck.widget.AlertDialog;
import com.fzu.facheck.widget.sectioned.StatelessSection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeClassSection extends StatelessSection {

    private List<RollCallInfo.ClassInfoBean.JoinedClassDataBean> jointedData;
    private List<RollCallInfo.ClassInfoBean.ManagedClassDataBean> createdData;
    private String type;
    private static final String JOINTED_DATA = "jointed_data";
    private static final String CREATE_DATA = "created_data";
    private Context mContext;
    private ArrayList<String> minutes = new ArrayList<>();
    private int rollCallTime;
    private OptionsPickerView pvOptions;
    private JSONObject jsonObject;
    private AlertDialog alertDialog;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private static OnSignInSuccessListener onSignInSuccessListener;
    private static OnSignInListener onSignInListener;


    private static AMapLocation mLocation; //当前点的位置
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;

    private String mRecordId;


    String TAG = "Home";


    public HomeClassSection(RollCallInfo.ClassInfoBean data, String type, SwipeRefreshLayout mSwipeRefreshLayout,
                            Context context) {
        super(R.layout.layout_home_class_header, R.layout.layout_home_class);
        this.jointedData = data.getJoinedClassData();
        this.createdData = data.getManagedClassData();
        this.type = type;
        this.mContext = context;
        this.mSwipeRefreshLayout = mSwipeRefreshLayout;
        getOptionData();
        initAmapLocation();
        initDialog();
    }



    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {

        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

        switch (type) {
            case JOINTED_DATA:
                final RollCallInfo.ClassInfoBean.JoinedClassDataBean joinedClassDataBean = jointedData.get(position);
                itemViewHolder.mClassName.setText(joinedClassDataBean.getJoinedClassName());
                itemViewHolder.mClassDuration.setText(getDtime(joinedClassDataBean.getJoinedClassTime()));


                if (joinedClassDataBean.getState().equals("0")) {
                    itemViewHolder.mBtn.setText(R.string.not_signed_in);
                    itemViewHolder.mBtn.setBackgroundResource(R.drawable.btn_rollcall_green);
                    if(onSignInSuccessListener!=null){
                        onSignInSuccessListener.setOnSignInSuccessListener(itemViewHolder);

                    }
                    itemViewHolder.mBtn.setOnClickListener(v -> {

                        Intent intent = new Intent(mContext, SignInActivity.class);

                        intent.putExtra("class_id", joinedClassDataBean.getJoinedClassId());
                        intent.putExtra("class_title", itemViewHolder.mClassName.getText());

                        mContext.startActivity(intent);



                    });

                } else if (joinedClassDataBean.getState().equals("1")){
                    itemViewHolder.mBtn.setText(R.string.signed_in);
                    itemViewHolder.mBtn.setBackgroundResource(R.drawable.btn_rollcall_blue);

                }else if (joinedClassDataBean.getState().equals("2")){
                    itemViewHolder.mBtn.setText(R.string.finished);
                    itemViewHolder.mBtn.setBackgroundResource(R.drawable.btn_rollcall_gray);

                }



                break;
            case CREATE_DATA:
                final RollCallInfo.ClassInfoBean.ManagedClassDataBean managedClassData = createdData.get(position);
                itemViewHolder.mClassName.setText(managedClassData.getManagedClassName());
                itemViewHolder.mClassDuration.setText(getDtime(managedClassData.getManagedClassTime()));
                if (managedClassData.isAbleRollCall().equals("0")) {
                    //开始点名
                    itemViewHolder.mBtn.setText(R.string.roll_call_on);
                    itemViewHolder.mBtn.setBackgroundResource(R.drawable.btn_rollcall_green);


                } else if (managedClassData.isAbleRollCall().equals("1")) {
                    //正在点名
                    itemViewHolder.mBtn.setText(R.string.roll_calling);
                    itemViewHolder.mBtn.setBackgroundResource(R.drawable.btn_rollcall_blue);

                } else if (managedClassData.isAbleRollCall().equals("2")) {
                    //点名结束
                    itemViewHolder.mBtn.setText(R.string.roll_call_off);
                    itemViewHolder.mBtn.setBackgroundResource(R.drawable.btn_rollcall_gray);

                }

                itemViewHolder.mBtn.setOnClickListener(v -> {

                    if (managedClassData.isAbleRollCall().equals("0")) {
                        initOptionPicker(position, itemViewHolder);
                    } else if (managedClassData.isAbleRollCall().equals("2")) {
                        Intent intent = new Intent(mContext, RollCallResultActivity.class);


                        mRecordId = PreferenceUtil.getString(createdData.get(position).getManagedClassId(), "wrong");
                        intent.putExtra("record_id", mRecordId);
                        intent.putExtra("class_title", itemViewHolder.mClassName.getText());
                        intent.putExtra("classId", createdData.get(position).getManagedClassId());
                        Log.i(TAG, "onBindItemViewHolder: " + mRecordId);

                        resetAbleRollCall(managedClassData.getManagedClassId());

                        mContext.startActivity(intent);
                        itemViewHolder.mBtn.setText(R.string.roll_call_on);
                        itemViewHolder.mBtn.setBackgroundResource(R.drawable.btn_rollcall_green);
                    }

                });

                itemViewHolder.classBody.setOnClickListener(v -> {

                    if (managedClassData.isAbleRollCall().equals("2")) {
                        Intent intent = new Intent(mContext, RollCallResultActivity.class);


                        mRecordId = PreferenceUtil.getString(createdData.get(position).getManagedClassId(), "wrong");
                        intent.putExtra("record_id", mRecordId);
                        intent.putExtra("class_title", itemViewHolder.mClassName.getText());
                        intent.putExtra("classId", createdData.get(position).getManagedClassId());
                        Log.i(TAG, "onBindItemViewHolder: " + mRecordId);

                        resetAbleRollCall(managedClassData.getManagedClassId());

                        mContext.startActivity(intent);
                        itemViewHolder.mBtn.setText(R.string.roll_call_on);
                        itemViewHolder.mBtn.setBackgroundResource(R.drawable.btn_rollcall_green);
                    }

                });


                break;

        }


    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {

        HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;

        switch (type) {
            case JOINTED_DATA:
                headerViewHolder.tileClass.setText(R.string.class_I_jointed);
                break;
            case CREATE_DATA:
                headerViewHolder.tileClass.setText(R.string.class_I_created);
                break;

        }
    }


    @Override
    public int getContentItemsTotal() {
        switch (type) {
            case JOINTED_DATA:
                return jointedData.size();
            case CREATE_DATA:
                return createdData.size();
            default:
                return 1;
        }
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.class_name)
        TextView mClassName;
        @BindView(R.id.class_duration)
        TextView mClassDuration;
        @BindView(R.id.item_btn_rollcall)
        public
        TextView mBtn;
        @BindView(R.id.class_body)
        RelativeLayout classBody;

        ItemViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title_class)
        TextView tileClass;


        HeaderViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    private void getOptionData() {
        for (int i = 1; i <= 10; i++) {
            minutes.add(i + "");
        }
    }

    private void initOptionPicker(int position, ItemViewHolder itemViewHolder) {
        pvOptions = new OptionsPickerBuilder(mContext, (options1, option2, options3, v1) -> {
            rollCallTime = Integer.valueOf(minutes.get(options1));
            if (mLocation != null) {
                uploadData(position, itemViewHolder);
            } else {
                alertDialog.setTitle("点名失败！");
                alertDialog.setType("failure");
                alertDialog.setMessage("当前网络状况不稳定\n请检查网络连接状况！");
                alertDialog.showup();
                alertDialog.show();
            }
        })
                .setTitleText("选择点名时间／分钟")
                .setSelectOptions(4)//默认选中项
                .build();
        pvOptions.setPicker(minutes);
        pvOptions.show();
    }

    private RequestBody initRequestbody(int position) {

        jsonObject = new JSONObject();

        try {

            jsonObject.put("longitude", mLocation.getLongitude());
            jsonObject.put("latitude", mLocation.getLatitude());

            jsonObject.put("classId", createdData.get(position).getManagedClassId());
            jsonObject.put("timeInterval", rollCallTime);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), jsonObject.toString());
        return requestBody;
    }


    private void uploadData(int position, ItemViewHolder itemViewHolder) {
        RetrofitHelper.getRollCallAPI()
                .getStartRollCallById(initRequestbody(position))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(resultBeans -> {

                    if (resultBeans.getCode().equals("0700")) {


                        PreferenceUtil.put(createdData.get(position).getManagedClassId(), resultBeans.getRecordId());
                        mSwipeRefreshLayout.setEnabled(false);

                        itemViewHolder.mBtn.setBackgroundResource(R.drawable.btn_rollcall_blue);
                        itemViewHolder.mBtn.setText(R.string.roll_calling);
                        createdData.get(position).setAbleRollCall("1");


                        RxTimerUtil.timer(rollCallTime, number -> {
                                    itemViewHolder.mBtn.setText(R.string.roll_call_off);
                                    itemViewHolder.mBtn.setBackgroundResource(R.drawable.btn_rollcall_gray);
                                    createdData.get(position).setAbleRollCall("2");
                                    mSwipeRefreshLayout.setEnabled(true);

                                }
                        );


                    } else {
                        alertDialog.setTitle("点名开启失败！");
                        alertDialog.setType("failure");
                        alertDialog.setMessage("请稍后再试！");
                        alertDialog.showup();
                        alertDialog.show();

                    }

                });


    }


    private void resetAbleRollCall(String classId) {

        jsonObject = new JSONObject();
        try {
            jsonObject.put("classId", classId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), jsonObject.toString());


        RetrofitHelper.getRollCallAPI()
                .resetAbleRollCall(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(resultBeans -> {

                    if (resultBeans.getCode().equals("1901")) {

                        alertDialog.setTitle("未知错误！");
                        alertDialog.setType("failure");
                        alertDialog.setMessage("请稍后再试！");
                        alertDialog.showup();
                        alertDialog.show();

                    }

                });


    }


    private void initAmapLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(mContext);
        //设置定位回调监听
        mLocationClient.setLocationListener(mAMapLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        // 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Transport);
        //设置定位模式为AMapLocationMode.Hight_Accuracy，设备定位模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(2000);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(50000);
        mLocationOption.setMockEnable(true);
        if (null != mLocationClient) {
            mLocationClient.setLocationOption(mLocationOption);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }
    }


    /**
     * 定位回调每1秒调用一次
     */
    public AMapLocationListener mAMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    mLocation = amapLocation;
                    if(onSignInListener!=null){
                        onSignInListener.setOnSignInListener(mLocation);
                    }
                    Log.e("loc", String.valueOf(mLocation.getLongitude()) + "   " + mLocation.getLatitude());
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:" + amapLocation.getErrorInfo());
                }
            }
        }
    };

    private String getDtime(String s) {
        String[] all = s.split(";");
        String s1 = all[0];
        for (int i = 1; i < all.length; i++) {
            s1 = s1 + "\n" + all[i];
        }
        return s1;
    }

    private void initDialog() {

        alertDialog = new AlertDialog(mContext);
        alertDialog.setCancelOnclickListener(new AlertDialog.cancelOnclickListener() {
            @Override
            public void onCancelClick() {
                alertDialog.dismiss();
            }
        });


    }

    public interface OnSignInSuccessListener{
        public void setOnSignInSuccessListener(ItemViewHolder itemViewHolder);
    }

    public static void setOnSignInSuccessListener(OnSignInSuccessListener mOnSignInSuccessListener) {
        onSignInSuccessListener = mOnSignInSuccessListener;
    }

    public interface OnSignInListener {
        public void setOnSignInListener(AMapLocation mLocation);
    }

    public static void setOnSignInListener(HomeClassSection.OnSignInListener mOnsignInlistener) {
        onSignInListener = mOnsignInlistener;
    }





}
