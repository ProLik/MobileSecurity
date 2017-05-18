package com.prolik.android.mobilesecurity.engine;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.prolik.android.mobilesecurity.bean.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ProLik on 2016/3/27.
 */
public class AppInfoEngine {
    public static List<AppInfo> getAppInfos(Context context){
        List<AppInfo> appInfos = new ArrayList<AppInfo>();
//       获取包管理者
        PackageManager packageManager = context.getPackageManager();
//        获取已安装的包
        /**
         * @param flags Additional option flags. Use any combination of 这个注释说明的 参数写0
         */
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        for (PackageInfo info :installedPackages) {
            AppInfo appInfo = new AppInfo();
            appInfo.setIcon(info.applicationInfo.loadIcon(packageManager));//获取应用程序图标
            appInfo.setName((String) info.applicationInfo.loadLabel(packageManager));//获取应用程序名字
            appInfo.setPackageName(info.packageName);//获取应用程序包名
            String dir = info.applicationInfo.sourceDir;//获取应用程序 资源路径
            File file = new File(dir);
            Long fileSize = file.length();
            appInfo.setSize(fileSize);

            //获取app安装标记

            int flag = info.applicationInfo.flags;
            if((flag & ApplicationInfo.FLAG_SYSTEM) != 0){
                appInfo.setIsUser(false);
            }else{
                appInfo.setIsUser(true);
            }

            if((flag & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0){
                appInfo.setIsRom(false);
            }else{
                appInfo.setIsRom(true);
            }

            appInfos.add(appInfo);
        }
        return appInfos;
    }
}
