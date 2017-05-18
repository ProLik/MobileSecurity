package com.prolik.android.mobilesecurity.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.util.Log;

import com.prolik.android.mobilesecurity.R;
import com.prolik.android.mobilesecurity.bean.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ProLik on 2016/5/12.
 */
public class TaskEngine {

    /**
     * 获取系统中所有进程信息
     * @param context
     * @return
     */
    public static List<TaskInfo> getAllTaskInfo(Context context){
        List<TaskInfo> list = new ArrayList<TaskInfo>();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        //获取所有正在运行的进程信息
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcess :runningAppProcesses) {
            TaskInfo taskInfo = new TaskInfo();
            String packageName = runningAppProcess.processName;
            taskInfo.setPackageName(packageName);
            //获取进程所占的内存空间,int[] pids : 输入几个进程的pid,就会返回几个进程所占的空间
            Debug.MemoryInfo[] processMemoryInfo = activityManager.getProcessMemoryInfo(new int[]{runningAppProcess.pid});

            int totalPss = processMemoryInfo[0].getTotalPss();    //Return total PSS memory usage in kB.
            long ramSize = totalPss * 1024; //MB
            taskInfo.setRamSize(ramSize);
            ApplicationInfo applicationInfo = null;
            try {
                applicationInfo = pm.getApplicationInfo(packageName, 0);

            } catch (PackageManager.NameNotFoundException e) {
                Log.d("mobile","存在空的进程：" + runningAppProcess.processName);
                e.printStackTrace();
            }
            Drawable icon = null;
            if(applicationInfo != null){
                icon = applicationInfo.loadIcon(pm);
                String name  = applicationInfo.loadLabel(pm).toString();
                taskInfo.setName(name);
                int flags = applicationInfo.flags;
                if ((applicationInfo.FLAG_SYSTEM & flags) == applicationInfo.FLAG_SYSTEM){
                    taskInfo.setUser(false);
                }else{
                    taskInfo.setUser(true);
                }
            }else{
                taskInfo.setName(packageName);
                taskInfo.setUser(false);
            }
            if (icon == null){
                icon = context.getResources().getDrawable(R.drawable.tg);
            }
            taskInfo.setIcon(icon);

            list.add(taskInfo);

        }
        return list;
    }
}
