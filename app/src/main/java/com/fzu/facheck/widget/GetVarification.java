package com.fzu.facheck.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.fzu.facheck.R;

public class GetVarification extends LinearLayout {
    public GetVarification(Context context, AttributeSet attrs)
    {
        super(context,attrs);
        LayoutInflater.from(context).inflate(R.layout.verification_layout,this);
    }

}
