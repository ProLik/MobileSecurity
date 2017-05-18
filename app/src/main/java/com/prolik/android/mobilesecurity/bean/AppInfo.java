package com.prolik.android.mobilesecurity.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by ProLik on 2016/3/27.
 */
public class AppInfo {

    private Drawable mIcon;//程序图片

    private String mName;//程序名称

    private String mPackageName;//程序包名

    private Long mSize;//程序大小

    private boolean mIsUser;//是否是用户程序 是 true 否 false

    private boolean mIsRom;//程序存放位置，内部存储 true  外部存储 false

    public Drawable getIcon() {
        return mIcon;
    }

    public void setIcon(Drawable icon) {
        mIcon = icon;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String packageName) {
        mPackageName = packageName;
    }

    public Long getSize() {
        return mSize;
    }

    public void setSize(Long size) {
        mSize = size;
    }

    public boolean isUser() {
        return mIsUser;
    }

    public void setIsUser(boolean isUser) {
        mIsUser = isUser;
    }

    public boolean isRom() {
        return mIsRom;
    }

    public void setIsRom(boolean isRom) {
        mIsRom = isRom;
    }
}
