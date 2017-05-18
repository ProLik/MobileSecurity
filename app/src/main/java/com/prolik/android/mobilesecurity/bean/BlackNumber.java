package com.prolik.android.mobilesecurity.bean;

/**
 * 黑名单
 * Created by ProLik on 2016/2/15.
 */
public class BlackNumber  {

    private String number;      //号码
    private String mode;        //拦截模式   1全部拦截:电话拦截+短信拦截  2：电话拦截  3：短信拦截

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
