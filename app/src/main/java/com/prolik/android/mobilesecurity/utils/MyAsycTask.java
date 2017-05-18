package com.prolik.android.mobilesecurity.utils;


import android.os.Handler;
import android.os.Message;

/**
 * Created by ProLik on 2016/5/14.
 */
public abstract class MyAsycTask {
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            postTask();
        }
    };
    /**
     * 在子线程之前执行的方法
     */
    public abstract void preTask();
    /**
     * 在子线程之中执行的方法
     */
    public abstract void doingTask();
    /**
     * 在子线程之后执行的方法
     */
    public abstract void postTask();

    public void execute(){
        preTask();
        new Thread (){
            @Override
            public void run() {
                doingTask();
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }
}
