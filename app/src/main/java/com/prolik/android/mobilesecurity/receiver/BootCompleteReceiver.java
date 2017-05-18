package com.prolik.android.mobilesecurity.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * 监听手机启动广播
 * Created by ProLik on 2016/1/5.
 */
public class BootCompleteReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        String sim = sharedPreferences.getString("sim",null);
        if(!TextUtils.isEmpty(sim)){
            /*获取当前sim卡序列号*/
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String currentSim = telephonyManager.getSimSerialNumber();
            if(sim.equals(currentSim)){
                System.out.printf("手机安全");
            }else{
                System.out.printf("sim卡已变更，发送报警短信!!!");
                String phone = sharedPreferences.getString("safe_phone","");
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone,null,"sim卡已变更，发送报警短信!",null,null);

            }
        }
    }
}
