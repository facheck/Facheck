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
import com.fzu.facheck.widget.sectioned.SectionedRecyclerViewAdapter;
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
    private SectionedRecyclerViewAdapter sectionAdapter;
    boolean expanded = false;
    boolean empty=false;


    public HomeClassInfoSection(ClassInfo classInfo,String type,Context context,SectionedRecyclerViewAdapter sectionAdapter){
        super(R.layout.layout_class_header,R.layout.layout_per_info);
        students=classInfo.getStudents();
        records=classInfo.getRecords();
        this.type=type;
        this.mContext=context;
        this.sectionAdapter = sectionAdapter;
    }
    @Override
    public int getContentItemsTotal() {

        if (!expanded) {
            return 0;
        } else {
            switch (type) {
                case STUDENT:
                    if (students != null&&students.size()!=0)
                        return students.size();
                    else {
                        empty=true;
                        return 1;
                    }
                case RECORDS:
                    if (records != null&&records.size()!=0)
                        return records.size();
                    else {
                        empty=true;
                        return 1;
                    }
                default:
                    return 1;
            }
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
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder=(ItemViewHolder)holder;
        switch (this.type){
            case STUDENT:
                if(empty){
                    itemViewHolder.midlinear.setVisibility(View.GONE);
                    itemViewHolder.iconView.setVisibility(View.GONE);
                    itemViewHolder.LeftnameText.setText("请让学生通过邀请码加入班级");
                }else {
                    final ClassInfo.Student student = students.get(position);
                    if (position == 0) {
                        if (position == students.size() - 1)
                            itemViewHolder.midlinear.setVisibility(View.GONE);
                        else
                            itemViewHolder.bottomlinear.setVisibility(View.GONE);
                    } else if (position == students.size() - 1) {
                        itemViewHolder.toplinear.setVisibility(View.GONE);
                        itemViewHolder.midlinear.setVisibility(View.GONE);
                    } else {
                        itemViewHolder.bottomlinear.setVisibility(View.GONE);
                        itemViewHolder.toplinear.setVisibility(View.GONE);
                    }
                    Glide.with(mContext).load(R.mipmap.photo).bitmapTransform(new RoundedCornersTransformation(mContext
                            , 28, 1, RoundedCornersTransformation.CornerType.ALL)).into(itemViewHolder.iconView);
                    itemViewHolder.LeftnameText.setText(student.getName());
                    itemViewHolder.relativeLayout.setOnClickListener(v -> {
                        Intent intent = new Intent(mContext, StudentinfoActivity.class);
                        intent.putExtra("master", ((ClassPageActivity) mContext).master);
                        intent.putExtra("studentname", student.getName());
                        intent.putExtra("phoneNumber", student.getPhoneNumber());
                        intent.putExtra("classname", ((ClassPageActivity) mContext).classname);
                        intent.putExtra("classid", ((ClassPageActivity) mContext).classid);
                        mContext.startActivity(intent);
                    });
                }
                break;
            case RECORDS:
                if(empty){
                    itemViewHolder.midlinear.setVisibility(View.GONE);
                    itemViewHolder.iconView.setVisibility(View.GONE);
                    itemViewHolder.LeftnameText.setText("暂无！");
                }else {
                    final ClassInfo.Record record = records.get(position);
                    itemViewHolder.iconView.setVisibility(View.GONE);
                    itemViewHolder.midlinear.setVisibility(View.GONE);
                    if (position == 0) {
                        if (position != records.size() - 1)
                            itemViewHolder.bottomlinear.setVisibility(View.GONE);
                    } else
                        itemViewHolder.toplinear.setVisibility(View.GONE);
                    itemViewHolder.LeftnameText.setText(record.getTime());
                    itemViewHolder.RightnameText.setText(record.getAttendratio());
                    itemViewHolder.relativeLayout.setOnClickListener(v -> {
                        Intent intent = new Intent(mContext, RollCallResultActivity.class);
                        intent.putExtra("record_id", record.getRecordid());
                        intent.putExtra("class_title", ((ClassPageActivity) mContext).classname);
                        mContext.startActivity(intent);
                    });
                }
                break;
        }
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerViewHolder=(HeaderViewHolder)holder;
        switch (this.type){
            case STUDENT:
                headerViewHolder.titleClass.setText("总人数："+students.size()+"人");
                break;
            case RECORDS:
                headerViewHolder.titleClass.setText("点名记录");
                break;
        }

        headerViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                expanded = !expanded;
                headerViewHolder.iconControl.setImageResource(
                        expanded ? R.drawable.arrow_up : R.drawable.arrow_down
                );

                sectionAdapter.notifyDataSetChanged();

            }
        });
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title_class)
        TextView titleClass;
        @BindView(R.id.icon_control)
        ImageView iconControl;
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
}
