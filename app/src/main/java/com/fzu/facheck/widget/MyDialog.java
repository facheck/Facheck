package com.fzu.facheck.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.fzu.facheck.R;

public class MyDialog extends Dialog {
    public MyDialog(Context context) {
        super(context);
    }
    public MyDialog(Context context, int themeResId) {
        super(context, themeResId);
    }
    public static class Builder{
        private Context context;
        private String title;
        private String msg;
        private View contentView;
        private EditText passText;
        private TextView msgText;
        private OnClickListener leftClick;
        private OnClickListener rightClick;
        public Builder(Context context){
            this.context=context;
        }
        public Builder setTitle(String title){
            this.title=title;
            return this;
        }
        public Builder setLeft(OnClickListener listener){
            this.leftClick=listener;
            return this;
        }
        public Builder setRight(OnClickListener listener){
            this.rightClick=listener;
            return this;
        }
        public Builder setcontentView(View view){
            this.contentView=view;
            return this;
        }
        public Builder setMsg(String msg){
            this.msg=msg;
            return this;
        }
        public String getpass(){
            return passText.getText().toString();
        }
        public MyDialog create(int type){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            final MyDialog dialog=new MyDialog(context,R.style.custom_dialog2);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            View layout=inflater.inflate(R.layout.my_dialog_layout,null);
            dialog.addContentView(layout,new LinearLayout.LayoutParams(LinearLayout.LayoutParams
            .MATCH_PARENT,RadioGroup.LayoutParams.WRAP_CONTENT));
            ((TextView)layout.findViewById(R.id.title)).setText(title);
            passText=(EditText)layout.findViewById(R.id.password);
            msgText=(TextView)layout.findViewById(R.id.dialog_msg);
            switch (type){
                case 1:
                    msgText.setVisibility(View.GONE);
                    break;
                case 2:
                    passText.setVisibility(View.GONE);
                    msgText.setText(msg);
                    break;
            }
            if(leftClick!=null){
                ((TextView)layout.findViewById(R.id.left)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        leftClick.onClick(dialog,DialogInterface.BUTTON_NEGATIVE);
                    }
                });
            }
            if(rightClick!=null){
                ((TextView)layout.findViewById(R.id.right)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rightClick.onClick(dialog,DialogInterface.BUTTON_POSITIVE);
                    }
                });
            }
            dialog.setContentView(layout);
            return dialog;
        }
    }
}
