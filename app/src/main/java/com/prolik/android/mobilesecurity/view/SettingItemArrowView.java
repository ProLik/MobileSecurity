package com.prolik.android.mobilesecurity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.prolik.android.mobilesecurity.R;

import junit.framework.Test;

/**
 * Created by ProLik on 2016/1/17.
 */
public class SettingItemArrowView extends RelativeLayout {
    private TextView mTvTitle;
    private TextView mTvDesc;

    public SettingItemArrowView(Context context) {
        super(context);
        initView();
    }

    public SettingItemArrowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SettingItemArrowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        View.inflate(getContext(), R.layout.view_setting_item_arrow, this);
        if(mTvTitle == null){
            mTvTitle = (TextView) findViewById(R.id.tv_title);
        }

        if(mTvDesc == null){
            mTvDesc = (TextView) findViewById(R.id.tv_desc);
        }
    }

    public void setTitle(String title){
        mTvTitle.setText(title);
    }

    public void setDesc(String desc){
        mTvDesc.setText(desc);
    }

}
