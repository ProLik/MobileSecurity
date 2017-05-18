package com.prolik.android.mobilesecurity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.prolik.android.mobilesecurity.R;

/**
 * Created by ProLik on 2015/11/29.
 */
public class SettingItemView extends RelativeLayout {
    private  TextView mTvTitle;
    private TextView mTvDesc;
    private CheckBox mCbStatus;
    private String mTitle;
    private String mDesc_on;
    private String mDesc_off;
    public static final String sName_Space = "http://schemas.android.com/apk/res/com.prolik.android.mobilesecurity";
    public SettingItemView(Context context) {
        super(context);
        initView();
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        /*int attributeCount = attrs.getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            String attributeName = attrs.getAttributeName(i);
            String attributeValue = attrs.getAttributeValue(i);
            System.out.println(attributeName + "=" + attributeValue);
        }*/
        mTitle = attrs.getAttributeValue(sName_Space,"title_info");
        mDesc_on = attrs.getAttributeValue(sName_Space,"desc_on");
        mDesc_off = attrs.getAttributeValue(sName_Space,"desc_off");
        initView();
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /*
    * 初始化布局
    * */
    private void initView(){
        /*
        *  @param root A view group that will be the parent.  Used to properly inflate the
        * layout_* parameters.
        * */
        View.inflate(getContext(), R.layout.view_setting_item,this);
        if(mTvTitle == null){
            mTvTitle = (TextView) findViewById(R.id.tv_title);
        }

        if(mTvDesc == null){
            mTvDesc = (TextView) findViewById(R.id.tv_desc);
        }
        if(mCbStatus == null){
            mCbStatus = (CheckBox) findViewById(R.id.cb_status);
        }
        setTitle(mTitle);
    }


    public void setDesc(String s) {
        mTvDesc.setText(s);
    }

    public void setTitle(String s) {
        mTvTitle.setText(s);
    }

    public boolean isCheck() {
        return mCbStatus.isChecked();
    }

    public void setCheck(boolean b) {
        mCbStatus.setChecked(b);
        // 根据选择的状态,更新文本描述
        if (b) {
            setDesc(mDesc_on);
        } else {
            setDesc(mDesc_off);
        }
    }
}
