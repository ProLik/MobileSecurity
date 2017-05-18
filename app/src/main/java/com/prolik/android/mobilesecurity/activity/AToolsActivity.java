package com.prolik.android.mobilesecurity.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.prolik.android.mobilesecurity.R;
import com.prolik.android.mobilesecurity.engine.SMSEngine;

public class AToolsActivity extends AppCompatActivity {

    private Context mContext;

    @ViewInject(R.id.processbar)
    private ProgressBar mProgressBar;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
        ViewUtils.inject(this);
        mContext = this;
    }

    /**
     * 归属地查询
     *
     * @param view
     */
    public void numberAddressQuery(View view) {
        startActivity(new Intent(this, AddressActivity.class));
    }

    /**
     * 备份短信
     * */
    public void backupsms(View v){
        dialog = new ProgressDialog(mContext);
        dialog.setTitle("提示");
        dialog.setMessage("稍安勿躁，正在努力备份中...");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.show();
        new Thread(){
            @Override
            public void run() {
                boolean result = SMSEngine.getAllSMS(mContext, new SMSEngine.ShowProgress() {
                    @Override
                    public void setMax(int max) {
                        dialog.setMax(max);
                        mProgressBar.setMax(max);
                    }

                    @Override
                    public void setProgress(int progress) {
                        dialog.setProgress(progress);
                        mProgressBar.setProgress(progress);
                    }

                    @Override
                    public void dismiss() {
                        dialog.dismiss();
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
                String message = "";
                if (result){
                    message = "备份成功";
                }else{
                    message = "备份失败";
                }
                Looper.prepare();
                Toast.makeText(mContext,message,Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();
    }

}
