package com.prolik.android.mobilesecurity.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.prolik.android.mobilesecurity.R;
import com.prolik.android.mobilesecurity.view.SettingItemView;

public class Setup2Activity extends BaseSetupActivity {

    private SettingItemView mSettingItemView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        mSettingItemView = (SettingItemView) findViewById(R.id.siv_sim);

        if(mSp.getString("sim",null) != null){
            mSettingItemView.setCheck(true);
        }else{
            mSettingItemView.setCheck(false);
        }
        mSettingItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSettingItemView.isCheck()){
                    mSettingItemView.setCheck(false);
                    mSp.edit().remove("sim").commit();//删除sim卡序列号
                }else{
                    mSettingItemView.setCheck(true);
                    //保存sim卡信息 保存sim卡序列号
                    /*系统服务*/
                    TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    /*sim卡*/
                    String simSerialNumber = tm.getSimSerialNumber();
                    mSp.edit().putString("sim",simSerialNumber).commit();//将sim卡序列号保存
                    Toast.makeText(Setup2Activity.this,"序列号："+simSerialNumber,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void showPreviousPage(){
        startActivity(new Intent(this, Setup1Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);//进入动画和退出动画
    }

    @Override
    public void showNextPage(){
        // 如果sim卡没有绑定,就不允许进入下一个页面
        String sim = mSp.getString("sim", null);
        if (TextUtils.isEmpty(sim)) {
            Toast.makeText(this, "必须绑定sim卡!", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(this, Setup3Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);//进入动画和退出动画
    }
}
