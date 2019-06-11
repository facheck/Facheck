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
import com.fzu.facheck.entity.RollCall.RollCallInfo;
import com.fzu.facheck.module.home.ClassPageActivity;
import com.fzu.facheck.widget.sectioned.StatelessSection;
import com.thinkcool.circletextimageview.CircleTextImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class HomeClassPageSection extends StatelessSection {
    private List<RollCallInfo.ClassInfoBean.JoinedClassDataBean> jointedData;
    private List<RollCallInfo.ClassInfoBean.ManagedClassDataBean> createdData;
    private String type;
    private static final String JOINTED_DATA = "jointed_data";
    private static final String CREATE_DATA = "created_data";
    private Context mContext;

    public HomeClassPageSection(RollCallInfo.ClassInfoBean data, String type, Context context){
        super(R.layout.layout_home_class_header, R.layout.linear_layout,R.layout.layout_per_class_info);
        this.jointedData = data.getJoinedClassData();
        this.createdData = data.getManagedClassData();
        this.type = type;
        this.mContext=context;
    }

    @Override
    public int getContentItemsTotal() {
        switch (type){
            case JOINTED_DATA:
                if(jointedData!=null)
                    return jointedData.size();
                else
                    return 0;
            case CREATE_DATA:
                if(createdData!=null)
                    return createdData.size();
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
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        switch (type){
            case JOINTED_DATA:
                final RollCallInfo.ClassInfoBean.JoinedClassDataBean joinedClassDataBean = jointedData.get(position);
                if(position==0){
                    if(position==jointedData.size()-1)
                        itemViewHolder.midlinear.setVisibility(View.GONE);
                    else
                        itemViewHolder.bottomlinear.setVisibility(View.GONE);
                }
                else if(position==jointedData.size()-1){
                    itemViewHolder.toplinear.setVisibility(View.GONE);
                    itemViewHolder.midlinear.setVisibility(View.GONE);
                }
                else{
                    itemViewHolder.bottomlinear.setVisibility(View.GONE);
                    itemViewHolder.toplinear.setVisibility(View.GONE);
                }
                itemViewHolder.leftText.setText(joinedClassDataBean.getJoinedClassName());
                itemViewHolder.iconView.setText(String.valueOf(joinedClassDataBean.getJoinedClassName().charAt(0)));
                itemViewHolder.relativeLayout.setOnClickListener(v->{
                    Intent intent=new Intent(mContext,ClassPageActivity.class);
                    intent.putExtra("master",false);
                    intent.putExtra("classid",joinedClassDataBean.getJoinedClassId());
                    intent.putExtra("classname",joinedClassDataBean.getJoinedClassName());
                    mContext.startActivity(intent);
                });
                break;
            case CREATE_DATA:
                final RollCallInfo.ClassInfoBean.ManagedClassDataBean managedClassData = createdData.get(position);
                if(position==0){
                    if(position==createdData.size()-1)
                        itemViewHolder.midlinear.setVisibility(View.GONE);
                    else
                        itemViewHolder.bottomlinear.setVisibility(View.GONE);
                }
                else if(position==createdData.size()-1){
                    itemViewHolder.toplinear.setVisibility(View.GONE);
                    itemViewHolder.midlinear.setVisibility(View.GONE);
                }
                else{
                    itemViewHolder.bottomlinear.setVisibility(View.GONE);
                    itemViewHolder.toplinear.setVisibility(View.GONE);
                }
                itemViewHolder.iconView.setText(String.valueOf(managedClassData.getManagedClassName().charAt(0)));
                itemViewHolder.leftText.setText(managedClassData.getManagedClassName());
                itemViewHolder.relativeLayout.setOnClickListener(v->{
                    Intent intent=new Intent(mContext,ClassPageActivity.class);
                    intent.putExtra("master",true);
                    intent.putExtra("classid",managedClassData.getManagedClassId());
                    intent.putExtra("classname",managedClassData.getManagedClassName());
                    mContext.startActivity(intent);
                });
                break;
        }
    }
    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
        switch (type){
            case JOINTED_DATA:
                headerViewHolder.tileClass.setText(R.string.class_I_jointed);
                break;
            case CREATE_DATA:
                headerViewHolder.tileClass.setText(R.string.class_I_created);
                break;
        }
    }
    @Override
    public void onBindFooterViewHolder(RecyclerView.ViewHolder holder) {
            FooterViewHolder footerViewHolder=(FooterViewHolder) holder;
            switch (this.type){
                case JOINTED_DATA:
                    if(jointedData!=null&&jointedData.size()>0){
                        footerViewHolder.hintText.setVisibility(View.GONE);
                        footerViewHolder.linearLayout.setVisibility(View.GONE);
                    }
                    else
                        footerViewHolder.hintText.setText("您还未加入班级!");
                    break;
                case CREATE_DATA:
                    if(createdData!=null&&createdData.size()>0) {
                        footerViewHolder.hintText.setVisibility(View.GONE);
                        footerViewHolder.linearLayout.setVisibility(View.GONE);
                    }
                    else
                        footerViewHolder.hintText.setText("您还未创建过班级!");
                    break;
            }
        }
    static class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.left_name)
        TextView leftText;
        @BindView(R.id.item_view)
        RelativeLayout relativeLayout;
        @BindView(R.id.item_icon)
        CircleTextImageView iconView;
        @BindView(R.id.midline)
        LinearLayout midlinear;
        @BindView(R.id.bottomline)
        LinearLayout bottomlinear;
        @BindView(R.id.topline)
        LinearLayout toplinear;
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
