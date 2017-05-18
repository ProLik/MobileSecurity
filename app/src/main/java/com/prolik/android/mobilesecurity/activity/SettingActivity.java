package com.prolik.android.mobilesecurity.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.prolik.android.mobilesecurity.R;
import com.prolik.android.mobilesecurity.service.AddressService;
import com.prolik.android.mobilesecurity.service.CallSafeService;
import com.prolik.android.mobilesecurity.utils.ServiceStatusUtils;
import com.prolik.android.mobilesecurity.view.SettingItemArrowView;
import com.prolik.android.mobilesecurity.view.SettingItemView;

public class SettingActivity extends AppCompatActivity {


    private SettingItemView mAutoUpdate;
    private SettingItemView mAddress;
    private SettingItemView mCallSafe;
    private SettingItemArrowView mAddressStyle;
    private SettingItemArrowView mAddressLocation;
    private SettingItemArrowView mClearData;
    private SharedPreferences mSharedPreferences;

    private final static String[] items = new String[]{"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mSharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
        initUpdateView();
        initAddressView();
        initCallSafeView();
        initAddressStyle();
        initAddressLocation();

        mClearData = (SettingItemArrowView) findViewById(R.id.siav_clear_data);
        mClearData.setTitle("清除数据");
        mClearData.setDesc("清除守护卫士中设置数据");
        mClearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSharedPreferences.edit().clear().commit();
                mSharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
                initUpdateView();
                initAddressView();
                initAddressStyle();
                initAddressLocation();
            }
        });
    }


    /*
    * 设置升级
    * */
    private void initUpdateView(){
        mAutoUpdate = (SettingItemView) findViewById(R.id.si_update);
        if(mSharedPreferences.getBoolean("auto_update",true)){
            mAutoUpdate.setCheck(true);
        }else{
            mAutoUpdate.setCheck(false);
        }
        mAutoUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAutoUpdate.isCheck()) {
                    mAutoUpdate.setCheck(false);
                    mSharedPreferences.edit().putBoolean("auto_update", false).commit();
                } else {
                    mAutoUpdate.setCheck(true);
                    mSharedPreferences.edit().putBoolean("auto_update", true).commit();
                }
            }
        });
    }

    /*
    * 自动更新显示
    * */
    private void initAddressView() {
        mAddress = (SettingItemView) findViewById(R.id.siv_address);

        mAddress.setCheck(ServiceStatusUtils.isServiceRunning(this,"com.prolik.android.mobilesecurity.service.AddressService"));
        mAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAddress.isCheck()){
                    mAddress.setCheck(false);
                    stopService(new Intent(SettingActivity.this, AddressService.class));
                }else{
                    mAddress.setCheck(true);
                    startService(new Intent(SettingActivity.this, AddressService.class));
                }

            }
        });
    }


    /*
    * 黑名单拦截
    * */
    private void initCallSafeView() {
        mCallSafe = (SettingItemView) findViewById(R.id.siv_callsafe);

        mCallSafe.setCheck(ServiceStatusUtils.isServiceRunning(this,"com.prolik.android.mobilesecurity.service.CallSafeService"));
        mCallSafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCallSafe.isCheck()){
                    mCallSafe.setCheck(false);
                    stopService(new Intent(SettingActivity.this, CallSafeService.class));
                }else{
                    mCallSafe.setCheck(true);
                    startService(new Intent(SettingActivity.this, CallSafeService.class));
                }

            }
        });
    }

    /*
    * 修改提示框显示风格
    * */
    private void initAddressStyle(){

        mAddressStyle = (SettingItemArrowView) findViewById(R.id.siav_address_style);
        mAddressStyle.setTitle("归属地提示风格");
        int style = mSharedPreferences.getInt("address_style",0);
        mAddressStyle.setDesc(items[style]);
        mAddressStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSingleChooseDialog();
            }
        });
    }

    private void showSingleChooseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setTitle("归属地提示风格");
        int style = mSharedPreferences.getInt("address_style",0);
        builder.setSingleChoiceItems(items, style, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mSharedPreferences.edit().putInt("address_style",which).commit();
                dialog.dismiss();
                mAddressStyle.setDesc(items[which]);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }


    private void initAddressLocation(){
        mAddressLocation = (SettingItemArrowView) findViewById(R.id.siav_address_location);
        mAddressLocation.setTitle("归属地提示框显示位置");
        mAddressLocation.setDesc("设置归属地提示框显示位置");
        mAddressLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,DragViewActivity.class));
            }
        });
    }

}
