package com.prolik.android.mobilesecurity.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.prolik.android.mobilesecurity.R;

/**
 * 设置引导页的基类，不需要再清单文件中注册，不需要界面展示
 * Created by ProLik on 2016/1/5.
 */
public abstract class BaseSetupActivity extends Activity{
    private GestureDetector mGestureDetector;//手势识别器
    public SharedPreferences mSp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSp = getSharedPreferences("config", MODE_PRIVATE);
        mGestureDetector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            /*监听手势滑动*/
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //velocity速度  velocityX x轴速度
                //e1 表示滑动起始点 e2 表示滑动的终点
                /*判断纵向滑动幅度是否过大，不允许切换界面*/
                if(Math.abs(e2.getRawY() - e1.getRawY()) > 300){
                    Toast.makeText(BaseSetupActivity.this,"不能这样滑哦！",Toast.LENGTH_SHORT).show();
                    return true;
                }
                /*滑动是否太慢*/
                if(Math.abs(velocityX) < 2000){
                    Toast.makeText(BaseSetupActivity.this,"滑动得太慢哦！",Toast.LENGTH_SHORT).show();
                    return true;
                }
                /*向左划 下一页*/
                if(e1.getRawX()- e2.getRawX() > 200){
                    showNextPage();
                    return true;
                }
                 /*向右划 上一页*/
                if(e2.getRawX() - e1.getRawX() > 200){
                    showPreviousPage();
                    return true;
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    /*点击下一页按钮*/
    public void next(View view){
        showNextPage();
    }
    /*点击上一页按钮*/
    public void previous(View view){
        showPreviousPage();
    }



    //手势识别
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);//委托手势识别器处理 手势滑动
        return super.onTouchEvent(event);
    }

    public  abstract void showPreviousPage();

    public abstract void showNextPage();
}
