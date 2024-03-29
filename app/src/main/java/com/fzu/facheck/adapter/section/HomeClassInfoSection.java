package com.fzu.facheck.adapter.section;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.fzu.facheck.widget.DynamicLineChartManager;
import com.fzu.facheck.widget.sectioned.SectionedRecyclerViewAdapter;
import com.fzu.facheck.widget.sectioned.StatelessSection;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static android.support.constraint.Constraints.TAG;

public class HomeClassInfoSection extends StatelessSection {

    private static final String STUDENT = "student";
    private static final String RECORDS = "records";
    private static final String CHART = "chart";

    private DynamicLineChartManager dynamicLineChartManager;


    private List<ClassInfo.Student> students;
    private List<ClassInfo.Record> records;
    private String type;

    public Context getmContext() {
        return mContext;
    }

    private Context mContext;
    private SectionedRecyclerViewAdapter sectionAdapter;
    boolean expanded = false;
    boolean chartExpanded = true;
    boolean empty = false;


    public HomeClassInfoSection(ClassInfo classInfo, String type, Context context, SectionedRecyclerViewAdapter sectionAdapter) {
        super(R.layout.layout_class_header, R.layout.layout_per_info);
        students = classInfo.getStudents();
        records = classInfo.getRecords();
        this.type = type;
        this.mContext = context;
        this.sectionAdapter = sectionAdapter;
    }

    @Override
    public int getContentItemsTotal() {

        if (type.equals(CHART)){
            if (records != null && records.size() != 0)
                return 1;
            else {
                empty = true;
                return 1;
            }
        }

        if (!expanded) {
            return 0;
        } else {
            switch (type) {
                case STUDENT:
                    if (students != null && students.size() != 0)
                        return students.size();
                    else {
                        empty = true;
                        return 1;
                    }
                case RECORDS:
                    if (records != null && records.size() != 0)
                        return records.size();
                    else {
                        empty = true;
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
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        switch (this.type) {
            case STUDENT:
                if (empty) {
                    itemViewHolder.righticon.setVisibility(View.GONE);
                    itemViewHolder.lineChart.setVisibility(View.GONE);
                    itemViewHolder.LeftnameText.setText("请让学生通过邀请码加入班级");
                } else {
                    final ClassInfo.Student student = students.get(position);
                    if (position != 0)
                        itemViewHolder.toplinear.setVisibility(View.GONE);

                    itemViewHolder.lineChart.setVisibility(View.GONE);
                    itemViewHolder.RightnameText.setVisibility(View.GONE);
                    itemViewHolder.LeftnameText.setText(student.getStudentId());
                    itemViewHolder.StudentnameText.setText(student.getName());
                    itemViewHolder.relativeLayout.setOnClickListener(v -> {
                        Intent intent = new Intent(mContext, StudentinfoActivity.class);
                        intent.putExtra("master", ((ClassPageActivity) mContext).master);
                        intent.putExtra("studentName", student.getName());
                        intent.putExtra("studentId", student.getStudentId());
                        intent.putExtra("className", ((ClassPageActivity) mContext).classname);
                        intent.putExtra("classId", ((ClassPageActivity) mContext).classid);
                        mContext.startActivity(intent);
                    });
                }
                break;
            case RECORDS:
                if (empty) {
                    itemViewHolder.righticon.setVisibility(View.GONE);
                    itemViewHolder.lineChart.setVisibility(View.GONE);
                    itemViewHolder.LeftnameText.setText("暂无！");
                } else {
                    final ClassInfo.Record record = records.get(position);
                    itemViewHolder.righticon.setVisibility(View.GONE);
                    itemViewHolder.StudentnameText.setVisibility(View.GONE);
                    if (position != 0) {
                        itemViewHolder.toplinear.setVisibility(View.GONE);
                    }

                    itemViewHolder.lineChart.setVisibility(View.GONE);
                    itemViewHolder.LeftnameText.setText(record.getTime());
                    itemViewHolder.RightnameText.setText(record.getAttendanceRatio());

                    itemViewHolder.relativeLayout.setOnClickListener(v -> {
                        Intent intent = new Intent(mContext, RollCallResultActivity.class);
                        intent.putExtra("record_id", record.getRecordId());
                        intent.putExtra("master",((ClassPageActivity)mContext).master);
                        intent.putExtra("classId",((ClassPageActivity)mContext).classid);
                        intent.putExtra("class_title", ((ClassPageActivity) mContext).classname);
                        mContext.startActivity(intent);
                    });
                }
                break;
            case CHART:
                if (empty) {
                    itemViewHolder.righticon.setVisibility(View.GONE);
                    itemViewHolder.lineChart.setVisibility(View.GONE);
                    itemViewHolder.LeftnameText.setText("暂无！");
                }
                else{
                    initLineChart(itemViewHolder.lineChart);
                    itemViewHolder.righticon.setVisibility(View.INVISIBLE);
                }


                break;
        }
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
        switch (this.type) {
            case STUDENT:
                headerViewHolder.titleClass.setText("总人数：" + students.size() + "人");
                break;
            case RECORDS:
                headerViewHolder.titleClass.setText("点名记录");
                break;
            case CHART:
                headerViewHolder.titleClass.setText("班级出勤率概况");
                headerViewHolder.iconControl.setVisibility(View.INVISIBLE);
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
        @BindView(R.id.student_name)
        TextView StudentnameText;
        @BindView(R.id.right_icon)
        ImageView righticon;

        @BindView(R.id.bottomline)
        LinearLayout bottomlinear;
        @BindView(R.id.topline)
        LinearLayout toplinear;
        @BindView(R.id.line_chart)
        LineChart lineChart;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private void initLineChart(LineChart lineChart) {

        dynamicLineChartManager = new DynamicLineChartManager(lineChart, mContext.getResources().getColor(R.color.colorPrimary), mContext.getResources().getColor(R.color.subColorPrimary));

        dynamicLineChartManager.setYAxis(1.1f, 0, 6);

//
        ArrayList<Entry> values = new ArrayList<Entry>();
        for (int i=0;i<records.size();i++) {
//            Log.i(TAG, "initLineChart: "+);
            values.add(new Entry(i + 1, Float.valueOf(records.get(i).getAttendanceRate())));
        }

        dynamicLineChartManager.setData(values);


    }
}
