package com.prolik.android.mobilesecurity.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by ProLik on 2016/5/10.
 */
public class TaskUtil {

    /**
     * 获取正在运行的进程的个数
     * @param context
     * @return
     */
    public static int getProcessCount(Context context){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcess = activityManager.getRunningAppProcesses();
        return runningAppProcess.size();
    }

    /**
     * 获取剩余内存
     * @param context
     * @return
     */
    public static long getAvailableRam(Context context){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memory = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memory);
        //获取总的存储
        //long totalMem = memory.totalMem;//api 16以上
        return  memory.availMem;
    }

    /**
     * 获取总内存
     * @param context
     * @return
     * @deprecated
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static long getTotalRam(Context context){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memory = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memory);
        //获取总的存储
        //long totalMem = memory.totalMem;//api 16以上
        return  memory.totalMem;
    }

    /**
     * 兼容低版本
     * @return
     */
    public static long getTotalRam(){
        File file = new File("/proc/meminfo");
        StringBuffer sb = new StringBuffer();
        try {
            BufferedReader br  = new BufferedReader(new FileReader(file));
            String readLine = br.readLine();
            char [] chars = readLine.toCharArray();
            for (char c : chars){
                if(c  >= '0' && c <= '9'){
                    sb.append(c);
                }
            }
            String str = sb.toString();
            long l = Long.parseLong(str);
            return l * 1024;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
