package com.prolik.android.mobilesecurity.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by ProLik on 2016/5/12.
 */
public class TaskInfo {
    private String mName;               //名称
    private Drawable mIcon;             //图标
    private long mRamSize;              //占用的内存空间
    private String mPackageName;        //包名
    private boolean mIsUser;            //是否是用户程序
    private boolean isChecked = false;  //checkbox是否被选中

    public TaskInfo() {
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public void setIcon(Drawable icon) {
        mIcon = icon;
    }

    public long getRamSize() {
        return mRamSize;
    }

    public void setRamSize(long ramSize) {
        mRamSize = ramSize;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String packageName) {
        mPackageName = packageName;
    }

    public boolean isUser() {
        return mIsUser;
    }

    public void setUser(boolean user) {
        mIsUser = user;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public TaskInfo(String name, Drawable icon, long ramSize,
                    String packageName, boolean isUser) {
        super();
        this.mName = name;
        this.mIcon = icon;
        this.mRamSize = ramSize;
        this.mPackageName = packageName;
        this.mIsUser = isUser;
    }
    @Override
    public String toString() {
        return "TaskInfo [name=" + mName + ", icon=" + mIcon + ", ramSize="
                + mRamSize + ", packageName=" + mPackageName + ", isUser="
                + mIsUser + "]";
    }
}
