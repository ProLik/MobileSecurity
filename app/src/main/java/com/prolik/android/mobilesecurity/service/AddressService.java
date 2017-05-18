package com.prolik.android.mobilesecurity.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.prolik.android.mobilesecurity.R;
import com.prolik.android.mobilesecurity.db.dao.AddressDao;
import com.prolik.android.mobilesecurity.utils.Tool;

/**
 * Created by ProLik on 2016/1/16.
 */
public class AddressService extends Service {
    private SharedPreferences mSharedPreferences;
    private TelephonyManager mTelephonyManager;
    private OutCallReceiver mReceiver;

    private MyListener mMyListener;

    private WindowManager mWindowManager;
    private View mToastView;

    private int startX;
    private int startY;
    private int winHeight;
    private int winWidth;
    private WindowManager.LayoutParams params;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSharedPreferences = getSharedPreferences("config",MODE_PRIVATE);

        mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mMyListener = new MyListener();
        mTelephonyManager.listen(mMyListener, PhoneStateListener.LISTEN_CALL_STATE);//监听来电状态

        mReceiver = new OutCallReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(mReceiver,intentFilter);//动态注册广播
    }

    class OutCallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String number = getResultData();// 获取去电电话号码

            String address = AddressDao.getAddress(number);
            // Toast.makeText(context, address, Toast.LENGTH_LONG).show();
            if(number.contains("15800854494")){
                //Toast.makeText(AddressService.this,"老婆大大，来电话啦，快接！！！！",Toast.LENGTH_LONG).show();
                showToast("调戏一下老婆!!!");
            }else{
                Toast.makeText(AddressService.this,address,Toast.LENGTH_LONG).show();
                showToast(address);
            }
        }
    }


    class MyListener extends PhoneStateListener{


        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING://电话铃声响了
                    String address = AddressDao.getAddress(incomingNumber);
                    System.out.printf("来电了！！！！！！！！！！！！！！！！");
                    if(incomingNumber.contains("15800854494")){
                        //Toast.makeText(AddressService.this,"老婆大大，来电话啦，快接！！！！",Toast.LENGTH_LONG).show();
                        showToast("老婆大大，来电话啦，快接！！！！");
                    }else{
//                        Toast.makeText(AddressService.this,address,Toast.LENGTH_LONG).show();
                        showToast(address);
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE://电话闲置状态
                    if(mWindowManager != null && mToastView != null){
                        mWindowManager.removeView(mToastView);
                        mToastView = null;
                    }
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mTelephonyManager.listen(mMyListener,PhoneStateListener.LISTEN_NONE);//取消监听来电显示
        unregisterReceiver(mReceiver);//注销广播
    }


    private void showToast(String text){
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // 获取屏幕宽高
        winWidth = mWindowManager.getDefaultDisplay().getWidth();
        winHeight = mWindowManager.getDefaultDisplay().getHeight();

        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.gravity = Gravity.TOP + Gravity.LEFT;//将重心位置设置为左上方,
                // 也就是(0,0)从左上方开始,而不是默认的重心位置
        params.setTitle("Toast");
        params.x = mSharedPreferences.getInt("lastX",0);
        params.y = mSharedPreferences.getInt("lastY",0);
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mToastView =  View.inflate(AddressService.this, R.layout.toast_address,null);
        int[] bgs = new int[] { R.drawable.call_locate_white,
                R.drawable.call_locate_orange, R.drawable.call_locate_blue,
                R.drawable.call_locate_gray, R.drawable.call_locate_green };
        int style = mSharedPreferences.getInt("address_style", 0);

        mToastView.setBackgroundResource(bgs[style]);// 根据存储的样式更新背景

        TextView tvText = (TextView) mToastView.findViewById(R.id.tv_number);
        tvText.setText(text);

        mWindowManager.addView(mToastView, params);// 将view添加在屏幕上(Window)

        mToastView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();

                        //计算偏移量
                        int dx = endX - startX;
                        int dy = endY - startY;
                        // 更新左上右下距离
                        params.x += dx;
                        params.y += dy;

                        // 防止坐标偏离屏幕
                        if (params.x < 0) {
                            params.x = 0;
                        }

                        if (params.y < 0) {
                            params.y = 0;
                        }

                        // 防止坐标偏离屏幕
                        if (params.x > winWidth - mToastView.getWidth()) {
                            params.x = winWidth - mToastView.getWidth();
                        }

                        if (params.y > winHeight - mToastView.getHeight()) {
                            params.y = winHeight - mToastView.getHeight();
                        }

                        mWindowManager.updateViewLayout(mToastView, params);
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        mSharedPreferences.edit().putInt("lastX", params.x).putInt("lastY",params.y).commit();
                        break;
                }
                return true;
            }
        });
    }
}
