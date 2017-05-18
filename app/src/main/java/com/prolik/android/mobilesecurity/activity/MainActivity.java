package com.prolik.android.mobilesecurity.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.prolik.android.mobilesecurity.R;
import com.prolik.android.mobilesecurity.utils.MathUtils;
import com.prolik.android.mobilesecurity.utils.StreamUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    String versionName = "";
    int versionCode = 0;
    String explain = "" ;
    String downloadUrl = "";
    public TextView tv_progress;

    private SharedPreferences mSharedPreferences;

    public static final int Code_Update_Dialog = 1;
    public static final int Code_Net_Error = 2;
    public static final int Code_IO_Error = 3;
    public static final int Code_Json_Error = 4;
    public static final int Code_Go_Home = 5;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Code_Update_Dialog:
                    showUpdateDialog(versionName,explain,downloadUrl);
                    break;
                case Code_Net_Error:
                    Toast.makeText(MainActivity.this,"网络连接错误",Toast.LENGTH_SHORT).show();
                    goHomeActivity();
                    break;
                case Code_IO_Error:
                    Toast.makeText(MainActivity.this,"数据流错误",Toast.LENGTH_SHORT).show();
                    goHomeActivity();
                    break;
                case Code_Json_Error:
                    Toast.makeText(MainActivity.this,"解析数据错误",Toast.LENGTH_SHORT).show();
                    goHomeActivity();
                    break;
                case Code_Go_Home:
                    goHomeActivity();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = (TextView) findViewById(R.id.tv_version);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        tv.setText("版本号：" + getVersionName());
        mSharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
        copyDB("address.db");// 拷贝归属地查询数据库
        if(mSharedPreferences.getBoolean("auto_update",true)){
            checkVersion();
        }else{
            mHandler.sendEmptyMessageDelayed(Code_Go_Home,2000);
        }
        // 渐变的动画效果
        AlphaAnimation anim = new AlphaAnimation(0.3f, 1f);
        anim.setDuration(2000);
        RelativeLayout rlRoot = (RelativeLayout) findViewById(R.id.rl_root);
        rlRoot.startAnimation(anim);


        shortcut();
    }

    /*创建快捷方式*/
    private void shortcut() {
        if(mSharedPreferences.getBoolean("firstshortcut",true)){
//          给桌面发送一个广播
            Intent intent1 = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            intent1.putExtra(Intent.EXTRA_SHORTCUT_NAME,"安全卫士");
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tg);
            intent1.putExtra(Intent.EXTRA_SHORTCUT_ICON,bitmap);
            // 设置快捷方式执行的操作
            intent1.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(this,MainActivity.class));
            intent1.putExtra("duplicate",false);
            sendBroadcast(intent1);
        }
    }

    private String getVersionName(){
        String versionName = "";
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
            System.out.println(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (versionName.equals("")){
            versionName = "1.0";
        }
        return versionName;
    }

    private int getVersionCode (){
        int versionCode = 1;
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }


    private void checkVersion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                Message message = Message.obtain();
                long startTime = System.currentTimeMillis();
//      模拟器加载本机的地址
                try {
                    URL url = new URL("http://10.0.2.2:8080/update.json");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(5000);//响应超时
                    conn.setConnectTimeout(5000);//连接超时
                    conn.connect();//连接
                    int responseCode = conn.getResponseCode();//获取响应码
                    if (responseCode == 200){
                        InputStream inputStream = conn.getInputStream();
                        String result = StreamUtils.readFromString(inputStream);
                        System.out.println(result);

                        JSONObject jsonObject = new JSONObject(result);
                        versionName = jsonObject.getString("versionName");
                        versionCode = jsonObject.getInt("versionCode");
                        explain = jsonObject.getString("explain");
                        downloadUrl = jsonObject.getString("downloadUrl");
                        System.out.println(downloadUrl);
                        if(getVersionCode() < versionCode){
                            message.what = Code_Update_Dialog;
                        }else{
                            message.what = Code_Go_Home;
                        }
                    }

                } catch (MalformedURLException e) {
//            url错误
                    message.what = Code_Net_Error;
                    e.printStackTrace();
                } catch (IOException e) {
//            网络错误
                    message.what = Code_IO_Error;
                    e.printStackTrace();
                } catch (JSONException e) {
                    //json 解析失败！
                    message.what = Code_Json_Error;
                    e.printStackTrace();
                } finally {
                    long endTime = System.currentTimeMillis();
                    if (endTime-startTime <2000){
                        try {
                            Thread.sleep(2000-endTime + startTime);
                        }catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                    mHandler.sendMessage(message);
                    if(conn != null){
                        conn.disconnect();
                    }
                }
            }

        }).start();
    }


    /*升级对话框*/
    private void showUpdateDialog(  String versionName,String explain,String downloadUrl){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("最新版本：" + versionName);
        builder.setMessage(explain);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("立即更新");
                downloadUpdate();

            }
        });
        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goHomeActivity();
            }
        });

        //设置取消监听，用户点击返回键时触发
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                goHomeActivity();
            }
        });

        builder.show();

    }

    private void goHomeActivity(){
        Intent intent = new Intent(MainActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void downloadUpdate(){
        HttpUtils http = new HttpUtils();
        String filePath = "";
        /*如果手机SD卡不可用则下载到内部存储空间*/
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            filePath = Environment.getExternalStorageDirectory() + "/MobileSecurity/update.apk";
        }else{
            filePath = "data/data/com.prolik.android.mobilesecurity/update.apk";
        }
        tv_progress.setVisibility(View.VISIBLE);
        System.out.println(downloadUrl);
        HttpHandler handler = http.download(downloadUrl, filePath, new RequestCallBack<File>() {


            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                double percent = current / total;
                MathUtils mathUtils = new MathUtils();
                tv_progress.setText("进度：" + mathUtils.round(percent,4) * 100 + "%" );
            }

            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setDataAndType(Uri.fromFile(responseInfo.result), "application/vnd.android.package-archive");
                startActivityForResult(intent,0);
            }
            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(MainActivity.this, "下载失败!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*用户取消安装*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        goHomeActivity();
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void copyDB(String dbName){
        File destFile = new File(getFilesDir(), dbName);// 要拷贝的目标地址
        System.out.printf(String.valueOf(getFilesDir()));
        if(destFile.exists()){//数据库已经存在
            System.out.printf("数据库已经存在！");
        }else{//拷贝数据库
            FileOutputStream out = null;
            InputStream  in = null;
            try {
                out = new FileOutputStream(destFile);
                in = getAssets().open(dbName);
                int length = 0;
                byte [] buffer = new byte[1024];
                while ( (length = in.read(buffer)) != -1){
                    out.write(buffer,0,length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    in.close();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
