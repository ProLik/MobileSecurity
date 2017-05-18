package com.prolik.android.mobilesecurity.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.prolik.android.mobilesecurity.R;
import com.prolik.android.mobilesecurity.service.LocationService;

public class SmsReceiver extends BroadcastReceiver {
    /**
     * 接收短信
     */
    public static final String  SMS_RECEIVE_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    /**
     * 接收彩信
     */
    public static final String  MMS_RECEIVE_ACTION = "android.provider.Telephony.WAP_PUSH_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.printf("信息来啦！");
        handSMS(context, intent);


    }

    // 激活设备管理器, 也可以在设置->安全->设备管理器中手动激活
    private void activeDevice(Context context,ComponentName componentName) {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                componentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "测试安全卫士设备管理器!");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    private void handSMS(Context context, Intent intent){
        Object[] objects = (Object[]) intent.getExtras().get("pdus");//获取短信内容
        for (Object obj : objects){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
            String body = smsMessage.getMessageBody().trim();
            System.out.printf(body);
            if ("#*alarm*#".equals(body) || body.contains("#*alarm*#")) {
                // 播放报警音乐, 即使手机调为静音,也能播放音乐, 因为使用的是媒体声音的通道,和铃声无关
                MediaPlayer player = MediaPlayer.create(context, R.raw.wzh );
                player.setVolume(1f, 1f);
                player.setLooping(true);
                player.start();
                abortBroadcast();// 中断短信的传递, 从而系统短信app就收不到内容了
            }else if("#*location*#".equals(body)|| body.contains("#*location*#")){
                context.startService(new Intent(context, LocationService.class));
                SharedPreferences sp = context.getSharedPreferences("config",
                        Context.MODE_PRIVATE);
                String location = sp.getString("location",
                        "getting location...");

                System.out.println("location:" + location);

                abortBroadcast();// 中断短信的传递, 从而系统短信app就收不到内容了
            }else if ("#*wipedata*#".equals(body)|| body.contains("#*wipedata*#")) {
                System.out.println("远程清除数据");
                DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);// 获取设备策略服务
                ComponentName componentName = new ComponentName(context, AdminReceiver.class);// 设备管理组件
                if (dpm.isAdminActive(componentName)) {// 判断设备管理器是否已经激活
                    dpm.wipeData(0);// 清除数据,恢复出厂设置
                } else {
                    Toast.makeText(context,"必须先激活设备管理器!", Toast.LENGTH_SHORT).show();
                    activeDevice(context,componentName);
                }
                abortBroadcast();
            } else if ("#*lockscreen*#".equals(body)|| body.contains("#*lockscreen*#")) {
                System.out.println("*****远程锁屏");
                DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);// 获取设备策略服务
                ComponentName componentName = new ComponentName(context, AdminReceiver.class);// 设备管理组件
                if (dpm.isAdminActive(componentName)) {// 判断设备管理器是否已经激活
                    System.out.println("*****锁屏中");
                    dpm.lockNow();// 立即锁屏
                } else {
                    Toast.makeText(context,"必须先激活设备管理器!", Toast.LENGTH_SHORT).show();
                    activeDevice(context,componentName);
                }
                abortBroadcast();
            }
        }
    }

    private void handMMS(Context context, Intent intent){
        System.out.println("彩信！！！！！！！！！");
    }


    /*
    // 卸载程序
    public void unInstall(View view) {
		mDPM.removeActiveAdmin(mDeviceAdminSample);//取消激活
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setData(Uri.parse("package:" + getPackageName()));
		startActivity(intent);
	}
    * */
}
