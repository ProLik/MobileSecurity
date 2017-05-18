package com.prolik.android.mobilesecurity.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by ProLik on 2016/1/17.
 */
public class ServiceStatusUtils {
    /*
    * 检测服务是否在运行
    * */
    public static boolean isServiceRunning(Context context,String serviceName){
        ActivityManager manager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningServiceInfo> services = manager.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo service : services) {
            if(serviceName.equals(service.service.getClassName())){//服务存在
                return true;
            }
        }
        return false;
    }
}
