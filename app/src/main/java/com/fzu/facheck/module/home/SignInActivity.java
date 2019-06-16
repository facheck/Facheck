package com.fzu.facheck.module.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amap.api.location.AMapLocation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fzu.facheck.R;
import com.fzu.facheck.adapter.section.HomeClassSection;
import com.fzu.facheck.base.RxBaseActivity;
import com.fzu.facheck.entity.RollCall.RollCallInfo;
import com.fzu.facheck.entity.RollCall.RollCallResult;
import com.fzu.facheck.module.common.CameraActivity;
import com.fzu.facheck.network.RetrofitHelper;
import com.fzu.facheck.utils.ConstantUtil;
import com.fzu.facheck.utils.PhotoUtil;
import com.fzu.facheck.utils.PreferenceUtil;
import com.fzu.facheck.widget.AlertDialog;
import com.fzu.facheck.widget.CircleProgressView;
import com.fzu.facheck.widget.CustomImageDialog;
import com.fzu.facheck.widget.sectioned.SectionedRecyclerViewAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SignInActivity extends RxBaseActivity {

    String TAG = "SignIn";
    @BindView(R.id.circle_progress)
    CircleProgressView circleProgress;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.title_class)
    TextView titleClass;

    @BindView(R.id.tips_icon)
    ImageView tipsIcon;
    @BindView(R.id.bt_del)
    Button btDel;
    @BindView(R.id.commit_selfile)
    Button commitSelfile;
    @BindView(R.id.selfile)
    ImageView selfile;


    private String mTitle;
    private SectionedRecyclerViewAdapter mSectionedAdapter;
    private boolean mIsRefreshing = false;
    private String mClassId;
    private RollCallResult mRollCallResult;
    private Bitmap bitmap = null;
    private static final int TAKE_POTHO = 1;
    private File outImage;
    private AlertDialog alertDialog;
    private JSONObject jsonObject;
    private AMapLocation myLocation;


    @Override
    public int getLayoutId() {
        return R.layout.activity_sign_in;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        Intent newIntent = getIntent();

        if (newIntent != null) {
            mTitle = newIntent.getStringExtra(ConstantUtil.EXTRA_CLASS_TITLE) + "-签到";
            mClassId = newIntent.getStringExtra(ConstantUtil.EXTRA_CLASS_ID);
        }

        HomeClassSection.setOnSignInListener(new HomeClassSection.OnSignInListener() {
            @Override
            public void setOnSignInListener(AMapLocation mLocation) {
                myLocation = mLocation;
            }
        });
        initToolBar();

        selfile.setOnClickListener(v -> {
            if (selfile.getDrawable() == null) {
                Intent intent = new Intent(SignInActivity.this, CameraActivity.class);
                startActivityForResult(intent, TAKE_POTHO);
            } else {
                CustomImageDialog customImageDialog = null;
                customImageDialog = new CustomImageDialog(SignInActivity.this, outImage);
                customImageDialog.show();
            }
        });

        btDel.setVisibility(View.INVISIBLE);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_POTHO:
                if (resultCode == RESULT_OK) {

                    if (data != null) {
                        outImage = new File(data.getStringExtra("photo"));

                        Glide.with(this)
                                .load(outImage)
                                .priority(Priority.HIGH)
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(selfile);


                        btDel.setVisibility(View.VISIBLE);

                        btDel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (outImage.exists()) {
                                    outImage.delete();
                                    selfile.setImageDrawable(null);
                                }
                                btDel.setVisibility(View.INVISIBLE);


                            }
                        });
                    }

                }
                break;

            default:
                break;
        }
    }


    @Override
    public void initToolBar() {
        toolbar.setTitle("");
        toolbarTitle.setText(mTitle);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        commitSelfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selfile.getDrawable() == null) {

                    alertDialog.setTitle("签到失败！");
                    alertDialog.setType("failure");
                    alertDialog.setMessage("请先上传自拍照！");
                    alertDialog.showup();
                    alertDialog.show();

                } else if (myLocation == null) {
                    alertDialog.setTitle("签到失败！");
                    alertDialog.setType("failure");
                    alertDialog.setMessage("当前网络状况不稳定\n请检查网络连接状况！");
                    alertDialog.showup();
                    alertDialog.show();

                } else {
                    alertDialog.setTitle("请稍后...");
                    alertDialog.setType("loading");
                    alertDialog.setMessage("正在上传和识别...");
                    alertDialog.showup();
                    alertDialog.show();
                    uploadData();


                }

            }
        });

    }

    @Override
    public void initNavigationView() {

    }


    @Override
    public void finishTask() {

        hideProgressBar();


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

        }
        return super.onOptionsItemSelected(item);
    }



    private RequestBody initRequestbody() {

        jsonObject = new JSONObject();
        String photo = PhotoUtil.base64(PhotoUtil.amendRotatePhoto(PhotoUtil.getPath()));
        try {
            jsonObject.put("comparedPhoto", photo);

            jsonObject.put("longitude", myLocation.getLongitude());
            jsonObject.put("latitude", myLocation.getLatitude());

            String phone_number = PreferenceUtil.getString(ConstantUtil.PHONE_NUMBER, "wrong");

            jsonObject.put("phoneNumber", phone_number);
            jsonObject.put("classId", mClassId);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), jsonObject.toString());
        return requestBody;
    }

    private void uploadData() {
        RetrofitHelper.postSignInAPI()
                .postSignInInfo(initRequestbody())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(resultBeans -> {


                    switch (resultBeans.getCode()) {
                        case "0300":
                            alertDialog.setTitle("签到成功！");
                            alertDialog.setType("success");
                            alertDialog.setMessage("好好学习哦！");
                            HomeClassSection.setOnSignInSuccessListener(new HomeClassSection.OnSignInSuccessListener() {
                                @Override
                                public void setOnSignInSuccessListener(HomeClassSection.ItemViewHolder itemViewHolder) {
                                    itemViewHolder.mBtn.setText(R.string.signed_in);
                                    itemViewHolder.mBtn.setBackgroundResource(R.drawable.btn_rollcall_blue);
                                }
                            });


                            break;
                        case "0301":
                            alertDialog.setTitle("签到失败！");
                            alertDialog.setType("failure");
                            alertDialog.setMessage("还未开启点名！");
                            break;
                        case "0302":
                            alertDialog.setTitle("签到失败！");
                            alertDialog.setType("failure");
                            alertDialog.setMessage("不允许重复签到！");
                            break;
                        case "0303":
                            alertDialog.setTitle("签到失败！");
                            alertDialog.setType("failure");
                            alertDialog.setMessage("未监测到人脸！");
                            break;
                        case "0304":
                            alertDialog.setTitle("签到失败！");
                            alertDialog.setType("failure");
                            alertDialog.setMessage("人脸对比失败！");
                            break;
                        case "0305":
                            alertDialog.setTitle("签到失败！");
                            alertDialog.setType("failure");
                            alertDialog.setMessage("您不在点名范围内！");
                            break;
                        case "0306":
                            alertDialog.setTitle("签到失败！");
                            alertDialog.setType("failure");
                            alertDialog.setMessage("请重新再试！");
                            break;



                    }
                    alertDialog.showup();
                    alertDialog.show();






                });


    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
