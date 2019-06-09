package com.fzu.facheck.adapter.section;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fzu.facheck.R;
import com.fzu.facheck.entity.RollCall.ClassInfo;
import com.fzu.facheck.module.home.ClassPageActivity;
import com.fzu.facheck.module.home.RollCallResultActivity;
import com.fzu.facheck.module.home.StudentinfoActivity;
import com.fzu.facheck.widget.sectioned.StatelessSection;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class HomeClassInfoSection extends StatelessSection {

    private static final String STUDENT = "student";
    private static final String RECORDS = "records";
    private List<ClassInfo.Student> students;
    private List<ClassInfo.Record> records;
    private String type;
    private Context mContext;


    public HomeClassInfoSection(ClassInfo classInfo,String type,Context context){
        super(R.layout.layout_home_class_header,R.layout.linear_layout,R.layout.layout_per_info);
        students=classInfo.getStudents();
        records=classInfo.getRecords();
        this.type=type;
        this.mContext=context;
    }
    @Override
    public int getContentItemsTotal() {
        switch (type){
            case STUDENT:
                if(students!=null)
                    return students.size();
                else
                    return 0;
            case RECORDS:
                if(records!=null)
                    return records.size();
                else
                    return 0;
            default:
                return 1;
        }
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
    public RecyclerView.ViewHolder getFooterViewHolder(View view) {
        return new FooterViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder=(ItemViewHolder)holder;
        switch (this.type){
            case STUDENT:
                final ClassInfo.Student student=students.get(position);
                if(position==0){
                    if(position==students.size()-1)
                        itemViewHolder.midlinear.setVisibility(View.GONE);
                    else
                        itemViewHolder.bottomlinear.setVisibility(View.GONE);
                }
                else if(position==students.size()-1){
                    itemViewHolder.toplinear.setVisibility(View.GONE);
                    itemViewHolder.midlinear.setVisibility(View.GONE);
                }
                else{
                    itemViewHolder.bottomlinear.setVisibility(View.GONE);
                    itemViewHolder.toplinear.setVisibility(View.GONE);
                }
                Glide.with(mContext).load(R.mipmap.photo).bitmapTransform(new RoundedCornersTransformation(mContext
                        ,28,1,RoundedCornersTransformation.CornerType.ALL)).into(itemViewHolder.iconView);
                itemViewHolder.LeftnameText.setText(student.getName());
                itemViewHolder.relativeLayout.setOnClickListener(v->{
                    Intent intent=new Intent(mContext,StudentinfoActivity.class);
                    intent.putExtra("master",((ClassPageActivity)mContext).master);
                    intent.putExtra("studentname",student.getName());
                    intent.putExtra("phoneNumber",student.getPhoneNumber());
                    intent.putExtra("classname",((ClassPageActivity)mContext).classname);
                    intent.putExtra("classid",((ClassPageActivity)mContext).classid);
                    mContext.startActivity(intent);
                });
                break;
            case RECORDS:
                final ClassInfo.Record record=records.get(position);
                itemViewHolder.iconView.setVisibility(View.GONE);
                itemViewHolder.midlinear.setVisibility(View.GONE);
                if(position==0) {
                    if(position!=records.size()-1)
                        itemViewHolder.bottomlinear.setVisibility(View.GONE);
                }
                else
                    itemViewHolder.toplinear.setVisibility(View.GONE);
                itemViewHolder.LeftnameText.setText(record.getTime());
                itemViewHolder.RightnameText.setText(record.getAttendratio());
                itemViewHolder.relativeLayout.setOnClickListener(v->{
                    Intent intent=new Intent(mContext,RollCallResultActivity.class);
                    intent.putExtra("record_id",record.getRecordid());
                    intent.putExtra("class_title",((ClassPageActivity)mContext).classname);
                    mContext.startActivity(intent);
                });
                break;
        }
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerViewHolder=(HeaderViewHolder)holder;
        switch (this.type){
            case STUDENT:
                headerViewHolder.tileText.setText("总人数："+students.size()+"人");
                break;
            case RECORDS:
                headerViewHolder.tileText.setText("点名记录");
                break;
        }
    }
    @Override
    public void onBindFooterViewHolder(RecyclerView.ViewHolder holder) {
            FooterViewHolder footerViewHolder=(FooterViewHolder) holder;
            switch (this.type){
                case STUDENT:
                    if(students!=null&&students.size()>0) {
                        footerViewHolder.hintText.setVisibility(View.GONE);
                        footerViewHolder.linearLayout.setVisibility(View.GONE);
                    }
                    else
                        footerViewHolder.hintText.setText("请让学生通过邀请码加入班级");
                    break;
                case RECORDS:
                    if(records!=null&&records.size()>0) {
                        footerViewHolder.hintText.setVisibility(View.GONE);
                        footerViewHolder.linearLayout.setVisibility(View.GONE);
                    }
                    else
                        footerViewHolder.hintText.setText("暂无！");
                    break;
            }
        }


    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title_class)
        TextView tileText;
        HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
    static class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.left_name)
        TextView LeftnameText;
        @BindView(R.id.right_name)
        TextView RightnameText;
        @BindView(R.id.item_view)
        RelativeLayout relativeLayout;
        @BindView(R.id.item_icon)
        ImageView iconView;
        @BindView(R.id.midline)
        LinearLayout midlinear;
        @BindView(R.id.bottomline)
        LinearLayout bottomlinear;
        @BindView(R.id.topline)
        LinearLayout toplinear;
        ItemViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
    static class FooterViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.hint)
        TextView hintText;
        @BindView(R.id.foot_view)
        LinearLayout linearLayout;
        FooterViewHolder(View itemview){
            super(itemview);
            ButterKnife.bind(this, itemView);
        }
    }
}
