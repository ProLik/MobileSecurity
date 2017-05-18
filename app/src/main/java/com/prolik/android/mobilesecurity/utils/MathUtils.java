package com.prolik.android.mobilesecurity.utils;

import java.math.BigDecimal;

/**
 * Created by ProLik on 2015/11/27.
 */
public class MathUtils {
    public double round(double number){
        return round(number,2);
    }
    public double round(double number,int num_digits){
        BigDecimal b = new BigDecimal(String.format("%." + (num_digits + 2) +"f",number) );
        BigDecimal one = new BigDecimal("1");
        return b.divide(one,num_digits,BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}
