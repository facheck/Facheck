package com.fzu.facheck.adapter.section;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fzu.facheck.R;
import com.fzu.facheck.entity.RollCall.SearchClassInfo;
import com.fzu.facheck.module.home.SearchActivity;
import com.fzu.facheck.network.RetrofitHelper;
import com.fzu.facheck.utils.ToastUtil;
import com.fzu.facheck.widget.MyDialog;
import com.fzu.facheck.widget.sectioned.StatelessSection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchClassSection extends StatelessSection {
    private List<SearchClassInfo.ResultClass> resultClasses;
    private Context mContext;
    public SearchClassSection(SearchClassInfo data,Context context){
        super(R.layout.layout_home_class);
        this.resultClasses=data.getResultClasses();
        this.mContext=context;
    }
    @Override
    public int getContentItemsTotal() {
        if(resultClasses!=null)
            return resultClasses.size();
        else
            return 0;
    }
    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }
    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder=(ItemViewHolder)holder;
        final SearchClassInfo.ResultClass resultClass=resultClasses.get(position);
        itemViewHolder.mClassName.setText(resultClass.getClassName());
        itemViewHolder.mClassDuration.setText("任课教师："+resultClass.getTeacherName());
        if(resultClass.getState()){
            itemViewHolder.mBtn.setText("已加入");
            itemViewHolder.mBtn.setBackgroundResource(R.drawable.btn_rollcall_gray);
            itemViewHolder.mBtn.setClickable(false);
        }
        else{
            itemViewHolder.mBtn.setText("申请加入");
            itemViewHolder.mBtn.setBackgroundResource(R.drawable.btn_rollcall_green);
            itemViewHolder.mBtn.setOnClickListener(v->showDialog(resultClass.getClassid()));
        }
    }
    static class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.class_name)
        TextView mClassName;
        @BindView(R.id.class_duration)
        TextView mClassDuration;
        @BindView(R.id.item_btn_rollcall)
        Button mBtn;
        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
    public void showDialog(String id){
        MyDialog.Builder builder=new MyDialog.Builder(mContext);
        builder.setTitle("加入班级");
        builder.setLeft(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setRight(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String pass=builder.getpass();
                final JSONObject userobject=new JSONObject();
                try {
                    userobject.put("phoneNumber",((SearchActivity)mContext).phoneNumber);
                    userobject.put("classId",id);
                    userobject.put("classCode",pass);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody requestBody=RequestBody.create(MediaType.parse("application/json;charset=utf-8"),userobject.toString());
                RetrofitHelper.getClassInfo()
                        .joinclass("join_class",requestBody)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(resultbean->{
                            if(resultbean.code.equals("0600")){
                                ToastUtil.showShort(mContext,"加入成功");
                                dialog.dismiss();
                                ((SearchActivity) mContext).finish();
                            }else if(resultbean.code.equals("0602")){
                                ToastUtil.showShort(mContext,"邀请码错误");
                                dialog.dismiss();
                            }else if(resultbean.code.equals("0603")){
                                ToastUtil.showShort(mContext,"您还未进行身份认证");
                                dialog.dismiss();
                            }else if(resultbean.equals("0601")){
                                ToastUtil.showShort(mContext,"已经加入班级");
                                dialog.dismiss();
                            }else {
                                ToastUtil.showShort(mContext, "未知情况错误");
                                dialog.dismiss();
                            }
                        },throwable ->{ ToastUtil.showShort(mContext,"加入失败");dialog.dismiss();});
            }
        });
        builder.create(1).show();
    }
}
