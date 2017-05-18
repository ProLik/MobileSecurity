package com.prolik.android.mobilesecurity.utils;

import android.content.Context;

import java.lang.reflect.Field;

/**
 * Created by ProLik on 2016/1/20.
 */
public class Tool {
    public static int getSystemStatusHeight(Context context){
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }
}
