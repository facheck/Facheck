package com.fzu.facheck.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fzu.facheck.R;

import butterknife.BindView;

public class AlertDialog extends Dialog {
    private String title_str;
    private String dialogMsg_str;
    private String type;
    private TextView title;
    private CircleProgressView circleProgress;
    private ImageView iconStatus;
    private TextView dialogMsg;
    private ImageView cancel;
    private cancelOnclickListener cancelOnclickListener;
    private Context mContext;


    public AlertDialog(Context context) {
        super(context, R.style.custom_dialog2);
        mContext = context;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_dialog_alert_layout);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);

        initView();
        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();

    }

    public void showup() {
        setContentView(R.layout.my_dialog_alert_layout);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);

        initView();
        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();
    }


    public void initView() {

        title = (TextView) findViewById(R.id.title);
        circleProgress = (com.fzu.facheck.widget.CircleProgressView) findViewById(R.id.circle_progress);
        iconStatus = (ImageView) findViewById(R.id.icon_status);
        dialogMsg = (TextView) findViewById(R.id.dialog_msg);
        cancel = (ImageView) findViewById(R.id.cancel);


    }

    public void initData() {

        if (title_str != null) {
            title.setText(title_str);
        }
        if (dialogMsg_str != null) {
            dialogMsg.setText(dialogMsg_str);
        }

        if (type != null) {
            switch (type) {
                case "success":
                    circleProgress.setVisibility(View.GONE);
                    iconStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_success));
                    iconStatus.setColorFilter(mContext.getResources().getColor(R.color.colorPrimary));
                    break;
                case "failure":
                    circleProgress.setVisibility(View.GONE);
                    iconStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_failure));
                    iconStatus.setColorFilter(mContext.getResources().getColor(R.color.delete_backgroud));

                    break;
                case "loading":
                    iconStatus.setVisibility(View.GONE);
                    circleProgress.spin();
                    break;
                default:
                    break;


            }
        }


    }


    /**
     * @param cancelOnclickListener
     */
    public void setCancelOnclickListener(cancelOnclickListener cancelOnclickListener) {

        this.cancelOnclickListener = cancelOnclickListener;
    }


    /**
     * @param title
     */
    public void setTitle(String title) {
        this.title_str = title;
    }

    /**
     * @param message
     */
    public void setMessage(String message) {
        this.dialogMsg_str = message;
    }


    /**
     * @param type
     */
    public void setType(String type) {

        this.type = type;


    }

    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface cancelOnclickListener {
        public void onCancelClick();
    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelOnclickListener != null) {
                    cancelOnclickListener.onCancelClick();
                }
            }
        });

    }






}
