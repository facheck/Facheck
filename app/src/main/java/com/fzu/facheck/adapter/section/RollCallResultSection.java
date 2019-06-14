package com.fzu.facheck.adapter.section;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fzu.facheck.R;
import com.fzu.facheck.entity.RollCall.RollCallResult;
import com.fzu.facheck.module.home.ClassPageActivity;
import com.fzu.facheck.module.home.StudentinfoActivity;
import com.fzu.facheck.widget.sectioned.StatelessSection;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RollCallResultSection extends StatelessSection {
    private static final String NOT_SIGNRE = "not_signed";
    private static final String SIGNED = "signed";

    private List<RollCallResult.RecordInfoBean.AlreadySignedInBean> signedBeans;
    private List<RollCallResult.RecordInfoBean.NotYetSignedInBean> notSignedBeans;
    private String type;
    private String mSigned_num;
    private String mNot_Signed_num;


    private Context mContext;


    public RollCallResultSection(RollCallResult rollCallResult, String type, Context context) {
        super(R.layout.layout_roll_call_result_header, R.layout.layout_roll_call_result_body);
        this.signedBeans = rollCallResult.getRecordInfo().getAlreadySignedIn();
        this.notSignedBeans = rollCallResult.getRecordInfo().getNotYetSignedIn();
        this.mSigned_num = String.valueOf(rollCallResult.getRecordInfo().getAlreadyNumber()) + "/" +
                String.valueOf(rollCallResult.getRecordInfo().getTotalNumber());
        this.mNot_Signed_num = String.valueOf(rollCallResult.getRecordInfo().getNotYetNumber()) + "/" +
                String.valueOf(rollCallResult.getRecordInfo().getTotalNumber());
        this.type = type;
        this.mContext = context;

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
            case NOT_SIGNRE:
                final RollCallResult.RecordInfoBean.NotYetSignedInBean notYetSignedInBean = notSignedBeans.get(position);
                itemViewHolder.mStudentID.setText(notYetSignedInBean.getAuthenticationId());
                itemViewHolder.mStudentName.setText(notYetSignedInBean.getName());
//                itemViewHolder.mRollCallResultBody.setOnClickListener(v -> {
//                    Intent intent = new Intent(mContext, StudentinfoActivity.class);
//                    intent.putExtra("master", ((ClassPageActivity) mContext).master);
//                    intent.putExtra("studentname", notYetSignedInBean.getName());
//                    intent.putExtra("phoneNumber", student.getPhoneNumber());
//                    intent.putExtra("classname", ((ClassPageActivity) mContext).classname);
//                    intent.putExtra("classid", ((ClassPageActivity) mContext).classid);
//                    mContext.startActivity(intent);
//                });
                break;
            case SIGNED:
                final RollCallResult.RecordInfoBean.AlreadySignedInBean alreadySignedInBean = signedBeans.get(position);
                itemViewHolder.mStudentID.setText(alreadySignedInBean.getAuthenticationId());
                itemViewHolder.mStudentName.setText(alreadySignedInBean.getName());

                break;

        }


    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {

        HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;

        switch (type) {
            case NOT_SIGNRE:
                headerViewHolder.mTitle_signed.setText(R.string.not_signed);
                headerViewHolder.mTitle_num.setText(mNot_Signed_num);

                break;
            case SIGNED:
                headerViewHolder.mTitle_signed.setText(R.string.signed);
                headerViewHolder.mTitle_num.setText(mSigned_num);

                break;

        }
    }


    @Override
    public int getContentItemsTotal() {
        switch (type) {
            case NOT_SIGNRE:
                return notSignedBeans.size();
            case SIGNED:
                return signedBeans.size();
            default:
                return 1;
        }
    }


    static class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.student_id)
        TextView mStudentID;
        @BindView(R.id.student_name)
        TextView mStudentName;
        @BindView(R.id.roll_call_result_body)
        RelativeLayout mRollCallResultBody;

        ItemViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title_signed)
        TextView mTitle_signed;
        @BindView(R.id.title_signed_num)
        TextView mTitle_num;


        HeaderViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }


}
