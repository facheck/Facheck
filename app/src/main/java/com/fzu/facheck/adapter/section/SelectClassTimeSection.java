package com.fzu.facheck.adapter.section;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fzu.facheck.R;
import com.fzu.facheck.module.home.SelectClassTimeActivity;
import com.fzu.facheck.widget.sectioned.StatelessSection;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectClassTimeSection extends StatelessSection {
//    public List<String> timeList;
    private Context mContext;
    public SelectClassTimeSection(List<String> time_list,Context context){
        super(R.layout.layout_per_time);
//        this.timeList=time_list;
        this.mContext=context;
    }
    @Override
    public int getContentItemsTotal() {
        return ((SelectClassTimeActivity)mContext).times.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder=(ItemViewHolder)holder;
        final String time=((SelectClassTimeActivity)mContext).times.get(position);
        itemViewHolder.timeText.setText(time);
        itemViewHolder.deleteButton.setOnClickListener(v->{((SelectClassTimeActivity)mContext).delete_time(position);});
    }
    static class ItemViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.the_time)
        TextView timeText;
        @BindView(R.id.delete_the_time)
        Button deleteButton;
        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
